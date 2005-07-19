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

import java.util.Vector;

import org.martus.common.MartusXml;
import org.martus.util.xml.SimpleXmlVectorLoader;
import org.xml.sax.SAXParseException;

public class CustomDropDownFieldSpec extends DropDownFieldSpec
{

	public int getCount()
	{
		return super.getCount() + 1;
	}
	
	public ChoiceItem getChoice(int index)
	{
		if(index > 0)
			return super.getChoice(index - 1);
		
		return new ChoiceItem(EMPTY_FIRST_CHOICE, EMPTY_FIRST_CHOICE);
	}
	
	public String getDefaultValue()
	{
		return EMPTY_FIRST_CHOICE;
	}

	public void setChoices(Vector stringChoicesToUse)
	{
		ChoiceItem[] choicesArray = new ChoiceItem[stringChoicesToUse.size()];
		for(int i = 0; i < stringChoicesToUse.size(); i++)
		{
			String item = (String)stringChoicesToUse.get(i);
			choicesArray[i] = new ChoiceItem(item,item);
		}
		setChoices(choicesArray);
	}

	public String getDetailsXml()
	{
		String xml = MartusXml.getTagStartWithNewline(DROPDOWN_SPEC_CHOICES_TAG);
		
		// skip fake/blank first entry
		for(int i = 1 ; i < getCount(); ++i)
		{
			xml += MartusXml.getTagStart(DROPDOWN_SPEC_CHOICE_TAG) +
					getValue(i) +
					MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICE_TAG);
		}
		xml += MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICES_TAG);
		return xml;
	}

	static class DropDownSpecLoader extends SimpleXmlVectorLoader
	{
		public DropDownSpecLoader(CustomDropDownFieldSpec spec)
		{
			super(DROPDOWN_SPEC_CHOICES_TAG, DROPDOWN_SPEC_CHOICE_TAG);
			this.spec = spec;
		}

		public void endDocument() throws SAXParseException
		{
			Vector stringChoices = getVector();
			spec.setChoices(stringChoices);
		}
		CustomDropDownFieldSpec spec;
	}
	
	public final static String EMPTY_FIRST_CHOICE = " ";
	public final static String DROPDOWN_SPEC_CHOICES_TAG = "Choices";
	public final static String DROPDOWN_SPEC_CHOICE_TAG = "Choice";
}
