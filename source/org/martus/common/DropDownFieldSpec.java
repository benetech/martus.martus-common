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
package org.martus.common;

import java.util.Vector;
import org.martus.util.xml.SimpleXmlVectorLoader;
import org.xml.sax.SAXParseException;



public class DropDownFieldSpec extends FieldSpec
{
	public DropDownFieldSpec()
	{
		super(TYPE_DROPDOWN);
		choices = new Vector();
	}
	
	public String getDefaultValue()
	{
		return EMPTY_FIRST_CHOICE;
	}

	public String getDetailsXml()
	{
		String xml = MartusXml.getTagStartWithNewline(DROPDOWN_SPEC_CHOICES_TAG);
		for(int i = 0 ; i < getCount(); ++i)
		{
			xml += MartusXml.getTagStart(DROPDOWN_SPEC_CHOICE_TAG) +
					get(i) +
					MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICE_TAG);
		}
		xml += MartusXml.getTagEnd(DROPDOWN_SPEC_CHOICES_TAG);
		return xml;
	}

	public void setChoices(Vector choicesToUse)
	{
		choices = choicesToUse;
	}
	
	public Vector getChoices()
	{
		return choices;
	}
	
	public int getCount()
	{
		return choices.size();
	}
	
	public String get(int index) throws ArrayIndexOutOfBoundsException 
	{
		return (String)choices.get(index);
	}
	
	static class DropDownSpecLoader extends SimpleXmlVectorLoader
	{
		public DropDownSpecLoader(DropDownFieldSpec spec)
		{
			super(DROPDOWN_SPEC_CHOICES_TAG, DROPDOWN_SPEC_CHOICE_TAG);
			this.spec = spec;
		}

		public void endDocument() throws SAXParseException
		{
			spec.setChoices(getVector());
		}
		DropDownFieldSpec spec;
	}
	
	private Vector choices;
	
	public final static String EMPTY_FIRST_CHOICE = " ";
	public final static String DROPDOWN_SPEC_CHOICES_TAG = "Choices";
	public final static String DROPDOWN_SPEC_CHOICE_TAG = "Choice";
}
