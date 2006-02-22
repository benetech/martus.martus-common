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
package org.martus.common.bulletinstore;

import org.martus.common.database.DatabaseKey;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.UniversalId;
import org.martus.util.Stopwatch;
import org.martus.util.TestCaseEnhanced;

public class TestLeafNodeCache extends TestCaseEnhanced
{
	public TestLeafNodeCache(String name)
	{
		super(name);
	}

	public void testLeafNodeCacheSpeed()
	{
		BulletinStore store = new BulletinStore();
		LeafNodeCache cache = new LeafNodeCache(store);
		BulletinHistory history = new BulletinHistory();
		for(int i = 0; i < 10; ++i)
			history.add(UniversalId.createDummyUniversalId().getLocalId());
		
		Stopwatch watch = new Stopwatch();
		for(int i = 0; i < 500; ++i)
		{
			UniversalId uid = UniversalId.createDummyUniversalId();
			DatabaseKey key = DatabaseKey.createSealedKey(uid);
			cache.addToCachedLeafInformation(key, history);
		}
		long millisFor500 = watch.elapsed();

		for(int i = 0; i < 1000; ++i)
		{
			UniversalId uid = UniversalId.createDummyUniversalId();
			DatabaseKey key = DatabaseKey.createSealedKey(uid);
			cache.addToCachedLeafInformation(key, history);
		}
		long millisFor1000 = watch.elapsed();
		assertTrue("Took too long?", millisFor1000 < millisFor500 * 4);
	}
}
