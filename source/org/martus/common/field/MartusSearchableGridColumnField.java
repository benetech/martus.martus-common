/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005-2006, Beneficent
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

package org.martus.common.field;

import org.martus.common.FieldCollection;
import org.martus.common.GridData;
import org.martus.common.MiniLocalization;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;


public class MartusSearchableGridColumnField extends MartusField
{
	public MartusSearchableGridColumnField(MartusGridField gridToUse, int columnToUse) throws Exception
	{
		super(gridToUse.getFieldSpec());
		grid = gridToUse;
		column = columnToUse;
		
		GridFieldSpec gridSpec = gridToUse.getGridFieldSpec();
		GridData gridData = new GridData(gridSpec);
		gridData.setFromXml(gridToUse.getData());
	
		FieldSpec[] columnSpecs = getFieldSpecsFromGrid(gridSpec);
		
		fields = new FieldCollection(columnSpecs);
		for(int row = 0; row < gridData.getRowCount(); ++row)
		{
			fields.getField(row).setData(gridData.getValueAt(row, column));
		}
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
	
	public MartusSearchableGridColumnField(MartusSearchableGridColumnField source, String tag, MiniLocalization localization) throws Exception
	{
		super(source.getFieldSpec());

		// TODO: There is clearly duplication here, but I'm in too much of a hurry
		// to try to figure out how to eliminate it. ARGH! kbs 2006-04-26
		
		FieldSpec[] columnSpecs = new FieldSpec[source.size()];
		for(int c = 0; c < columnSpecs.length; ++c)
		{
			MartusField thisField = source.fields.getField(c);
			MartusField thisSubfield = thisField.getSubField(tag, localization);
			columnSpecs[c] = thisSubfield.getFieldSpec();
		}

		fields = new FieldCollection(columnSpecs);
		for(int row = 0; row < source.size(); ++row)
		{
			MartusField thisField = source.fields.getField(row);
			MartusField thisSubfield = thisField.getSubField(tag, localization);
			fields.getField(row).setData(thisSubfield.getData());
		}
	
	}

	public boolean doesMatch(int compareOp, String searchForValue, MiniLocalization localization)
	{
		for(int row = 0; row < fields.count(); ++row)
		{
			if(fields.getField(row).doesMatch(compareOp, searchForValue, localization))
				return true;
		}
		
		return false;
	}

	public MartusField getSubField(String tag, MiniLocalization localization)
	{
		try
		{
			return new MartusSearchableGridColumnField(this, tag, localization);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public int size()
	{
		return fields.count();
	}

	MartusGridField grid;
	int column;
	FieldCollection fields;
}
