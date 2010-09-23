/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2010, Beneficent
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

import org.martus.common.MiniLocalization;
import org.martus.common.PoolOfReusableChoicesLists;
import org.martus.common.ReusableChoices;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.fieldspec.CustomDropDownFieldSpec;
import org.martus.util.TestCaseEnhanced;

public class TestMartusDropdownField extends TestCaseEnhanced
{
	public TestMartusDropdownField(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		localization = new MiniLocalization();
		reusableChoicesPool = new PoolOfReusableChoicesLists();
		choicesA = new ReusableChoices("a", "Choices A");
		choicesA.add(new ChoiceItem(levelACode1, "first"));
		choicesA.add(new ChoiceItem(levelACode2, "second"));
		choicesB = new ReusableChoices("b", "Choices B");
		choicesB.add(new ChoiceItem(levelACode1 + "." + levelBCode1, "innerfirst1"));
		choicesB.add(new ChoiceItem(levelACode1 + "." + levelBCode2, "innersecond1"));
		choicesB.add(new ChoiceItem(levelACode2 + "." + levelBCode1, "innerfirst2"));
		choicesB.add(new ChoiceItem(levelACode2 + "." + levelBCode2, "innersecond2"));
		reusableChoicesPool.add(choicesA);
		reusableChoicesPool.add(choicesB);
	}
	
	public void testContains() throws Exception
	{
		CustomDropDownFieldSpec spec = new CustomDropDownFieldSpec();
		MartusDropdownField field = new MartusDropdownField(spec, reusableChoicesPool);
		assertFalse(field.contains("", localization));
	}
	
	public void testSubfields() throws Exception
	{
		CustomDropDownFieldSpec spec = new CustomDropDownFieldSpec();
		spec.addReusableChoicesCode(choicesA.getCode());
		spec.addReusableChoicesCode(choicesB.getCode());
		MartusDropdownField field = new MartusDropdownField(spec, reusableChoicesPool);
		ChoiceItem sampleChoice = choicesB.get(0);
		field.setData(sampleChoice.getCode());
		MartusField subB = field.getSubField(choicesB.getCode(), localization);
		assertTrue("sub b isn't a dropdown?", subB.getType().isDropdown());
		assertEquals("sub b didn't inherit full code?", field.getData(), subB.getData());
		String exportableData = subB.getFieldSpec().convertStoredToExportable(subB.getData(), reusableChoicesPool, localization);
		assertEquals("sub b exportable wrong?", field.getData(), exportableData);
		String htmlData = subB.getFieldSpec().convertStoredToHtml(subB, localization);
		assertEquals("sub b html wrong?", sampleChoice.toString(), htmlData);
		String searchableData = subB.getFieldSpec().convertStoredToSearchable(subB.getData(), reusableChoicesPool, localization);
		assertEquals("sub b searchable wrong?", field.getData(), searchableData);
	}
	
	private static final String levelACode1 = "A1";
	private static final String levelACode2 = "A2";
	private static final String levelBCode1 = "B1";
	private static final String levelBCode2 = "B2";
	private MiniLocalization localization;
	private PoolOfReusableChoicesLists reusableChoicesPool;
	private ReusableChoices choicesA;
	private ReusableChoices choicesB;
}
