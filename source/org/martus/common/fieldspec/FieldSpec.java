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

import org.martus.common.MartusXml;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.martus.util.xml.XmlUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;


public class FieldSpec
{
	public static FieldSpec createStandardField(String tagToUse, FieldType typeToUse)
	{
		return createCustomField(tagToUse, "", typeToUse);
	}
	
	public static FieldSpec createCustomField(String tagToUse, String labelToUse, FieldType typeToUse)
	{
		return new FieldSpec(tagToUse, labelToUse, typeToUse, false);
	}
	
	public FieldSpec(FieldType typeToUse)
	{
		this("", typeToUse);
	}
	
	public FieldSpec(String labelToUse, FieldType typeToUse)
	{
		this("",labelToUse,typeToUse,false);
	}

	public FieldSpec(String tagToUse, String labelToUse, FieldType typeToUse, boolean hasUnknownToUse)
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
	
	public FieldType getType()
	{
		return type;
	}
	
	public String getDefaultValue()
	{
		return(getType().getDefaultValue());
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
	
	public void setType(FieldType type)
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
		if(!type.equals(otherSpec.type))
			return false;
		return true;
	}

	public static String getTypeString(FieldType type)
	{
		return type.getTypeName();
	}
	
	public static FieldType getTypeCode(String type)
	{
		return FieldType.createFromTypeName(type);
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
			FieldType type = getTypeCode(attrs.getValue(FieldSpec.FIELD_SPEC_TYPE_ATTR));
			spec = type.createEmptyFieldSpec();
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
	FieldType type;
	String label;
	boolean hasUnknown;

	public static final String FIELD_SPEC_XML_TAG = "Field";
	public static final String FIELD_SPEC_TAG_XML_TAG = "Tag";
	public static final String FIELD_SPEC_LABEL_XML_TAG = "Label";
	public static final String FIELD_SPEC_TYPE_ATTR = "type";

	public static final String TRUESTRING = "1";
	public static final String FALSESTRING = "0";

}
