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

package org.martus.common;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipFile;

import org.martus.common.MartusUtilities.FileVerificationException;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.FileDatabase.MissingAccountMapException;
import org.martus.common.database.FileDatabase.MissingAccountMapSignatureException;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.Packet;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.WrongAccountException;


public class BulletinStore
{
	public BulletinStore()
	{
	}

	public void doAfterSigninInitialization(File dataRootDirectory, Database db) throws FileVerificationException, MissingAccountMapException, MissingAccountMapSignatureException
	{
		dir = dataRootDirectory;
		database = db;
		database.initialize();
	}

	public void setSignatureGenerator(MartusCrypto securityToUse)
	{
		security = securityToUse;
	}
	
	public MartusCrypto getSignatureGenerator()
	{
		return security;
	}
	
	public MartusCrypto getSignatureVerifier()
	{
		return security;
	}
	
	public Database getDatabase()
	{
		return database;
	}
	
	public void setDatabase(Database toUse)
	{
		database = toUse;
	}
	
	public File getStoreRootDir()
	{
		return dir;
	}

	public String getAccountId()
	{
		return security.getPublicKeyString();
	}

	public int getBulletinCount()
	{
		return scanForLeafUids().size();
	}

	public Vector getAllBulletinUids()
	{
		return getUidsOfAllBulletinRevisions();
	}

	public void visitAllBulletins(Database.PacketVisitor visitorToUse)
	{
		visitAllBulletinRevisions(visitorToUse);
	}

	public boolean doesBulletinRevisionExist(UniversalId uid)
	{
		DatabaseKey key = new DatabaseKey(uid);
		return doesBulletinRevisionExist(key);
	}

	protected boolean doesBulletinRevisionExist(DatabaseKey key)
	{
		return getDatabase().doesRecordExist(key);
	}
	
	public void deleteAllData() throws Exception
	{
		deleteAllBulletins();
	}

	public void deleteAllBulletins() throws Exception
	{
		database.deleteAllData();
	}

	public void importZipFileToStoreWithSameUids(File inputFile) throws IOException, MartusCrypto.CryptoException, Packet.InvalidPacketException, Packet.SignatureVerificationException
	{
		ZipFile zip = new ZipFile(inputFile);
		try
		{
			BulletinZipUtilities.importBulletinPacketsFromZipFileToDatabase(getDatabase(), null, zip, getSignatureVerifier());
		}
		catch (Database.RecordHiddenException shouldBeImpossible)
		{
			shouldBeImpossible.printStackTrace();
			throw new IOException(shouldBeImpossible.toString());
		}
		catch(WrongAccountException shouldBeImpossible)
		{
			throw new Packet.InvalidPacketException("Wrong account???");
		}
		finally
		{
			zip.close();
		}
	}
	
	public Vector scanForLeafUids()
	{
		LeafScanner scanner = new LeafScanner(getDatabase(), getSignatureVerifier());
		visitAllBulletinRevisions(scanner);
		return scanner.getLeafUids();
	}
	
	

	private Vector getUidsOfAllBulletinRevisions()
	{
		class UidCollector implements Database.PacketVisitor
		{
			public void visit(DatabaseKey key)
			{
				uidList.add(key.getUniversalId());
			}
			Vector uidList = new Vector();
		}
	
		UidCollector uidCollector = new UidCollector();
		visitAllBulletins(uidCollector);
		return uidCollector.uidList;
	}

	private void visitAllBulletinRevisions(Database.PacketVisitor visitorToUse)
	{
		class BulletinKeyFilter implements Database.PacketVisitor
		{
			BulletinKeyFilter(Database db, Database.PacketVisitor visitorToUse2)
			{
				visitor = visitorToUse2;
				db.visitAllRecords(this);
			}
	
			public void visit(DatabaseKey key)
			{
				if(BulletinHeaderPacket.isValidLocalId(key.getLocalId()))
				{
					++count;
					visitor.visit(key);
				}
			}
			Database.PacketVisitor visitor;
			int count;
		}
	
		new BulletinKeyFilter(getDatabase(), visitorToUse);
	}

	private MartusCrypto security;
	private File dir;
	private Database database;
}

class LeafScanner implements Database.PacketVisitor
{
	public LeafScanner(Database databaseToScan, MartusCrypto cryptoToUse)
	{
		db = databaseToScan;
		crypto = cryptoToUse;
		leafUids = new Vector();
		nonLeafUids = new Vector();
	}
	
	public Vector getLeafUids()
	{
		return leafUids;
	}
	
	public void visit(DatabaseKey key)
	{
		BulletinHeaderPacket bhp = new BulletinHeaderPacket();
		try
		{
			UniversalId maybeLeaf = key.getUniversalId();
			if(!nonLeafUids.contains(maybeLeaf))
				leafUids.add(maybeLeaf);
			
			bhp.loadFromXml(db.openInputStream(key, crypto), crypto);
			Vector history = bhp.getHistory();
			for(int i=0; i < history.size(); ++i)
			{
				String thisLocalId = (String)history.get(i);
				UniversalId uidOfNonLeaf = UniversalId.createFromAccountAndLocalId(bhp.getAccountId(), thisLocalId);
				leafUids.remove(uidOfNonLeaf);
				nonLeafUids.add(uidOfNonLeaf);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	Database db;
	MartusCrypto crypto;
	Vector leafUids;
	Vector nonLeafUids;
}
