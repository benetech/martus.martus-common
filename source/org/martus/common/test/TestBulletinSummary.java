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

package org.martus.common.test;

import java.util.Vector;

import org.martus.common.BulletinSummary;
import org.martus.common.network.NetworkInterfaceConstants;

import junit.framework.TestCase;


public class TestBulletinSummary extends TestCase
{

	public TestBulletinSummary(String name)
	{
		super(name);
	}

	public void testGetNormalRetrieveTags() throws Exception
	{
		Vector tags = BulletinSummary.getNormalRetrieveTags();
		assertEquals(3, tags.size());
		assertEquals(NetworkInterfaceConstants.TAG_BULLETIN_SIZE, tags.get(0));
		assertEquals(NetworkInterfaceConstants.TAG_BULLETIN_DATE_SAVED, tags.get(1));
		assertEquals(NetworkInterfaceConstants.TAG_BULLETIN_HISTORY, tags.get(2));
	}
}
