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

package org.martus.common.packet;

import java.util.Vector;


public class AccountId
{
	public static AccountId create(String accountId)
	{
		AccountId newId = new AccountId(accountId);
		int at = ids.indexOf(newId);
		if(at >= 0)
			return (AccountId)ids.get(at);
		ids.add(newId);
		return newId;
	}
	
	public int objectId()
	{
		return super.hashCode();
	}
	
	public String toString()
	{
		return id;
	}
	
	public boolean equals(Object obj)
	{
		return(toString().equals(obj.toString()));
	}

	public int hashCode()
	{
		return id.hashCode();
	}

	private AccountId(String accountId)
	{
		id = accountId;
	}
	
	private String id;
	
	private static Vector ids = new Vector();
}