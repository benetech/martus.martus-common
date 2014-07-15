/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2002-2014, Beneficent
Technology, Inc. (The Benetech Initiative).

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
package org.martus.common.network;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.martus.common.packet.UniversalId;
import org.miradi.utils.EnhancedJsonArray;
import org.miradi.utils.EnhancedJsonObject;

public class SummaryOfAvailableBulletins
{
	public SummaryOfAvailableBulletins()
	{
		accountsMap = new HashMap<String, Set<ShortServerBulletinSummary>>();
		highestServerTimestamp = "";
		countOfSummaries = 0;
	}
	
	public void addBulletin(ServerBulletinSummary summary) throws Exception
	{
		String serverTimestamp = summary.getServerTimestamp();
		++countOfSummaries;
		if(serverTimestamp.compareTo(highestServerTimestamp) > 0)
			highestServerTimestamp = serverTimestamp;

		UniversalId uid = summary.getUniversalId();
		String authorAccountId = uid.getAccountId();
		Set<ShortServerBulletinSummary> infosForAccount = accountsMap.get(authorAccountId);
		if(infosForAccount == null)
		{
			infosForAccount = new HashSet<ShortServerBulletinSummary>();
			accountsMap.put(authorAccountId, infosForAccount);
		}
		
		String localId = uid.getLocalId();
		String lastSavedTime = summary.getLastModified();
		ShortServerBulletinSummary shortSummary = new ShortServerBulletinSummary(localId, lastSavedTime, serverTimestamp);
		infosForAccount.add(shortSummary);
	}
	
	public int size()
	{
		return countOfSummaries;
	}
	
	public String getNextServerTimestamp() throws Exception
	{
		if(highestServerTimestamp.length() == 0)
			return "";
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date highest = df.parse(highestServerTimestamp);
		long highestMillisSinceEpoch = highest.getTime();
		long nextMillisSinceEpoch = highestMillisSinceEpoch + 1;
		String nextServerTimestamp = df.format(new Date(nextMillisSinceEpoch));
		return nextServerTimestamp;
	}
	
	public Set<String> getAccountIds()
	{
		Set<String> accountIds = new HashSet<String>();
		accountIds.addAll(accountsMap.keySet());
		return accountIds;
	}
	
	public Set<ShortServerBulletinSummary> getSummaries(String accountId)
	{
		Set<ShortServerBulletinSummary> summaries = new HashSet<ShortServerBulletinSummary>();
		summaries.addAll(accountsMap.get(accountId));
		return summaries;
	}

	public EnhancedJsonObject toJson() throws Exception
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		
		json.put(JSON_KEY_COUNT, size());
		
		String nextServerTimestamp = getNextServerTimestamp();
		json.put(JSON_KEY_NEXT_SERVER_TIMESTAMP, nextServerTimestamp);

		EnhancedJsonArray accounts = new EnhancedJsonArray();
		json.put(JSON_KEY_ACCOUNTS, accounts);

		Iterator<String> accountIterator = accountsMap.keySet().iterator();
		while(accountIterator.hasNext())
		{
			String authorAccountId = accountIterator.next();

			EnhancedJsonObject account = new EnhancedJsonObject();
			accounts.put(account);
			account.put(JSON_KEY_AUTHOR_ACCOUNT_ID, authorAccountId);
			
			EnhancedJsonArray bulletins = new EnhancedJsonArray();
			account.put(JSON_KEY_BULLETINS, bulletins);

			Set<ShortServerBulletinSummary> setOfBulletins = accountsMap.get(authorAccountId);
			Iterator<ShortServerBulletinSummary> bulletinIterator = setOfBulletins.iterator();
			while(bulletinIterator.hasNext())
			{
				ShortServerBulletinSummary summary = bulletinIterator.next();
				String localId = summary.getLocalId();
				String lastModifiedIso = summary.getLastModified();

				EnhancedJsonObject bulletinInfo = new EnhancedJsonObject();
				bulletins.put(bulletinInfo);

				bulletinInfo.put(JSON_KEY_LOCAL_ID, localId);
				bulletinInfo.put(JSON_KEY_LAST_MODIFIED, lastModifiedIso);
			}
		}
		
		return json;
	}
	
	public final static String JSON_KEY_COUNT = "Count";
	public final static String JSON_KEY_NEXT_SERVER_TIMESTAMP = "NextServerTimestamp";
	public final static String JSON_KEY_ACCOUNTS = "Accounts";
	public final static String JSON_KEY_BULLETINS = "Bulletins";
	public final static String JSON_KEY_AUTHOR_ACCOUNT_ID = "AuthorAccountId";
	public final static String JSON_KEY_LOCAL_ID = "LocalId";
	public final static String JSON_KEY_LAST_MODIFIED = "LastModified";
	public static final String JSON_KEY_EARLIEST_SERVER_TIMESTAMP = "EarliestServerTimestamp";
	
	private Map<String, Set<ShortServerBulletinSummary>> accountsMap;
	private String highestServerTimestamp;
	private int countOfSummaries;
}
