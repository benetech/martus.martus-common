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

import java.util.Vector;

import org.martus.common.crypto.MartusCrypto;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.UniversalId;


class LeafScanner implements Database.PacketVisitor
{
	public LeafScanner(ReadableDatabase databaseToScan, MartusCrypto cryptoToUse)
	{
		db = databaseToScan;
		crypto = cryptoToUse;
		leafKeys = new Vector();
		nonLeafUids = new Vector();
	}
	
	public Vector getLeafKeys()
	{
		return leafKeys;
	}
	
	public void visit(DatabaseKey key)
	{
		try
		{
			UniversalId maybeLeaf = key.getUniversalId();
			if(!nonLeafUids.contains(maybeLeaf))
				leafKeys.add(key);
			
			BulletinHeaderPacket bhp = BulletinStore.loadBulletinHeaderPacket(db, key, crypto);
			BulletinHistory history = bhp.getHistory();
			for(int i=0; i < history.size(); ++i)
			{
				String thisLocalId = history.get(i);
				UniversalId uidOfNonLeaf = UniversalId.createFromAccountAndLocalId(bhp.getAccountId(), thisLocalId);
				leafKeys.remove(DatabaseKey.createSealedKey(uidOfNonLeaf));
				leafKeys.remove(DatabaseKey.createDraftKey(uidOfNonLeaf));
				nonLeafUids.add(uidOfNonLeaf);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	ReadableDatabase db;
	MartusCrypto crypto;
	Vector leafKeys;
	Vector nonLeafUids;
}
