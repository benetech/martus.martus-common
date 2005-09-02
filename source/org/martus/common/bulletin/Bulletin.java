/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.martus.common.HQKey;
import org.martus.common.HQKeys;
import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.SessionKey;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.field.MartusField;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;
import org.martus.common.packet.AttachmentPacket;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongPacketTypeException;
import org.martus.common.utilities.DateUtilities;
import org.martus.util.Base64.InvalidBase64Exception;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;


public class Bulletin implements BulletinConstants
{
	public static class DamagedBulletinException extends Exception
	{
	}

	public Bulletin(MartusCrypto securityToUse)
	{
		this(securityToUse, StandardFieldSpecs.getDefaultPublicFieldSpecs(), StandardFieldSpecs.getDefaultPrivateFieldSpecs());
	}
	
	public Bulletin(MartusCrypto securityToUse, FieldSpec[] publicFieldSpecs, FieldSpec[] privateFieldSpecs)
	{
		this(securityToUse, BulletinHeaderPacket.createUniversalId(securityToUse), FieldDataPacket.createUniversalId(securityToUse), FieldDataPacket.createUniversalId(securityToUse), publicFieldSpecs, privateFieldSpecs);
	}

	public Bulletin(MartusCrypto securityToUse, UniversalId headerUid, UniversalId publicDataUid, UniversalId privateDataUid, FieldSpec[] publicFieldSpecs, FieldSpec[] privateFieldSpecs)
	{
		security = securityToUse;
		isValidFlag = true;

		header = createHeaderPacket(headerUid);

		fieldData = createPublicFieldDataPacket(publicDataUid, publicFieldSpecs);
		fieldData.setEncrypted(true);
		header.setFieldDataPacketId(publicDataUid.getLocalId());
		
		privateFieldData = createPrivateFieldDataPacket(privateDataUid, privateFieldSpecs);
		privateFieldData.setEncrypted(true);
		header.setPrivateFieldDataPacketId(privateDataUid.getLocalId());
		
		clearAllUserData();
	}

	public MartusCrypto getSignatureGenerator()
	{
		return security;
	}

	public UniversalId getUniversalId()
	{
		return getBulletinHeaderPacket().getUniversalId();
	}

	public String getUniversalIdString()
	{
		return getUniversalId().toString();
	}

	public String getAccount()
	{
		return getBulletinHeaderPacket().getAccountId();
	}

	public String getLocalId()
	{
		return getBulletinHeaderPacket().getLocalId();
	}

	public void setIsValid(boolean isValid)
	{
		isValidFlag = isValid;
	}

	public boolean isValid()
	{
		return isValidFlag;
	}
	
	public String toFileName()
	{
		String bulletinTitle = get(Bulletin.TAGTITLE);
		return MartusUtilities.toFileName(bulletinTitle, MartusUtilities.DEFAULT_FILE_NAME);
	}
	
	public boolean hasUnknownTags()
	{
		if(getBulletinHeaderPacket().hasUnknownTags())
			return true;
		if(getFieldDataPacket().hasUnknownTags())
			return true;
		if(getPrivateFieldDataPacket().hasUnknownTags())
			return true;
		return false;
	}
	
	public boolean hasUnknownCustomField()
	{
		FieldDataPacket fdp = getFieldDataPacket();
		FieldSpec[] specs = fdp.getFieldSpecs();
		for(int i=0; i < specs.length; ++i)
		{
			if(specs[i].hasUnknownStuff())
				return true;
		}
		
		return false;
	}

	public long getLastSavedTime()
	{
		return getBulletinHeaderPacket().getLastSavedTime();
	}
	
	public String getLastSavedDate()
	{
		DateFormat df = FieldSpec.getStoredDateFormat();
		return df.format(new Date(getLastSavedTime()));
	}
	
	public void setHistory(BulletinHistory newHistory)
	{
		getBulletinHeaderPacket().setHistory(newHistory);
	}
	
	public BulletinHistory getHistory()
	{
		return getBulletinHeaderPacket().getHistory();
	}
	
	public int getVersion()
	{
		return getBulletinHeaderPacket().getVersionNumber();
	}

	public boolean isDraft()
	{
		return getStatus().equals(STATUSDRAFT);
	}

	public boolean isSealed()
	{
		return getStatus().equals(STATUSSEALED);
	}

	public void setDraft()
	{
		setStatus(STATUSDRAFT);
	}

	public void setSealed()
	{
		setStatus(STATUSSEALED);
	}

	public void setStatus(String newStatus)
	{
		getBulletinHeaderPacket().setStatus(newStatus);
	}

	public String getStatus()
	{
		return getBulletinHeaderPacket().getStatus();
	}
	
	public FieldSpec[] getPublicFieldSpecs()
	{
		return fieldData.getFieldSpecs();
	}
	
	public FieldSpec[] getPrivateFieldSpecs()
	{
		return getPrivateFieldDataPacket().getFieldSpecs();
	}

	public void set(String fieldTag, String value)
	{
		MartusField field = getField(fieldTag);
		if(field == null)
			return;
		
		field.setData(value);
				
	}
	
	public MartusField getField(String fieldTag)
	{
		if(fieldTag.equals(PSEUDOFIELD_LOCAL_ID))
		{
			MartusField localIdField = new MartusField(FieldSpec.createStandardField(fieldTag, FieldSpec.TYPE_NORMAL));
			localIdField.setData(getLocalId());
			return localIdField;
		}
		
		if(fieldTag.equals(PSEUDOFIELD_LAST_SAVED_DATE))
		{
			MartusField lastSavedDateField = new MartusField(FieldSpec.createStandardField(fieldTag, FieldSpec.TYPE_DATE));
			lastSavedDateField.setData(getLastSavedDate());
			return lastSavedDateField;
		}
		
		if(isFieldInPublicSection(fieldTag))
			return fieldData.getField(fieldTag);
		return getPrivateFieldDataPacket().getField(fieldTag);
	}

	public String get(String fieldTag)
	{
		MartusField field = getField(fieldTag);
		if(field == null)
			return "";
		
		return field.getData();
	}
	
	public int getFieldType(String fieldTag)
	{
		MartusField field = getField(fieldTag);
		if(field == null)
			return FieldSpec.TYPE_UNKNOWN;
		
		return field.getType();
	}

	public void addPublicAttachment(AttachmentProxy a) throws
		IOException,
		MartusCrypto.EncryptionException
	{
		BulletinHeaderPacket bhp = getBulletinHeaderPacket();
		File rawFile = a.getFile();
		if(rawFile != null)
		{
			SessionKey sessionKey = getSignatureGenerator().createSessionKey();
			AttachmentPacket ap = new AttachmentPacket(getAccount(), sessionKey, rawFile, getSignatureGenerator());
			bhp.addPublicAttachmentLocalId(ap.getLocalId());
			a.setPendingPacket(ap, sessionKey);
		}
		else
		{
			bhp.addPublicAttachmentLocalId(a.getUniversalId().getLocalId());
		}

		getFieldDataPacket().addAttachment(a);
	}

	public void addPrivateAttachment(AttachmentProxy a) throws
		IOException,
		MartusCrypto.EncryptionException
	{
		BulletinHeaderPacket bhp = getBulletinHeaderPacket();
		File rawFile = a.getFile();
		if(rawFile != null)
		{
			SessionKey sessionKeyBytes = getSignatureGenerator().createSessionKey();
			AttachmentPacket ap = new AttachmentPacket(getAccount(), sessionKeyBytes, rawFile, getSignatureGenerator());
			bhp.addPrivateAttachmentLocalId(ap.getLocalId());
			a.setPendingPacket(ap, sessionKeyBytes);
		}
		else
		{
			bhp.addPrivateAttachmentLocalId(a.getUniversalId().getLocalId());
		}

		getPrivateFieldDataPacket().addAttachment(a);
	}

	public AttachmentProxy[] getPublicAttachments()
	{
		return getFieldDataPacket().getAttachments();
	}

	public AttachmentProxy[] getPrivateAttachments()
	{
		return getPrivateFieldDataPacket().getAttachments();
	}

	public void addAuthorizedToReadKeys(HQKeys keysToAdd)
	{
		HQKeys keys = getAuthorizedToReadKeys();
		for(int i = 0; i < keysToAdd.size(); ++i)
		{
			HQKey keyToAdd = keysToAdd.get(i);
			if(!keys.containsKey(keyToAdd.getPublicKey()))
			{
				keys.add(keyToAdd);
			}
		}
		setAuthorizedToReadKeys(keys);
	}
	
	public void clearAllUserData()
	{
		getBulletinHeaderPacket().clearAllUserData();
		getFieldDataPacket().clearAll();
		getPrivateFieldDataPacket().clearAll();
		
		FieldSpec[] specs = fieldData.getFieldSpecs();
		for(int i = 0; i < specs.length; ++i)
		{
			set(specs[i].getTag(), specs[i].getDefaultValue());
		}
		
		set(TAGENTRYDATE, DateUtilities.getToday());
		set(TAGEVENTDATE, DateUtilities.getFirstOfThisYear());
		
		setDraft();
	}

	public void clearPublicAttachments()
	{
		getBulletinHeaderPacket().removeAllPublicAttachments();
		getFieldDataPacket().clearAttachments();
	}

	public void clearPrivateAttachments()
	{
		getBulletinHeaderPacket().removeAllPrivateAttachments();
		getPrivateFieldDataPacket().clearAttachments();
	}

	public boolean contains(String lookFor)
	{
		if(doesSectionContain(getFieldDataPacket(), lookFor))
			return true;
		if(doesSectionContain(getPrivateFieldDataPacket(), lookFor))
			return true;
		if(doesAttachmentsContain(getPublicAttachments(), lookFor))
			return true;
		if(doesAttachmentsContain(getPrivateAttachments(), lookFor))
			return true;
		return false;
	}

	private boolean doesAttachmentsContain(AttachmentProxy[] attachments, String lookFor) 
	{
		for(int i = 0; i < attachments.length; ++i)
		{
			String lookForLowerCase = lookFor.toLowerCase();
			String label = attachments[i].getLabel().toLowerCase();
			if(label.indexOf(lookForLowerCase) >=0)
				return true;
		}
		return false;
	}

	private boolean doesSectionContain(FieldDataPacket section, String lookFor)
	{
		FieldSpec fields[] = section.getFieldSpecs();
		for(int f = 0; f < fields.length; ++f)
		{
			MartusField field = getField(fields[f].getTag());
			if(field.contains(lookFor))
				return true;
		}
		return false;
	}

	public HQKeys getAuthorizedToReadKeys()
	{
		return getBulletinHeaderPacket().getAuthorizedToReadKeys();
	}

	public void setAuthorizedToReadKeys(HQKeys authorizedKeys)
	{
		getBulletinHeaderPacket().setAuthorizedToReadKeys(authorizedKeys);
		getFieldDataPacket().setAuthorizedToReadKeys(authorizedKeys);
		getPrivateFieldDataPacket().setAuthorizedToReadKeys(authorizedKeys);
	}

	public DatabaseKey getDatabaseKey()
	{
		return getDatabaseKeyForLocalId(getLocalId());
	}

	public DatabaseKey getDatabaseKeyForLocalId(String localId)
	{
		UniversalId uid = UniversalId.createFromAccountAndLocalId(getAccount(), localId);
		return getBulletinHeaderPacket().createKeyWithHeaderStatus(uid);
	}

	public boolean isFieldInPublicSection(String fieldName)
	{
		return getFieldDataPacket().fieldExists(fieldName);
	}

	public boolean isFieldInPrivateSection(String fieldName)
	{
		return getPrivateFieldDataPacket().fieldExists(fieldName);
	}

	public int getFieldCount()
	{
		return fieldData.getFieldCount();
	}

	public boolean isAllPrivate()
	{

		BulletinHeaderPacket bhp = getBulletinHeaderPacket();
		if(!bhp.hasAllPrivateFlag())
		{
			FieldDataPacket fdp = getFieldDataPacket();
			bhp.setAllPrivate(fdp.isEncrypted());
		}
		return bhp.isAllPrivate();
	}

	public void setAllPrivate(boolean newValue)
	{
		getBulletinHeaderPacket().setAllPrivate(newValue);
	}

	public void createDraftCopyOf(Bulletin other, ReadableDatabase otherDatabase) throws
		CryptoException, 
		InvalidPacketException, 
		SignatureVerificationException, 
		WrongPacketTypeException, 
		IOException, 
		InvalidBase64Exception
	{
		clearAllUserData();
		
		boolean originalIsMine = other.getAccount().equals(getAccount());
		if(originalIsMine && other.isSealed())
		{
			BulletinHistory history = new BulletinHistory(other.getHistory());
			history.add(other.getLocalId());
			setHistory(history);
		}

		setDraft();
		setAllPrivate(other.isAllPrivate());

		pullFields(other, getFieldDataPacket().getFieldSpecs());
		pullFields(other, getPrivateFieldDataPacket().getFieldSpecs());

		setAuthorizedToReadKeys(other.getAuthorizedToReadKeys());
		
		AttachmentProxy[] attachmentPublicProxies = other.getPublicAttachments();
		for(int aIndex = 0; aIndex < attachmentPublicProxies.length; ++aIndex)
		{
			AttachmentProxy ap = attachmentPublicProxies[aIndex];
			ap = getAsFileProxy(ap, otherDatabase, Bulletin.STATUSDRAFT);
			addPublicAttachment(ap);
		}

		AttachmentProxy[] attachmentPrivateProxies = other.getPrivateAttachments();
		for(int aIndex = 0; aIndex < attachmentPrivateProxies.length; ++aIndex)
		{
			AttachmentProxy ap = attachmentPrivateProxies[aIndex];
			ap = getAsFileProxy(ap, otherDatabase, Bulletin.STATUSDRAFT);
			addPrivateAttachment(ap);
		}

	}
	
	public void pullFields(Bulletin other, FieldSpec[] fields)
	{
		for(int f = 0; f < fields.length; ++f)
		{
			set(fields[f].getTag(), other.get(fields[f].getTag()));
		}
	}

	public AttachmentProxy getAsFileProxy(AttachmentProxy ap, ReadableDatabase otherDatabase, String status)
		throws
			IOException,
			CryptoException,
			InvalidPacketException,
			SignatureVerificationException,
			WrongPacketTypeException,
			InvalidBase64Exception
	{
		if(ap.getFile() != null) 
			return ap;
		if(otherDatabase == null)
			return ap;

		DatabaseKey key = DatabaseKey.createKey(ap.getUniversalId(),status);
		InputStreamWithSeek packetIn = otherDatabase.openInputStream(key, security);
		if(packetIn == null)
			return ap;

		try
		{
			return AttachmentProxy.createFileProxyFromAttachmentPacket(packetIn, ap, security);
		}
		finally
		{
			packetIn.close();
		}
	}

	public BulletinHeaderPacket getBulletinHeaderPacket()
	{
		return header;
	}

	public FieldDataPacket getFieldDataPacket()
	{
		return fieldData;
	}

	public FieldDataPacket getPrivateFieldDataPacket()
	{
		return privateFieldData;
	}

	public PendingAttachmentList getPendingPublicAttachments()
	{
		return getPendingAttachments(getFieldDataPacket());
	}

	public PendingAttachmentList getPendingPrivateAttachments()
	{
		return getPendingAttachments(getPrivateFieldDataPacket());
	}
	
	private PendingAttachmentList getPendingAttachments(FieldDataPacket fdp)
	{
		AttachmentProxy[] proxies = fdp.getAttachments();
		PendingAttachmentList pending = new PendingAttachmentList(); 
		for(int i=0; i < proxies.length; ++i)
		{
			AttachmentPacket packet = proxies[i].getPendingPacket(); 
			if(packet != null)
				pending.add(packet);
		}
		return pending;
	}

	protected BulletinHeaderPacket createHeaderPacket(UniversalId headerUid)
	{
		return new BulletinHeaderPacket(headerUid);
	}

	protected FieldDataPacket createPrivateFieldDataPacket(
		UniversalId privateDataUid,
		FieldSpec[] privateFieldSpecs)
	{
		return createFieldDataPacket(privateDataUid, privateFieldSpecs);
	}

	protected FieldDataPacket createPublicFieldDataPacket(
		UniversalId dataUid,
		FieldSpec[] publicFieldSpecs)
	{
		return createFieldDataPacket(dataUid, publicFieldSpecs);
	}

	protected FieldDataPacket createFieldDataPacket(
		UniversalId dataUid,
		FieldSpec[] publicFieldSpecs)
	{
		return new FieldDataPacket(dataUid, publicFieldSpecs);
	}

	public static final String PSEUDOFIELD_LOCAL_ID = "_localId";
	public static final String PSEUDOFIELD_LAST_SAVED_DATE = "_lastSavedDate";
	
	private boolean isValidFlag;
	private MartusCrypto security;
	private BulletinHeaderPacket header;
	private FieldDataPacket fieldData;
	private FieldDataPacket privateFieldData;
}
