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
package org.martus.common.test;

import java.util.Arrays;

import org.martus.common.GridRow;
import org.martus.util.TestCaseEnhanced;


public class TestGridRow extends TestCaseEnhanced
{
	public TestGridRow(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		int max_columns = 2;
		GridRow row = new GridRow(max_columns);
		assertEquals ("Should start with 2 columns", 2, row.columns());
		String[] empty = {"", ""};
		assertTrue("Should be empty?", Arrays.equals(empty, row.getRow()));
		
		String item1 = "data1";
		String item2 = "data2";
		String item3 = "data3";
		String item1b = "data1b";
		String item2b = "data2b";
		String[] data = {item1, item2};
		row.setRow(data);
		assertEquals ("Now should have 2 columns", 2, row.columns());
		assertEquals("cell 1 didn't come back with correct data", item1, row.getCellText(0));
		assertEquals("cell 2 didn't come back with correct data", item2, row.getCellText(1));

		String[] datab = {item1b, item2b};
		row.setRow(datab);
		assertEquals ("Should still have 2 columns", 2, row.columns());
		assertEquals("cell 1 didn't come back with correct data", item1b, row.getCellText(0));
		assertEquals("cell 2 didn't come back with correct data", item2b, row.getCellText(1));

		int testCell = 1;
		row.setCellText(testCell, item3);
		assertEquals("cell 1 didn't come back with new data", item3, row.getCellText(testCell));
		
		String[] expectedResult = {item1b, item3};
		assertTrue("Arrays don't match?", Arrays.equals(expectedResult, row.getRow()));
		
		GridRow rowEmpty = GridRow.createEmptyRow(max_columns);
		assertEquals ("Should now have 2 empty columns", 2, rowEmpty.columns());
		assertTrue("Empty Row don't match?", Arrays.equals(empty, rowEmpty.getRow()));

	}

	public void testBoundries()
	{
		int max_columns = 2;
		GridRow row = new GridRow(max_columns);
		String item1 = "data1";
		String item2 = "data2";
		String item3 = "data3";
		String[] data1Item = {item1};
		try
		{
			row.setRow(data1Item);
			fail("Should have thrown only one column.");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			String[] data3Items = {item1, item2, item2};
			row.setRow(data3Items);
			fail("Should have thrown 3 columns.");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		String[] data2Items = {item1, item2};
		row.setRow(data2Items);
		
		assertEquals ("Now should have 2 columns", data2Items.length, row.columns());
		assertEquals("cell 1 didn't come back with correct data", item1, row.getCellText(0));
		assertEquals("cell 2 didn't come back with correct data", item2, row.getCellText(1));
	
	
		try
		{
			row.setCellText(-1,item3);
			fail("should have thrown invalid column -1");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
	
		try
		{
			row.setCellText(2,item3);
			fail("should have thrown invalid column 2");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		
	}
}
