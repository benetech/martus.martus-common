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
package org.martus.common;

import org.martus.common.fieldspec.ChoiceItem;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ListOfChoicesXmlLoader extends SimpleXmlDefaultLoader
{
	public ListOfChoicesXmlLoader(String tag)
	{
		super(tag);
		setOfChoices = new ReusableChoices();
	}

	@Override
	public void startDocument(Attributes attrs) throws SAXParseException
	{
		name = attrs.getValue(ATTRIBUTE_SET_OF_CHOICES_NAME);
		super.startDocument(attrs);
	}
	
	@Override
	public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
	{
		if(tag.equals(TAG_CHOICE))
			return new ChoiceItemXmlLoader(tag);

		return super.startElement(tag);
	}
	
	@Override
	public void endElement(String tag, SimpleXmlDefaultLoader ended)
			throws SAXParseException
	{
		if(tag.equals(TAG_CHOICE))
		{
			ChoiceItemXmlLoader loader = (ChoiceItemXmlLoader)ended;
			String code = loader.getCode();
			String label = loader.getLabel();
			ChoiceItem choice = new ChoiceItem(code, label);
			setOfChoices.add(choice);
		}
		super.endElement(tag, ended);
	}

	public String getName()
	{
		return name;
	}

	public ReusableChoices getSetOfChoices()
	{
		return setOfChoices;
	}
	
	private static String ATTRIBUTE_SET_OF_CHOICES_NAME = "name";
	private static String TAG_CHOICE = "Choice";

	private String name;
	private ReusableChoices setOfChoices;

}
