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
import org.martus.common.FieldSpec;
import org.martus.common.LegacyCustomFields;


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
		xml += "</CustomFields>";
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

	private FieldSpec[] getSampleSpecs()
	{
		return new FieldSpec[] {
			FieldSpec.createStandardField("date", FieldSpec.TYPE_DATE),
			FieldSpec.createStandardField("text", FieldSpec.TYPE_NORMAL),
			FieldSpec.createStandardField("multi", FieldSpec.TYPE_MULTILINE),
			FieldSpec.createStandardField("range", FieldSpec.TYPE_DATERANGE),
			FieldSpec.createStandardField("bool", FieldSpec.TYPE_BOOLEAN),
			FieldSpec.createStandardField("choice", FieldSpec.TYPE_CHOICE),
			LegacyCustomFields.createFromLegacy("custom,Custom <label>"),
		};
	}
	
}
