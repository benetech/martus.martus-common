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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.clientside.Localization;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;


public class FieldSpec
{
	public static FieldSpec createStandardField(String tagToUse, int typeToUse)
	{
		return createCustomField(tagToUse, "", typeToUse);
	}
	
	public static FieldSpec createCustomField(String tagToUse, String labelToUse, int typeToUse)
	{
		return new FieldSpec(tagToUse, labelToUse, typeToUse, false);
	}
	
	public FieldSpec(int typeToUse)
	{
		this("","",typeToUse,false);
	}
	
	FieldSpec(String tagToUse, String labelToUse, int typeToUse, boolean hasUnknownToUse)
	{
		tag = tagToUse;
		label = labelToUse;
		type = typeToUse;
		hasUnknown = hasUnknownToUse;
	}
	
	public String toString()
	{
		return MartusXml.getTagStartWithNewline("Field", "type", MartusUtilities.getXmlEncoded(getTypeString(getType()))) +  
				MartusXml.getTagStart("Tag") + 
				MartusUtilities.getXmlEncoded(getTag()) + 
				MartusXml.getTagEnd("Tag") +
				MartusXml.getTagStart("Label") + 
				MartusUtilities.getXmlEncoded(getLabel()) + 
				MartusXml.getTagEnd("Label") +
				getDetailsXml() +
				MartusXml.getTagEnd("Field");
	}
	
	protected String getDetailsXml()
	{
		return "";
	}

	public String getTag()
	{
		return tag;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public int getType()
	{
		return type;
	}
	
	public String getDefaultValue()
	{
		if(type == TYPE_BOOLEAN)
			return FieldSpec.FALSESTRING;
		if(type == TYPE_DATE)
			return Bulletin.getToday();
		if(type == TYPE_DATERANGE)
			return Bulletin.getToday();
		if(type == TYPE_LANGUAGE)
			return Localization.LANGUAGE_OTHER;
		return "";
	}
	
	public boolean hasUnknownStuff()
	{
		return hasUnknown;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public static boolean isAllFieldsPresent(FieldSpec[] previousSpec, FieldSpec[] currentSpec)
	{
		for (int i = 0; i < previousSpec.length; i++)
		{
			FieldSpec thisField = previousSpec[i];
			boolean fieldFound = false;
			for (int j = 0; j < currentSpec.length; j++)
			{
				if(currentSpec[j].equals(thisField))
					fieldFound = true;
			}
			if(!fieldFound)
				return false;
		}
		return true;
	}
	
	public boolean equals(Object obj)
	{
		if(!obj.getClass().equals(getClass()))
			return false;
		FieldSpec otherSpec = (FieldSpec)obj;
		if(hasUnknown != otherSpec.hasUnknown)
			return false;
		if(!label.equals(otherSpec.label))
			return false;
		if(!tag.equals(otherSpec.tag))
			return false;
		if(type != otherSpec.type)
			return false;
		return true;
	}

	public static String getTypeString(int type)
	{
		Map map = getTypeCodesAndStrings();
		if(!map.containsKey(new Integer(type)))
			type = TYPE_UNKNOWN;
		String value = (String)map.get(new Integer(type));
		return value;		 
	}
	
	public static int getTypeCode(String type)
	{
		Map map = getTypeCodesAndStrings();
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry)iter.next();
			String value = (String)entry.getValue();
			if(value.equals(type))
				return ((Integer)entry.getKey()).intValue();
		} 
		return TYPE_UNKNOWN;
	}
	
	private static Map getTypeCodesAndStrings()
	{
		HashMap map = new HashMap();
		map.put(new Integer(TYPE_NORMAL), "STRING");
		map.put(new Integer(TYPE_MULTILINE), "MULTILINE");
		map.put(new Integer(TYPE_DATE), "DATE");
		map.put(new Integer(TYPE_DATERANGE), "DATERANGE");
		map.put(new Integer(TYPE_BOOLEAN), "BOOLEAN");
		map.put(new Integer(TYPE_LANGUAGE), "LANGUAGE");
		map.put(new Integer(TYPE_GRID), "GRID");
		map.put(new Integer(TYPE_DROPDOWN), "DROPDOWN");
		map.put(new Integer(TYPE_UNKNOWN), "UNKNOWN");
		return map;
	}

	public static class XmlFieldSpecLoader extends SimpleXmlDefaultLoader
	{
		public XmlFieldSpecLoader()
		{
			super(FieldSpec.FIELD_SPEC_XML_TAG);
		}
		
		public FieldSpec getFieldSpec()
		{
			return spec;
		}
		
		public void startDocument(Attributes attrs) throws SAXParseException
		{
			int type = getTypeCode(attrs.getValue(FieldSpec.FIELD_SPEC_TYPE_ATTR));
			if(type == TYPE_GRID)
				spec = new GridFieldSpec();
			else if(type == TYPE_DROPDOWN)
				spec = new DropDownFieldSpec();
			else
				spec = new FieldSpec(type);
			super.startDocument(attrs);
		}
	
		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals(FieldSpec.FIELD_SPEC_TAG_XML_TAG) || tag.equals(FieldSpec.FIELD_SPEC_LABEL_XML_TAG))
				return new SimpleXmlStringLoader(tag);
			else if(tag.equals(GridFieldSpec.GRID_SPEC_DETAILS_TAG))
				return new GridFieldSpec.GridSpecDetailsLoader((GridFieldSpec)spec);
			else if(tag.equals(DropDownFieldSpec.DROPDOWN_SPEC_CHOICES_TAG))
				return new DropDownFieldSpec.DropDownSpecLoader((DropDownFieldSpec)spec);
			
			return super.startElement(tag);
		}
	
		public void endElement(String thisTag, SimpleXmlDefaultLoader ended)
			throws SAXParseException
		{
			if(thisTag.equals(FieldSpec.FIELD_SPEC_TAG_XML_TAG))
				spec.setTag(getText(ended));
			else if(thisTag.equals(FieldSpec.FIELD_SPEC_LABEL_XML_TAG))
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

	String tag;
	int type;
	String label;
	boolean hasUnknown;

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_MULTILINE = 1;
	public static final int TYPE_DATE = 2;
	public static final int TYPE_LANGUAGE = 4;
	public static final int TYPE_DATERANGE = 5;
	public static final int TYPE_BOOLEAN = 6;
	public static final int TYPE_GRID = 7;
	public static final int TYPE_DROPDOWN = 8;
	public static final int TYPE_UNKNOWN = 99;
	
	public static final String FIELD_SPEC_XML_TAG = "Field";
	public static final String FIELD_SPEC_TAG_XML_TAG = "Tag";
	public static final String FIELD_SPEC_LABEL_XML_TAG = "Label";
	public static final String FIELD_SPEC_TYPE_ATTR = "type";

	public static final String TRUESTRING = "1";
	public static final String FALSESTRING = "0";

}
