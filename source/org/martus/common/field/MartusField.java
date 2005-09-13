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

package org.martus.common.field;

import org.martus.common.MiniLocalization;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldType;

public class MartusField
{
	public MartusField(FieldSpec specToUse)
	{
		spec = specToUse;
		setData(getDefaultValue());
	}
	
	public MartusField getSubField(String tag)
	{
		return null;
	}
	
	public String getTag()
	{
		return spec.getTag();
	}
	
	public String getLabel()
	{
		return spec.getLabel();
	}
	
	public FieldType getType()
	{
		return spec.getType();
	}
	
	public String getData()
	{
		if(data == null)
			return "";
		
		return data;
	}
	
	public String getSearchableData(MiniLocalization localization)
	{
		return getFieldSpec().convertStoredToDisplay(getData(), localization);
	}
	
	public void clearData()
	{
		data = null;
	}
	
	public FieldSpec getFieldSpec()
	{
		return spec;
	}
	
	public void setData(String newValue)
	{
		data = newValue;
	}
	
	public boolean contains(String value, MiniLocalization localization)
	{
		return (getSearchableData(localization).toLowerCase().indexOf(value.toLowerCase()) >= 0);
	}
	
	public int compareTo(String value, MiniLocalization localization)
	{
		return getData().trim().compareTo(value.trim());
	}
	
	public String toString()
	{
		return data;
	}
	
	private String getDefaultValue()
	{
		return spec.getDefaultValue();
	}
	

	FieldSpec spec;
	String data;
}
