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

import junit.framework.TestCase;

import org.martus.common.CustomFields;
import org.martus.common.LegacyCustomFields;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;


public class TestCustomFields extends TestCase
{
	public TestCustomFields(String name)
	{
		super(name);
	}

	public void testToString() throws Exception
	{
		FieldSpec[] specs = getSampleSpecs();
		CustomFields fields = new CustomFields(specs);
		String xml = "<CustomFields>\n";
		for(int i=0; i < specs.length; ++i)
			xml += specs[i].toString() + "\n";
		xml += "</CustomFields>\n";
		assertEquals(xml, fields.toString());
	}

	public void testParseXml() throws Exception
	{
		FieldSpec[] specs = getSampleSpecs();
		CustomFields fields = new CustomFields(specs);
		String xml = fields.toString();
		
		CustomFields parsed = new CustomFields(CustomFields.parseXml(xml));
		assertEquals(fields.toString(), parsed.toString());
	}
	
	public void testGrid() throws Exception
	{
		String xml = "<CustomFields>" + TestGridFieldSpec.SAMPLE_GRID_FIELD_XML_LEGACY +
				"</CustomFields>";
		CustomFields fields = new CustomFields();
		CustomFields.XmlCustomFieldsLoader loader = new CustomFields.XmlCustomFieldsLoader(fields);
		loader.parse(xml);
		GridFieldSpec spec = (GridFieldSpec)fields.getSpecs()[0];
		assertEquals(FieldSpec.TYPE_GRID, spec.getType());
		assertEquals(2, spec.getColumnCount());
		assertEquals(TestGridFieldSpec.SAMPLE_GRID_HEADER_LABEL_1, spec.getColumnLabel(0));
		assertEquals(TestGridFieldSpec.SAMPLE_GRID_HEADER_LABEL_2, spec.getColumnLabel(1));
		
	}

	private FieldSpec[] getSampleSpecs()
	{
		GridFieldSpec gridSpec = new GridFieldSpec();
		gridSpec.setTag("grid");
		
		return new FieldSpec[] {
			FieldSpec.createStandardField("date", FieldSpec.TYPE_DATE),
			FieldSpec.createStandardField("text", FieldSpec.TYPE_NORMAL),
			FieldSpec.createStandardField("multi", FieldSpec.TYPE_MULTILINE),
			FieldSpec.createStandardField("range", FieldSpec.TYPE_DATERANGE),
			FieldSpec.createStandardField("bool", FieldSpec.TYPE_BOOLEAN),
			FieldSpec.createStandardField("language", FieldSpec.TYPE_LANGUAGE),
			gridSpec,
			LegacyCustomFields.createFromLegacy("custom,Custom <label>"),
		};
	}

}
