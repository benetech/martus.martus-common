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

import java.util.Vector;

import org.martus.common.LegacyCustomFields;
import org.martus.common.MiniLocalization;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.fieldspec.CustomDropDownFieldSpec;
import org.martus.common.fieldspec.DropDownFieldSpec;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldType;
import org.martus.common.fieldspec.FieldTypeAnyField;
import org.martus.common.fieldspec.FieldTypeBoolean;
import org.martus.common.fieldspec.FieldTypeDate;
import org.martus.common.fieldspec.FieldTypeDateRange;
import org.martus.common.fieldspec.FieldTypeDropdown;
import org.martus.common.fieldspec.FieldTypeGrid;
import org.martus.common.fieldspec.FieldTypeLanguage;
import org.martus.common.fieldspec.FieldTypeMessage;
import org.martus.common.fieldspec.FieldTypeMultiline;
import org.martus.common.fieldspec.FieldTypeNormal;
import org.martus.common.fieldspec.FieldTypeSearchValue;
import org.martus.common.fieldspec.FieldTypeUnknown;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.MessageFieldSpec;
import org.martus.common.utilities.DateUtilities;
import org.martus.util.MartusCalendar;
import org.martus.util.TestCaseEnhanced;


public class TestFieldSpec extends TestCaseEnhanced
{
	public TestFieldSpec(String name)
	{
		super(name);
	}
	
	public void testDateFormatConversions() throws Exception
	{
		String wayOldDate = "1853-05-21";
		String oldDate = "1931-07-19";
		String recentDate = "1989-09-28";
		String nearFutureDate = "2017-06-28";
		String farFutureDate = "2876-08-16";
		
		verifyRoundTripDateConversion("recent past", recentDate);
		verifyRoundTripDateConversion("near future", nearFutureDate);
		verifyRoundTripDateConversion("after 2020", farFutureDate);
		verifyRoundTripDateConversion("before 1970", oldDate);
		verifyRoundTripDateConversion("before 1900", wayOldDate);
	}
	
	void verifyRoundTripDateConversion(String text, String dateString) throws Exception
	{
		MartusCalendar cal = MartusCalendar.yyyymmddWithDashesToCalendar(dateString);
		String result = cal.calendarToYYYYMMDD();
		assertEquals("date conversion failed: " + text, dateString, result);
	}

	public void testLegacy()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("a,b");
		assertFalse("has unknown?", plainField.hasUnknownStuff());
		assertEquals("a", plainField.getTag());
		assertEquals("b", plainField.getLabel());
		assertEquals("not normal?", new FieldTypeNormal(), plainField.getType());
		
		FieldSpec fieldWithExtra = LegacyCustomFields.createFromLegacy("c,d,e");
		assertTrue("doesn't have unknown?", fieldWithExtra.hasUnknownStuff());
		assertEquals("c", fieldWithExtra.getTag());
		assertEquals("d", fieldWithExtra.getLabel());
		assertEquals("not unknown?", new FieldTypeUnknown(), fieldWithExtra.getType());
		
		FieldSpec fieldWithIllegalCharacters = LegacyCustomFields.createFromLegacy("!<a9-._@#jos"+UnicodeConstants.ACCENT_E_LOWER+"e,!<a9-._@#jos"+UnicodeConstants.ACCENT_E_LOWER+"e");
		assertEquals("__a9-.___jos"+UnicodeConstants.ACCENT_E_LOWER+"e", fieldWithIllegalCharacters.getTag());
		assertEquals("!<a9-._@#jos"+UnicodeConstants.ACCENT_E_LOWER+"e", fieldWithIllegalCharacters.getLabel());

		FieldSpec fieldWithIllegalFirstCharacter = LegacyCustomFields.createFromLegacy(".ok,ok");
		assertEquals("_ok", fieldWithIllegalFirstCharacter.getTag());
	}
	
	public void testCreateFromTag()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("author");
		assertFalse("has unknown?", plainField.hasUnknownStuff());
		assertEquals("author", plainField.getTag());
		assertEquals("", plainField.getLabel());
		assertEquals("not normal?", new FieldTypeNormal(), plainField.getType());

		FieldSpec dateField = LegacyCustomFields.createFromLegacy("entrydate");
		assertFalse("has unknown?", dateField.hasUnknownStuff());
		assertEquals("entrydate", dateField.getTag());
		assertEquals("", dateField.getLabel());
		assertEquals("not date?", new FieldTypeDate(), dateField.getType());
	}
	
	public void testDefaultValues()
	{
		String emptyString = "";
		FieldSpec spec = new FieldSpec(new FieldTypeBoolean());
		assertEquals(FieldSpec.FALSESTRING, spec.getDefaultValue());
		
		spec = new FieldSpec(new FieldTypeDate());
		assertEquals(DateUtilities.getFirstOfThisYear(), spec.getDefaultValue());

		spec = new FieldSpec(new FieldTypeDateRange());
		assertEquals(DateUtilities.getFirstOfThisYear(), spec.getDefaultValue());

		spec = new GridFieldSpec();
		assertEquals(emptyString, spec.getDefaultValue());
		
		spec = new FieldSpec(new FieldTypeLanguage());
		assertEquals(MiniLocalization.LANGUAGE_OTHER, spec.getDefaultValue());
		
		spec = new FieldSpec(new FieldTypeMultiline());
		assertEquals(emptyString, spec.getDefaultValue());

		spec = new FieldSpec(new FieldTypeNormal());
		assertEquals(emptyString, spec.getDefaultValue());

		spec = new FieldSpec(new FieldTypeSearchValue());
		assertEquals(emptyString, spec.getDefaultValue());
		
		spec = new DropDownFieldSpec(new ChoiceItem[] {new ChoiceItem("first", "First item"), new ChoiceItem("", "")});
		assertEquals("first", spec.getDefaultValue());
		
		spec = new FieldSpec(new FieldTypeMessage());
		assertEquals(emptyString, spec.getDefaultValue());
	
		CustomDropDownFieldSpec dropdownSpec = new CustomDropDownFieldSpec();
		dropdownSpec.setChoices(new Vector());
		assertEquals("", dropdownSpec.getDefaultValue());

		String message = "Message in FieldSpec";
		MessageFieldSpec messageSpec = new MessageFieldSpec();
		messageSpec.putMessage(message);
		assertEquals(message, messageSpec.getDefaultValue());

	}
	
	public void testToString()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("a,<&b>");
		String xml = "<Field type='STRING'>\n<Tag>a</Tag>\n<Label>&lt;&amp;b&gt;</Label>\n</Field>\n";
		assertEquals(xml, plainField.toString());
	}
	
	public void testGetTypeString()
	{
		assertEquals("STRING", FieldSpec.getTypeString(new FieldTypeNormal()));
		assertEquals("MULTILINE", FieldSpec.getTypeString(new FieldTypeMultiline()));
		assertEquals("DATE", FieldSpec.getTypeString(new FieldTypeDate()));
		assertEquals("DATERANGE", FieldSpec.getTypeString(new FieldTypeDateRange()));
		assertEquals("BOOLEAN", FieldSpec.getTypeString(new FieldTypeBoolean()));
		assertEquals("LANGUAGE", FieldSpec.getTypeString(new FieldTypeLanguage()));
		assertEquals("GRID", FieldSpec.getTypeString(new FieldTypeGrid()));
		assertEquals("DROPDOWN", FieldSpec.getTypeString(new FieldTypeDropdown()));
		assertEquals("MESSAGE", FieldSpec.getTypeString(new FieldTypeMessage()));
		assertEquals("UNKNOWN", FieldSpec.getTypeString(new FieldTypeUnknown()));
		assertEquals("UNKNOWN", FieldSpec.getTypeString(new FieldTypeAnyField()));
	}
	
	public void testGetTypeCode()
	{
		verifyCreatedTypeString("STRING");
		verifyCreatedTypeString("MULTILINE");
		verifyCreatedTypeString("DATE");
		verifyCreatedTypeString("DATERANGE");
		verifyCreatedTypeString("BOOLEAN");
		verifyCreatedTypeString("LANGUAGE");
		verifyCreatedTypeString("GRID");
		verifyCreatedTypeString("DROPDOWN");
		verifyCreatedTypeString("MESSAGE");
		assertEquals("UNKNOWN", FieldSpec.getTypeCode("anything else").getTypeName());
	}
	
	private void verifyCreatedTypeString(String typeString)
	{
		FieldType type = FieldSpec.getTypeCode(typeString);
		assertEquals("wrong type: " + typeString, typeString, type.getTypeName());
	}
	
	public void testEqualsAndCompareTo()
	{
		FieldSpec a = new FieldSpec(new FieldTypeNormal());
		String labelA = "label a";
		String tagA = "NewTagA";
		String labelB = "label b";
		String tagB = "NewTagB";
		a.setLabel(labelA);
		a.setTag(tagA);
		FieldSpec b = new FieldSpec(new FieldTypeNormal());
		b.setLabel(labelA);
		b.setTag(tagA);
		assertTrue("A & B should be identical (equals)", a.equals(b));
		assertEquals("A & B should be identical (compareTo)", 0, a.compareTo(b));
		
		b.setLabel(labelB);
		assertFalse("B has different Label (equals)", a.equals(b));
		assertNotEquals("B has different Label (compareTo)", 0, a.compareTo(b));

		b.setLabel(labelA);
		b.setTag(tagB);
		assertFalse("B has different Tag (equals)", a.equals(b));
		assertNotEquals("B has different Tag (compareTo)", 0, a.compareTo(b));
		
		b.setTag("AAA");
		b.setLabel("zzz");
		assertTrue("a not less than b?", a.compareTo(b) < 0);
		assertTrue("b not greater than a?", b.compareTo(a) > 0);
		
		FieldSpec c = new FieldSpec(new FieldTypeMultiline());
		c.setLabel(labelA);
		c.setTag(tagA);
		assertFalse("C has different Type (equals)", a.equals(b));
		assertNotEquals("C has different Type (compareTo)", 0, a.compareTo(b));
		
		String d = "someString";
		assertFalse("FieldSpec is not a String", a.equals(d));
		
		assertTrue("not greater than null?", a.compareTo(null) > 0);
	}
	
}
