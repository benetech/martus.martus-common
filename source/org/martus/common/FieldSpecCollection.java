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
import java.util.Vector;

import org.martus.common.fieldspec.FieldSpec;

public class FieldSpecCollection implements Comparable
{
	public FieldSpecCollection(FieldSpec[] specsToUse)
	{
		specs = new Vector(Arrays.asList(specsToUse));
		reusableChoicesPool = new PoolOfReusableChoicesLists();
	}
	
	public FieldSpecCollection()
	{
		this(new FieldSpec[0]);
	}
	
	public int size()
	{
		return specs.size();
	}
	
	public void add(FieldSpec spec)
	{
		specs.add(spec);
	}
	
	public FieldSpec get(int index)
	{
		return (FieldSpec) specs.get(index);
	}
	
	public Set asSet() 
	{
		return new HashSet(specs);
	}
	
	public void setReusableDropdownChoices(PoolOfReusableChoicesLists dropdownChoices)
	{
		reusableChoicesPool = dropdownChoices;
	}

	public Set getReusableChoiceNames()
	{
		return reusableChoicesPool.getAvailableNames();
	}

	public ReusableChoices getReusableChoices(String name)
	{
		return reusableChoicesPool.getChoices(name);
	}

	public FieldSpec[] asArray()
	{
		return (FieldSpec[]) specs.toArray(new FieldSpec[0]);
	}
	
	public int hashCode() 
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + FieldSpecCollection.hashCode(specs.toArray());
		return result;
	}

	private static int hashCode(Object[] array) 
	{
		final int PRIME = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
		}
		return result;
	}

	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FieldSpecCollection other = (FieldSpecCollection) obj;
		return (compareTo(other) == 0);
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
			FieldSpec thisFieldSpec = get(i);
			FieldSpec otherFieldSpec = other.get(i);
			int result = thisFieldSpec.compareTo(otherFieldSpec);
			if(result != 0)
				return result;
		}
		
		return 0;
	}
	
	private Vector specs;
	private PoolOfReusableChoicesLists reusableChoicesPool;
}
