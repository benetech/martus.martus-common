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

import org.martus.common.GridData;
import org.martus.common.GridRow;
import org.martus.common.MartusUtilities;
import org.martus.util.TestCaseEnhanced;


public class TestGridData extends TestCaseEnhanced
{
	public TestGridData(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		final int MAX_COLS = 3;
		
		GridData grid = new GridData(MAX_COLS);
		assertEquals(MAX_COLS, grid.getColumnCount());
		GridRow row1 = new GridRow(MAX_COLS);
		String mustEncodeXMLData = "<&>";
		String[] row1Data = {"column1a", mustEncodeXMLData, "column3a"};
		row1.setRow(row1Data);
		
		GridRow row2 = new GridRow(MAX_COLS);
		String[] row2Data = {"column1b", "column2b", "column3b"};
		row2.setRow(row2Data);
		
		assertEquals("row count should start at 0", 0, grid.getRowCount());
		grid.addRow(row1);
		assertEquals("row count should still be at 1", 1, grid.getRowCount());
		grid.addRow(row2);
		assertEquals("row count should now be 2", 2, grid.getRowCount());
		String xmlEncodedString = MartusUtilities.getXmlEncoded(mustEncodeXMLData);

		String expectedXml = 
			"<"+GridData.GRID_DATA_TAG+ " columns='3'>\n" +
			"<"+GridData.ROW_TAG + ">\n" + 
				"<" + GridData.COLUMN_TAG + ">" + row1.getCellText(0) + "</" + GridData.COLUMN_TAG + ">\n" +
				"<" + GridData.COLUMN_TAG + ">" + xmlEncodedString + "</" + GridData.COLUMN_TAG + ">\n" +
				"<" + GridData.COLUMN_TAG + ">" + row1.getCellText(2) + "</" + GridData.COLUMN_TAG + ">\n" +
			"</" + GridData.ROW_TAG + ">\n" +
			"<" + GridData.ROW_TAG + ">\n" +
				"<" + GridData.COLUMN_TAG + ">" + row2.getCellText(0) + "</" + GridData.COLUMN_TAG + ">\n" +
				"<" + GridData.COLUMN_TAG + ">" + row2.getCellText(1) + "</" + GridData.COLUMN_TAG + ">\n" +
				"<" + GridData.COLUMN_TAG + ">" + row2.getCellText(2) + "</" + GridData.COLUMN_TAG + ">\n" +
			"</" + GridData.ROW_TAG + ">\n"+
			"</" + GridData.GRID_DATA_TAG+ ">\n";
			
		String xml = grid.getXmlRepresentation();
		assertEquals("xml incorrect?", expectedXml, xml);
		
		assertEquals("Show now have 2 rows", 2 , grid.getRowCount());
		grid.addEmptyRow();
		assertEquals("Show now have an empty row", 3, grid.getRowCount());
		assertEquals("should be empty", "", grid.getValueAt(2,2));
	}
	
	public void testArrayBoundries()
	{
		final int MAX_COLS = 3;
		
		GridData grid = new GridData(MAX_COLS);
		String[] row1Data = {"column1a", "column2a"};
		GridRow row1 = new GridRow(2);
		row1.setRow(row1Data);
		try
		{
			grid.addRow(row1);
			fail("Should have thrown only 2 columns");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
		//assertEquals("row count should still be at 1", 1, grid.getRowCount());

		String[] row2Data = {"column1b", "column2b", "column3b", "column4b"};
		GridRow row2 = new GridRow(4);
		row2.setRow(row2Data);
		try
		{
			grid.addRow(row2);
			fail("Should have thrown 4 columns");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			grid.getValueAt(-1,1);
			fail("get at Row at -1 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			grid.getValueAt(2,1);
			fail("get at Row at 2 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
		String data1 = "column1 data";
		String data2 = "column2 data";
		String data3 = "column3 data";
		String[] rowValidData = {data1, data2, data3};
		GridRow rowValid = new GridRow(3);
		rowValid.setRow(rowValidData);
		grid.addRow(rowValid);

		try
		{
			grid.getValueAt(0,-1);
			fail("get at Column at -1 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			grid.getValueAt(0,3);
			fail("get at Column at 3 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
	
		assertEquals(data1, grid.getValueAt(0,0));
		assertEquals(data2, grid.getValueAt(0,1));
		assertEquals(data3, grid.getValueAt(0,2));

		String data1a = "column1 data";
		String data2a = "column2 data";
		String data3a = "column3 data";
		String[] rowValidaData = {data1a, data2a, data3a};
		GridRow rowValida = new GridRow(3);
		rowValida.setRow(rowValidaData);
		grid.addRow(rowValida);
		assertEquals(data1a, grid.getValueAt(1,0));
		assertEquals(data2a, grid.getValueAt(1,1));
		assertEquals(data3a, grid.getValueAt(1,2));

		String modifiedData = "new Text";
		try
		{
			grid.setValueAt(modifiedData,-1,1);
			fail("set at Row at -1 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			grid.setValueAt(modifiedData,2,1);
			fail("set at Row at 2 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
		try
		{
			grid.setValueAt(modifiedData,0,-1);
			fail("set at Column at -1 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}

		try
		{
			grid.setValueAt(modifiedData,0,3);
			fail("set at Column at 3 worked?");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
		
		assertEquals(data2, grid.getValueAt(0,1));
		grid.setValueAt(modifiedData,0,1);
		assertEquals(modifiedData, grid.getValueAt(0,1));
	}
	
	public void testXmlGridLoader() throws Exception
	{
		GridData original = createSampleGrid();
		String xml = original.getXmlRepresentation();
		GridData loaded = new GridData(0);
		loaded.addEmptyRow();
		loaded.setFromXml(xml);
		assertEquals("Columns?", original.getColumnCount(), loaded.getColumnCount());
		assertEquals("Rows?", original.getRowCount(), loaded.getRowCount());
		assertEquals(xml, loaded.getXmlRepresentation());
		assertEquals("unescaped xml?", original.getValueAt(0,1), loaded.getValueAt(0,1));
	}
	
	public void testEmptyGridText() throws Exception
	{
		GridData grid = new GridData(4);
		grid.setFromXml("");
		assertEquals("No first row?", 1, grid.getRowCount());
		assertEquals("column 1 not empty?", "", grid.getValueAt(0,0));
		assertEquals("column 2 not empty?", "", grid.getValueAt(0,1));
		assertEquals("column 3 not empty?", "", grid.getValueAt(0,2));
		assertEquals("column 4 not empty?", "", grid.getValueAt(0,3));
		assertEquals("Should return an empty string for no data", "", grid.getXmlRepresentation());
	}
	
	public void testResetColumns() throws Exception
	{
		GridData grid = createSampleGrid();
		try
		{
			grid.setMaxColumns(1);
			fail("Didn't throw after we reset the # of columns?");
		}
		catch (GridData.AlreadyInitalizedException expectedException)
		{
		}
		grid.setMaxColumns(2);
		
	}

	public static GridData createSampleGrid()
	{
		GridData grid = new GridData(2);
		GridRow row1 = GridRow.createEmptyRow(2);
		row1.setCellText(0, SAMPLE_DATA1);
		row1.setCellText(1, SAMPLE_DATA2_RAW);
		grid.addRow(row1);
		GridRow row2 = GridRow.createEmptyRow(2);
		row2.setCellText(0, SAMPLE_DATA3);
		row2.setCellText(1, SAMPLE_DATA4);
		grid.addRow(row2);
		return grid;
	}

	static public final String SAMPLE_DATA1 = "data1";
	static public final String SAMPLE_DATA2_RAW = "<&data2>";
	static public final String SAMPLE_DATA3 = "data3";
	static public final String SAMPLE_DATA4 = "data4";
	
}
