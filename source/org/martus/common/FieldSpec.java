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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FieldSpec
{
	public static FieldSpec createStandardField(String tagToUse, int typeToUse)
	{
		return new FieldSpec(tagToUse, "", typeToUse, false);
	}
	
	FieldSpec(String tagToUse, String labelToUse, int typeToUse, boolean hasUnknownToUse)
	{
		tag = tagToUse;
		label = labelToUse;
		type = typeToUse;
		hasUnknown = hasUnknownToUse;
	}
	
	public String toString()
	{
		return "<Field><Tag>" + getTag() + 
				"</Tag><Label>" + getLabel() + 
				"</Label><Type>" + getTypeString(getType()) +
				"</Type></Field>";
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
	
	public static String getTypeString(int type)
	{
		Map map = getTypeCodesAndStrings();
		if(!map.containsKey(new Integer(type)))
			type = TYPE_UNKNOWN;
		String value = (String)map.get(new Integer(type));
		return value;		 
	}
	
	public static int getTypeCode(String type)
	{
		Map map = getTypeCodesAndStrings();
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry)iter.next();
			String value = (String)entry.getValue();
			if(value.equals(type))
				return ((Integer)entry.getKey()).intValue();
		} 
		return TYPE_UNKNOWN;
	}
	
	private static Map getTypeCodesAndStrings()
	{
		HashMap map = new HashMap();
		map.put(new Integer(TYPE_NORMAL), "STRING");
		map.put(new Integer(TYPE_MULTILINE), "MULTILINE");
		map.put(new Integer(TYPE_DATE), "DATE");
		map.put(new Integer(TYPE_DATERANGE), "DATERANGE");
		map.put(new Integer(TYPE_BOOLEAN), "BOOLEAN");
		map.put(new Integer(TYPE_CHOICE), "SINGLECHOICE");
		map.put(new Integer(TYPE_UNKNOWN), "UNKNOWN");
		return map;
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
