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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

	
	public boolean importTemplate(MartusCrypto security, File fileToImport) throws FutureVersionException
	{
		try
		{
			clearData();
			String templateXMLToImportTopSection = "";
			String templateXMLToImportBottomSection = "";
			FileInputStream in = new FileInputStream(fileToImport);
			byte[] dataBundle = new byte[(int)fileToImport.length()];
			int len = dataBundle.length;
			in.read(dataBundle);
			in.close();
			byte[] dataBundleTopSection;
			if(isLegacyTemplateFile(fileToImport))
			{
				dataBundleTopSection = dataBundle;
				FieldSpecCollection defaultBottomFields = StandardFieldSpecs.getDefaultBottomSectionFieldSpecs();
				templateXMLToImportBottomSection = defaultBottomFields.toXml();
			}
			else
			{
				ByteArrayInputStream bIn = new ByteArrayInputStream(dataBundle);
				DataInputStream bundleIn = new DataInputStream(bIn);
				bundleIn.skip(versionHeader.length()); //ignore header
				int templateVersion = bundleIn.readInt();
				if(templateVersion > exportVersionNumber)
					throw new FutureVersionException();
				int topSectionBundleLength = bundleIn.readInt();
				int bottomSectionBundleLength = bundleIn.readInt();
				int titleLength = bundleIn.readInt();
				int descriptionLength = bundleIn.readInt();
				dataBundleTopSection = new byte[topSectionBundleLength];
				byte[] dataBundleBottomSection = new byte[bottomSectionBundleLength];
				byte[] dataTitle = new byte[titleLength];
				byte[] dataDescription = new byte[descriptionLength];
				bundleIn.read(dataBundleTopSection,0, topSectionBundleLength);
				bundleIn.read(dataBundleBottomSection,0, bottomSectionBundleLength);
				bundleIn.read(dataTitle,0, titleLength);
				bundleIn.read(dataDescription,0, descriptionLength);
				
				Vector authorizedKeys = getSignedByAsVector(dataBundleBottomSection, security);
				byte[] xmlBytesBottomSection = security.extractFromSignedBundle(dataBundleBottomSection, authorizedKeys);
				templateXMLToImportBottomSection = new String(xmlBytesBottomSection, "UTF-8");
				byte[] bytesTitleOnly = security.extractFromSignedBundle(dataTitle, authorizedKeys);
				title = new String(bytesTitleOnly, "UTF-8");
				byte[] bytesDescruotionOnly = security.extractFromSignedBundle(dataDescription, authorizedKeys);
				description = new String(bytesDescruotionOnly, "UTF-8");
			}

			Vector authorizedKeys = getSignedByAsVector(dataBundleTopSection, security);
			byte[] xmlBytesTopSection = security.extractFromSignedBundle(dataBundleTopSection, authorizedKeys);
			templateXMLToImportTopSection = new String(xmlBytesTopSection, "UTF-8");
			
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
		return false;
	}

	public Vector getSignedByAsVector(byte[] dataBundleBottomSection, MartusCrypto security) throws MartusSignatureException, IOException
	{
		String signedBy = security.getSignedBundleSigner(dataBundleBottomSection);
		if(signedByPublicKey == null)
			signedByPublicKey = signedBy;
		else if(!signedByPublicKey.equals(signedBy))
			throw new MartusSignatureException();
		
		String[] authorizedKeysArray = new String[] { signedBy };
		Vector authorizedKeysVector = new Vector(Arrays.asList(authorizedKeysArray));
		return authorizedKeysVector;
	}
	
	public boolean isLegacyTemplateFile(File fileToImport) throws IOException
	{
		FileInputStream in = new FileInputStream(fileToImport);
		byte[] versionHeaderInBytes = new byte[versionHeader.length()];
		in.read(versionHeaderInBytes);
		in.close();
		String versionHeaderInString = new String(versionHeaderInBytes);
		return !versionHeaderInString.equals(versionHeader);
	}
	
	public boolean ExportTemplate(MartusCrypto security, File fileToExportXml, String xmlToExportTopSection, String xmlToExportBottomSection, String toExportTitle, String toExportDescription)
	{
		if(!setData(toExportTitle, toExportDescription, xmlToExportTopSection, xmlToExportBottomSection))
			return false;

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
		clearData();
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
			byte[] signedBundleTopSection = security.createSignedBundle(xmlTopSectionText.getBytes("UTF-8"));
			byte[] signedBundleBottomSection = security.createSignedBundle(xmlBottomSectionText.getBytes("UTF-8"));
			byte[] signedBundleTitle = security.createSignedBundle(title.getBytes("UTF-8"));
			byte[] signedBundleDescription = security.createSignedBundle(description.getBytes("UTF-8"));
			int lengthTop = signedBundleTopSection.length;
			dataOut.writeInt(lengthTop);
			int lengthBottom = signedBundleBottomSection.length;
			dataOut.writeInt(lengthBottom);
			int lengthTitle = signedBundleTitle.length;
			dataOut.writeInt(lengthTitle);
			int lengthDesc = signedBundleDescription.length;
			dataOut.writeInt(lengthDesc);
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
	private Vector errors;
	private String xmlTopSectionText;
	private String xmlBottomSectionText;
	private String signedByPublicKey;
	//Version 3
	private String title;
	private String description;
}
