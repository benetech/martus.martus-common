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

import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.util.TestCaseEnhanced;


public class TestGridFieldSpec extends TestCaseEnhanced
{

	public TestGridFieldSpec(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		FieldSpec.XmlFieldSpecLoader loader = new FieldSpec.XmlFieldSpecLoader();
		loader.parse(SAMPLE_GRID_FIELD_XML);
		GridFieldSpec spec = (GridFieldSpec)loader.getFieldSpec();
		assertEquals(2, spec.getColumnCount());
		assertContains(SAMPLE_GRID_HEADER_LABEL_1, spec.getAllColumnLabels());
		assertContains(SAMPLE_GRID_HEADER_LABEL_2, spec.getAllColumnLabels());
		assertEquals(SAMPLE_GRID_FIELD_XML, spec.toString());
	}

	public static final String SAMPLE_GRID_HEADER_LABEL_1 = "column1";
	public static final String SAMPLE_GRID_HEADER_LABEL_2 = "column2";
	public static final String SAMPLE_GRID_FIELD_XML = "<Field type='GRID'>\n" +
			"<Tag>custom</Tag>\n" +
			"<Label>me</Label>\n" +
			"<GridSpecDetails>\n<Column><Label>" +
			SAMPLE_GRID_HEADER_LABEL_1 +
			"</Label></Column>\n<Column><Label>" +
			SAMPLE_GRID_HEADER_LABEL_2 +
			"</Label></Column>\n</GridSpecDetails>\n</Field>\n";
}
