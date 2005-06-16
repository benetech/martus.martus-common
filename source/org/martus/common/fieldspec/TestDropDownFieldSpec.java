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
package org.martus.common.fieldspec;
import org.martus.common.clientside.ChoiceItem;
import org.martus.util.TestCaseEnhanced;


public class TestDropDownFieldSpec extends TestCaseEnhanced
{

	public TestDropDownFieldSpec(String name)
	{
		super(name);
	}
	
	public void testGetValueFromTag() throws Exception
	{
		DropDownFieldSpec spec = new DropDownFieldSpec(choices);
		assertNull("found a non-existant tag?", spec.getDisplayString("nontag"));
		assertNull("not case sensitive?", spec.getDisplayString("TAG"));
		assertEquals("value", spec.getDisplayString("tag"));
		assertEquals("othervalue", spec.getDisplayString("othertag"));
	}
	
	public void testGetValueFromIndex() throws Exception
	{
		DropDownFieldSpec spec = new DropDownFieldSpec(choices);
		assertEquals("value", spec.getValue(0));
		assertEquals("othervalue", spec.getValue(1));
	}
	
	public void testGetChoice() throws Exception
	{
		DropDownFieldSpec spec = new DropDownFieldSpec(choices);
		assertEquals(choices[0], spec.getChoice(0));
		assertEquals(choices[1], spec.getChoice(1));
		
	}
	
	public void testFindCode()
	{
		DropDownFieldSpec spec = new DropDownFieldSpec(choices);
		assertEquals(0, spec.findCode(choices[0].getCode()));
		assertEquals(1, spec.findCode(choices[1].getCode()));
		assertEquals(-1, spec.findCode("no such code"));
	}
	
	public void testSetChoices()
	{
		DropDownFieldSpec spec = new DropDownFieldSpec();
		spec.setChoices(choices);
		assertEquals("didn't add all choices?", choices.length, spec.getCount());
	}

	static final ChoiceItem[] choices = {new ChoiceItem("tag", "value"), new ChoiceItem("othertag", "othervalue"),};
}
