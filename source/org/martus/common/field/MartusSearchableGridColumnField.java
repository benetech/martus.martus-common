/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.common.field;

import org.martus.common.GridData;
import org.martus.common.MiniLocalization;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldType;
import org.martus.common.fieldspec.GridFieldSpec;


public class MartusSearchableGridColumnField extends MartusField
{
	public MartusSearchableGridColumnField(MartusGridField gridToUse, int columnToUse) throws Exception
	{
		super(createMartusField(gridToUse.getGridFieldSpec().getFieldSpec(columnToUse)).getFieldSpec());
		grid = gridToUse;
		column = columnToUse;
		
		GridFieldSpec gridSpec = gridToUse.getGridFieldSpec();
		GridData gridData = new GridData(gridSpec);
		gridData.setFromXml(gridToUse.getData());
	
		dataInEachRow = new MartusField[gridData.getRowCount()];
		for(int row = 0; row < gridData.getRowCount(); ++row)
		{
			dataInEachRow[row] = createMartusField(getFieldSpec());
			String cellData = gridData.getValueAt(row, column);
			dataInEachRow[row].setData(cellData);
		}
		
	}
	
	public int getRowCount()
	{
		return dataInEachRow.length;
	}
	
	public String getData(int row)
	{
		return dataInEachRow[row].getData();
	}
	
	public String getData()
	{
		StringBuffer result = new StringBuffer();
		for(int row = 0; row < getRowCount(); ++row)
		{
			result.append(getData(row));
			result.append("\n");
		}
		return result.toString();
	}
	
	public MartusField createClone() throws Exception
	{
		MartusField clone = new MartusSearchableGridColumnField(grid, column);
		clone.setData(getData());
		return clone;
	}
	


	static FieldSpec[] getFieldSpecsFromGrid(GridFieldSpec gridSpec)
	{
		FieldSpec[] columnSpecs = new FieldSpec[gridSpec.getColumnCount()];
		for(int c = 0; c < columnSpecs.length; ++c)
			columnSpecs[c] = gridSpec.getFieldSpec(c);
		return columnSpecs;
	}
	
	public MartusSearchableGridColumnField(FieldSpec specToUse, MartusField[] rowData, MiniLocalization localization) throws Exception
	{
		super(specToUse);
		dataInEachRow = rowData;
	}

	public boolean doesMatch(int compareOp, String searchForValue, MiniLocalization localization)
	{
		for(int row = 0; row < dataInEachRow.length; ++row)
		{
			if(dataInEachRow[row].doesMatch(compareOp, searchForValue, localization))
				return true;
		}
		
		return false;
	}

	public MartusField getSubField(String tag, MiniLocalization localization)
	{
		try
		{
			MartusField thisField = createMartusField(getFieldSpec());
			MartusField field = thisField.getSubField(tag, localization);
			if(field == null)
				return null;
			
			MartusField[] subFieldDataInEachRow = new MartusField[dataInEachRow.length];
			for(int row = 0; row < dataInEachRow.length; ++row)
			{
				subFieldDataInEachRow[row] = dataInEachRow[row].getSubField(tag, localization);
			}

			return new MartusSearchableGridColumnField(getFieldSpec(), subFieldDataInEachRow, localization);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private static MartusField createMartusField(FieldSpec newSpec)
	{
		FieldType type = newSpec.getType();
		if(type.isDateRange())
			return new MartusDateRangeField(newSpec);
		else if(type.isDate())
			return new MartusDateField(newSpec);
		else if(type.isGrid())
			return new MartusGridField(newSpec);
		else
			return new MartusField(newSpec);
	}

	public String internalGetHtml(MiniLocalization localization) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table>");
		for(int row = 0; row < dataInEachRow.length; ++row)
		{
			buffer.append("<tr><td>");
			buffer.append(dataInEachRow[row].html(localization));
			buffer.append("</td></tr>");
		}
		buffer.append("</table>");
		
		return buffer.toString();
	}

	public int size()
	{
		return dataInEachRow.length;
	}
	

	MartusGridField grid;
	int column;
	MartusField[] dataInEachRow;
}
