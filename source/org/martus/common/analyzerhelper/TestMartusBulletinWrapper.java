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
package org.martus.common.analyzerhelper;

import java.io.File;

import org.martus.common.HQKey;
import org.martus.common.HQKeys;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.test.MockBulletinStore;
import org.martus.util.TestCaseEnhanced;


public class TestMartusBulletinWrapper extends TestCaseEnhanced
{
	public TestMartusBulletinWrapper(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
	  	super.setUp();
	  	if(security == null)
	  	{
			security = new MartusSecurity();
			security.createKeyPair(512);
			fosecurity = new MartusSecurity();
			fosecurity.createKeyPair(512);
	  	}
	}
	
	public void testBasics() throws Exception
	{
		Bulletin bulletin = new Bulletin(security);
		String author = "author";
		String title = "title";
		String location = "location";
		String privateData = "private";
		bulletin.set(BulletinConstants.TAGAUTHOR, author);
		bulletin.set(BulletinConstants.TAGTITLE, title);
		bulletin.set(BulletinConstants.TAGLOCATION, location);
		bulletin.set(BulletinConstants.TAGPRIVATEINFO, privateData);
		
		File tempDirectory = createTempFileFromName("$$$TestBulletinWrapper");
		tempDirectory.deleteOnExit();
		tempDirectory.delete();
		tempDirectory.mkdirs();

		MockBulletinStore store = new MockBulletinStore(this);
		store.saveEncryptedBulletinForTesting(bulletin);
		File bulletinZipFile = createTempFileFromName("$$$TestBulletinWrapperZipFile");
		BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(store.getDatabase(), bulletin.getDatabaseKey(), bulletinZipFile, security);
		
		MartusBulletinWrapper bulletinWrapper = new MartusBulletinWrapper(bulletin.getUniversalId(), bulletinZipFile, security);
		assertEquals("Data for author not correct?", author, bulletinWrapper.getAuthor());
		assertEquals("Data for title not correct?", title, bulletinWrapper.getTitle());
		assertEquals("Data for location not correct?", location, bulletinWrapper.getLocation());
		assertEquals("PrivateData not visible?", privateData, bulletinWrapper.getPrivateInfo());
		bulletinZipFile.delete();
		store.deleteAllData();
	}
	
	public void testHQAuthorized() throws Exception
	{
		Bulletin bulletin = new Bulletin(fosecurity);
		String author = "author";
		String title = "title";
		String location = "location";
		String privateData = "private";
		String entryDate = "2004-01-23";
		
		bulletin.set(BulletinConstants.TAGAUTHOR, author);
		bulletin.set(BulletinConstants.TAGTITLE, title);
		bulletin.set(BulletinConstants.TAGLOCATION, location);
		bulletin.set(BulletinConstants.TAGPRIVATEINFO, privateData);
		bulletin.set(BulletinConstants.TAGENTRYDATE, entryDate);
		bulletin.set(BulletinConstants.TAGEVENTDATE, "2003-08-20,20030820+3");
		
		HQKey key = new HQKey(security.getPublicKeyString());
		HQKeys keys = new HQKeys(key);
		bulletin.setAuthorizedToReadKeys(keys);
		
		
		File tempDirectory = createTempFileFromName("$$$TestBulletinHQWrapper");
		tempDirectory.deleteOnExit();
		tempDirectory.delete();
		tempDirectory.mkdirs();

		MockBulletinStore store = new MockBulletinStore(this);
		store.saveEncryptedBulletinForTesting(bulletin);
		File bulletinZipFile = createTempFileFromName("$$$TestBulletinWrapperHQZipFile");
		BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(store.getDatabase(), bulletin.getDatabaseKeyForLocalId(bulletin.getLocalId()), bulletinZipFile, fosecurity);
		
		MartusBulletinWrapper bulletinWrapper = new MartusBulletinWrapper(bulletin.getUniversalId(), bulletinZipFile, security);
		assertEquals("Data for author not correct?", author, bulletinWrapper.getAuthor());
		assertEquals("Data for title not correct?", title, bulletinWrapper.getTitle());
		assertEquals("Data for location not correct?", location, bulletinWrapper.getLocation());
		assertEquals("PrivateData not visible?", privateData, bulletinWrapper.getPrivateInfo());
		assertEquals("Is All Private incorrect?", bulletin.isAllPrivate(), bulletinWrapper.isAllPrivate());
		assertEquals("Entry Date incorrect?", entryDate, Bulletin.getStoredDateFormat().format(bulletinWrapper.getEntryDate()));
		assertEquals("Event Begin Date incorrect?", "2003-08-20", Bulletin.getStoredDateFormat().format(bulletinWrapper.getEventDate().getBeginDate()));
		assertEquals("Event End Date incorrect?", "2003-08-23", Bulletin.getStoredDateFormat().format(bulletinWrapper.getEventDate().getEndDate()));
		bulletinZipFile.delete();
		store.deleteAllData();
	}
	private MartusSecurity security;
	private MartusSecurity fosecurity;
}
