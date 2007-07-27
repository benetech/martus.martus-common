/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
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


import org.martus.common.MiniLocalization;
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
		return createCustomField(tagToUse, labelToUse, typeToUse, false);
	}
	
	public static FieldSpec createCustomField(String tagToUse, String labelToUse, FieldType typeToUse, boolean hasUnknownToUse)
	{
		return new FieldSpec(tagToUse, labelToUse, typeToUse, hasUnknownToUse);
	}
	
	public static FieldSpec createFieldSpec(FieldType typeToUse)
	{
		return new FieldSpec(typeToUse);
	}

	public static FieldSpec createFieldSpec(String labelToUse, FieldType typeToUse)
	{
		return createCustomField("", labelToUse, typeToUse);
	}
	
	public static FieldSpec createSubField(FieldSpec parentToUse, String tagToUse, String labelToUse, FieldType typeToUse)
	{
		return new FieldSpec(parentToUse, tagToUse, labelToUse, typeToUse, false);
	}

	protected FieldSpec(FieldType typeToUse)
	{
		this("", "", typeToUse, false);
	}
	
	private FieldSpec(String tagToUse, String labelToUse, FieldType typeToUse, boolean hasUnknownToUse)
	{
		this(null, tagToUse, labelToUse, typeToUse, hasUnknownToUse);
	}
	
	private FieldSpec(FieldSpec parentToUse, String tagToUse, String labelToUse, FieldType typeToUse, boolean hasUnknownToUse)
	{
		parent = parentToUse;
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
		// NOTE: Optimized for speed because this was a BIG bottleneck!
		String typeString = XmlUtilities.getXmlEncoded(getTypeString(getType()));
		StringBuffer rootTagLine = new StringBuffer();
		rootTagLine.append((("<" + rootTag + " " + FIELD_SPEC_TYPE_ATTR + "='" + typeString + "'>") + "\n"));
		rootTagLine.append(("<" + FIELD_SPEC_TAG_XML_TAG + ">"));
		rootTagLine.append(XmlUtilities.getXmlEncoded(getTag()));
		rootTagLine.append((("</" + FIELD_SPEC_TAG_XML_TAG + ">") + "\n"));
		rootTagLine.append(("<" + FIELD_SPEC_LABEL_XML_TAG + ">")); 
		rootTagLine.append(XmlUtilities.getXmlEncoded(getLabel()));
		rootTagLine.append((("</" + FIELD_SPEC_LABEL_XML_TAG + ">") + "\n"));
		rootTagLine.append(getDetailsXml());
		rootTagLine.append((("</" + rootTag + ">") + "\n"));
		
		return rootTagLine.toString();
	}
	
	protected String getDetailsXml()
	{
		return "";
	}

	public String getTag()
	{
		if(getParent() == null)
			return getSubFieldTag();
		return getParent().getTag() + "." + getSubFieldTag();
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public FieldType getType()
	{
		return type;
	}
	
	public FieldSpec getParent()
	{
		return parent;
	}
	
	public void setParent(FieldSpec newParent)
	{
		parent = newParent;
	}
	
	public String getSubFieldTag()
	{
		return tag;
	}
	
	public String convertStoredToSearchable(String storedData, MiniLocalization localization)
	{
		return getType().convertStoredToSearchable(storedData, localization);
	}
	
	public String convertStoredToHtml(String storedData, MiniLocalization localization)
	{
		return getType().convertStoredToHtml(storedData, localization);
	}
	
	public String convertStoredToExportable(String storedData, MiniLocalization localization)
	{
		return getType().convertStoredToExportable(storedData, localization);
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
	
	public int compareTo(Object other)
	{
		if(other == null)
			return 1;
		
		FieldSpec otherSpec = (FieldSpec)other;
		int weHaveUnknown = (hasUnknownStuff() ? 1 : 0);
		int theyHaveUnknown = (otherSpec.hasUnknownStuff() ? 1 : 0);
		int result = weHaveUnknown - theyHaveUnknown;
		if(result == 0)
			result = getLabel().compareTo(otherSpec.getLabel());
		if(result == 0)
			result = getTag().compareTo(otherSpec.getTag());
		if(result == 0)
			result = getTypeString(getType()).compareTo(getTypeString(otherSpec.getType()));
		if(result == 0)
			result = getDetailsXml().compareTo(otherSpec.getDetailsXml());
		return result;
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof FieldSpec))
			return false;

		return (compareTo(other) == 0);
	}

	public int hashCode()
	{
		return tag.hashCode() ^ label.hashCode();
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
	FieldSpec parent;

	public static final String FIELD_SPEC_XML_TAG = "Field";
	public static final String FIELD_SPEC_TAG_XML_TAG = "Tag";
	public static final String FIELD_SPEC_LABEL_XML_TAG = "Label";
	public static final String FIELD_SPEC_TYPE_ATTR = "type";

	public static final String TRUESTRING = "1";
	public static final String FALSESTRING = "0";

}
