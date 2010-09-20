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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class PoolOfReusableChoicesLists
{
	public PoolOfReusableChoicesLists()
	{
		namedReusableChoices = new HashMap();
	}
	
	public void add(ReusableChoices choices)
	{
		namedReusableChoices.put(choices.getCode(), choices);
	}

	public void addAll(PoolOfReusableChoicesLists reusableChoicesLists)
	{
		Set otherNames = reusableChoicesLists.getAvailableNames();
		Iterator it = otherNames.iterator();
		while(it.hasNext())
		{
			String name = (String)it.next();
			add(reusableChoicesLists.getChoices(name));
		}
	}

	public Object size()
	{
		return namedReusableChoices.size();
	}

	public Set getAvailableNames()
	{
		return namedReusableChoices.keySet();
	}

	public ReusableChoices getChoices(String name)
	{
		return (ReusableChoices)namedReusableChoices.get(name);
	}

	public String toXml() throws Exception
	{
		StringBuffer xml = new StringBuffer();
		
		Set reusableChoicesListNames = getAvailableNames();
		Iterator iter = reusableChoicesListNames.iterator();
		while(iter.hasNext())
		{
			String name = (String)iter.next();
			ReusableChoices choices = getChoices(name);
			xml.append(choices.toExportedXml());
		}
		
		return xml.toString();
	}
	
	public static final PoolOfReusableChoicesLists EMPTY_POOL = new PoolOfReusableChoicesLists();

	private Map namedReusableChoices;

}
