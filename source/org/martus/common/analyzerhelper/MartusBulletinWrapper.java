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
import java.util.zip.ZipFile;
import org.martus.common.MartusUtilities.ServerErrorException;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.common.bulletin.BulletinLoader;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.database.ClientFileDatabase;
import org.martus.common.database.DatabaseKey;
import org.martus.common.packet.UniversalId;


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
	
			ClientFileDatabase db = new ClientFileDatabase(tempDirectory, security);
			db.initialize();
			ZipFile zipFile = new ZipFile(bulletinZipFile);
			BulletinZipUtilities.importBulletinPacketsFromZipFileToDatabase(db, null, zipFile, security);
			zipFile.close();
			DatabaseKey key = new DatabaseKey(uid);
			bulletin = BulletinLoader.loadFromDatabase(db, key, security);
			
			//TODO:Once we implement the ability to have attachments we will not delete the attachments
			//but mark them all deleteOnExit, and also implement a cleanup function which must be called when this object is no longer needed
			//which will then delete the attachments, and the database.
			deleteAllAttachments();
			db.deleteAllData();

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
	
	private void deleteAllAttachments()
	{
		AttachmentProxy[] publicAttachments = bulletin.getPublicAttachments();
		for(int i = 0; i < publicAttachments.length; ++i)
		{
			publicAttachments[i].getFile().delete();
		}
		
		AttachmentProxy[] privateAttachments = bulletin.getPublicAttachments();
		for(int i = 0; i < privateAttachments.length; ++i)
		{
			privateAttachments[i].getFile().delete();
		}
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
	
	public String getEventDate()
	{
		return bulletin.get(BulletinConstants.TAGEVENTDATE);
	}
	
	public String getEntryDate()
	{
		return bulletin.get(BulletinConstants.TAGENTRYDATE);
	}
	
	private Bulletin bulletin;
}
