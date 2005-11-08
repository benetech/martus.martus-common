/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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
package org.martus.common.bulletin;

import org.martus.common.FieldCollection;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.SAXParseException;

public class XmlBulletinLoader extends SimpleXmlDefaultLoader
{
	
	public XmlBulletinLoader()
	{
		super(MartusBulletinElementName);
	}
	
	public SimpleXmlDefaultLoader startElement(String tag)
		throws SAXParseException
	{
		if(tag.equals(MainFieldSpecsElementName))
			return new FieldCollection.XmlCustomFieldsLoader(tag, new FieldCollection());
		else if(tag.equals(PrivateFieldSpecsElementName))
			return new FieldCollection.XmlCustomFieldsLoader(tag, new FieldCollection());
		return super.startElement(tag);
	}

	public void endElement(String tag, SimpleXmlDefaultLoader ended)
		throws SAXParseException
	{
		if(tag.equals(MainFieldSpecsElementName))
		{
			FieldCollection.XmlCustomFieldsLoader loader = (FieldCollection.XmlCustomFieldsLoader)ended;
			mainFieldSpecs = loader.getFields();
		}
		else if(tag.equals(PrivateFieldSpecsElementName))
		{
			FieldCollection.XmlCustomFieldsLoader loader = (FieldCollection.XmlCustomFieldsLoader)ended;
			privateFieldSpecs = loader.getFields();
		}
		else
			super.endElement(tag, ended);
	}
	
	public FieldCollection getMainFieldSpecs()
	{
		return mainFieldSpecs;
	}
	
	public FieldCollection getPrivateFieldSpecs()
	{
		return privateFieldSpecs;
	}

	private FieldCollection mainFieldSpecs;
	private FieldCollection privateFieldSpecs;

	public static final String MartusBulletinElementName = "MartusBulletin";
	public static final String MainFieldSpecsElementName = "MainFieldSpecs";
	public static final String PrivateFieldSpecsElementName = "PrivateFieldSpecs";
	public static final String FieldValuesElementName = "FieldValues";
	
}
