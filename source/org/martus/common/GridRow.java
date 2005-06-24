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

import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;


public class GridRow
{
	public GridRow(GridFieldSpec gridSpec)
	{
		data = new String[gridSpec.getColumnCount()];
		for(int i = 0; i < getColumnCount(); ++i)
			data[i] = "";
		setRow(data);
	}
	
	public int getColumnCount()
	{
		return data.length;
	}
	
	static public GridRow createEmptyRow(GridFieldSpec gridSpec)
	{
		return new GridRow(gridSpec);
	}

	public void setRow(String[] newData) throws ArrayIndexOutOfBoundsException   
	{
		if(newData.length != getColumnCount())
			throw new ArrayIndexOutOfBoundsException("columns incorrect expected " + getColumnCount() + " but was " + newData.length);
		data = newData;
	}
	
	public String[] getRow() throws ArrayIndexOutOfBoundsException
	{
		return data;
	}

	public void setCellText(int column, String value) throws ArrayIndexOutOfBoundsException
	{
		data[column] = value;
	}
	
	public String getCellText(int column) throws ArrayIndexOutOfBoundsException
	{
		return data[column];
	}
	
	public static class XmlGridRowLoader extends SimpleXmlDefaultLoader
	{
		public XmlGridRowLoader(GridFieldSpec gridSpec)
		{
			super(GridData.ROW_TAG);
			thisRow = new GridRow(gridSpec);
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
		
		public void endElement(String tag, SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			if(tag.equals(GridData.COLUMN_TAG))
			{
				String cellText = ((SimpleXmlStringLoader)ended).getText();
				thisRow.setCellText(currentColumn++, cellText);
			}
			super.endElement(tag, ended);
		}
		GridRow thisRow;
		int currentColumn;

	}
	
	String[] data;
}
