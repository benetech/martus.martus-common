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

import java.io.File;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ServerFileDatabase;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.UniversalId;
import org.martus.util.Stopwatch;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.UnicodeStringWriter;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;

public class TestLeafNodeCache extends TestCaseEnhanced
{
	public TestLeafNodeCache(String name)
	{
		super(name);
	}

	public void testLeafNodeCacheSpeed()
	{
		if(!DO_SPEED_TESTS)
			return;
		
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
	
	public void testLeafNodeCacheDiskSpeed() throws Exception
	{
		if(!DO_SPEED_TESTS)
			return;
		
		BulletinStore store = new BulletinStore();
		File tempDirectory = createTempDirectory();
		MartusSecurity security = MockMartusSecurity.createServer();
		ServerFileDatabase db = new ServerFileDatabase(tempDirectory, security);
		store.doAfterSigninInitialization(tempDirectory, db);
		
		MartusSecurity clientSecurity = MockMartusSecurity.createClient();
		Stopwatch watch = new Stopwatch();
		for(int i = 0; i < 100; ++i)
		{
			Bulletin testBulletin = new Bulletin(clientSecurity);
			store.saveBulletinForTesting(testBulletin);
		}
//		System.out.println("Time to create bulletins: " + watch.elapsed());

		watch.start();
		LeafNodeCache cache = new LeafNodeCache(store);
		assertFalse("cache not flushed?", cache.isCacheValid());
		cache.getLeafKeys();
		long buildCacheTime = watch.elapsed();
//		System.out.println("Time for initial cache build: " + buildCacheTime);
		assertTrue("Leaf key cache  build too slow? (was " + buildCacheTime + ")", buildCacheTime < 1000);
		
		watch.start();
		cache.getLeafKeys();
		long queryCacheTime = watch.elapsed();
//		System.out.println("Time to query using the cache: " + queryCacheTime);
		assertTrue("Time to query using cache too slow? (was " + queryCacheTime + ")", queryCacheTime < 100);
		
		Bulletin b = new Bulletin(clientSecurity);
		UnicodeStringWriter writer = UnicodeStringWriter.create();
		watch.start();
		b.getBulletinHeaderPacket().writeXml(writer, clientSecurity);
//		long timeToWrite = watch.elapsed();
//		System.out.println(timeToWrite);
		String xml = writer.toString();
		
		BulletinHeaderPacket bhp = new BulletinHeaderPacket();
		watch.start();
		for(int i = 0; i < 1000; ++i)
		{
			StringInputStreamWithSeek in = new StringInputStreamWithSeek(xml);
			try
			{
				bhp.loadFromXml(in, null, clientSecurity);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw(e);
			}
		}
		long readHeadersTime = watch.elapsed();
//		System.out.println("Time to read header packets: " + readHeadersTime);
		assertTrue("Read packets too slow? (was " + readHeadersTime + ")", readHeadersTime < 10000);
	}
	
	static final boolean DO_SPEED_TESTS = false;
}
