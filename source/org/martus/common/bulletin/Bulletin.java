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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.martus.common.FieldSpec;
import org.martus.common.HQKeys;
import org.martus.common.MartusUtilities;
import org.martus.common.StandardFieldSpecs;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.SessionKey;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.packet.AttachmentPacket;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongPacketTypeException;
import org.martus.common.utilities.MartusFlexidate;
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
		security = securityToUse;
		isValidFlag = true;

		UniversalId headerUid = BulletinHeaderPacket.createUniversalId(security);
		header = createHeaderPacket(headerUid);

		UniversalId dataUid = FieldDataPacket.createUniversalId(security);
		fieldData = createPublicFieldDataPacket(dataUid, publicFieldSpecs);
		fieldData.setEncrypted(true);
		header.setFieldDataPacketId(dataUid.getLocalId());
		
		UniversalId privateDataUid = FieldDataPacket.createUniversalId(security);
		privateFieldData = createPrivateFieldDataPacket(privateDataUid, privateFieldSpecs);
		privateFieldData.setEncrypted(true);
		header.setPrivateFieldDataPacketId(privateDataUid.getLocalId());
		
		clear();
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
	
	public String getLastSavedDateTime()
	{		
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(getLastSavedTime());		
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(cal.getTime());
	}
	
	public void setHistory(BulletinHistory newHistory)
	{
		getBulletinHeaderPacket().setHistory(newHistory);
	}
	
	public BulletinHistory getHistory()
	{
		return getBulletinHeaderPacket().getHistory();
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

	public void set(String fieldName, String value)
	{
		if(isFieldInPublicSection(fieldName))
			fieldData.set(fieldName, value);
		else
			getPrivateFieldDataPacket().set(fieldName, value);
				
	}

	public String get(String fieldName)
	{			 
		if(isFieldInPublicSection(fieldName))
			return fieldData.get(fieldName);
		return getPrivateFieldDataPacket().get(fieldName);
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

	public void clear()
	{
		getBulletinHeaderPacket().clearAttachments();
		getFieldDataPacket().clearAll();
		getPrivateFieldDataPacket().clearAll();
		
		FieldSpec[] specs = fieldData.getFieldSpecs();
		for(int i = 0; i < specs.length; ++i)
		{
			set(specs[i].getTag(), specs[i].getDefaultValue());
		}
		
		set(TAGENTRYDATE, getToday());
		set(TAGEVENTDATE, getFirstOfThisYear());
		set(TAGLASTSAVED, getLastSavedDateTime());
		
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
		
		return false;
	}

	private boolean doesSectionContain(FieldDataPacket section, String lookFor)
	{
		FieldSpec fields[] = section.getFieldSpecs();
		String lookForLowerCase = lookFor.toLowerCase();
		for(int f = 0; f < fields.length; ++f)
		{
			String contents = get(fields[f].getTag()).toLowerCase();
			if(contents.indexOf(lookForLowerCase) >= 0)
				return true;
		}
		return false;
	}

	public boolean withinDates(String beginDate, String endDate)
	{
		String eventDate = fieldData.get(Bulletin.TAGEVENTDATE);
		String entryDate = fieldData.get(Bulletin.TAGENTRYDATE);
		
		if(eventDate.compareTo(beginDate) >= 0 && eventDate.compareTo(endDate) <= 0)
			return true;
		if(entryDate.compareTo(beginDate) >= 0 && entryDate.compareTo(endDate) <= 0)
			return true;
			
		int comma = eventDate.indexOf(MartusFlexidate.DATE_RANGE_SEPARATER);		
		if (comma > 0 && isWithinFlexiDates(eventDate.substring(comma+1), beginDate, endDate))
			return true;

		return false;
	}
	
	private boolean isWithinFlexiDates(String flexiDate, String searchBeginDate, String searchEndDate)
	{		
		if (flexiDate.indexOf(MartusFlexidate.FLEXIDATE_RANGE_DELIMITER) < 0)
			return false;
					
		MartusFlexidate mf = new MartusFlexidate(flexiDate);		
		DateFormat df = Bulletin.getStoredDateFormat();						
		String beginDate = df.format(mf.getBeginDate());
		String endDate = df.format(mf.getEndDate());

		if (beginDate.compareTo(searchEndDate) > 0 ||
			endDate.compareTo(searchBeginDate) < 0)
			return false;		
		return true;
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

	public static DateFormat getStoredDateFormat()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);
		return df;
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
		clear();
		
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

		MartusCrypto security = getSignatureGenerator();
		AttachmentProxy[] attachmentPublicProxies = other.getPublicAttachments();
		for(int aIndex = 0; aIndex < attachmentPublicProxies.length; ++aIndex)
		{
			AttachmentProxy ap = attachmentPublicProxies[aIndex];
			ap = getAsFileProxy(ap, otherDatabase, Bulletin.STATUSDRAFT, security);
			addPublicAttachment(ap);
		}

		AttachmentProxy[] attachmentPrivateProxies = other.getPrivateAttachments();
		for(int aIndex = 0; aIndex < attachmentPrivateProxies.length; ++aIndex)
		{
			AttachmentProxy ap = attachmentPrivateProxies[aIndex];
			ap = getAsFileProxy(ap, otherDatabase, Bulletin.STATUSDRAFT, security);
			addPrivateAttachment(ap);
		}

	}
	
	private void pullFields(Bulletin other, FieldSpec[] fields)
	{
		for(int f = 0; f < fields.length; ++f)
		{
			set(fields[f].getTag(), other.get(fields[f].getTag()));
		}
	}

	public AttachmentProxy getAsFileProxy(AttachmentProxy ap, ReadableDatabase otherDatabase, String status, MartusCrypto security)
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

	static String getFirstOfThisYear()
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.MONTH, 0);
		cal.set(GregorianCalendar.DATE, 1);
		DateFormat df = getStoredDateFormat();
		return df.format(cal.getTime());
	}

	public static String getLastDayOfThisYear()
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.MONTH, 11);
		cal.set(GregorianCalendar.DATE, 31);
		DateFormat df = getStoredDateFormat();
		return df.format(cal.getTime());
	}

	public static String getToday()
	{
		DateFormat df = getStoredDateFormat();
		return df.format(new Date());
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
	
	private boolean isValidFlag;
	private MartusCrypto security;
	private BulletinHeaderPacket header;
	private FieldDataPacket fieldData;
	private FieldDataPacket privateFieldData;
}
