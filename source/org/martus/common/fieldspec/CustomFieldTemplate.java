/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.common.fieldspec;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Vector;

import org.martus.common.FieldCollection;
import org.martus.common.FieldCollection.CustomFieldsParseException;
import org.martus.common.FieldSpecCollection;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.MartusSignatureException;
import org.martus.util.StreamableBase64;
import org.martus.util.UnicodeUtilities;
import org.martus.util.inputstreamwithseek.ByteArrayInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;


public class CustomFieldTemplate
{
	public CustomFieldTemplate()
	{
		super();
		clearData();
	}
	
	public CustomFieldTemplate(String title, String description, FieldCollection topSection, FieldCollection bottomSection) 
	{
		setData(title, description, topSection.toString(), bottomSection.toString());
	}

	private boolean setData(String title, String description, String xmlTopSection, String xmlBottomSection)
	{
		clearData();
		if(!isvalidTemplateXml(xmlTopSection, xmlBottomSection))
			return false;
		this.title = title;
		this.description = description;
		this.xmlTopSectionText = xmlTopSection;
		this.xmlBottomSectionText = xmlBottomSection;
		return true;
	}

	private void clearData()
	{
		errors = new Vector();
		xmlTopSectionText = "";
		xmlBottomSectionText = "";
		title = "";
		description = "";
	}
	
	public class FutureVersionException extends Exception
	{
	}

	public boolean importTemplate(MartusCrypto security, InputStreamWithSeek inputStreamWithSeek) throws FutureVersionException, IOException
	{
		try
		{
			clearData();
			String templateXMLToImportTopSection = "";
			String templateXMLToImportBottomSection = "";
			
			InputStreamWithSeek dataBundleTopSectionInputStream;
			if(isLegacyTemplateFile(inputStreamWithSeek))
			{
				dataBundleTopSectionInputStream = inputStreamWithSeek;
				FieldSpecCollection defaultBottomFields = StandardFieldSpecs.getDefaultBottomSectionFieldSpecs();
				templateXMLToImportBottomSection = defaultBottomFields.toXml();
			}
			else
			{
				DataInputStream bundleIn = new DataInputStream(inputStreamWithSeek);
				bundleIn.skip(versionHeader.length()); //ignore header
				int templateVersion = bundleIn.readInt();
				if(templateVersion > exportVersionNumber)
					throw new FutureVersionException();
				
				int topSectionBundleLength = bundleIn.readInt();
				int bottomSectionBundleLength = bundleIn.readInt();
				int titleLength = bundleIn.readInt();
				int descriptionLength = bundleIn.readInt();
				
				byte[] dataBundleTopSection = new byte[topSectionBundleLength];
				byte[] dataBundleBottomSection = new byte[bottomSectionBundleLength];
				byte[] dataTitle = new byte[titleLength];
				byte[] dataDescription = new byte[descriptionLength];
				
				bundleIn.read(dataBundleTopSection,0, topSectionBundleLength);
				dataBundleTopSectionInputStream = new ByteArrayInputStreamWithSeek(dataBundleTopSection);
				dataBundleTopSectionInputStream.seek(0);
				bundleIn.read(dataBundleBottomSection,0, bottomSectionBundleLength);
				bundleIn.read(dataTitle,0, titleLength);
				bundleIn.read(dataDescription,0, descriptionLength);
				
				Vector bottomSectionSignedByKeys = getSignedByAsVector(dataBundleBottomSection, security);
				byte[] xmlBytesBottomSection = security.extractFromSignedBundle(dataBundleBottomSection, bottomSectionSignedByKeys);
				templateXMLToImportBottomSection = UnicodeUtilities.toUnicodeString(xmlBytesBottomSection);

				Vector titleSignedByKeys = getSignedByAsVector(dataTitle, security);
				byte[] bytesTitleOnly = security.extractFromSignedBundle(dataTitle, titleSignedByKeys);
				title = UnicodeUtilities.toUnicodeString(bytesTitleOnly);

				Vector descriptionSignedByKeys = getSignedByAsVector(dataDescription, security);
				byte[] bytesDescriptionOnly = security.extractFromSignedBundle(dataDescription, descriptionSignedByKeys);
				description = UnicodeUtilities.toUnicodeString(bytesDescriptionOnly);
			}

			Vector topSectionSignedByKeys = getSignedByAsVector(dataBundleTopSectionInputStream, security);
			byte[] xmlBytesTopSection = security.extractFromSignedBundle(dataBundleTopSectionInputStream, topSectionSignedByKeys);
			templateXMLToImportTopSection = UnicodeUtilities.toUnicodeString(xmlBytesTopSection);
			
			if(isvalidTemplateXml(templateXMLToImportTopSection, templateXMLToImportBottomSection))
			{
				xmlTopSectionText = templateXMLToImportTopSection;
				xmlBottomSectionText = templateXMLToImportBottomSection;
				return true;
			}
		}
		catch(IOException e)
		{
			errors.add(CustomFieldError.errorIO(e.getMessage()));
		}
		catch(MartusSignatureException e)
		{
			errors.add(CustomFieldError.errorSignature());
		}
		catch(AuthorizationFailedException e)
		{
			errors.add(CustomFieldError.errorUnauthorizedKey());
		}
		finally
		{
			inputStreamWithSeek.close();
		}
		return false;
	}

	public Vector getSignedByAsVector(InputStreamWithSeek dataBundleSection, MartusCrypto security) throws MartusSignatureException, IOException
	{
		String signedBy = security.getSignedBundleSigner(dataBundleSection);
		return getSignedByAsVector(signedBy);
	}
	
	public Vector getSignedByAsVector(byte[] dataBundleSection, MartusCrypto security) throws MartusSignatureException, IOException
	{
		return getSignedByAsVector(new ByteArrayInputStreamWithSeek(dataBundleSection), security);
	}

	private Vector getSignedByAsVector(String signedBy)	throws MartusSignatureException
	{
		if(signedByPublicKey == null)
			signedByPublicKey = signedBy;
		else if(!signedByPublicKey.equals(signedBy))
			throw new MartusSignatureException();
		
		String[] authorizedKeysArray = new String[] { signedBy };
		Vector authorizedKeysVector = new Vector(Arrays.asList(authorizedKeysArray));
		return authorizedKeysVector;
	}
	
	public boolean isLegacyTemplateFile(InputStreamWithSeek in) throws IOException
	{
		byte[] versionHeaderInBytes = new byte[versionHeader.length()];
		in.read(versionHeaderInBytes);
		in.seek(0);
		String versionHeaderInString = new String(versionHeaderInBytes);
		return !versionHeaderInString.equals(versionHeader);
	}
	
	public boolean exportTemplate(MartusCrypto security, File fileToExportXml, String xmlToExportTopSection, String xmlToExportBottomSection, String toExportTitle, String toExportDescription)
	{
		if(!setData(toExportTitle, toExportDescription, xmlToExportTopSection, xmlToExportBottomSection))
			return false;
		boolean result = exportTemplate(security, fileToExportXml);
		clearData();
		return result;
	}

	public boolean exportTemplate(MartusCrypto security, File fileToExportXml)
	{
		FileOutputStream out = null;
		boolean result = false;
		try
		{
			out = new FileOutputStream(fileToExportXml);
			result = saveContentsToOutputStream(security, out);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public String getExportedTemplateAsBase64String(MartusCrypto security)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if(saveContentsToOutputStream(security, out))
			return StreamableBase64.encode(out.toByteArray());
		return "";
	}

	private boolean saveContentsToOutputStream(MartusCrypto security, OutputStream out)
	{
		try
		{
			DataOutputStream dataOut = new DataOutputStream(out);
			dataOut.write(versionHeader.getBytes());
			dataOut.writeInt(exportVersionNumber);
			byte[] signedBundleTopSection = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(xmlTopSectionText));
			byte[] signedBundleBottomSection = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(xmlBottomSectionText));
			byte[] signedBundleTitle = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(title));
			byte[] signedBundleDescription = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(description));
			dataOut.writeInt(signedBundleTopSection.length);
			dataOut.writeInt(signedBundleBottomSection.length);
			dataOut.writeInt(signedBundleTitle.length);
			dataOut.writeInt(signedBundleDescription.length);
			dataOut.write(signedBundleTopSection);
			dataOut.write(signedBundleBottomSection);
			dataOut.write(signedBundleTitle);
			dataOut.write(signedBundleDescription);
			dataOut.flush();
			dataOut.close();
			out.flush();
			out.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isvalidTemplateXml(String xmlToValidateTopSection, String xmlToValidateBottomSection)
	{
		try
		{
			FieldSpecCollection newSpecsTopSection = FieldCollection.parseXml(xmlToValidateTopSection);
			FieldSpecCollection newSpecsBottomSection = FieldCollection.parseXml(xmlToValidateBottomSection);
			CustomFieldSpecValidator checker = new CustomFieldSpecValidator(newSpecsTopSection, newSpecsBottomSection);
			if(checker.isValid())
				return true;
			errors.addAll(checker.getAllErrors());
		}
		catch (InvalidIsoDateException e)
		{
			System.out.println("isValidTemplateXml");
			e.printStackTrace();
			errors.add(CustomFieldError.errorInvalidIsoDate(e.getTag(), e.getLabel(), e.getType()));
		}
		catch (CustomFieldsParseException e)
		{
			System.out.println("isValidTemplateXml");
			e.printStackTrace();
			errors.add(CustomFieldError.errorParseXml(e.getMessage()));
		}
		return false;
	}
	
	public Vector getErrors()
	{
		return errors;
	}
	
	public String getImportedTopSectionText()
	{
		return xmlTopSectionText;
	}
	
	public String getImportedBottomSectionText()
	{
		return xmlBottomSectionText;
	}

	public String getSignedBy()
	{
		return signedByPublicKey;
	} 
	
	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public static final String versionHeader = "Export Version Number:";
	public static final int exportVersionNumber = 3;
	public static final String CUSTOMIZATION_TEMPLATE_EXTENSION = ".mct";
	
	private Vector errors;
	private String xmlTopSectionText;
	private String xmlBottomSectionText;
	private String signedByPublicKey;
	//Version 3
	private String title;
	private String description;
}
