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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;

public class BulletinSummary
{
	public static BulletinSummary createFromString(String accountId, String parameters) throws WrongValueCount
	{
		String args[] = parameters.split(fieldDelimeter, -1);
		if(args.length < 3)
			throw new WrongValueCount(args.length);
		
		int at = 0;
		String bulletinLocalId= args[at++];
		String fdpLocalId = args[at++];
		int size = Integer.parseInt(args[at++]);
		String date = "";
		if(args.length > at)
			date = args[at++];
		
		UniversalId uid = UniversalId.createFromAccountAndLocalId(accountId, bulletinLocalId);
		return new BulletinSummary(uid, fdpLocalId, size, date);
	}

	private BulletinSummary(UniversalId bulletinIdToUse, String fieldDataPacketLocalIdToUse, int sizeToUse, String dateSavedToUse)
	{
		accountId = bulletinIdToUse.getAccountId();
		localId = bulletinIdToUse.getLocalId();
		fdpLocalId = fieldDataPacketLocalIdToUse;
		size = sizeToUse;
		dateTimeSaved = dateSavedToUse; 
	}
	
	public void setFieldDataPacket(FieldDataPacket fdpToUse)
	{
		fdp = fdpToUse;
		fdpLocalId = fdp.getLocalId();
		title = fdpToUse.get(Bulletin.TAGTITLE);
		author = fdpToUse.get(Bulletin.TAGAUTHOR);
	}

	public void setChecked(boolean newValue)
	{
		if(downloadable)
			checkedFlag = newValue;
	}

	public boolean isChecked()
	{
		return checkedFlag;
	}
	
	public UniversalId getUniversalId()
	{
		return UniversalId.createFromAccountAndLocalId(getAccountId(), getLocalId());
	}

	public String getAccountId()
	{
		return accountId;
	}

	public String getLocalId()
	{
		return localId;
	}
	
	public String getFieldDataPacketLocalId()
	{
		return fdpLocalId;
	}

	public String getTitle()
	{
		return title;
	}

	public String getAuthor()
	{
		return author;
	}
	
	
	public String getDateTimeSaved()
	{
		String dateToConvert = dateTimeSaved;
		return getLastDateTimeSaved(dateToConvert);
	}

	static public String getLastDateTimeSaved(String dateToConvert)
	{
		if(dateToConvert.length() == 0)
			return "";
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(Long.parseLong(dateToConvert));		
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(cal.getTime());
	}

	public boolean isDownloadable()
	{
		return downloadable;
	}

	public void setDownloadable(boolean downloadable)
	{
		this.downloadable = downloadable;
	}

	public int getSize()
	{
		return size;
	}

	public FieldDataPacket getFieldDataPacket()
	{
		return fdp;
	}

	public static Vector getNormalRetrieveTags()
	{
		Vector tags = new Vector();
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_SIZE);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_DATE_SAVED);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_HISTORY);
		return tags;
	}
	
	public static class WrongValueCount extends Exception
	{
		WrongValueCount(int gotCount)
		{
			got = gotCount;
			expected = getNormalRetrieveTags().size();
		}
		
		public int got;
		public int expected;
	}

	private FieldDataPacket fdp;
	private String accountId;
	private String fdpLocalId;
	String localId;
	String title;
	String author;
	String dateTimeSaved;
	int size;
	boolean checkedFlag;
	boolean downloadable;
	public final static String fieldDelimeter = "=";
}
