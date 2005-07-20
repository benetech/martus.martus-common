/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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

import org.martus.common.BulletinStore;
import org.martus.common.HQKey;
import org.martus.common.HQKeys;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.database.MockClientDatabase;
import org.martus.util.TestCaseEnhanced;


public class TestBulletinStoreCache extends TestCaseEnhanced
{
	public TestBulletinStoreCache(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
	   	db = new MockClientDatabase();
    	security = MockMartusSecurity.createClient();
		store = new BulletinStore();
		store.doAfterSigninInitialization(createTempDirectory(), db);
		store.setSignatureGenerator(security);
		
	}

    public void tearDown() throws Exception
    {
    	assertEquals("Still some mock streams open?", 0, db.getOpenStreamCount());
		store.deleteAllData();
		super.tearDown();
	}
    
    public void testBasics() throws Exception
	{
    	Vector none = store.getFieldOffices("not a real account");
    	assertEquals(0, none.size());
    	
    	MartusCrypto hqSecurity = MockMartusSecurity.createHQ();
		Bulletin b = new Bulletin(security);
		b.addAuthorizedToReadKeys(new HQKeys(new HQKey(hqSecurity.getPublicKeyString())));
		store.saveBulletinForTesting(b);
    	Vector one = store.getFieldOffices(hqSecurity.getPublicKeyString());
    	assertEquals(1, one.size());
    	assertEquals(security.getPublicKeyString(), one.get(0));
    	
    	MartusCrypto hqOther = MockMartusSecurity.createOtherClient();
    	Bulletin b2 = new Bulletin(security);
    	HQKeys twoHqs = new HQKeys();
    	twoHqs.add(new HQKey(hqSecurity.getPublicKeyString()));
    	twoHqs.add(new HQKey(hqOther.getPublicKeyString()));
		b2.addAuthorizedToReadKeys(twoHqs);
		store.saveBulletinForTesting(b2);
		
    	Vector stillOne = store.getFieldOffices(hqSecurity.getPublicKeyString());
    	assertEquals(1, stillOne.size());
    	assertEquals(security.getPublicKeyString(), stillOne.get(0));
    	
    	Vector otherHqHasOne = store.getFieldOffices(hqOther.getPublicKeyString());
    	assertEquals(1, otherHqHasOne.size());
    	assertEquals(security.getPublicKeyString(), otherHqHasOne.get(0));
		
	}
   
    public void testIsCacheValid()
    {
    	BulletinStore.LeafNodeCache cache = new BulletinStore.LeafNodeCache(store);
    	assertFalse("cache already valid?", cache.isCacheValid());
    	
    	cache.getLeafKeys();
    	assertTrue("get leaf didn't fill cache?", cache.isCacheValid());
    	cache.clear();
    	assertFalse("clear didn't work?", cache.isCacheValid());
    	
    	cache.getNonLeafUids();
    	assertTrue("get nonleaf didn't fill cache?", cache.isCacheValid());
    	cache.clear();
    	
    	cache.getFieldOffices("test");
    	assertTrue("get fo's didn't fill cache?", cache.isCacheValid());

    }
    
	private static BulletinStore store;
	private static MockMartusSecurity security;
	private static MockClientDatabase db;
}
