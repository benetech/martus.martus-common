/*

/*
The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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
import org.martus.common.ReusableChoices;
import org.martus.util.xml.XmlUtilities;

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
		ReusableChoices newChoices = new ReusableChoices("", "");
		newChoices.addAll(choicesToUse);
		choices = new ReusableChoices[] {newChoices};
		updateDetailsXml();
	}
	
	public ChoiceItem[] getAllChoices()
	{
		int LAST = choices.length - 1;
		return choices[LAST].getChoices();
	}

	public int getCount()
	{
		return getAllChoices().length;
	}
	
	public ChoiceItem getChoice(int index)
	{
		return getAllChoices()[index];
	}
	
	public String getValue(int index) throws ArrayIndexOutOfBoundsException 
	{
		return getChoice(index).toString();
	}
	
	public String convertStoredToSearchable(String storedData, MiniLocalization localization)
	{
		return getDisplayString(storedData);
	}

	public String convertStoredToHtml(String storedData, MiniLocalization localization)
	{
		return XmlUtilities.getXmlEncoded(getDisplayString(storedData));
	}

	public String getDisplayString(String code)
	{
		int at = findCode(code);
		if(at < 0)
			return code;
		return getValue(at);
	}
	
	public String getDetailsXml()
	{
		return detailsXml;
	}
	
	void updateDetailsXml()
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<" + DROPDOWN_SPEC_CHOICES_TAG + ">\n");
		
		for(int i = 0 ; i < getCount(); ++i)
		{
			xml.append(("<" + DROPDOWN_SPEC_CHOICE_TAG + ">"));
			xml.append(getChoice(i).toString());
			xml.append("</" + DROPDOWN_SPEC_CHOICE_TAG + ">\n");
		}
		xml.append("</" + DROPDOWN_SPEC_CHOICES_TAG + ">\n");
		detailsXml = xml.toString();
	}

	public int findCode(String code)
	{
		for(int i=0; i < getCount(); ++i)
			if(getChoice(i).getCode().equals(code))
				return i;
		
		if(code.equals(" "))
			return findCode("");
		
		return -1;
	}
	
	public String getDefaultValue()
	{
		if(getCount() == 0)
			return "";
		return getChoice(0).getCode();
	}
	
	public String[] getReusableChoicesCodes()
	{
		return new String[0];
	}

	public String getReusableChoicesCode()
	{
		return null;
	}
	
	public Object getDataSource()
	{
		return null;
	}

	public String getDataSourceGridTag()
	{
		return null;
	}

	public String getDataSourceGridColumn()
	{
		return null;
	}

	public static final String DROPDOWN_SPEC_CHOICES_TAG = "Choices";
	public static final String DROPDOWN_SPEC_CHOICE_TAG = "Choice";
	
	public static final String DROPDOWN_SPEC_DATA_SOURCE = "DataSource";

	
	private ReusableChoices[] choices;
	private String detailsXml;
}
