/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006-2007, Beneficent
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
package org.martus.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.martus.common.fieldspec.FieldSpec;

public class FieldSpecCollection implements Comparable
{
	public FieldSpecCollection(FieldSpec[] specsToUse)
	{
		specs = specsToUse;
	}
	
	public FieldSpecCollection(int size)
	{
		this(new FieldSpec[size]);
	}
	
	public int size()
	{
		return specs.length;
	}
	
	public void set(int index, FieldSpec spec)
	{
		specs[index] = spec;
	}
	
	public FieldSpec get(int index)
	{
		return specs[index];
	}
	
	public Set asSet() 
	{
		return new HashSet(Arrays.asList(specs));
	}
	
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof FieldSpecCollection))
			return false;
		FieldSpecCollection other = (FieldSpecCollection)rawOther;
		return Arrays.equals(specs, other.specs);
	}
	
	public int hashCode()
	{
		return specs.hashCode();
	}
	
	public int compareTo(Object rawOther) 
	{
		if(!(rawOther instanceof FieldSpecCollection))
			return 0;
		FieldSpecCollection other = (FieldSpecCollection)rawOther;
		if(size() < other.size())
			return -1;
		else if(size() > other.size())
			return 1;
		
		for(int i = 0; i < size(); ++i)
		{
			int result = get(i).compareTo(other.get(i));
			if(result != 0)
				return result;
		}
		
		return 0;
	}

	FieldSpec[] specs;

}
