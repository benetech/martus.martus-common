/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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


public class FieldSpec
{
	public FieldSpec(String thisFieldDescription, int typeToUse)
	{
		initializeFromDescription(thisFieldDescription);

		type = typeToUse;
	}

	public FieldSpec(String thisFieldDescription)
	{
		initializeFromDescription(thisFieldDescription);

		type = CustomFields.getStandardType(tag);
		if(type == TYPE_UNKNOWN && !hasUnknownStuff())
			type = TYPE_NORMAL;
	}

	private void initializeFromDescription(String thisFieldDescription)
	{
		tag = LegacyCustomFields.extractFieldSpecElement(thisFieldDescription, LegacyCustomFields.TAG_ELEMENT_NUMBER);
		label = LegacyCustomFields.extractFieldSpecElement(thisFieldDescription, LegacyCustomFields.LABEL_ELEMENT_NUMBER);
		String unknownStuff = LegacyCustomFields.extractFieldSpecElement(thisFieldDescription, LegacyCustomFields.UNKNOWN_ELEMENT_NUMBER);
		if(!unknownStuff.equals(""))
		{
			//System.out.println("FieldSpec.initializeFromDescription unknown: " + tag + ": " + unknownStuff);
			hasUnknown = true;
		}
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean hasUnknownStuff()
	{
		return hasUnknown;
	}

	String tag;
	int type;
	String label;
	boolean hasUnknown;

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_MULTILINE = 1;
	public static final int TYPE_DATE = 2;
	public static final int TYPE_CHOICE = 4;
	public static final int TYPE_DATERANGE = 5;
	public static final int TYPE_BOOLEAN = 6;
	public static final int TYPE_UNKNOWN = 99;

}
