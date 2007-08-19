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

import java.util.Map;
import java.util.Vector;

import org.martus.common.MartusXml;
import org.martus.util.xml.SimpleXmlMapLoader;
import org.martus.util.xml.SimpleXmlVectorLoader;
import org.xml.sax.SAXParseException;

public class CustomDropDownFieldSpec extends DropDownFieldSpec
{
	public void setChoices(Vector stringChoicesToUse)
	{
		boolean hasEmptyCode = false;
		Vector choices = new Vector();
		for(int i = 0; i < stringChoicesToUse.size(); i++)
		{
			String item = (String)stringChoicesToUse.get(i);
			choices.add(new ChoiceItem(item,item));
			if(item.length() == 0)
				hasEmptyCode = true;
		}
		
		if(!hasEmptyCode)
			choices.insertElementAt(new ChoiceItem("", ""), 0);
		
		setChoices((ChoiceItem[])choices.toArray(new ChoiceItem[0]));
	}

	public String getDefaultValue()
	{
		return "";
	}
	
	
	public String getDetailsXml()
	{
		String xml = super.getDetailsXml();
		if(getDataSourceGridTag() != null)
		{
			String ourXml = MartusXml.getTagStartWithNewline(DROPDOWN_SPEC_DATA_SOURCE) + 
				MartusXml.getTagStart(DROPDOWN_SPEC_DATA_SOURCE_GRID_TAG_TAG) + 
				getDataSourceGridTag() + 
				MartusXml.getTagEnd(DROPDOWN_SPEC_DATA_SOURCE_GRID_TAG_TAG) +

				MartusXml.getTagStart(DROPDOWN_SPEC_DATA_SOURCE_GRID_COLUMN_TAG) + 
				getDataSourceGridColumn() + 
				MartusXml.getTagEnd(DROPDOWN_SPEC_DATA_SOURCE_GRID_COLUMN_TAG) +
				
				MartusXml.getTagEnd(DROPDOWN_SPEC_DATA_SOURCE);
				
			xml += ourXml;
		}
		
		return xml;
	}

	public void setDataSource(String gridTagToUse, String gridColumnToUse)
	{
		gridTag = gridTagToUse;
		gridColumn = gridColumnToUse;
		updateDetailsXml();
	}

	public Object getDataSource()
	{
		if(getDataSourceGridTag() == null || getDataSourceGridColumn() == null)
			return null;
		
		return getDataSourceGridTag() + "." + getDataSourceGridColumn();
	}

	public String getDataSourceGridTag()
	{
		return gridTag;
	}
	
	public String getDataSourceGridColumn()
	{
		return gridColumn;
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
	
	static class DropDownDataSourceLoader extends SimpleXmlMapLoader
	{
		public DropDownDataSourceLoader(CustomDropDownFieldSpec spec)
		{
			super(DROPDOWN_SPEC_DATA_SOURCE);
			this.spec = spec;
		}

		public void endDocument() throws SAXParseException
		{
			Map map = getMap();
			String gridTag = (String)map.get(CustomDropDownFieldSpec.DROPDOWN_SPEC_DATA_SOURCE_GRID_TAG_TAG);
			String gridColumn = (String)map.get(CustomDropDownFieldSpec.DROPDOWN_SPEC_DATA_SOURCE_GRID_COLUMN_TAG);
			spec.setDataSource(gridTag, gridColumn);
		}

		CustomDropDownFieldSpec spec;
	}

	public static final String DROPDOWN_SPEC_DATA_SOURCE_GRID_TAG_TAG = "GridFieldTag";
	public static final String DROPDOWN_SPEC_DATA_SOURCE_GRID_COLUMN_TAG = "GridColumnLabel";

	String gridTag;
	String gridColumn;
}
