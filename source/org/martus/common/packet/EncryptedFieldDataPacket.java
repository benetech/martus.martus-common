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

package org.martus.common.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;

import org.martus.common.AuthorizedSessionKeys;
import org.martus.common.HQKey;
import org.martus.common.MartusXml;
import org.martus.common.XmlWriterFilter;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.SessionKey;
import org.martus.common.crypto.MartusCrypto.EncryptionException;
import org.martus.util.Base64;
import org.martus.util.Base64.InvalidBase64Exception;

class EncryptedFieldDataPacket extends Packet
{
	EncryptedFieldDataPacket(UniversalId uid, String plainTextData, MartusCrypto crypto) throws IOException
	{
		super(uid);
		authorizedToReadKeys = new Vector();
		security = crypto;
		try
		{
			sessionKey = security.createSessionKey();
			byte[] plainTextBytes = plainTextData.getBytes("UTF-8");
			ByteArrayInputStream inPlain = new ByteArrayInputStream(plainTextBytes);
			ByteArrayOutputStream outEncrypted = new ByteArrayOutputStream();
			security.encrypt(inPlain, outEncrypted, sessionKey);
			byte[] encryptedBytes = outEncrypted.toByteArray();
			encryptedData = Base64.encode(encryptedBytes);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new IOException("UnsupportedEncodingException");
		}
		catch(MartusCrypto.NoKeyPairException e)
		{
			throw new IOException("NoKeyPairException");
		}
		catch(MartusCrypto.EncryptionException e)
		{
			throw new IOException("EncryptionException");
		}
	}

	protected String getPacketRootElementName()
	{
		return MartusXml.FieldDataPacketElementName;
	}

	void setHQPublicKeys(Vector hqKeys)
	{
		authorizedToReadKeys = hqKeys;
	}

	protected void internalWriteXml(XmlWriterFilter dest) throws IOException
	{
		super.internalWriteXml(dest);
		writeElement(dest, MartusXml.EncryptedFlagElementName, "");
		if(authorizedToReadKeys.size() > 0)
		{
			try
			{
				//Legacy HQ
				HQKey publicKey = (HQKey)authorizedToReadKeys.get(0);
				String sessionKeyString = getSessionKeyString(publicKey.getPublicKey());
				writeElement(dest, MartusXml.HQSessionKeyElementName, sessionKeyString);
				
				HashMap sessionKeysAndPublicCodes = new HashMap();
				String publicCode = publicKey.getRawPublicCode();
				sessionKeysAndPublicCodes.put(publicCode, sessionKeyString);

				for(int i = 1; i < authorizedToReadKeys.size(); ++i)
				{
					publicKey = (HQKey)authorizedToReadKeys.get(i);
					sessionKeyString = getSessionKeyString(publicKey.getPublicKey());
					publicCode = publicKey.getRawPublicCode();
					sessionKeysAndPublicCodes.put(publicCode, sessionKeyString);
				}
				if(!sessionKeysAndPublicCodes.isEmpty())
				{
					AuthorizedSessionKeys sessionKeys = new AuthorizedSessionKeys(sessionKeysAndPublicCodes);
					writeNonEncodedXMLString(dest, sessionKeys.toString());
				}
			}
			catch(EncryptionException e)
			{
				throw new IOException("FieldDataPacket.internalWriteXml Encryption Exception");
			}
			catch (InvalidBase64Exception e)
			{
				throw new IOException("FieldDataPacket.internalWriteXml InvalidBase64 Exception on HQ Public Key");
			}
		}
		writeElement(dest, MartusXml.EncryptedDataElementName, encryptedData);
	}

	private String getSessionKeyString(String publicCode) throws EncryptionException
	{
		SessionKey encryptedSessionKey = security.encryptSessionKey(sessionKey, publicCode);
		String sessionKeyString = Base64.encode(encryptedSessionKey.getBytes());
		return sessionKeyString;
	}

	MartusCrypto security;
	String encryptedData;
	private Vector authorizedToReadKeys;
	private SessionKey sessionKey;
}
