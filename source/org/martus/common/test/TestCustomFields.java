/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
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

package org.martus.common.test;

import junit.framework.TestCase;

import org.martus.common.FieldCollection;
import org.martus.common.LegacyCustomFields;
import org.martus.common.ReusableChoices;
import org.martus.common.PoolOfReusableChoicesLists;
import org.martus.common.XmlCustomFieldsLoader;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeBoolean;
import org.martus.common.fieldspec.FieldTypeDate;
import org.martus.common.fieldspec.FieldTypeDateRange;
import org.martus.common.fieldspec.FieldTypeGrid;
import org.martus.common.fieldspec.FieldTypeLanguage;
import org.martus.common.fieldspec.FieldTypeMultiline;
import org.martus.common.fieldspec.FieldTypeNestedDropdown;
import org.martus.common.fieldspec.FieldTypeNormal;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.NestedDropDownFieldSpec;
import org.martus.common.fieldspec.NestedDropdownLevel;


public class TestCustomFields extends TestCase
{
	public TestCustomFields(String name)
	{
		super(name);
	}

	public void testToString() throws Exception
	{
		FieldSpec[] specs = getSampleSpecs();
		FieldCollection fields = new FieldCollection(specs);
		String xml = "<CustomFields>\n\n";
		for(int i=0; i < specs.length; ++i)
			xml += specs[i].toString() + "\n";
		xml += "</CustomFields>\n";
		assertEquals(xml, fields.toString());
	}

	public void testParseXml() throws Exception
	{
		FieldSpec[] specs = getSampleSpecs();
		FieldCollection fields = new FieldCollection(specs);
		String xml = fields.toString();
		
		FieldCollection parsed = new FieldCollection(FieldCollection.parseXml(xml));
		assertEquals(fields.toString(), parsed.toString());
	}
	
	public void testGrid() throws Exception
	{
		String xml = "<CustomFields>" + TestGridFieldSpec.SAMPLE_GRID_FIELD_XML_LEGACY +
				"</CustomFields>";
		XmlCustomFieldsLoader loader = new XmlCustomFieldsLoader();
		loader.parse(xml);
		GridFieldSpec spec = (GridFieldSpec)loader.getFieldSpecs()[0];
		assertEquals(new FieldTypeGrid(), spec.getType());
		assertEquals(2, spec.getColumnCount());
		assertEquals(TestGridFieldSpec.SAMPLE_GRID_HEADER_LABEL_1, spec.getColumnLabel(0));
		assertEquals(TestGridFieldSpec.SAMPLE_GRID_HEADER_LABEL_2, spec.getColumnLabel(1));
		
	}
	
	public void testDefineReusableChoicesXml() throws Exception
	{
		String xml = "<CustomFields>" + SAMPLE_DROPDOWN_CHOICES + "</CustomFields>";
		XmlCustomFieldsLoader loader = new XmlCustomFieldsLoader();
		loader.parse(xml);
		PoolOfReusableChoicesLists choiceDefinitions = loader.getChoiceDefinitions();
		assertEquals("Didn't see two choice definitions?", 2, choiceDefinitions.size());
		ReusableChoices outer = choiceDefinitions.getChoices(OUTER_LEVEL_NAME);
		assertEquals("Wrong reusable outer label?", "District:", outer.getLabel());
		assertEquals("Wrong number of outer choices?", 3, outer.size());
		assertEquals("Wrong outer choice code?", "2", outer.get(1).getCode());
		assertEquals("Wrong outer choice label?", "Netrokona", outer.get(1).toString());

		ReusableChoices middle = choiceDefinitions.getChoices(MIDDLE_LEVEL_NAME);
		assertEquals("Wrong reusable middle label?", "Upazilla:", middle.getLabel());
		assertEquals("Wrong number of middle choices?", 4, middle.size());
		assertEquals("Wrong middle choice code?", "2.01", middle.get(2).getCode());
		assertEquals("Wrong middle choice label?", "Netrokona Sadar", middle.get(2).toString());
	}
	
	public void testDefineNestedDropdown() throws Exception
	{
		String xml = "<CustomFields>" + SAMPLE_DROPDOWN_CHOICES + SAMPLE_NESTED_DROPDOWN + "</CustomFields>";
		XmlCustomFieldsLoader loader = new XmlCustomFieldsLoader();
		loader.parse(xml);
		FieldSpec[] specs = loader.getFieldSpecs();
		assertEquals("Not one spec?", 1, specs.length);
		NestedDropDownFieldSpec spec = (NestedDropDownFieldSpec) specs[0];
		assertEquals("Wrong type?", new FieldTypeNestedDropdown(), spec.getType());
		assertEquals("Wrong tag?", "location", spec.getTag());
		assertEquals("Wrong label?", "Location: ", spec.getLabel());
		assertEquals("Wrong number of levels?", 3, spec.getLevelCount());
		
		NestedDropdownLevel outer = spec.getLevel(0);
		assertEquals("DistrictChoices", outer.getChoicesName());
		NestedDropdownLevel middle = spec.getLevel(1);
		assertEquals("UpazillaChoices", middle.getChoicesName());
	}

	private FieldSpec[] getSampleSpecs()
	{
		GridFieldSpec gridSpec = new GridFieldSpec();
		gridSpec.setTag("grid");
		
		return new FieldSpec[] {
			FieldSpec.createStandardField("date", new FieldTypeDate()),
			FieldSpec.createStandardField("text", new FieldTypeNormal()),
			FieldSpec.createStandardField("multi", new FieldTypeMultiline()),
			FieldSpec.createStandardField("range", new FieldTypeDateRange()),
			FieldSpec.createStandardField("bool", new FieldTypeBoolean()),
			FieldSpec.createStandardField("language", new FieldTypeLanguage()),
			gridSpec,
			LegacyCustomFields.createFromLegacy("custom,Custom <label>"),
		};
	}

	private static final String OUTER_LEVEL_NAME = "DistrictChoices";
	private static final String MIDDLE_LEVEL_NAME = "UpazillaChoices";
	
	private static final String SAMPLE_DROPDOWN_CHOICES = 
		"<ReusableChoices code='" + OUTER_LEVEL_NAME + "' label='District:' >" + 
			"<Choice code='1' label='Madaripur'/>" + 
			"<Choice code='2' label='Netrokona'/>" + 
			"<Choice code='3' label='Bogra'/>" + 
		"</ReusableChoices>" + 
		"<ReusableChoices code='" + MIDDLE_LEVEL_NAME + "' label='Upazilla:'>" + 
			"<Choice code='1.01' label='Madaripur Sadar'/>" + 
			"<Choice code='1.02' label='Rajoir'/>" + 
			"<Choice code='2.01' label='Netrokona Sadar'/>" + 
			"<Choice code='3.01' label='Bogra Sadar'/>" + 
		"</ReusableChoices>";
	
	private static final String SAMPLE_NESTED_DROPDOWN =
		"<Field type='NESTEDDROPDOWN'>" + 
		"<Tag>location</Tag>" + 
		"<Label>Location: </Label>" + 
		"<UseReusableChoices code='DistrictChoices' />" + 
		"<UseReusableChoices code='UpazillaChoices'  />" + 
		"<UseReusableChoices code='UnionChoices' />" + 
		"</Field>";
}
