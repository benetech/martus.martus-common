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

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.martus.common.MartusXml;
import org.martus.common.XmlWriterFilter;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.database.DatabaseKey;
import org.martus.util.Base64;
import org.martus.util.InputStreamWithSeek;
import org.martus.util.UnicodeReader;
import org.martus.util.ZipEntryInputStream;
import org.martus.util.xml.SimpleXmlParser;

public class BulletinHeaderPacket extends Packet
{
	public BulletinHeaderPacket(String accountString)
	{
		super(createUniversalId(accountString));
		initialize();
	}

	public BulletinHeaderPacket(UniversalId universalIdToUse)
	{
		super(universalIdToUse);
		initialize();
	}

	public static UniversalId createUniversalId(String accountId)
	{
		return UniversalId.createFromAccountAndPrefix(accountId, prefix);
	}

	public static boolean isValidLocalId(String localId)
	{
		return localId.startsWith(prefix);
	}

	public void clearAttachments()
	{
		publicAttachments.clear();
		privateAttachments.clear();
	}

	public boolean hasAllPrivateFlag()
	{
		return knowsWhetherAllPrivate;
	}

	public void setStatus(String newStatus)
	{
		status = newStatus;
	}

	public String getStatus()
	{
		return status;
	}

	public long getLastSavedTime()
	{
		return lastSavedTime;
	}
	
	void setLastSavedTime(long timeToUse)
	{
		lastSavedTime = timeToUse;
	}

	public void updateLastSavedTime()
	{
		lastSavedTime = System.currentTimeMillis();
	}

	public void setFieldDataPacketId(String id)
	{
		fieldDataPacketId = id;
	}

	public String getFieldDataPacketId()
	{
		return fieldDataPacketId;
	}

	public void setPrivateFieldDataPacketId(String id)
	{
		privateFieldDataPacketId = id;
	}

	public String getPrivateFieldDataPacketId()
	{
		return privateFieldDataPacketId;
	}

	public String getHQPublicKey()
	{
		return hqPublicKey;
	}

	public void setHQPublicKey(String key)
	{
		hqPublicKey = key;
	}

	public void setAllPrivate(boolean newValue)
	{
		allPrivate = newValue;
		knowsWhetherAllPrivate = true;
	}

	public boolean isAllPrivate()
	{
		return allPrivate;
	}

	public void setFieldDataSignature(byte[] sig)
	{
		fieldDataPacketSig = sig;
	}

	public byte[] getFieldDataSignature()
	{
		return fieldDataPacketSig;
	}

	public void setPrivateFieldDataSignature(byte[] sig)
	{
		privateFieldDataPacketSig = sig;
	}

	public byte[] getPrivateFieldDataSignature()
	{
		return privateFieldDataPacketSig;
	}

	public String[] getPublicAttachmentIds()
	{
		String[] result = new String[publicAttachments.size()];
		for(int i = 0; i < result.length; ++i)
			result[i] = (String)publicAttachments.get(i);

		Arrays.sort(result);
		return result;
	}

	public String[] getPrivateAttachmentIds()
	{
		String[] result = new String[privateAttachments.size()];
		for(int i = 0; i < result.length; ++i)
			result[i] = (String)privateAttachments.get(i);

		Arrays.sort(result);
		return result;
	}

	public void addPublicAttachmentLocalId(String id)
	{
		if(publicAttachments.contains(id))
			return;
		publicAttachments.add(id);
	}

	public void addPrivateAttachmentLocalId(String id)
	{
		if(privateAttachments.contains(id))
			return;
		privateAttachments.add(id);
	}

	public void removeAllPublicAttachments()
	{
		publicAttachments.clear();
	}

	public void removeAllPrivateAttachments()
	{
		privateAttachments.clear();
	}

	public void loadFromXml(InputStreamWithSeek inputStream, byte[] expectedSig, MartusCrypto verifier) throws
		IOException,
		InvalidPacketException,
		WrongPacketTypeException,
		SignatureVerificationException,
		MartusCrypto.DecryptionException,
		MartusCrypto.NoKeyPairException
	{
		if(verifier != null)
			verifyPacketSignature(inputStream, expectedSig, verifier);
		XmlHeaderPacketLoader loader = new XmlHeaderPacketLoader(this);
		try
		{
			knowsWhetherAllPrivate = false;
			SimpleXmlParser.parse(loader, new UnicodeReader(inputStream));
		}
		catch (Exception e)
		{
			// TODO: Be more specific with exceptions!
			e.printStackTrace();
			System.out.println(e.getCause());
			System.out.println(e.getClass());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			throw new InvalidPacketException(e.getMessage());
		}
	}

	public void loadFromXml(InputStreamWithSeek inputStream, MartusCrypto verifier) throws
		IOException,
		InvalidPacketException,
		WrongPacketTypeException,
		SignatureVerificationException,
		MartusCrypto.DecryptionException,
		MartusCrypto.NoKeyPairException
	{
		loadFromXml(inputStream, null, verifier);
	}

	public static BulletinHeaderPacket loadFromZipFile(ZipFile zip, MartusCrypto verifier)
		throws IOException,
		SignatureVerificationException
	{
		BulletinHeaderPacket header = new BulletinHeaderPacket("Unknown");
		ZipEntry headerZipEntry = getBulletinHeaderEntry(zip);

		InputStreamWithSeek headerIn = new ZipEntryInputStream(zip, headerZipEntry);
		try
		{
			header.loadFromXml(headerIn, verifier);
			if(!header.getLocalId().equals(headerZipEntry.getName()))
				throw new IOException("Misnamed header entry");
		}
		catch(Packet.SignatureVerificationException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new IOException(e.getMessage());
		}
		finally
		{
			headerIn.close();
		}
		return header;
	}

	public DatabaseKey[] getPublicPacketKeys()
	{
		String accountId = getAccountId();
		String[] publicAttachmentIds = getPublicAttachmentIds();
	
		int corePacketCount = 2;
		int publicAttachmentCount = publicAttachmentIds.length;
		int totalPacketCount = corePacketCount + publicAttachmentCount;
		DatabaseKey[] keys = new DatabaseKey[totalPacketCount];
	
		int next = 0;
		UniversalId dataUid = UniversalId.createFromAccountAndLocalId(accountId, getFieldDataPacketId());
		keys[next++] = createKeyWithHeaderStatus(dataUid);
	
		for(int i=0; i < publicAttachmentIds.length; ++i)
		{
			UniversalId uid = UniversalId.createFromAccountAndLocalId(accountId, publicAttachmentIds[i]);
			keys[next++] = createKeyWithHeaderStatus(uid);
		}
		keys[next++] = createKeyWithHeaderStatus(getUniversalId());
	
		return keys;
	}

	public DatabaseKey createKeyWithHeaderStatus(UniversalId uid)
	{
		if(getStatus().equals(BulletinConstants.STATUSDRAFT))
			return DatabaseKey.createDraftKey(uid);
		else
			return DatabaseKey.createSealedKey(uid);
	}

	static ZipEntry getBulletinHeaderEntry(ZipFile zip) throws IOException
	{
		Enumeration entries = zip.entries();
		while(entries.hasMoreElements())
		{
			ZipEntry headerEntry = (ZipEntry)entries.nextElement();
			if(isValidLocalId(headerEntry.getName()))
				return headerEntry;
		}

		throw new IOException("Missing header entry");
	}

	protected String getPacketRootElementName()
	{
		return MartusXml.BulletinHeaderPacketElementName;
	}

	protected void internalWriteXml(XmlWriterFilter dest) throws IOException
	{
		super.internalWriteXml(dest);

		writeElement(dest, MartusXml.BulletinStatusElementName, getStatus());
		writeElement(dest, MartusXml.LastSavedTimeElementName, Long.toString(getLastSavedTime()));

		String allPrivateValue = ALL_PRIVATE;
		if(!isAllPrivate())
			allPrivateValue = NOT_ALL_PRIVATE;
		writeElement(dest, MartusXml.AllPrivateElementName, allPrivateValue);

		String hqPublicKey = getHQPublicKey();
		if(hqPublicKey.length() > 0)
			writeElement(dest, MartusXml.HQPublicKeyElementName, hqPublicKey);

		String dataId = getFieldDataPacketId();
		if(dataId != null)
		{
			writeElement(dest, MartusXml.DataPacketIdElementName, dataId);
			writeElement(dest, MartusXml.DataPacketSigElementName, Base64.encode(fieldDataPacketSig));
		}

		String privateId = getPrivateFieldDataPacketId();
		if(privateId != null)
		{
			writeElement(dest, MartusXml.PrivateDataPacketIdElementName, privateId);
			writeElement(dest, MartusXml.PrivateDataPacketSigElementName, Base64.encode(privateFieldDataPacketSig));
		}

		String[] publicAttachmentIds = getPublicAttachmentIds();
		for(int i = 0; i < publicAttachmentIds.length; ++i)
		{
			writeElement(dest, MartusXml.PublicAttachmentIdElementName, publicAttachmentIds[i]);
		}

		String[] privateAttachmentIds = getPrivateAttachmentIds();
		for(int i = 0; i < privateAttachmentIds.length; ++i)
		{
			writeElement(dest, MartusXml.PrivateAttachmentIdElementName, privateAttachmentIds[i]);
		}
	}

	void setAllPrivateFromXmlTextValue(String data)
	{
		if(data.equals(NOT_ALL_PRIVATE))
			setAllPrivate(false);
		else
			setAllPrivate(true);
	}

	protected void initialize()
	{
		status = "";
		allPrivate = true;
		fieldDataPacketSig = new byte[1];
		privateFieldDataPacketSig = new byte[1];
		publicAttachments = new Vector();
		privateAttachments = new Vector();
		hqPublicKey = "";
		lastSavedTime = TIME_UNKNOWN;
	}

	private final static String ALL_PRIVATE = "1";
	private final static String NOT_ALL_PRIVATE = "0";

	public static final long TIME_UNKNOWN = 0;

	boolean knowsWhetherAllPrivate;
	boolean allPrivate;
	String fieldDataPacketId;
	String privateFieldDataPacketId;
	String status;
	String hqPublicKey;
	private long lastSavedTime;
	private byte[] fieldDataPacketSig;
	private byte[] privateFieldDataPacketSig;
	private Vector publicAttachments;
	private Vector privateAttachments;
	private static final String prefix = "B-";
}
