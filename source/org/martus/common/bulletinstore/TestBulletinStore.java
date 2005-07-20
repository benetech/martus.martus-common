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

package org.martus.common.bulletinstore;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipFile;

import org.martus.common.LoggerToNull;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinLoader;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.bulletin.Bulletin.DamagedBulletinException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.crypto.MartusCrypto.DecryptionException;
import org.martus.common.crypto.MartusCrypto.NoKeyPairException;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.MockClientDatabase;
import org.martus.common.database.Database.RecordHiddenException;
import org.martus.common.database.ReadableDatabase.PacketVisitor;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongAccountException;
import org.martus.common.test.MockBulletinStore;
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
		store.setSignatureGenerator(security);

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
    
    public void testLeafKeyCache() throws Exception
	{
    	Bulletin one = createAndSaveBulletin();
    	store.saveBulletinForTesting(one);
       	assertEquals("Leaf Keys should be 1?", 1, store.scanForLeafKeys().size());
       	assertEquals("NonLeaf uid should be 0?", 0, store.getNonLeafUids().size());
       	Bulletin clone = createAndSaveClone(one);
    	assertEquals("not just clone?", 1, store.scanForLeafKeys().size());
       	assertEquals("NonLeaf uid should be 1?", 1, store.getNonLeafUids().size());
       	Bulletin clone2 = createAndSaveClone(clone);
    	assertEquals("not just clone2?", 1, store.scanForLeafKeys().size());
       	assertEquals("NonLeaf uid should be 2?", 2, store.getNonLeafUids().size());
    	store.deleteBulletinRevisionFromDatabase(clone2.getBulletinHeaderPacket());
      	
    	store.deleteBulletinRevisionFromDatabase(clone.getBulletinHeaderPacket());
    	assertEquals("didn't delete?", 1, store.scanForLeafKeys().size());
       	assertEquals("NonLeaf uid should be 0 after delete?", 0, store.getNonLeafUids().size());
    	
    	File tempZip = createTempFile();
    	store.saveBulletinForTesting(one);
    	BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(store.getDatabase(), one.getDatabaseKey(), tempZip, store.getSignatureVerifier());
    	store.deleteBulletinRevision(one.getDatabaseKey());
    	assertEquals("not ready for import?", 0, store.scanForLeafKeys().size());
    	store.importBulletinZipFile(new ZipFile(tempZip));
    	assertEquals("didn't import?", 1, store.scanForLeafKeys().size());
    	tempZip.delete();

    	Vector toHide = new Vector();
    	toHide.add(one.getUniversalId());
    	store.hidePackets(toHide, new LoggerToNull());
    	assertEquals("didn't hide?", 0, store.scanForLeafKeys().size());
    	
    	store.saveBulletinForTesting(clone);
    	store.deleteAllData();
    	assertEquals("didn't delete all?", 0, store.scanForLeafKeys().size());
	}
    
    public void testHasNewerRevision() throws Exception
	{
		Bulletin original = createAndSaveBulletin();
		Bulletin clone = createAndSaveClone(original);
		
		assertFalse("has newer than the clone?", store.hasNewerRevision(clone.getUniversalId()));
		assertTrue("didn't find the clone?", store.hasNewerRevision(original.getUniversalId()));
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
    
    public void testRemoveBulletinWithIncompleteHistory() throws Exception
	{
		Bulletin original = createAndSaveBulletin();
		Bulletin version1 = createAndSaveClone(original);
		Bulletin version2 = createAndSaveClone(version1);
		Bulletin version3 = createAndSaveClone(version2);
		store.removeBulletinFromStore(version3);
		store.saveBulletinForTesting(version3);
		store.removeBulletinFromStore(version3);
	}

	public void testGetBulletinCount() throws Exception
	{
		Bulletin original = createAndSaveBulletin();
		createAndSaveClone(original);
		assertEquals(1, store.getBulletinCount());
	}
	
    

	public void testGetAllBulletinUids() throws Exception
	{
		TRACE("testGetAllBulletinUids");
		Vector empty = store.getAllBulletinLeafUids();
		assertEquals("not empty?", 0, empty.size());

		Bulletin b = createAndSaveBulletin();
		Vector one = store.getAllBulletinLeafUids();
		assertEquals("not one?", 1, one.size());
		UniversalId gotUid = (UniversalId)one.get(0);
		UniversalId bUid = b.getUniversalId();
		assertEquals("wrong uid 1?", bUid, gotUid);

		Bulletin b2 = createAndSaveBulletin();
		Vector two = store.getAllBulletinLeafUids();
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
	
	public void testVisitAllBulletins() throws Exception
	{
		Bulletin original1 = createAndSaveBulletin();
		Bulletin clone1 = createAndSaveClone(original1);
		
		Bulletin original2 = createAndSaveBulletin();
		Bulletin clone2a = createAndSaveClone(original2);
		Bulletin clone2b = createAndSaveClone(original2);
		Bulletin clone2bx = createAndSaveClone(clone2b);
		
		class SimpleCollector implements PacketVisitor
		{
			public void visit(DatabaseKey key)
			{
				result.add(key.getLocalId());
			}

			Vector result = new Vector();
		}
		
		SimpleCollector collector = new SimpleCollector();
		store.visitAllBulletins(collector);
		
		assertEquals(3, collector.result.size());
		assertContains(clone1.getLocalId(), collector.result);
		assertContains(clone2a.getLocalId(), collector.result);
		assertContains(clone2bx.getLocalId(), collector.result);
		
	}

	public void testImportBulletinPacketsFromZipFileToDatabase() throws Exception
	{
		MartusCrypto authorSecurity = MockMartusSecurity.createClient();
		BulletinStore fromStore = new MockBulletinStore(this);
		Bulletin b = new Bulletin(authorSecurity);
		b.setAllPrivate(false);
		b.setSealed();
		fromStore.saveBulletinForTesting(b);
		
		File destFile = createTempFile();
		DatabaseKey key = DatabaseKey.createSealedKey(b.getUniversalId());
		BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(fromStore.getDatabase(), key, destFile, authorSecurity);
		ZipFile zip = new ZipFile(destFile);
		
		BulletinStore hqStore = new MockBulletinStore(this);
		hqStore.setSignatureGenerator(MockMartusSecurity.createHQ());
		verifyImportZip(hqStore, key, zip);
		hqStore.deleteAllData();
		
		BulletinStore otherStore = new MockBulletinStore(this);
		otherStore.setSignatureGenerator(MockMartusSecurity.createOtherClient());
		verifyImportZip(otherStore, key, zip);
		otherStore.deleteAllData();

		verifyImportZip(store, key, zip);
	}

	private void verifyImportZip(BulletinStore store, DatabaseKey key, ZipFile zip) throws IOException, RecordHiddenException, InvalidPacketException, SignatureVerificationException, WrongAccountException, DecryptionException, DamagedBulletinException, NoKeyPairException
	{
		store.importBulletinZipFile(zip);
		BulletinLoader.loadFromDatabase(store.getDatabase(), key, store.getSignatureGenerator());
	}
	
	private void verifyCloneIsLeaf(Bulletin original, Bulletin clone, UniversalId otherUid) throws IOException, CryptoException
	{
		original.setHistory(new BulletinHistory());
		store.saveBulletinForTesting(original);

		BulletinHistory history = new BulletinHistory();
		history.add(original.getLocalId());
		clone.setHistory(history);
		store.saveBulletinForTesting(clone);

		Vector leafKeys = store.scanForLeafKeys();
		assertEquals("wrong leaf count?", 2, leafKeys.size());
		Vector nonLeafKeys = store.getNonLeafUids();
		assertEquals("wrong nonleaf count?", 1, nonLeafKeys.size());
		assertContains("missing clone?", DatabaseKey.createSealedKey(clone.getUniversalId()), leafKeys);
		assertContains("missing other?", DatabaseKey.createSealedKey(otherUid), leafKeys);
		assertContains("Original uid not in nonleaf?", original.getUniversalId(), nonLeafKeys);
	}

	private Bulletin createAndSaveBulletin() throws IOException, CryptoException
	{
		Bulletin b = new Bulletin(security);
		store.saveBulletinForTesting(b);
		return b;
	}

	private Bulletin createAndSaveClone(Bulletin original) throws IOException, CryptoException
	{
		if(original.getFieldDataPacket().getAttachments().length > 0)
			fail("Not tested for attachments!");
		if(original.getPrivateFieldDataPacket().getAttachments().length > 0)
			fail("Not tested for attachments!");
		Bulletin clone = new Bulletin(security);
		BulletinHistory history = new BulletinHistory();
		history.add(original.getLocalId());
		clone.setHistory(history);
		store.saveBulletinForTesting(clone);
		return clone;
	}


	private static BulletinStore store;
	private static MockMartusSecurity security;
	private static MockClientDatabase db;

	private static File tempFile1;
	private static final byte[] sampleBytes1 = {1,1,2,3,0,5,7,11};
}
