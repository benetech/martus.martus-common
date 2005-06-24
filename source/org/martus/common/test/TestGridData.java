/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.GridFieldSpec.UnsupportedFieldTypeException;
import org.martus.util.TestCaseEnhanced;


public class TestGridData extends TestCaseEnhanced
{
	public TestGridData(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{

		gridSpec2Colunns = new GridFieldSpec();
		gridSpec2Colunns.addColumn(FieldSpec.createStandardField("a", FieldSpec.TYPE_NORMAL));
		gridSpec2Colunns.addColumn(FieldSpec.createStandardField("b", FieldSpec.TYPE_NORMAL));
	}
	
	public void testBasics()
	{
		GridData grid = new GridData(gridSpec2Colunns);
		int cols = grid.getColumnCount();
		assertEquals(gridSpec2Colunns.getColumnCount(), cols);

		String mustEncodeXMLData = "<&>";

		GridRow row1 = new GridRow(gridSpec2Colunns);
		String[] row1Data = {"column1a", mustEncodeXMLData};
		fillGridRow(row1, row1Data);
		
		GridRow row2 = new GridRow(gridSpec2Colunns);
		String[] row2Data = {"column1b", "column2b"};
		fillGridRow(row2, row2Data);
		
		assertEquals("row count should start at 0", 0, grid.getRowCount());
		grid.addRow(row1);
		assertEquals("row count should still be at 1", 1, grid.getRowCount());
		grid.addRow(row2);
		assertEquals("row count should now be 2", 2, grid.getRowCount());
		String xmlEncodedString = MartusUtilities.getXmlEncoded(mustEncodeXMLData);

		String expectedXml = 
			"<"+GridData.GRID_DATA_TAG+ " columns='2'>\n" +
			"<"+GridRow.ROW_TAG + ">\n" + 
				"<" + GridRow.COLUMN_TAG + ">" + row1.getCellText(0) + "</" + GridRow.COLUMN_TAG + ">\n" +
				"<" + GridRow.COLUMN_TAG + ">" + xmlEncodedString + "</" + GridRow.COLUMN_TAG + ">\n" +
			"</" + GridRow.ROW_TAG + ">\n" +
			"<" + GridRow.ROW_TAG + ">\n" +
				"<" + GridRow.COLUMN_TAG + ">" + row2.getCellText(0) + "</" + GridRow.COLUMN_TAG + ">\n" +
				"<" + GridRow.COLUMN_TAG + ">" + row2.getCellText(1) + "</" + GridRow.COLUMN_TAG + ">\n" +
			"</" + GridRow.ROW_TAG + ">\n"+
			"</" + GridData.GRID_DATA_TAG+ ">\n";
			
		String xml = grid.getXmlRepresentation();
		assertEquals("xml incorrect?", expectedXml, xml);
		
		assertEquals("Show now have 2 rows", 2 , grid.getRowCount());
		grid.addEmptyRow();
		assertEquals("Show now have an empty row", 3, grid.getRowCount());
		assertEquals("should be empty", "", grid.getValueAt(2,1));
	}
	
	public void testArrayBoundries() throws Exception
	{
		GridData grid = new GridData(gridSpec2Colunns);
		
		String[] row1Data = {"column1a"};
		
		GridFieldSpec gridSpecWithOneColumn = new GridFieldSpec();
		gridSpecWithOneColumn.addColumn(FieldSpec.createStandardField("a", FieldSpec.TYPE_NORMAL));
		GridRow row1 = new GridRow(gridSpecWithOneColumn);
		fillGridRow(row1, row1Data);
		try
		{
			grid.addRow(row1);
			fail("Should have thrown for too few columns");
		}
		catch (ArrayIndexOutOfBoundsException expected)
		{
		}
		//assertEquals("row count should still be at 1", 1, grid.getRowCount());

		String[] row2Data = {"column1b", "column2b", "column3b"};

		GridFieldSpec gridSpecWithThreeColumns = new GridFieldSpec();
		gridSpecWithThreeColumns.addColumn(FieldSpec.createStandardField("a", FieldSpec.TYPE_NORMAL));
		gridSpecWithThreeColumns.addColumn(FieldSpec.createStandardField("b", FieldSpec.TYPE_NORMAL));
		gridSpecWithThreeColumns.addColumn(FieldSpec.createStandardField("c", FieldSpec.TYPE_NORMAL));
		GridRow row2 = new GridRow(gridSpecWithThreeColumns);
		fillGridRow(row2, row2Data);
			
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
		String[] rowValidData = {data1, data2};
		GridRow rowValid = new GridRow(gridSpec2Colunns);
		fillGridRow(rowValid, rowValidData);
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

		String data1a = "column1 data";
		String data2a = "column2 data";
		String[] rowValidaData = {data1a, data2a};
		GridRow rowValida = new GridRow(gridSpec2Colunns);
		fillGridRow(rowValida, rowValidaData);
		grid.addRow(rowValida);
		assertEquals(data1a, grid.getValueAt(1,0));
		assertEquals(data2a, grid.getValueAt(1,1));

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

	private void fillGridRow(GridRow row2, String[] row2Data)
	{
		for(int col = 0; col < row2Data.length; ++col)
			row2.setCellText(col, row2Data[col]);
	}
	
	public void testXmlGridLoader() throws Exception
	{
		GridData original = createSampleGridWithData();
		String xml = original.getXmlRepresentation();
		GridData loaded = new GridData(createSampleGridSpec());
		loaded.addEmptyRow();
		loaded.setFromXml(xml);
		assertEquals("Columns?", original.getColumnCount(), loaded.getColumnCount());
		assertEquals("Rows?", original.getRowCount(), loaded.getRowCount());
		assertEquals(xml, loaded.getXmlRepresentation());
		assertEquals("unescaped xml?", original.getValueAt(0,1), loaded.getValueAt(0,1));
	}
	
	public void testEmptyGridText() throws Exception
	{
		GridData grid = new GridData(gridSpec2Colunns);
		grid.setFromXml("");
		assertEquals("No first row?", 1, grid.getRowCount());
		assertEquals("column 1 not empty?", "", grid.getValueAt(0,0));
		assertEquals("column 2 not empty?", "", grid.getValueAt(0,1));
		assertEquals("Should return an empty string for no data", "", grid.getXmlRepresentation());
	}
	
	public static GridData createSampleGridWithData() throws Exception
	{
		GridData grid = createSampleGrid();
		grid.addEmptyRow();
		grid.setValueAt(SAMPLE_DATA1, 0, 0);
		grid.setValueAt(SAMPLE_DATA2_RAW, 0, 1);
		grid.addEmptyRow();
		grid.setValueAt(SAMPLE_DATA3, 1, 0);
		grid.setValueAt(SAMPLE_DATA4, 1, 1);
		return grid;
	}

	public static GridData createSampleGridWithOneEmptyRow() throws Exception
	{
		GridData grid = createSampleGrid();
		GridRow row1 = GridRow.createEmptyRow(grid.getSpec());
		grid.addRow(row1);
		return grid;
	}

	public static GridData createSampleGrid() throws UnsupportedFieldTypeException
	{
		GridFieldSpec spec = createSampleGridSpec();
		GridData grid = new GridData(spec);
		return grid;
	}

	public static GridFieldSpec createSampleGridSpec() throws UnsupportedFieldTypeException
	{
		GridFieldSpec spec = new GridFieldSpec();
		spec.addColumn(FieldSpec.createCustomField("a", "Column 1", FieldSpec.TYPE_NORMAL));
		spec.addColumn(FieldSpec.createCustomField("b", "Column 2", FieldSpec.TYPE_NORMAL));
		return spec;
	}

	static public final String SAMPLE_DATA1 = "data1";
	static public final String SAMPLE_DATA2_RAW = "<&data2>";
	static public final String SAMPLE_DATA3 = "data3";
	static public final String SAMPLE_DATA4 = "data4";
	
	private GridFieldSpec gridSpec2Colunns;
}
