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
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;


public class CustomFields
{
	public CustomFields()
	{
		specs = new Vector();
	}
	
	public CustomFields(FieldSpec[] specsToUse)
	{
		this();
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
		CustomFieldLoader loader = new CustomFieldLoader(MartusXml.CustomFieldSpecsElementName, fields);
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
	
	public static class CustomFieldLoader extends SimpleXmlDefaultLoader
	{
		public CustomFieldLoader(String tag, CustomFields fieldsToLoad)
		{
			super(tag);
			fields = fieldsToLoad;
		}
		
		public CustomFields getFields()
		{
			return fields;
		}
		
		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals("Field"))
				return new FieldLoader(tag);
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
			FieldSpec spec = ((FieldLoader)ended).getFieldSpec();
			fields.add(spec);
		}

		CustomFields fields;
	}
	
	public static class FieldLoader extends SimpleXmlDefaultLoader
	{
		public FieldLoader(String tag)
		{
			super(tag);
		}
		
		public FieldSpec getFieldSpec()
		{
			return spec;
		}
		
		public void startDocument(Attributes attrs) throws SAXParseException
		{
			int type = FieldSpec.getTypeCode(attrs.getValue("type"));
			if(type == FieldSpec.TYPE_GRID)
				spec = new GridFieldSpec();
			else
				spec = new FieldSpec(type);
			super.startDocument(attrs);
		}

		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals("Tag") || tag.equals("Label"))
				return new SimpleXmlStringLoader(tag);
			else if(tag.equals(GridFieldSpec.GRID_SPEC_DETAILS_TAG))
				return new GridFieldSpec.GridSpecDetailsLoader((GridFieldSpec)spec);
			
			return super.startElement(tag);
		}

		public void endElement(String thisTag, SimpleXmlDefaultLoader ended)
			throws SAXParseException
		{
			if(thisTag.equals("Tag"))
				spec.setTag(getText(ended));
			else if(thisTag.equals("Label"))
				spec.setLabel(getText(ended));
			else
				super.endElement(thisTag, ended);
		}

		private String getText(SimpleXmlDefaultLoader ended)
		{
			return ((SimpleXmlStringLoader)ended).getText();
		}
		FieldSpec spec;
	}
	
	Vector specs;
}
