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

import java.util.Vector;

import org.martus.common.CustomFields;
import org.martus.common.GridData;
import org.martus.common.MartusXml;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.crypto.SessionKey;
import org.martus.util.Base64;
import org.martus.util.Base64.InvalidBase64Exception;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlMapLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;


public class XmlFieldDataPacketLoader extends XmlPacketLoader
{
	public XmlFieldDataPacketLoader(FieldDataPacket packetToFill)
	{
		super(packetToFill);
		fdp = packetToFill;
	}
	
	public SimpleXmlDefaultLoader startElement(String tag)
		throws SAXParseException
	{
		if(tag.startsWith(MartusXml.FieldElementPrefix))
			return new XmlFieldLoader(tag);
		else if(tag.equals(MartusXml.AttachmentElementName))
			return new XmlAttachmentLoader(tag);
		else if(tag.equals(MartusXml.CustomFieldSpecsElementName))
			return new CustomFields.XmlCustomFieldsLoader(new CustomFields());
		else if(getTagsContainingStrings().contains(tag))
			return new SimpleXmlStringLoader(tag);
		else
			return super.startElement(tag);
	}

	public void endElement(String tag, SimpleXmlDefaultLoader ended)
		throws SAXParseException
	{
		try
		{
			if(tag.startsWith(MartusXml.FieldElementPrefix))
			{
				XmlFieldLoader loader = (XmlFieldLoader)ended;
				fdp.set(loader.getFieldNameTag(), loader.getText());
			}
			else if(tag.equals(MartusXml.AttachmentElementName))
			{
				XmlAttachmentLoader loader = (XmlAttachmentLoader)ended;
					fdp.addAttachment(loader.getAttachmentProxy(fdp.getAccountId()));
			}
			else if(tag.equals(MartusXml.CustomFieldSpecsElementName))
			{
				CustomFields.XmlCustomFieldsLoader loader = (CustomFields.XmlCustomFieldsLoader)ended;
				fdp.setCustomFields(loader.getFields());
			}
			else if(getTagsContainingStrings().contains(tag))
			{
				String value = ((SimpleXmlStringLoader)ended).getText();
				if(tag.equals(MartusXml.EncryptedFlagElementName))
					fdp.setEncrypted(true);
				else if(tag.equals(MartusXml.FieldListElementName))
					setLegacyCustomFields(value);
				else if(tag.equals(MartusXml.EncryptedDataElementName))
					encryptedData = value;
				else if(tag.equals(MartusXml.HQSessionKeyElementName))
					encryptedHQSessionKey = new SessionKey(Base64.decode(value));
			}
			else
				super.endElement(tag, ended);
		}
		catch (InvalidBase64Exception e)
		{
			e.printStackTrace();
			throw new SAXParseException("Bad base64 in " + tag, null);
		} 
	}

	private void setLegacyCustomFields(String value)
	{
		if(fdp.getFieldCount() == 0)
			fdp.setFieldSpecsFromString(value);
	}
	
	private Vector getTagsContainingStrings()
	{
		if(stringTags == null)
		{
			stringTags = new Vector();
			stringTags.add(MartusXml.EncryptedFlagElementName);
			stringTags.add(MartusXml.FieldListElementName);
			stringTags.add(MartusXml.HQSessionKeyElementName);
			stringTags.add(MartusXml.EncryptedDataElementName);
		}
		return stringTags;
	}
	
	static class XmlFieldLoader extends SimpleXmlStringLoader
	{
		XmlFieldLoader(String tag)
		{
			super(tag);
		}
		
		String getFieldNameTag()
		{
			int prefixLength = MartusXml.FieldElementPrefix.length();
			String fieldNameTag = getTag().substring(prefixLength);
			return fieldNameTag;
		}

		public String getText()
		{
			if(complexData != null)
				return complexData;
			return super.getText();
		}

		public SimpleXmlDefaultLoader startElement(String tag)
				throws SAXParseException
		{
			if(tag.equals(GridData.GRID_DATA_TAG))
				return new GridData.XmlGridDataLoader(new GridData());
			return super.startElement(tag);
		}

		public void endElement(String tag, SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			if(tag.equals(GridData.GRID_DATA_TAG))
				complexData = ((GridData.XmlGridDataLoader)ended).getGridData().getXmlRepresentation(); 
			super.endElement(tag, ended);
		}
		String complexData;
	}
	
	static class XmlAttachmentLoader extends SimpleXmlMapLoader
	{
		public XmlAttachmentLoader(String tag)
		{
			super(tag);
		}

		public AttachmentProxy getAttachmentProxy(String accountId) throws InvalidBase64Exception
		{
			String attachmentLocalId = get(MartusXml.AttachmentLocalIdElementName);
			byte[] sessionKeyBytes = Base64.decode(get(MartusXml.AttachmentKeyElementName));
			String attachmentLabel = get(MartusXml.AttachmentLabelElementName);
			
			UniversalId uid = UniversalId.createFromAccountAndLocalId(accountId, attachmentLocalId);
			SessionKey sessionKey = new SessionKey(sessionKeyBytes);
			return new AttachmentProxy(uid, attachmentLabel, sessionKey);
		}

	}
	
	SessionKey encryptedHQSessionKey;
	String encryptedData;
	private FieldDataPacket fdp;
	private static Vector stringTags;
}
