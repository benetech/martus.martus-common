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

import org.martus.common.FieldSpec;
import org.martus.common.GridFieldSpec;
import org.martus.common.LegacyCustomFields;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.clientside.Localization;
import org.martus.util.TestCaseEnhanced;


public class TestFieldSpec extends TestCaseEnhanced
{
	public TestFieldSpec(String name)
	{
		super(name);
	}

	public void testLegacy()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("a,b");
		assertFalse("has unknown?", plainField.hasUnknownStuff());
		assertEquals("a", plainField.getTag());
		assertEquals("b", plainField.getLabel());
		assertEquals("not normal?", FieldSpec.TYPE_NORMAL, plainField.getType());
		
		FieldSpec fieldWithExtra = LegacyCustomFields.createFromLegacy("c,d,e");
		assertTrue("doesn't have unknown?", fieldWithExtra.hasUnknownStuff());
		assertEquals("c", fieldWithExtra.getTag());
		assertEquals("d", fieldWithExtra.getLabel());
		assertEquals("not unknown?", FieldSpec.TYPE_UNKNOWN, fieldWithExtra.getType());
		
		FieldSpec fieldWithIllegalCharacters = LegacyCustomFields.createFromLegacy("!<a9-._@#josée,!<a9-._@#josée");
		assertEquals("__a9-.___josée", fieldWithIllegalCharacters.getTag());
		assertEquals("!<a9-._@#josée", fieldWithIllegalCharacters.getLabel());

		FieldSpec fieldWithIllegalFirstCharacter = LegacyCustomFields.createFromLegacy(".ok,ok");
		assertEquals("_ok", fieldWithIllegalFirstCharacter.getTag());
	}
	
	public void testCreateFromTag()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("author");
		assertFalse("has unknown?", plainField.hasUnknownStuff());
		assertEquals("author", plainField.getTag());
		assertEquals("", plainField.getLabel());
		assertEquals("not normal?", FieldSpec.TYPE_NORMAL, plainField.getType());

		FieldSpec dateField = LegacyCustomFields.createFromLegacy("entrydate");
		assertFalse("has unknown?", dateField.hasUnknownStuff());
		assertEquals("entrydate", dateField.getTag());
		assertEquals("", dateField.getLabel());
		assertEquals("not date?", FieldSpec.TYPE_DATE, dateField.getType());
	}
	
	public void testDefaultValues()
	{
		String emptyString = "";
		FieldSpec spec = new FieldSpec(FieldSpec.TYPE_BOOLEAN);
		assertEquals(FieldSpec.FALSESTRING, spec.getDefaultValue());
		
		spec = new FieldSpec(FieldSpec.TYPE_DATE);
		assertEquals(Bulletin.getToday(), spec.getDefaultValue());

		spec = new FieldSpec(FieldSpec.TYPE_DATERANGE);
		assertEquals(Bulletin.getToday(), spec.getDefaultValue());

		spec = new GridFieldSpec();
		assertEquals(emptyString, spec.getDefaultValue());
		
		spec = new FieldSpec(FieldSpec.TYPE_LANGUAGE);
		assertEquals(Localization.LANGUAGE_OTHER, spec.getDefaultValue());
		
		spec = new FieldSpec(FieldSpec.TYPE_MULTILINE);
		assertEquals(emptyString, spec.getDefaultValue());

		spec = new FieldSpec(FieldSpec.TYPE_NORMAL);
		assertEquals(emptyString, spec.getDefaultValue());
	}
	
	public void testToString()
	{
		FieldSpec plainField = LegacyCustomFields.createFromLegacy("a,<&b>");
		String xml = "<Field type='STRING'>\n<Tag>a</Tag>\n<Label>&lt;&amp;b&gt;</Label>\n</Field>\n";
		assertEquals(xml, plainField.toString());
	}
	
	public void testGetTypeString()
	{
		assertEquals("STRING", FieldSpec.getTypeString(FieldSpec.TYPE_NORMAL));
		assertEquals("MULTILINE", FieldSpec.getTypeString(FieldSpec.TYPE_MULTILINE));
		assertEquals("DATE", FieldSpec.getTypeString(FieldSpec.TYPE_DATE));
		assertEquals("DATERANGE", FieldSpec.getTypeString(FieldSpec.TYPE_DATERANGE));
		assertEquals("BOOLEAN", FieldSpec.getTypeString(FieldSpec.TYPE_BOOLEAN));
		assertEquals("LANGUAGE", FieldSpec.getTypeString(FieldSpec.TYPE_LANGUAGE));
		assertEquals("GRID", FieldSpec.getTypeString(FieldSpec.TYPE_GRID));
		assertEquals("UNKNOWN", FieldSpec.getTypeString(-99));
	}
	
	public void testGetTypeCode()
	{
		assertEquals(FieldSpec.TYPE_UNKNOWN, FieldSpec.getTypeCode("anything else"));
		assertEquals(FieldSpec.TYPE_NORMAL, FieldSpec.getTypeCode("STRING"));
		assertEquals(FieldSpec.TYPE_MULTILINE, FieldSpec.getTypeCode("MULTILINE"));
		assertEquals(FieldSpec.TYPE_DATE, FieldSpec.getTypeCode("DATE"));
		assertEquals(FieldSpec.TYPE_DATERANGE, FieldSpec.getTypeCode("DATERANGE"));
		assertEquals(FieldSpec.TYPE_BOOLEAN, FieldSpec.getTypeCode("BOOLEAN"));
		assertEquals(FieldSpec.TYPE_LANGUAGE, FieldSpec.getTypeCode("LANGUAGE"));
		assertEquals(FieldSpec.TYPE_GRID, FieldSpec.getTypeCode("GRID"));
	}
}
