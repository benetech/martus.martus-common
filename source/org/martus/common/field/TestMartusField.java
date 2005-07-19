/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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

import org.martus.common.GridData;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.fieldspec.DropDownFieldSpec;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.util.TestCaseEnhanced;


public class TestMartusField extends TestCaseEnhanced
{
	public TestMartusField(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		FieldSpec spec = FieldSpec.createCustomField("tag", "label", FieldSpec.TYPE_NORMAL);
		MartusField f = new MartusField(spec);
		assertEquals("wrong tag?", spec.getTag(), f.getTag());
		assertEquals("wrong label?", spec.getLabel(), f.getLabel());
		assertEquals("wrong type?", spec.getType(), f.getType());
		assertEquals("not initially blank?", "", f.getData());
		
		assertEquals("wrong spec?", spec.toString(), f.getFieldSpec().toString());
		
		final String sampleData = "test data"; 
		f.setData(sampleData);
		assertEquals("didn't set data?", sampleData, f.getData());
	}
	
	public void testInitialValueForSimpleTypes()
	{
		verifyInitialValue(FieldSpec.TYPE_NORMAL);
		verifyInitialValue(FieldSpec.TYPE_BOOLEAN);
		verifyInitialValue(FieldSpec.TYPE_DATE);
		verifyInitialValue(FieldSpec.TYPE_DATERANGE);
		verifyInitialValue(FieldSpec.TYPE_LANGUAGE);
		verifyInitialValue(FieldSpec.TYPE_MESSAGE);
		verifyInitialValue(FieldSpec.TYPE_MULTILINE);
	}
	
	public void testDropDownInitialValue()
	{	
		DropDownFieldSpec spec = new DropDownFieldSpec(choices);
		MartusField f = new MartusField(spec);
		assertEquals("Dropdown didn't default to first entry?", choices[0].getCode(), f.getData());
	}
	
	public void testGridInitialValue() throws Exception
	{
		GridFieldSpec spec = new GridFieldSpec();
		spec.addColumn(createFieldSpec(FieldSpec.TYPE_NORMAL));
		spec.addColumn(createFieldSpec(FieldSpec.TYPE_BOOLEAN));
		spec.addColumn(new DropDownFieldSpec(choices));
		MartusField f = new MartusField(spec);
		GridData data = new GridData(spec);
		data.setFromXml(f.getData());
		for(int col = 0; col < spec.getColumnCount(); ++col)
			assertEquals("Normal column wrong data?", spec.getFieldSpec(col).getDefaultValue(), data.getValueAt(0, col));
		
		try
		{
			spec.addColumn(createFieldSpec(234234));
			fail("Should have thrown for unrecognized type");
		}
		catch(RuntimeException ignoreExpected)
		{
		}
	}
	
	private void verifyInitialValue(int type)
	{
		FieldSpec spec = createFieldSpec(type);
		MartusField f = new MartusField(spec);
		assertEquals("wrong initial value for type " + type + ": ", spec.getDefaultValue(), f.getData());
	}

	private FieldSpec createFieldSpec(int type)
	{
		FieldSpec spec = FieldSpec.createCustomField(Integer.toString(type), "label", type);
		return spec;
	}

	ChoiceItem[] choices = 
	{
		new ChoiceItem("firstcode", "First Value"),
		new ChoiceItem("secondcode", "Second Value"),
	};
}
