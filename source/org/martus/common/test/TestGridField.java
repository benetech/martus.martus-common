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

import org.martus.common.GridField;
import org.martus.util.TestCaseEnhanced;


public class TestGridField extends TestCaseEnhanced
{
	public TestGridField(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		GridField grid = new GridField();
		String[] row1 = {"column1a", "column2a", "column3a"};
		String[] row2 = {"column1b", "column2b", "column3b"};
		grid.addRow(row1);
		grid.addRow(row2);
		String expectedXml = 
			GridField.ROW_START_TAG + 
				GridField.COL_START_TAG + row1[0] + GridField.COL_END_TAG +
				GridField.COL_START_TAG + row1[1] + GridField.COL_END_TAG +
				GridField.COL_START_TAG + row1[2] + GridField.COL_END_TAG +
			GridField.ROW_END_TAG +
			GridField.ROW_START_TAG +
				GridField.COL_START_TAG + row2[0] + GridField.COL_END_TAG +
				GridField.COL_START_TAG + row2[1] + GridField.COL_END_TAG +
				GridField.COL_START_TAG + row2[2] + GridField.COL_END_TAG +
			GridField.ROW_END_TAG;
			
				
		String xml = grid.getXmlRepresentation();
		assertEquals("xml incorrect?", expectedXml, xml);
		
	}

}
