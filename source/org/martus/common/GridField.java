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


public class GridField
{
	public GridField()
	{
		rows = new Vector();
	}
	
	public void addRow(String[] rowToAdd)
	{
		rows.add(rowToAdd);
	}
	
	public String getXmlRepresentation()
	{
		String result = new String();
		for(int i = 0; i< rows.size(); ++i)
		{
			String[] contents = (String[])rows.get(i);
			result += ROW_START_TAG;
			for(int j= 0; j < contents.length; ++j)
			{
				result += COL_START_TAG + contents[j] + COL_END_TAG;
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
}
