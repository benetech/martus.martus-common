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
import java.util.zip.ZipFile;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinLoader;
import org.martus.common.bulletin.BulletinSaver;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.bulletin.Bulletin.DamagedBulletinException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.crypto.MartusCrypto.DecryptionException;
import org.martus.common.crypto.MartusCrypto.NoKeyPairException;
import org.martus.common.database.ClientFileDatabase;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.MockClientDatabase;
import org.martus.common.database.Database.RecordHiddenException;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongAccountException;
import org.martus.util.TestCaseEnhanced;


public class TestBulletinZipUtilities extends TestCaseEnhanced {

	public TestBulletinZipUtilities(String name)
	{
		super(name);
	}

	public void testImportBulletinPacketsFromZipFileToDatabase() throws Exception
	{
		MartusCrypto authorSecurity = MockMartusSecurity.createClient();
		Database originalDb = new MockClientDatabase();
		Bulletin b = new Bulletin(authorSecurity);
		b.setAllPrivate(false);
		b.setSealed();
		BulletinSaver.saveToClientDatabase(b, originalDb, false, authorSecurity);
		
		File destFile = createTempFile();
		DatabaseKey key = DatabaseKey.createSealedKey(b.getUniversalId());
		BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(originalDb, key, destFile, authorSecurity);
		ZipFile zip = new ZipFile(destFile);
		
		MartusCrypto hqSecurity = MockMartusSecurity.createHQ();
		Database fileDb = new ClientFileDatabase(createTempDirectory(), hqSecurity);
		fileDb.initialize();
		verifyImportZip(fileDb, key, zip, hqSecurity);
		
		Database authorDb = new MockClientDatabase();
		verifyImportZip(authorDb, key, zip, authorSecurity);

		Database hqDb = new MockClientDatabase();
		verifyImportZip(hqDb, key, zip, hqSecurity);
		
	}

	private void verifyImportZip(Database authorDb, DatabaseKey key, ZipFile zip, MartusCrypto authorSecurity) throws IOException, RecordHiddenException, InvalidPacketException, SignatureVerificationException, WrongAccountException, DecryptionException, DamagedBulletinException, NoKeyPairException
	{
		BulletinZipUtilities.importBulletinPacketsFromZipFileToDatabase(authorDb, null, zip, authorSecurity);
		BulletinLoader.loadFromDatabase(authorDb, key, authorSecurity);
	}

}
