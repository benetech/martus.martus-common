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

import org.martus.common.field.MartusDateField;
import org.martus.common.field.MartusDateRangeField;
import org.martus.common.field.MartusField;
import org.martus.common.field.MartusGridField;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldType;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlParser;
import org.xml.sax.SAXParseException;


public class FieldCollection
{
	public FieldCollection(FieldSpecCollection specsToUse)
	{
		specsToUse = reuseExistingSpecCollectionIfPossible(specsToUse);
		fields = new Vector();
		for(int i=0; i < specsToUse.size(); ++i)
			add(specsToUse.get(i));
	}

	private FieldSpecCollection reuseExistingSpecCollectionIfPossible(FieldSpecCollection specsToUse)
	{
		if(existingFieldSpecTemplates.contains(specsToUse))
		{
			int at = existingFieldSpecTemplates.indexOf(specsToUse);
			specsToUse = (FieldSpecCollection)existingFieldSpecTemplates.get(at);
		}
		else
		{
			existingFieldSpecTemplates.add(specsToUse);
		}
		return specsToUse;
	}
	
	public FieldCollection(FieldSpec[] specsToUse)
	{
		this(new FieldSpecCollection(specsToUse));
	}
	
	public void add(FieldSpec newSpec)
	{
		FieldType type = newSpec.getType();
		if(type.isDateRange())
			fields.add(new MartusDateRangeField(newSpec));
		else if(type.isDate())
			fields.add(new MartusDateField(newSpec));
		else if(type.isGrid())
			fields.add(new MartusGridField(newSpec));
		else
			fields.add(new MartusField(newSpec));
	}
	
	public int count()
	{
		return fields.size();
	}
	
	public MartusField getField(int i)
	{
		return ((MartusField)fields.get(i));
	}
	
	public MartusField findByTag(String fieldTag)
	{
		for(int i=0; i < count(); ++i)
		{
			MartusField thisField = getField(i);
			if(thisField.getTag().equals(fieldTag))
				return thisField;
		}
		
		return null;
	}
	
	public FieldSpec[] getSpecs()
	{
		FieldSpec[] specs = new FieldSpec[count()];
		for(int i=0; i < specs.length; ++i)
			specs[i] = getField(i).getFieldSpec();
		return specs;
	}
	
	public boolean isEmpty()
	{
		for(int i=0; i < count(); ++i)
			if(getField(i).getData().length() != 0)
				return false;
		
		return true;
	}
	
	public void clearAllData()
	{
		for(int i=0; i < count(); ++i)
			getField(i).clearData();
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append('<');
		result.append(MartusXml.CustomFieldSpecsElementName);
		result.append(">\n\n");
		
		for (int i = 0; i < fields.size(); i++)
		{
			FieldSpec spec = ((MartusField)fields.get(i)).getFieldSpec();
			result.append(spec.toString());
			result.append('\n');
		}
		result.append("</");
		result.append(MartusXml.CustomFieldSpecsElementName);
		result.append(">\n");
		return result.toString();
	}
	
	public static class CustomFieldsParseException extends Exception 
	{
	}
	
	public static FieldSpec[] parseXml(String xml) throws CustomFieldsParseException
	{
		XmlCustomFieldsLoader loader = new XmlCustomFieldsLoader();
		try
		{
			SimpleXmlParser.parse(loader, xml);
			return loader.getFieldSpecs();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new CustomFieldsParseException();
		}
	}
	
	public static class XmlCustomFieldsLoader extends SimpleXmlDefaultLoader
	{
		public XmlCustomFieldsLoader()
		{
			this(MartusXml.CustomFieldSpecsElementName);
		}

		public XmlCustomFieldsLoader(String tag)
		{
			super(tag);
			fields = new Vector();
		}
		
		public FieldSpec[] getFieldSpecs()
		{
			return (FieldSpec[])fields.toArray(new FieldSpec[0]);
		}

		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals(FieldSpec.FIELD_SPEC_XML_TAG))
				return new FieldSpec.XmlFieldSpecLoader(tag);
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

		private Vector fields;
	}
	
	static Vector existingFieldSpecTemplates = new Vector();
	
	Vector fields;
}
