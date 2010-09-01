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
package org.martus.common.fieldspec;

import java.util.Vector;

import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class NestedDropDownFieldSpec extends FieldSpec
{
	public NestedDropDownFieldSpec()
	{
		super(new FieldTypeNestedDropdown());
		levels = new Vector();
	}

	public int getLevelCount()
	{
		return levels.size();
	}

	public NestedDropdownLevel getLevel(int i)
	{
		return (NestedDropdownLevel) levels.get(i);
	}

	public void addLevel(NestedDropdownLevel newLevel)
	{
		levels.add(newLevel);
	}

	public static class LevelsXmlLoader extends SimpleXmlDefaultLoader
	{
		public LevelsXmlLoader(NestedDropDownFieldSpec nestedDropDownSpecToFill)
		{
			super(LEVELS_TAG);
			nestedDropDownSpec = nestedDropDownSpecToFill;
		}
		
		@Override
		public SimpleXmlDefaultLoader startElement(String tag)
				throws SAXParseException
		{
			if(tag.equals(LEVEL_TAG))
				return new LevelLoader(tag);
			return super.startElement(tag);
		}

		@Override
		public void endElement(String tag, SimpleXmlDefaultLoader ended)
				throws SAXParseException
		{
			if(tag.equals(LEVEL_TAG))
			{
				LevelLoader loader = (LevelLoader)ended;
				String label = loader.getLabel();
				String choicesName = loader.getChoicesName();
				NestedDropdownLevel newLevel = new NestedDropdownLevel(label, choicesName);
				nestedDropDownSpec.addLevel(newLevel);
			}
			else
				super.endElement(tag, ended);
		}
		
		private NestedDropDownFieldSpec nestedDropDownSpec;
	}
	
	private static class LevelLoader extends SimpleXmlDefaultLoader
	{
		public LevelLoader(String tag)
		{
			super(tag);
		}

		@Override
		public void startDocument(Attributes attrs) throws SAXParseException
		{
			super.startDocument(attrs);
			label = attrs.getValue(LEVEL_LABEL_ATTRIBUTE_NAME);
			choicesName = attrs.getValue(LEVEL_CHOICES_ATTRIBUTE_NAME);
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public String getChoicesName()
		{
			return choicesName;
		}
		
		private String label;
		private String choicesName;
	}

	public static final String LEVELS_TAG = "Levels";
	public static final String LEVEL_TAG = "Level";
	public static final String LEVEL_LABEL_ATTRIBUTE_NAME = "label";
	public static final String LEVEL_CHOICES_ATTRIBUTE_NAME = "choices";
	
	private Vector levels;

}
