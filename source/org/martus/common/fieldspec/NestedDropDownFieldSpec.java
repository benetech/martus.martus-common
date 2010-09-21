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

import org.martus.common.MartusXml;

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
	
	public String getDetailsXml()
	{
		StringBuffer details = new StringBuffer();
		for(int i = 0; i < levels.size(); ++i)
		{
			NestedDropdownLevel level = (NestedDropdownLevel) levels.get(i);
			details.append("<" + USE_REUSABLE_CHOICES_TAG + " ");
			details.append("code='" + level.getReusableChoicesCode() + "' />");
			details.append(MartusXml.getTagEnd(USE_REUSABLE_CHOICES_TAG));
		}
		
		return details.toString();
	}

	private Vector levels;
}
