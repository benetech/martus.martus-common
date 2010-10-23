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
import org.martus.common.fieldspec.CustomDropDownFieldSpec;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeDropdown;
import org.martus.common.fieldspec.StandardFieldSpecs;

public class MartusDropdownField extends MartusField
{
	public MartusDropdownField(FieldSpec specToUse, PoolOfReusableChoicesLists reusableChoicesToUse)
	{
		super(specToUse, reusableChoicesToUse);
	}

	public MartusField createClone() throws Exception
	{
		MartusDropdownField clone = new MartusDropdownField(getFieldSpec(), getReusableChoicesLists());
		clone.setData(getData());
		return clone;
	}
	
	protected String internalGetHtml(MiniLocalization localization) throws Exception
	{
		CustomDropDownFieldSpec dropDownSpec = getDropDownSpec();
		String[] reusableChoicesCodes = dropDownSpec.getReusableChoicesCodes();
		if(reusableChoicesCodes.length == 0)
			return super.internalGetHtml(localization);
		
		return dropDownSpec.convertStoredToHtml(this, localization);
	}

	public boolean contains(String value, MiniLocalization localization)
	{
		// NOTE: this type doesn't support contains searching at all!
		return false;
	}

	public MartusField getSubField(String tag, MiniLocalization localization)
	{
		if(StandardFieldSpecs.isStandardFieldTag(getTag()))
			return null;
		
		CustomDropDownFieldSpec outerSpec = getDropDownSpec();
		String[] reusableChoicesCodes = outerSpec.getReusableChoicesCodes();
		if(reusableChoicesCodes.length < 2)
			return new EmptyMartusFieldWithInfiniteSubFields(tag);

		int level = outerSpec.findReusableLevelByCode(tag);
		if(level < 0)
			return new EmptyMartusFieldWithInfiniteSubFields(tag);

		ReusableChoices reusableChoices = getReusableChoicesLists().getChoices(reusableChoicesCodes[level]);
		CustomDropDownFieldSpec subSpec = (CustomDropDownFieldSpec) FieldSpec.createSubField(outerSpec, tag, reusableChoices.getLabel(), new FieldTypeDropdown());
		subSpec.addReusableChoicesCode(tag);
		MartusField subField = new MartusDropdownField(subSpec, getReusableChoicesLists());
		subField.setData(getData());
		return subField;
	}

	public boolean doesMatch(int compareOp, String searchForValue, MiniLocalization localization)
	{
		boolean doesEqual = doesEqual(searchForValue);
		switch(compareOp)
		{
			case EQUAL:		return doesEqual;
			case NOT_EQUAL: return !doesEqual;
			default:		return false;
		}
	}
	
	private boolean doesEqual(String searchForValue)
	{
		CustomDropDownFieldSpec dropDownSpec = getDropDownSpec();
		if(StandardFieldSpecs.isStandardFieldTag(getTag()))
		{
			int found = dropDownSpec.findCode(searchForValue);
			return (found >= 0);
		}
		
		boolean isExactMatch = getData().equals(searchForValue);

		// Empty search always needs an exact match
		if(searchForValue.length() == 0)
			return isExactMatch;
		
		// Multi-level or non-reusable dropdown always needs an exact match
		if(dropDownSpec.getReusableChoicesCodes().length != 1)
			return isExactMatch;
		
		// Non-subfield always needs an exact match
		FieldSpec rawParentSpec = dropDownSpec.getParent();
		if(rawParentSpec == null)
			return isExactMatch;

		// if the code is not a valid reusable list in the parent (unlikely), fail
		String thisReusableChoicesListCode = dropDownSpec.getReusableChoicesCodes()[0];
		CustomDropDownFieldSpec parentSpec = (CustomDropDownFieldSpec) rawParentSpec;
		int level = parentSpec.findReusableLevelByCode(thisReusableChoicesListCode);
		if(level == -1)
			return false;

		// if searching for something at the wrong level, fail
		if(numberOfDots(searchForValue) != level)
			return false;

		// searching for specific level within possibly-deeper field data
		return (getData().startsWith(searchForValue));
	}

	private int numberOfDots(String searchForValue)
	{
		int dots = 0;
		for(int i = 0; i < searchForValue.length(); ++i)
			if(searchForValue.charAt(i) == '.')
				++dots;
		
		return dots;
	}

	private CustomDropDownFieldSpec getDropDownSpec()
	{
		return (CustomDropDownFieldSpec) getFieldSpec();
	}
}
