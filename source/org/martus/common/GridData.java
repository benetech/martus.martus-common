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


public class GridData
{
	public GridData(int columns)
	{
		rows = new Vector();
		maxColumns = columns;
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
		rowData.setCell(data, col);
	}

	public String getValueAt(int row, int col) throws ArrayIndexOutOfBoundsException
	{
		GridRow rowData = getRow(row);
		return rowData.getCell(col);
	}
	
	
	public void addRow(GridRow rowToAdd) throws ArrayIndexOutOfBoundsException
	{
		if(rowToAdd.columns() != getColumnCount())
			throw new ArrayIndexOutOfBoundsException("Column out of bounds");
		rows.add(rowToAdd);
	}
	
	public String getXmlRepresentation()
	{
		String result = new String();
		for(int i = 0; i< rows.size(); ++i)
		{
			GridRow contents = (GridRow)rows.get(i);
			result += ROW_START_TAG;
			for(int j= 0; j < contents.columns(); ++j)
			{
				result += COL_START_TAG + MartusUtilities.getXmlEncoded(contents.getCell(j)) + COL_END_TAG;
			}
			result += ROW_END_TAG;
		}
		return result;
	}

	public static final String ROW_START_TAG = "<Row>";
	public static final String ROW_END_TAG = "</Row>";
	public static final String COL_START_TAG = "<Col>";
	public static final String COL_END_TAG = "</Col>";

	Vector rows;
	int maxColumns;
}
