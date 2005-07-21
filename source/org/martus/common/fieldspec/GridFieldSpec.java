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

import java.util.Iterator;
import java.util.Vector;

import org.martus.common.MartusXml;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.SAXParseException;


public class GridFieldSpec extends FieldSpec
{
	public GridFieldSpec()
	{
		super(TYPE_GRID);
		columns = new Vector();
		columnZeroLabel = " ";
	}
	
	public int getColumnCount()
	{
		return columns.size();
	}
	
	public String getColumnLabel(int column)
	{
		FieldSpec columnSpec = (FieldSpec)columns.get(column);
		return columnSpec.getLabel();
	}
	
	public int getColumnType(int column)
	{
		FieldSpec columnSpec = (FieldSpec)columns.get(column);
		return columnSpec.getType();
	}
	
	public FieldSpec getFieldSpec(int column)
	{
		return (FieldSpec)columns.get(column);
	}

	public class UnsupportedFieldTypeException extends Exception
	{
	}

	public void addColumn(FieldSpec columnSpec) throws UnsupportedFieldTypeException
	{
		if(!isValidGridColumnType(columnSpec.getType()))
			throw new UnsupportedFieldTypeException();
		columns.add(columnSpec);
	}

	public static boolean isValidGridColumnType(int columnType)
	{
		boolean[] typeIsValid = new boolean[INSERT_NEXT_TYPE_HERE_AND_INCREASE_THIS_BY_ONE];
		typeIsValid[TYPE_NORMAL] = true;
		typeIsValid[TYPE_DROPDOWN] = true;
		typeIsValid[TYPE_BOOLEAN] = true;
		typeIsValid[TYPE_DATE] = true;
		typeIsValid[TYPE_DATERANGE] = true;
		
		if(columnType == TYPE_MORPHIC)
			return true;
		if(columnType == TYPE_UNKNOWN)
			return false;
		return typeIsValid[columnType];
	}
	
	public void setColumnZeroLabel(String columnZeroLabelToUse)
	{
		columnZeroLabel = columnZeroLabelToUse;
	}
	
	public String getColumnZeroLabel()
	{
		return columnZeroLabel;
	}
	
	public Vector getAllColumnLabels()
	{
		Vector columnLabels = new Vector();
		for(Iterator iter = columns.iterator(); iter.hasNext();)
		{
			FieldSpec element = (FieldSpec) iter.next();
			columnLabels.add(element.getLabel());
		}
		return columnLabels;
	}
	
	public String getDefaultValue()
	{
		return "";
	}

	public String getDetailsXml()
	{
		String xml = MartusXml.getTagStartWithNewline(GRID_SPEC_DETAILS_TAG);
		for(int i = 0 ; i < getColumnCount(); ++i)
		{
			FieldSpec thisColumn = (FieldSpec)columns.get(i);
			xml += thisColumn.toXml(GRID_COLUMN_TAG);
		}
		xml += MartusXml.getTagEnd(GRID_SPEC_DETAILS_TAG);
		
		return xml;
	}
	
	static class GridSpecDetailsLoader extends SimpleXmlDefaultLoader
	{
		public GridSpecDetailsLoader(GridFieldSpec spec)
		{
			super(GRID_SPEC_DETAILS_TAG);
			this.spec = spec;
		}
		
		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals(GRID_COLUMN_TAG))
				return new FieldSpec.XmlFieldSpecLoader(tag);

			return super.startElement(tag);
		}

		public void endElement(String thisTag, SimpleXmlDefaultLoader ended)
			throws SAXParseException
		{
			FieldSpec specToAdd = null;
			if(thisTag.equals(GRID_COLUMN_TAG))//Legacy XML
			{
				specToAdd = ((FieldSpec.XmlFieldSpecLoader)ended).getFieldSpec();
				if(specToAdd.getType() == FieldSpec.TYPE_UNKNOWN)
					specToAdd.setType(FieldSpec.TYPE_NORMAL);
			}
			else if (thisTag.equals(FIELD_SPEC_XML_TAG))
			{
				specToAdd = ((FieldSpec.XmlFieldSpecLoader)ended).getFieldSpec();
			}
			else
			{
				super.endElement(thisTag, ended);
				return;
			}
			try
			{
				spec.addColumn(specToAdd);
			}
			catch(UnsupportedFieldTypeException e)
			{
				e.printStackTrace();
				throw new SAXParseException("UnsupportedFieldTypeException", null);
			}
		}		
		GridFieldSpec spec;
	}
	
	public final static String GRID_SPEC_DETAILS_TAG = "GridSpecDetails";
	public final static String GRID_COLUMN_TAG = "Column";
	public final static String GRID_COLUMN_LABEL_TAG = "Label";
	
	Vector columns;
	String columnZeroLabel;
}
