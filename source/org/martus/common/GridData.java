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

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class GridData
{
	public GridData(GridFieldSpec spec)
	{
		gridSpec = spec;
		rows = new Vector();
	}

	public void addEmptyRow()
	{
		GridRow row = GridRow.createEmptyRow(gridSpec);
		addRow(row);
	}
	
	public int getRowCount()
	{
		return rows.size();
	}
	
	public int getColumnCount()
	{
		return gridSpec.getColumnCount();
	}
	
	public GridFieldSpec getSpec()
	{
		return gridSpec;
	}
	
	private GridRow getRow(int row)
	{
		return (GridRow)rows.get(row);
	}
	
	public void setValueAt(String data, int row, int col) throws ArrayIndexOutOfBoundsException
	{
		GridRow rowData = getRow(row);
		rowData.setCellText(col, data);
	}

	public String getValueAt(int row, int col) throws ArrayIndexOutOfBoundsException
	{
		GridRow rowData = getRow(row);
		return rowData.getCellText(col);
	}
	
	
	public void addRow(GridRow rowToAdd) throws ArrayIndexOutOfBoundsException
	{
		if(rowToAdd.columns() != getColumnCount())
			throw new ArrayIndexOutOfBoundsException("Column out of bounds");
		rows.add(rowToAdd);
	}
	
	public void setFromXml(String xmlData) throws IOException, ParserConfigurationException, SAXException
	{
		rows.clear();
		if(xmlData.equals(""))
		{
			addEmptyRow();
			return;
		}
		GridData.XmlGridDataLoader loader = new GridData.XmlGridDataLoader(this);
		SimpleXmlParser.parse(loader, xmlData);
	}
	
	
	public boolean isEmpty()
	{
		for(int i = 0; i < rows.size(); ++i)
		{
			GridRow contents = (GridRow)rows.get(i);
			for(int j = 0; j < getColumnCount(); ++ j)
			{
				if(contents.getCellText(j).length() != 0 )
					return false;
			}
		}
		return true;
	}
	
	public String getXmlRepresentation()
	{
		if(isEmpty())
			return "";
		String result = new String();
		result += MartusXml.getTagStart(GRID_DATA_TAG, GRID_ATTRIBUTE_COLUMNS, Integer.toString(getColumnCount()))+ MartusXml.newLine;
		for(int i = 0; i< rows.size(); ++i)
		{
			GridRow contents = (GridRow)rows.get(i);
			result += MartusXml.getTagStart(ROW_TAG) + MartusXml.newLine ;
			int columns = contents.columns();
			for(int j= 0; j < columns; ++j)
			{
				String rawCellText = contents.getCellText(j);
				result += MartusXml.getTagStart(COLUMN_TAG) + MartusUtilities.getXmlEncoded(rawCellText) + MartusXml.getTagEnd(COLUMN_TAG);
			}
			result += MartusXml.getTagEnd(ROW_TAG);
		}
		result += MartusXml.getTagEnd(GRID_DATA_TAG);
		return result;
	}
	
	public static class XmlGridDataLoader extends SimpleXmlDefaultLoader
	{
		public XmlGridDataLoader(GridData gridToLoad)
		{
			super(GRID_DATA_TAG);
			grid = gridToLoad;
		}

		public GridData getGridData()
		{
			return grid;
		}
		
		public void startDocument(Attributes attrs) throws SAXParseException
		{
			String cols = attrs.getValue(GridData.GRID_ATTRIBUTE_COLUMNS);
			int gotCols = Integer.parseInt(cols);
			int expectedCols = grid.getColumnCount();
			if(gotCols != expectedCols)
				System.out.println("XmlGridDataLoader.startDocument: wrong column count! expected " + expectedCols + " but was " + gotCols);
			super.startDocument(attrs);
		}

		public SimpleXmlDefaultLoader startElement(String tag)
				throws SAXParseException
		{
			if(tag.equals(GridData.ROW_TAG))
				return new GridRow.XmlGridRowLoader(grid.getSpec());
			return super.startElement(tag);
		}
		
		public void endElement(String tag, SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			if(tag.equals(GridData.ROW_TAG))
				grid.addRow(((GridRow.XmlGridRowLoader)ended).getGridRow()); 
			super.endElement(tag, ended);
		}
		GridData grid;
	}

	public static final String GRID_DATA_TAG = "GridData";
	public static final String GRID_ATTRIBUTE_COLUMNS = "columns";
	public static final String ROW_TAG = "Row";
	public static final String COLUMN_TAG = "Column";

	private Vector rows;
	private GridFieldSpec gridSpec;
}
