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
import org.martus.common.fieldspec.FieldType;
import org.martus.common.fieldspec.FieldTypeAnyField;
import org.martus.common.fieldspec.FieldTypeBoolean;
import org.martus.common.fieldspec.FieldTypeDate;
import org.martus.common.fieldspec.FieldTypeDateRange;
import org.martus.common.fieldspec.FieldTypeLanguage;
import org.martus.common.fieldspec.FieldTypeMessage;
import org.martus.common.fieldspec.FieldTypeMultiline;
import org.martus.common.fieldspec.FieldTypeNormal;
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
		FieldSpec spec = FieldSpec.createCustomField("tag", "label", new FieldTypeNormal());
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
	
	public void testGetSearchableDataForStringFields()
	{
		verifyNormalDataIsAlsoPrintable(new FieldTypeNormal(), "sample string");
		verifyNormalDataIsAlsoPrintable(new FieldTypeMultiline(), "sample string");
		verifyNormalDataIsAlsoPrintable(new FieldTypeBoolean(), "sample string");
		verifyNormalDataIsAlsoPrintable(new FieldTypeMessage(), "sample string");
	}
	
// FIXME: Enable these tests as soon as the refactoring is done
// If this comment is still here after 2005-09-14, yell at Kevin!
//	public void testGetSearchableDataForDateFields()
//	{
//		String rawDate = "2005-10-15";
//		MiniLocalization localization = new MiniLocalization();
//		String localizedDate = localization.convertStoredDateToDisplay(rawDate);
//		
//		verifyPrintableData(FieldSpec.TYPE_DATE, rawDate, localizedDate);
//		
//	}
//	
//	public void testGetSearchableDataForDateRangeFields()
//	{
//	}
//
//	public void testGetSearchableDataForBooleanFields()
//	{
//		MiniLocalization localization = new MiniLocalization();
//		verifyPrintableData(FieldSpec.TYPE_DATE, FieldSpec.TRUESTRING, localization.getButtonLabel("yes"));
//	}
//
//	public void testGetSearchableDataForLanguageFields()
//	{
//		MiniLocalization localization = new MiniLocalization();
//		String languageCode = MiniLocalization.ARABIC;
//		verifyPrintableData(FieldSpec.TYPE_DATE, languageCode, localization.getLanguageName(languageCode));
//	}
//
//	public void testGetSearchableDataForDropDownFields()
//	{
//		fail();
//	}
//
//	public void testGetSearchableDataForGridFields()
//	{
//		fail();
//	}
//
	private void verifyNormalDataIsAlsoPrintable(final FieldType type, final String rawData)
	{
		final String expectedPrintableData = rawData;
		verifyPrintableData(type, rawData, expectedPrintableData);
	}

	private void verifyPrintableData(final FieldType type, final String rawData, final String expectedPrintableData)
	{
		MartusField string = new MartusField(createFieldSpec(type));
		string.setData(rawData);
		assertEquals("Wrong printableData for " + FieldSpec.getTypeString(type), expectedPrintableData, string.getSearchableData());
	}
	
	public void testInitialValueForSimpleTypes()
	{
		verifyInitialValue(new FieldTypeNormal());
		verifyInitialValue(new FieldTypeBoolean());
		verifyInitialValue(new FieldTypeDate());
		verifyInitialValue(new FieldTypeDateRange());
		verifyInitialValue(new FieldTypeLanguage());
		verifyInitialValue(new FieldTypeMessage());
		verifyInitialValue(new FieldTypeMultiline());
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
		spec.addColumn(createFieldSpec(new FieldTypeNormal()));
		spec.addColumn(createFieldSpec(new FieldTypeBoolean()));
		spec.addColumn(new DropDownFieldSpec(choices));
		MartusField f = new MartusField(spec);
		GridData data = new GridData(spec);
		data.setFromXml(f.getData());
		for(int col = 0; col < spec.getColumnCount(); ++col)
			assertEquals("Normal column wrong data?", spec.getFieldSpec(col).getDefaultValue(), data.getValueAt(0, col));
		
		try
		{
			spec.addColumn(createFieldSpec(new FieldTypeAnyField()));
			fail("Should have thrown for unsupported type");
		}
		catch(GridFieldSpec.UnsupportedFieldTypeException ignoreExpected)
		{
		}
	}
	
	private void verifyInitialValue(FieldType type)
	{
		FieldSpec spec = createFieldSpec(type);
		MartusField f = new MartusField(spec);
		assertEquals("wrong initial value for type " + type + ": ", spec.getDefaultValue(), f.getData());
	}

	private FieldSpec createFieldSpec(FieldType type)
	{
		FieldSpec spec = FieldSpec.createCustomField(type.getTypeName(), "label", type);
		return spec;
	}

	ChoiceItem[] choices = 
	{
		new ChoiceItem("firstcode", "First Value"),
		new ChoiceItem("secondcode", "Second Value"),
	};
}
