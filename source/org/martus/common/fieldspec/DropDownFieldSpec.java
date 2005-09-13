/*

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

import org.martus.common.MiniLocalization;




public class DropDownFieldSpec extends FieldSpec
{
	public DropDownFieldSpec()
	{
		this(new ChoiceItem[] {});
	}
	
	public DropDownFieldSpec(ChoiceItem[] choicesToUse)
	{
		super(new FieldTypeDropdown());
		setChoices(choicesToUse);
	}
	
	public void setChoices(ChoiceItem[] choicesToUse)
	{
		choices = choicesToUse;
	}

	public int getCount()
	{
		return choices.length;
	}
	
	public ChoiceItem getChoice(int index)
	{
		return choices[index];
	}
	
	public String getValue(int index) throws ArrayIndexOutOfBoundsException 
	{
		return getChoice(index).toString();
	}
	
	public String convertStoredToDisplay(String storedData, MiniLocalization localization)
	{
		return getDisplayString(storedData);
	}

	public String getDisplayString(String code)
	{
		int at = findCode(code);
		if(at < 0)
			return code;
		return getValue(at);
	}
	
	public int findCode(String code)
	{
		for(int i=0; i < getCount(); ++i)
			if(getChoice(i).getCode().equals(code))
				return i;
		
		return -1;
	}
	
	public String getDefaultValue()
	{
		return getChoice(0).getCode();
	}
	
	
	private ChoiceItem[] choices;
}
