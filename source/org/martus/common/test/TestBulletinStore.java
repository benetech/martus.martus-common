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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.martus.common.BulletinStore;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinSaver;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.MockClientDatabase;
import org.martus.common.packet.UniversalId;
import org.martus.util.Stopwatch;
import org.martus.util.TestCaseEnhanced;


public class TestBulletinStore extends TestCaseEnhanced
{
	static Stopwatch sw = new Stopwatch();
	
    public TestBulletinStore(String name) {
        super(name);
    }

	public void TRACE(String text)
	{
		//System.out.println("before " + text + ": " + sw.elapsed());
		sw.start();
	}


    public void setUp() throws Exception
    {
    	super.setUp();
    	db = new MockClientDatabase();
    	security = MockMartusSecurity.createClient();
		store = new BulletinStore();
		store.doAfterSigninInitialization(createTempDirectory(), db);

    	if(tempFile1 == null)
    	{
			tempFile1 = createTempFileWithData(sampleBytes1);
    	}
    }

    public void tearDown() throws Exception
    {
    	assertEquals("Still some mock streams open?", 0, db.getOpenStreamCount());
		store.deleteAllData();
		super.tearDown();
	}
    
    public void testRemoveBulletinFromStore() throws Exception
	{
    	Bulletin unrelated = createAndSaveBulletin();
		assertEquals("didn't create unrelated bulletin?", 1, store.getBulletinCount());

		Bulletin original = createAndSaveBulletin();
		Bulletin clone = createAndSaveClone(original);
		store.removeBulletinFromStore(clone);
		assertEquals("didn't delete clone and ancestor?", 1, store.getBulletinCount());
		
		store.removeBulletinFromStore(unrelated);
		assertEquals("didn't delete unrelated?", 0, store.getBulletinCount());
	}

	public void testGetBulletinCount() throws Exception
	{
		Bulletin original = createAndSaveBulletin();
		createAndSaveClone(original);
		assertEquals(1, store.getBulletinCount());
	}
	
    

	private Bulletin createAndSaveClone(Bulletin original) throws IOException, CryptoException
	{
		if(original.getFieldDataPacket().getAttachments().length > 0)
			fail("Not tested for attachments!");
		if(original.getPrivateFieldDataPacket().getAttachments().length > 0)
			fail("Not tested for attachments!");
		Bulletin clone = new Bulletin(security);
		Vector history = new Vector();
		history.add(original.getLocalId());
		clone.setHistory(history);
		BulletinSaver.saveToClientDatabase(clone, db, false, security);
		return clone;
	}

	public void testGetAllBulletinUids() throws Exception
	{
		TRACE("testGetAllBulletinUids");
		Vector empty = store.getAllBulletinUids();
		assertEquals("not empty?", 0, empty.size());

		Bulletin b = createAndSaveBulletin();
		Vector one = store.getAllBulletinUids();
		assertEquals("not one?", 1, one.size());
		UniversalId gotUid = (UniversalId)one.get(0);
		UniversalId bUid = b.getUniversalId();
		assertEquals("wrong uid 1?", bUid, gotUid);

		Bulletin b2 = createAndSaveBulletin();
		Vector two = store.getAllBulletinUids();
		assertEquals("not two?", 2, two.size());
		assertTrue("missing 1?", two.contains(b.getUniversalId()));
		assertTrue("missing 2?", two.contains(b2.getUniversalId()));
	}

	public void testVisitAllBulletinRevisions() throws Exception
	{
		TRACE("testVisitAllBulletins");

		class BulletinUidCollector implements Database.PacketVisitor
		{
			BulletinUidCollector(BulletinStore store)
			{
				store.visitAllBulletinRevisions(this);
			}

			public void visit(DatabaseKey key)
			{
				uids.add(key.getUniversalId());
			}

			Vector uids = new Vector();
		}

		assertEquals("not empty?", 0, new BulletinUidCollector(store).uids.size());

		Bulletin b = createAndSaveBulletin();
		Vector one = new BulletinUidCollector(store).uids;
		assertEquals("not one?", 1, one.size());
		UniversalId gotUid = (UniversalId)one.get(0);
		UniversalId bUid = b.getUniversalId();
		assertEquals("wrong uid 1?", bUid, gotUid);

		Bulletin b2 = createAndSaveBulletin();
		Vector two = new BulletinUidCollector(store).uids;
		assertEquals("not two?", 2, two.size());
		assertTrue("missing 1?", two.contains(b.getUniversalId()));
		assertTrue("missing 2?", two.contains(b2.getUniversalId()));
	}

	public void testScanForLeafUids() throws Exception
	{
		Bulletin other = createAndSaveBulletin();
		
		Bulletin one = new Bulletin(security);
		one.setSealed();

		Bulletin two = new Bulletin(security);
		two.setSealed();
		
		verifyCloneIsLeaf(one, two, other.getUniversalId());
		verifyCloneIsLeaf(two, one, other.getUniversalId());
	}

	private void verifyCloneIsLeaf(Bulletin original, Bulletin clone, UniversalId otherUid) throws IOException, CryptoException
	{
		original.setHistory(new Vector());
		BulletinSaver.saveToClientDatabase(original, db, false, security);

		Vector history = new Vector();
		history.add(original.getLocalId());
		clone.setHistory(history);
		BulletinSaver.saveToClientDatabase(clone, db, false, security);

		Vector leafUids = store.scanForLeafUids();
		assertEquals("wrong leaf count?", 2, leafUids.size());
		assertContains("missing clone?", clone.getUniversalId(), leafUids);
		
		assertContains("missing other?", otherUid, leafUids);
	}

	private Bulletin createAndSaveBulletin() throws IOException, CryptoException
	{
		Bulletin b = new Bulletin(security);
		BulletinSaver.saveToClientDatabase(b, db, false, security);
		return b;
	}


	private static BulletinStore store;
	private static MockMartusSecurity security;
	private static MockClientDatabase db;

	private static File tempFile1;
	private static final byte[] sampleBytes1 = {1,1,2,3,0,5,7,11};
}
