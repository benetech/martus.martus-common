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

package org.martus.common.clientside;

import org.martus.common.fieldspec.FieldSpec;

public class ChoiceItem implements Comparable
{
	public ChoiceItem(String codeToUse, String displayToUse)
	{
		this(codeToUse, displayToUse, FieldSpec.TYPE_UNKNOWN);
	}
	
	public ChoiceItem(FieldSpec specToUse)
	{
		this(specToUse.getTag(), specToUse.getLabel(), specToUse.getType());
	}
	
	public ChoiceItem(String codeToUse, String displayToUse, int typeToUse)
	{
		code = codeToUse;
		display = displayToUse;
		type = typeToUse;
	}

	public String toString()
	{
		return display;
	}

	public String getCode()
	{
		return code;
	}
	
	public int getType()
	{
		return type;
	}

	public int compareTo(Object other)
	{
		return toString().compareTo(other.toString());
	}

	private String code;
	private String display;
	private int type;

}

