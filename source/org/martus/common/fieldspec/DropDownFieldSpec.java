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

import org.martus.common.MartusXml;
import org.martus.common.clientside.ChoiceItem;



public class DropDownFieldSpec extends FieldSpec
{
	public DropDownFieldSpec()
	{
		this(new ChoiceItem[] {});
	}
	
	public DropDownFieldSpec(ChoiceItem[] choicesToUse)
	{
		super(TYPE_DROPDOWN);
		setChoices(choicesToUse);
	}
	
	public String getDetailsXml()
	{
		String xml = MartusXml.getTagStartWithNewline(DROPDOWN_SPEC_CHOICES_TAG);
		for(int i = 0 ; i < getCount(); ++i)
		{
			xml += MartusXml.getTagStart(DROPDOWN_SPEC_CHOICE_TAG) +
					getValue(i) +
					MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICE_TAG);
		}
		xml += MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICES_TAG);
		return xml;
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
		return choices[index].toString();
	}
	
	public String getValue(String code)
	{
		int at = findCode(code);
		if(at < 0)
			return null;
		
		return choices[at].toString();
	}
	
	public int findCode(String code)
	{
		for(int i=0; i < choices.length; ++i)
			if(choices[i].getCode().equals(code))
				return i;
		
		return -1;
	}
	
	
	
	private ChoiceItem[] choices;
	
	public final static String DROPDOWN_SPEC_CHOICES_TAG = "Choices";
	public final static String DROPDOWN_SPEC_CHOICE_TAG = "Choice";
}
