/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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

import org.json.JSONObject;


public class MiniFieldSpec implements Comparable
{
	public MiniFieldSpec(FieldSpec basedOn)
	{
		tag = basedOn.getTag();
		label = basedOn.getLabel();
		type = basedOn.getType();
	}
	
	public MiniFieldSpec(JSONObject json)
	{
		tag = json.getString(TAG_TAG);
		label = json.getString(TAG_LABEL);
		type = FieldType.createFromTypeName(json.getString(TAG_TYPE));
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public FieldType getType()
	{
		return type;
	}

	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof MiniFieldSpec))
			return false;
			
		return compareTo((MiniFieldSpec)rawOther) == 0;
	}

	public int hashCode()
	{
		return tag.hashCode();
	}
	
	public int compareTo(Object rawOther)
	{
		if(rawOther == null)
			throw new NullPointerException();
		
		if(! (rawOther instanceof MiniFieldSpec))
			return 0;
		
		return compareTo((MiniFieldSpec)rawOther);
	}
	
	public int compareTo(MiniFieldSpec other)
	{
		int labelResult = label.compareTo(other.label);
		if(labelResult != 0)
			return labelResult;
		
		int tagResult = tag.compareTo(other.tag);
		if(tagResult != 0)
			return tagResult;
		
		int typeResult = type.getTypeName().compareTo(other.type.getTypeName());
		if(typeResult != 0)
			return typeResult;
		
		return 0;
	}
	
	public String toString()
	{
		return label + "(" + FieldSpec.getTypeString(type) + ", " + tag + ")";
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		json.put(TAG_TAG, tag);
		json.put(TAG_LABEL, label);
		json.put(TAG_TYPE, type.getTypeName());
		return json;
	}
	
	private static final String TAG_TAG = "Tag";
	private static final String TAG_LABEL = "Label";
	private static final String TAG_TYPE = "Type";

	String tag;
	String label;
	FieldType type;
}
