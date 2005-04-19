/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
Technology, Inc. (Benetech).

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

package org.martus.common.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.martus.common.CustomFields;
import org.martus.common.HQKeys;
import org.martus.common.LegacyCustomFields;
import org.martus.common.MartusConstants;
import org.martus.common.MartusXml;
import org.martus.common.XmlWriterFilter;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.SessionKey;
import org.martus.common.crypto.MartusCrypto.DecryptionException;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.util.Base64;
import org.martus.util.UnicodeReader;
import org.martus.util.Base64.InvalidBase64Exception;
import org.martus.util.inputstreamwithseek.ByteArrayInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.xml.SimpleXmlParser;
import org.xml.sax.SAXException;



public class FieldDataPacket extends Packet
{
	public FieldDataPacket(UniversalId universalIdToUse, FieldSpec[] fieldSpecsToUse)
	{
		super(universalIdToUse);
		setFieldSpecs(fieldSpecsToUse);
		authorizedToReadKeys = new HQKeys();
		clearAll();
	}
	
	void setCustomFields(CustomFields fieldsToUse)
	{
		fieldSpecs = fieldsToUse;
	}

	void setFieldSpecs(FieldSpec[] fieldSpecsToUse)
	{
		fieldSpecs = new CustomFields(fieldSpecsToUse);
	}
	
	void setFieldSpecsFromString(String delimitedFieldSpecs)
	{
		setFieldSpecs(LegacyCustomFields.parseFieldSpecsFromString(delimitedFieldSpecs));
	}

	public static UniversalId createUniversalId(MartusCrypto accountSecurity)
	{
		return UniversalId.createFromAccountAndLocalId(accountSecurity.getPublicKeyString(), createLocalId(accountSecurity, prefix));
	}

	public static boolean isValidLocalId(String localId)
	{
		return localId.startsWith(prefix);
	}

	public boolean isEncrypted()
	{
		return encryptedFlag;
	}

	public void setEncrypted(boolean newValue)
	{
		encryptedFlag = newValue;
	}

	public void setAuthorizedToReadKeys(HQKeys authorizedKeys)
	{
		authorizedToReadKeys = authorizedKeys;
	}

	public HQKeys getAuthorizedToReadKeys()
	{
		return authorizedToReadKeys;
	}

	public boolean isPublicData()
	{
		return !isEncrypted();
	}

	public boolean isEmpty()
	{
		if(fieldData.size() > 0)
			return false;

		if(attachments.size() > 0)
			return false;

		return true;
	}

	public int getFieldCount()
	{
		return fieldSpecs.count();
	}

	public FieldSpec[] getFieldSpecs()
	{
		return fieldSpecs.getSpecs();
	}

	public boolean fieldExists(String fieldTag)
	{
		FieldSpec[] specs = getFieldSpecs();
		for(int f = 0; f < specs.length; ++f)
		{
			if(specs[f].getTag().equals(fieldTag))
				return true;
		}
		return false;
	}

	public String get(String fieldTag)
	{
		Object value = fieldData.get(fieldTag);
		if(value == null)
			return "";

		return (String)value;
	}

	public void set(String fieldTag, String data)
	{
		if(!fieldExists(fieldTag))
			return;

		fieldData.put(fieldTag, data);
	}

	public void clearAll()
	{
		fieldData = new TreeMap();
		clearAttachments();
		authorizedToReadKeys.clear();
	}

	public void clearAttachments()
	{
		attachments = new Vector();
	}

	public AttachmentProxy[] getAttachments()
	{
		AttachmentProxy[] list = new AttachmentProxy[attachments.size()];
		for(int i = 0; i < list.length; ++i)
			list[i] = (AttachmentProxy)attachments.get(i);

		return list;
	}

	public void addAttachment(AttachmentProxy a)
	{
		attachments.add(a);
	}


	public byte[] writeXml(Writer writer, MartusCrypto signer) throws IOException
	{
		byte[] result = null;
		if(isEncrypted() && !isEmpty())
			result = writeXmlEncrypted(writer, signer);
		else
			result = writeXmlPlainText(writer, signer);
		return result;
	}

	public void loadFromXml(InputStreamWithSeek inputStream, byte[] expectedSig, MartusCrypto security) throws
		IOException,
		InvalidPacketException,
		WrongPacketTypeException,
		SignatureVerificationException,
		MartusCrypto.DecryptionException,
		MartusCrypto.NoKeyPairException
	{
		setEncrypted(false);
		fieldData.clear();
		if(security != null)
			verifyPacketSignature(inputStream, expectedSig, security);
		try
		{
			XmlFieldDataPacketLoader loader = loadXml(inputStream);
			
			String encryptedData = loader.encryptedData;
			if(encryptedData != null)
			{
				String publicCodeOfSecurity = MartusCrypto.computePublicCode(security.getPublicKeyString());
				SessionKey encryptedHQSessionKey = loader.GetHQSessionKey(publicCodeOfSecurity);
				loadEncryptedXml(encryptedData, encryptedHQSessionKey, security);
			}
			
		}
		catch(DecryptionException e)
		{
			throw(e);
		}
		catch(Exception e)
		{
			// TODO: Be more specific with exceptions!
			//e.printStackTrace();
			throw new InvalidPacketException(e.getMessage());
		}
	}

	private void loadEncryptedXml(
		String encryptedData,
		SessionKey encryptedHQSessionKey,
		MartusCrypto security)
		throws
			DecryptionException,
			InvalidBase64Exception,
			IOException,
			InvalidPacketException,
			SignatureVerificationException,
			ParserConfigurationException,
			SAXException
	{
		SessionKey sessionKey = null;
		boolean isOurBulletin = security.getPublicKeyString().equals(getAccountId());
		if(!isOurBulletin)
		{
			sessionKey = security.decryptSessionKey(encryptedHQSessionKey);
		}
		
		byte[] encryptedBytes = Base64.decode(encryptedData);
		ByteArrayInputStreamWithSeek inEncrypted = new ByteArrayInputStreamWithSeek(encryptedBytes);
		ByteArrayOutputStream outPlain = new ByteArrayOutputStream();
		security.decrypt(inEncrypted, outPlain, sessionKey);
		ByteArrayInputStreamWithSeek inDecrypted = new ByteArrayInputStreamWithSeek(outPlain.toByteArray());
		verifyPacketSignature(inDecrypted, security);
		loadXml(inDecrypted);
	}

	private XmlFieldDataPacketLoader loadXml(InputStreamWithSeek in)
		throws IOException, ParserConfigurationException, SAXException
	{
		XmlFieldDataPacketLoader loader = new XmlFieldDataPacketLoader(this);
		SimpleXmlParser.parse(loader, new UnicodeReader(in));
		return loader;
	}

	public byte[] writeXmlPlainText(Writer writer, MartusCrypto signer) throws IOException
	{
		return super.writeXml(writer, signer);
	}

	public byte[] writeXmlEncrypted(Writer writer, MartusCrypto signer) throws IOException
	{
		StringWriter plainTextWriter = new StringWriter();
		writeXmlPlainText(plainTextWriter, signer);
		String payload = plainTextWriter.toString();

		EncryptedFieldDataPacket efdp = new EncryptedFieldDataPacket(getUniversalId(), payload, signer);
		efdp.setHQPublicKeys(getAuthorizedToReadKeys());
		return efdp.writeXml(writer, signer);
	}

	protected String getPacketRootElementName()
	{
		return MartusXml.FieldDataPacketElementName;
	}

	protected void internalWriteXml(XmlWriterFilter dest) throws IOException
	{
		super.internalWriteXml(dest);
		if(isEncrypted() && !isEmpty())
			writeElement(dest, MartusXml.EncryptedFlagElementName, "");

		String xmlSpecs = fieldSpecs.toString();
		FieldSpec[] specs = fieldSpecs.getSpecs();
		
		if(isNonCustomFieldSpecs(fieldSpecs))
		{
			writeElement(dest, MartusXml.FieldListElementName, LegacyCustomFields.buildFieldListString(specs));
		}
		else
		{
			writeElement(dest, MartusXml.FieldListElementName, MartusConstants.deprecatedCustomFieldSpecs);
			dest.writeDirect(xmlSpecs);
		}
		
		for(int i = 0; i < specs.length; ++i)
		{
			FieldSpec spec = specs[i];
			String key = spec.getTag();
			String xmlTag = MartusXml.FieldElementPrefix + key;
			String fieldText = (String)(fieldData.get(key));
			if(fieldText == null)
				continue;
			if(spec.getType() == FieldSpec.TYPE_GRID)
				writeNonEncodedElement(dest, xmlTag, fieldText);
			else
				writeElement(dest, xmlTag, fieldText);
		}

		for(int i = 0 ; i <attachments.size(); ++i)
		{
			AttachmentProxy a = (AttachmentProxy)attachments.get(i);
			dest.writeStartTag(MartusXml.AttachmentElementName);
			writeElement(dest, MartusXml.AttachmentLocalIdElementName, a.getUniversalId().getLocalId());
			String sessionKeyString = Base64.encode(a.getSessionKey().getBytes());
			writeElement(dest, MartusXml.AttachmentKeyElementName, sessionKeyString);
			writeElement(dest, MartusXml.AttachmentLabelElementName, a.getLabel());
			dest.writeEndTag(MartusXml.AttachmentElementName);
		}
	}

	protected String getFieldListString()
	{
		return LegacyCustomFields.buildFieldListString(getFieldSpecs());
	}

	public static boolean isNonCustomFieldSpecs(CustomFields fields)
	{
		return fields.toString().equals(DEFAULT_LEGACY_SPECS_AS_XML);
	}
	
	private static final String DEFAULT_LEGACY_SPECS_AS_XML = 
		"<CustomFields>\n" +
		"<Field type='LANGUAGE'>\n" +
		"<Tag>language</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='STRING'>\n" +
		"<Tag>author</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='STRING'>\n" +
		"<Tag>organization</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='STRING'>\n" +
		"<Tag>title</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='STRING'>\n" +
		"<Tag>location</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='STRING'>\n" +
		"<Tag>keywords</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='DATERANGE'>\n" +
		"<Tag>eventdate</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='DATE'>\n" +
		"<Tag>entrydate</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='MULTILINE'>\n" +
		"<Tag>summary</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"<Field type='MULTILINE'>\n" +
		"<Tag>publicinfo</Tag>\n" +
		"<Label></Label>\n" +
		"</Field>\n" +
		"\n" +
		"</CustomFields>\n";
	
	final String packetHeaderTag = "packet";

	private boolean encryptedFlag;
	private CustomFields fieldSpecs;
	private Map fieldData;
	private Vector attachments;

	private static final String prefix = "F-";
	private HQKeys authorizedToReadKeys;
}

