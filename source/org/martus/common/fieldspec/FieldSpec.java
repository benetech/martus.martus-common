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

package org.martus.common.fieldspec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.martus.common.MartusXml;
import org.martus.common.MiniLocalization;
import org.martus.common.utilities.DateUtilities;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.martus.util.xml.XmlUtilities;
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
		this("", typeToUse);
	}
	
	public FieldSpec(String labelToUse, int typeToUse)
	{
		this("",labelToUse,typeToUse,false);
	}

	public FieldSpec(String tagToUse, String labelToUse, int typeToUse, boolean hasUnknownToUse)
	{
		tag = tagToUse;
		label = labelToUse;
		type = typeToUse;
		hasUnknown = hasUnknownToUse;
	}
	
	public String toString()
	{
		String rootTag = FIELD_SPEC_XML_TAG;
		return toXml(rootTag);
	}

	public String toXml(String rootTag)
	{
		String typeString = XmlUtilities.getXmlEncoded(getTypeString(getType()));
		String rootTagLine = MartusXml.getTagStartWithNewline(rootTag, FIELD_SPEC_TYPE_ATTR, typeString);
		return rootTagLine +  
				MartusXml.getTagStart(FIELD_SPEC_TAG_XML_TAG) + 
				XmlUtilities.getXmlEncoded(getTag()) + 
				MartusXml.getTagEnd(FIELD_SPEC_TAG_XML_TAG) +
				MartusXml.getTagStart(FIELD_SPEC_LABEL_XML_TAG) + 
				XmlUtilities.getXmlEncoded(getLabel()) + 
				MartusXml.getTagEnd(FIELD_SPEC_LABEL_XML_TAG) +
				getDetailsXml() +
				MartusXml.getTagEnd(rootTag);
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
		switch(getType())
		{
			case TYPE_BOOLEAN:
				return FieldSpec.FALSESTRING;
			case TYPE_DATE:
			case TYPE_DATERANGE:
				return DateUtilities.getFirstOfThisYear();
			case TYPE_LANGUAGE:
				return MiniLocalization.LANGUAGE_OTHER;
			case TYPE_NORMAL:
			case TYPE_MULTILINE:
			case TYPE_MESSAGE:
			case TYPE_UNKNOWN:
			case TYPE_SEARCH_VALUE:
			case TYPE_ANY_FIELD:
				return "";
			default:
				throw new RuntimeException("This class or a subclass needs to define the default value for type " + getType());
		}
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
	
	public void setType(int type)
	{
		this.type = type;
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
		map.put(new Integer(TYPE_MESSAGE), "MESSAGE");
		map.put(new Integer(TYPE_UNKNOWN), "UNKNOWN");
		return map;
	}
	
	public static FieldSpec createFromXml(String xml) throws Exception
	{
		XmlFieldSpecLoader loader = new XmlFieldSpecLoader();
		loader.parse(xml);
		return loader.getFieldSpec();
	}

	public static DateFormat getStoredDateFormat()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);
		return df;
	}

	public static class XmlFieldSpecLoader extends SimpleXmlDefaultLoader
	{
		public XmlFieldSpecLoader()
		{
			this(FieldSpec.FIELD_SPEC_XML_TAG);
		}
		
		public XmlFieldSpecLoader(String rootTag)
		{
			super(rootTag);
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
				spec = new CustomDropDownFieldSpec();
			else if(type == TYPE_MESSAGE)
				spec = new MessageFieldSpec();
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
			else if(tag.equals(CustomDropDownFieldSpec.DROPDOWN_SPEC_CHOICES_TAG))
				return new CustomDropDownFieldSpec.DropDownSpecLoader((CustomDropDownFieldSpec)spec);
			else if(tag.equals(MessageFieldSpec.MESSAGE_SPEC_MESSAGE_TAG))
				return new MessageFieldSpec.MessageSpecLoader((MessageFieldSpec)spec);
			
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
	public static final int TYPE_MESSAGE = 9;
	public static final int INSERT_NEXT_TYPE_HERE_AND_INCREASE_THIS_BY_ONE = 10;
	
	public static final int TYPE_UNKNOWN = 99;
	public static final int TYPE_SEARCH_VALUE = 100;	// only used internally
	public static final int TYPE_ANY_FIELD = 101;		// only used internally
	
	public static final String FIELD_SPEC_XML_TAG = "Field";
	public static final String FIELD_SPEC_TAG_XML_TAG = "Tag";
	public static final String FIELD_SPEC_LABEL_XML_TAG = "Label";
	public static final String FIELD_SPEC_TYPE_ATTR = "type";

	public static final String TRUESTRING = "1";
	public static final String FALSESTRING = "0";

}
