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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.zip.ZipFile;
import org.martus.common.BulletinStore;
import org.martus.common.MartusUtilities.ServerErrorException;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.common.bulletin.BulletinLoader;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.database.ClientFileDatabase;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.packet.UniversalId;
import org.martus.common.utilities.MartusFlexidate;


public class MartusBulletinWrapper
{
	public MartusBulletinWrapper(UniversalId uid, File bulletinZipFile, MartusSecurity security) throws ServerErrorException
	{
		File tempDirectory = null;
		try
		{
			tempDirectory = File.createTempFile("$$$BulletinWrapperDB", null);
			tempDirectory.deleteOnExit();
			tempDirectory.delete();
			tempDirectory.mkdirs();
			File dbDirectory = new File(tempDirectory, "packets");
	
			store = new BulletinStore();
			store.setSignatureGenerator(security);
			store.doAfterSigninInitialization(tempDirectory, new ClientFileDatabase(dbDirectory, security));
			ZipFile zipFile = new ZipFile(bulletinZipFile);
			store.importBulletinZipFile(zipFile);
			zipFile.close();
			DatabaseKey key = DatabaseKey.createLegacyKey(uid);
			bulletin = BulletinLoader.loadFromDatabase(store.getDatabase(), key, security);
			if(bulletin == null)
				throw new ServerErrorException("No Bulletin?");


		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new ServerErrorException(e.getMessage());
		}
		finally
		{
			bulletinZipFile.delete();
			tempDirectory.delete();
		}
	}
	
	public void deleteAllData() 
	{
		deleteAllAttachments();
		try
		{
			store.deleteAllData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isAllPrivate()
	{
		return bulletin.isAllPrivate();
	}
	
	public String getTitle()
	{
		return bulletin.get(BulletinConstants.TAGTITLE);
	}
	
	public String getAuthor()
	{
		return bulletin.get(BulletinConstants.TAGAUTHOR);
	}
	
	public String getKeyWords()
	{
		return bulletin.get(BulletinConstants.TAGKEYWORDS);
	}
	
	public String getLanguage()
	{
		return bulletin.get(BulletinConstants.TAGLANGUAGE);
	}
	
	public String getLocation()
	{
		return bulletin.get(BulletinConstants.TAGLOCATION);
	}
	
	public String getOrganization()
	{
		return bulletin.get(BulletinConstants.TAGORGANIZATION);
	}
	
	public String getSummary()
	{
		return bulletin.get(BulletinConstants.TAGSUMMARY);
	}
	
	public String getPublicInfo()
	{
		return bulletin.get(BulletinConstants.TAGPUBLICINFO);
	}

	public String getPrivateInfo()
	{
		return bulletin.get(BulletinConstants.TAGPRIVATEINFO);
	}
	
	public MartusFlexidate getEventDate()
	{
		String rawEventDate = bulletin.get(BulletinConstants.TAGEVENTDATE);
		return MartusFlexidate.createFromMartusDateString(rawEventDate);
	}
	
	public Date getEntryDate()
	{
		String entryDate = bulletin.get(BulletinConstants.TAGENTRYDATE);
		DateFormat dfStored = Bulletin.getStoredDateFormat();
		try
		{
			return dfStored.parse(entryDate);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public File[] getPublicAttachments()
	{
		return getFileAttachments(bulletin.getPublicAttachments());
	}
	
	public File[] getPrivateAttachments()
	{
		return getFileAttachments(bulletin.getPrivateAttachments());
	}

	private File[] getFileAttachments(AttachmentProxy[] attachmentProxies)
	{
		File[] publicAttachmentFiles = new File[attachmentProxies.length];
		for(int i = 0; i < attachmentProxies.length; ++i)
		{
			publicAttachmentFiles[i] = getFileFromProxy(attachmentProxies[i]);
		}
		return publicAttachmentFiles;
	}
	
	private File getFileFromProxy(AttachmentProxy attachmentProxy)
	{
		File file = attachmentProxy.getFile();
		if(file != null)
			return file;

		try
		{
			ReadableDatabase db = store.getDatabase();
			file = new File(System.getProperty("user.home"), attachmentProxy.getLabel());
			file.deleteOnExit();
			BulletinLoader.extractAttachmentToFile(db, attachmentProxy, store.getSignatureVerifier(), file);
			return file;
		}
		catch(Exception e)
		{
			System.out.println("Unable to save file :" + e);
			e.printStackTrace();
			return null;
		}
	}

private void deleteAllAttachments()
	{
		
		AttachmentProxy[] publicAttachments = bulletin.getPublicAttachments();
		if(publicAttachments != null)
		{
			for(int i = 0; i < publicAttachments.length; ++i)
			{
				File file = publicAttachments[i].getFile();
				if(file != null)
					file.delete();
			}
		}
		bulletin.clearPublicAttachments();
		
		AttachmentProxy[] privateAttachments = bulletin.getPublicAttachments();
		if(privateAttachments != null)
		{
			for(int i = 0; i < privateAttachments.length; ++i)
			{
				File file = privateAttachments[i].getFile();
				if(file != null)
					file.delete();
			}
		}
		bulletin.clearPrivateAttachments();
	}
	
	
	
	private Bulletin bulletin;
	private BulletinStore store;
}
