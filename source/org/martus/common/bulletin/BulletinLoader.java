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

package org.martus.common.bulletin;

import java.io.IOException;
import java.util.Vector;

import org.martus.common.StandardFieldSpecs;
import org.martus.common.FieldSpec;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.database.Database;
import org.martus.common.database.DatabaseKey;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.Packet;
import org.martus.common.packet.UniversalId;
import org.martus.util.InputStreamWithSeek;


public class BulletinLoader
{

	public static Bulletin loadFromDatabase(Database db, DatabaseKey key, MartusCrypto verifier) throws
			IOException,
			Bulletin.DamagedBulletinException,
			MartusCrypto.NoKeyPairException
	{
		FieldSpec[] standardFieldNames = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		FieldSpec[] privateFieldNames = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		Bulletin b = new Bulletin(verifier, standardFieldNames, privateFieldNames);
		b.clear();
		b.setIsValid(false);

		BulletinHeaderPacket headerPacket = b.getBulletinHeaderPacket();
		DatabaseKey headerKey = key;
		boolean isHeaderValid = BulletinLoader.loadAnotherPacket(headerPacket, db, headerKey, null, verifier);

		if(isHeaderValid)
		{
			FieldDataPacket dataPacket = b.getFieldDataPacket();
			FieldDataPacket privateDataPacket = b.getPrivateFieldDataPacket();

			DatabaseKey dataKey = b.getDatabaseKeyForLocalId(headerPacket.getFieldDataPacketId());

			byte[] dataSig = headerPacket.getFieldDataSignature();
			boolean isDataValid = BulletinLoader.loadAnotherPacket(dataPacket, db, dataKey, dataSig, verifier);

			DatabaseKey privateDataKey = b.getDatabaseKeyForLocalId(headerPacket.getPrivateFieldDataPacketId());
			byte[] privateDataSig = headerPacket.getPrivateFieldDataSignature();
			boolean isPrivateDataValid = BulletinLoader.loadAnotherPacket(privateDataPacket, db, privateDataKey, privateDataSig, verifier);

			if(isDataValid)
				isDataValid = isAttachmentsValid(db, verifier, b.getPublicAttachments());
			if(isPrivateDataValid)
				isPrivateDataValid = isAttachmentsValid(db, verifier, b.getPrivateAttachments());
			
			b.setIsValid(isDataValid && isPrivateDataValid);
		}

		if(b.isValid())
		{
			b.setAuthorizedToReadKeys(headerPacket.getAuthorizedToReadKeys());
		}
		else
		{
			Vector emptySetOfKeys = new Vector();
			b.setAuthorizedToReadKeys(emptySetOfKeys);
			if(!isHeaderValid)
			{
				//System.out.println("Bulletin.loadFromDatabase: Header invalid");
				throw new Bulletin.DamagedBulletinException();
			}
		}

		return b;
	}

	private static boolean isAttachmentsValid(Database db, MartusCrypto verifier, AttachmentProxy[] attachmentProxies)
	{
		if(attachmentProxies == null)
			return true;
		for(int i = 0; i< attachmentProxies.length; ++i)
		{
			UniversalId id = attachmentProxies[i].getUniversalId();
			DatabaseKey key = DatabaseKey.createSealedKey(id);
			InputStreamWithSeek in = null;
			try
			{
				in = db.openInputStream(key, verifier);
			}
			catch (Exception e)
			{
				return false;
			}
			if(in == null)
				return false;

			try
			{
				Packet.verifyPacketSignature(in,verifier);
			}
			catch (Exception e1)
			{
				return false;
			}
		}
		return true;
	}

	private static boolean loadAnotherPacket(Packet packet, Database db, DatabaseKey key, byte[] expectedSig, MartusCrypto verifier) throws
			IOException,
			MartusCrypto.NoKeyPairException
	{
		packet.setUniversalId(key.getUniversalId());
		try
		{
			InputStreamWithSeek in = db.openInputStream(key, verifier);
			if(in == null)
			{
				//System.out.println("Packet not found: " + key.getLocalId());
				return false;
			}
			packet.loadFromXml(in, expectedSig, verifier);
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch(MartusCrypto.NoKeyPairException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			return false;
		}
	}

}
