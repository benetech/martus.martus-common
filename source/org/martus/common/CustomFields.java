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

package org.martus.common;

import java.util.Vector;

import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlParser;
import org.xml.sax.SAXParseException;


public class CustomFields
{
	public CustomFields()
	{
		this(new FieldSpec[0]);
	}
	
	public CustomFields(FieldSpec[] specsToUse)
	{
		specs = new Vector();
		for(int i=0; i < specsToUse.length; ++i)
			add(specsToUse[i]);
	}
	
	public void add(FieldSpec newSpec)
	{
		specs.add(newSpec);
	}
	
	public int count()
	{
		return specs.size();
	}
	
	public FieldSpec[] getSpecs()
	{
		return (FieldSpec[])specs.toArray(new FieldSpec[0]);
	}
	
	public String toString()
	{
		String result = "<" + MartusXml.CustomFieldSpecsElementName + ">\n";
		for (int i = 0; i < specs.size(); i++)
		{
			FieldSpec spec = (FieldSpec)specs.get(i);
			result += spec.toString();
			result += "\n";
		}
		result += "</" + MartusXml.CustomFieldSpecsElementName + ">\n";
		return result;
	}
	
	public static class CustomFieldsParseException extends Exception {}
	
	public static FieldSpec[] parseXml(String xml) throws CustomFieldsParseException
	{
		CustomFields fields = new CustomFields();
		XmlCustomFieldsLoader loader = new XmlCustomFieldsLoader(fields);
		try
		{
			SimpleXmlParser.parse(loader, xml);
			return fields.getSpecs();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new CustomFieldsParseException();
		}
	}
	
	public static class XmlCustomFieldsLoader extends SimpleXmlDefaultLoader
	{
		public XmlCustomFieldsLoader(CustomFields fieldsToLoad)
		{
			super(MartusXml.CustomFieldSpecsElementName);
			fields = fieldsToLoad;
		}
		
		public CustomFields getFields()
		{
			return fields;
		}
		
		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals(FieldSpec.FIELD_SPEC_XML_TAG))
				return new FieldSpec.XmlFieldSpecLoader();
			return super.startElement(tag);
		}

		public void addText(char[] ch, int start, int length)
			throws SAXParseException
		{
			return;
		}

		public void endElement(String tag, SimpleXmlDefaultLoader ended)
			throws SAXParseException
		{
			FieldSpec spec = ((FieldSpec.XmlFieldSpecLoader)ended).getFieldSpec();
			fields.add(spec);
		}

		CustomFields fields;
	}
	
	Vector specs;
}
