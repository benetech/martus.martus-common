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

import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class GridData
{
	public GridData(int columns)
	{
		this();
		maxColumns = columns;
	}

	public GridData()
	{
		rows = new Vector();
	}
	
	public class ColumnsAlreadySetException extends Exception{}
	
	public void setMaxColumns(int maxColumns) throws ColumnsAlreadySetException
	{
		if(this.maxColumns != 0)
			throw new ColumnsAlreadySetException();
		this.maxColumns = maxColumns;
	}
	
	
	public void addEmptyRow()
	{
		GridRow row = GridRow.createEmptyRow(getColumnCount());
		addRow(row);
	}
	
	public int getRowCount()
	{
		return rows.size();
	}
	
	public int getColumnCount()
	{
		return maxColumns;
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
		GridData.XmlGridDataLoader loader = new GridData.XmlGridDataLoader(this);
		SimpleXmlParser.parse(loader, xmlData);
	}
	
	public String getXmlRepresentation()
	{
		String result = new String();
		result += MartusXml.getTagStart(GRID_DATA_TAG, GRID_ATTRIBUTE_COLUMNS, Integer.toString(getColumnCount()))+ MartusXml.newLine;
		for(int i = 0; i< rows.size(); ++i)
		{
			GridRow contents = (GridRow)rows.get(i);
			result += MartusXml.getTagStart(ROW_TAG) + MartusXml.newLine ;
			for(int j= 0; j < contents.columns(); ++j)
			{
				result += MartusXml.getTagStart(COLUMN_TAG) + MartusUtilities.getXmlEncoded(contents.getCellText(j)) + MartusXml.getTagEnd(COLUMN_TAG);
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
			try
			{
				grid.setMaxColumns(Integer.parseInt(cols));
				super.startDocument(attrs);
			}
			catch (ColumnsAlreadySetException e)
			{
				e.printStackTrace();
			}
		}

		public SimpleXmlDefaultLoader startElement(String tag)
				throws SAXParseException
		{
			if(tag.equals(GridData.ROW_TAG))
				return new GridRow.XmlGridRowLoader(grid.getColumnCount());
			return super.startElement(tag);
		}
		
		public void endElement(SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			String tag = ended.getTag();
			if(tag.equals(GridData.ROW_TAG))
				grid.addRow(((GridRow.XmlGridRowLoader)ended).getGridRow()); 
			super.endElement(ended);
		}
		GridData grid;
	}

	public static final String GRID_DATA_TAG = "GridData";
	public static final String GRID_ATTRIBUTE_COLUMNS = "columns";
	public static final String ROW_TAG = "Row";
	public static final String COLUMN_TAG = "Column";

	Vector rows;
	int maxColumns;
}
