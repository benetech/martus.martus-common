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
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;


public class GridRow
{
	public GridRow(int maxColumns)
	{
		row = new Vector(maxColumns);
		this.maxColumns = maxColumns;
		String[] data = new String[maxColumns];
		for(int i = 0; i < maxColumns ; ++i)
			data[i] = "";
		setRow(data);
	}
	
	public int columns()
	{
		return row.size();
	}
	
	static public GridRow createEmptyRow(int columns)
	{
		return new GridRow(columns);
	}

	public void setRow(String[] data) throws ArrayIndexOutOfBoundsException   
	{
		if(data.length != maxColumns)
			throw new ArrayIndexOutOfBoundsException("columns incorrect");
		row.clear();
		for(int i = 0; i < data.length; ++i)
			row.add(data[i]);
	}
	
	public String[] getRow() throws ArrayIndexOutOfBoundsException
	{
		String[] data = new String[row.size()];
		for(int i = 0; i < row.size(); ++i)
		{
			data[i] = (String)row.get(i);
		}
		return data;
	}

	public void setCellText(int column, String data) throws ArrayIndexOutOfBoundsException
	{
		row.set(column, data);
	}
	
	public String getCellText(int column) throws ArrayIndexOutOfBoundsException
	{
		return (String)row.get(column);
	}
	
	public static class XmlGridRowLoader extends SimpleXmlDefaultLoader
	{
		public XmlGridRowLoader(int maxColumns)
		{
			super(GridData.ROW_TAG);
			thisRow = new GridRow(maxColumns);
		}
		
		public GridRow getGridRow()
		{
			return thisRow;
		}

		public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
		{
			if(tag.equals(GridData.COLUMN_TAG))
				return new SimpleXmlStringLoader(tag);
			return super.startElement(tag);
		}
		
		public void endElement(SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			String tag = ended.getTag();
			if(tag.equals(GridData.COLUMN_TAG))
			{
				String cellText = ((SimpleXmlStringLoader)ended).getText();
				thisRow.setCellText(currentColumn++, cellText);
			}
			super.endElement(ended);
		}
		GridRow thisRow;
		int currentColumn;

	}
	
	int maxColumns;
	Vector row; 
}
