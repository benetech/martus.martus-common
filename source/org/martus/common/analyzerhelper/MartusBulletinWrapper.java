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

import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinConstants;


public class MartusBulletinWrapper
{
	public MartusBulletinWrapper(Bulletin bulletinToUse)
	{
		bulletin = bulletinToUse;
	}
	
	public String getTitle()
	{
		return bulletin.get(BulletinConstants.TAGTITLE);
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
