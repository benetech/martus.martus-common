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

import org.martus.common.MartusXml;
import org.martus.util.Base64;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;


public class XmlHeaderPacketLoader extends XmlPacketLoader
{
	public XmlHeaderPacketLoader(BulletinHeaderPacket bhpToFill)
	{
		super(bhpToFill);
		bhp = bhpToFill;
	}

	public SimpleXmlDefaultLoader startElement(String tag)
		throws SAXParseException
	{
		if(getTagsContainingStrings().contains(tag))
			return new SimpleXmlStringLoader(tag);

		return super.startElement(tag);
	}

	public void addText(char[] ch, int start, int length)
		throws SAXParseException
	{
	}

	public void endElement(SimpleXmlDefaultLoader ended)
		throws SAXParseException
	{
		String tag = ended.getTag();
		if(getTagsContainingStrings().contains(tag))
			endStringElement(ended);
		else
			super.endElement(ended);
	}

	private void endStringElement(SimpleXmlDefaultLoader ended)
		throws SAXParseException
	{
		
		try
		{
			String tag = ended.getTag();
			String value = ((SimpleXmlStringLoader)ended).getText();
			if(tag.equals(MartusXml.BulletinStatusElementName))
				bhp.setStatus(value);
			else if(tag.equals(MartusXml.LastSavedTimeElementName))
				bhp.setLastSavedTime(Long.parseLong(value));
			else if(tag.equals(MartusXml.AllPrivateElementName))
				bhp.setAllPrivateFromXmlTextValue(value);
			else if(tag.equals(MartusXml.DataPacketIdElementName))
				bhp.setFieldDataPacketId(value);
			else if(tag.equals(MartusXml.DataPacketSigElementName))
				bhp.setFieldDataSignature(Base64.decode(value));
			else if(tag.equals(MartusXml.PrivateDataPacketIdElementName))
				bhp.setPrivateFieldDataPacketId(value);
			else if(tag.equals(MartusXml.PrivateDataPacketSigElementName))
				bhp.setPrivateFieldDataSignature(Base64.decode(value));
			else if(tag.equals(MartusXml.PublicAttachmentIdElementName))
				bhp.addPublicAttachmentLocalId(value);
			else if(tag.equals(MartusXml.PrivateAttachmentIdElementName))
				bhp.addPrivateAttachmentLocalId(value);
			else if(tag.equals(MartusXml.HQPublicKeyElementName))
				bhp.setHQPublicKey(value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SAXParseException(e.getMessage(), null);
		}
	}

	private Vector getTagsContainingStrings()
	{
		if(stringTags == null)
		{
			stringTags = new Vector();
			stringTags.add(MartusXml.BulletinStatusElementName);
			stringTags.add(MartusXml.LastSavedTimeElementName);
			stringTags.add(MartusXml.AllPrivateElementName);
			stringTags.add(MartusXml.DataPacketIdElementName);
			stringTags.add(MartusXml.DataPacketSigElementName);
			stringTags.add(MartusXml.PrivateDataPacketIdElementName);
			stringTags.add(MartusXml.PrivateDataPacketSigElementName);
			stringTags.add(MartusXml.PublicAttachmentIdElementName);
			stringTags.add(MartusXml.PrivateAttachmentIdElementName);
			stringTags.add(MartusXml.HQPublicKeyElementName);
		}
		return stringTags;
	}

	BulletinHeaderPacket bhp;
	private static Vector stringTags;
}
