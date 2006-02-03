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

package org.martus.common.utilities;

import java.util.GregorianCalendar;

import org.martus.util.MultiCalendar;


public class DateUtilities
{
	public static String getStartDateRange(String storedFlexidateString)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromBulletinFlexidateFormat(storedFlexidateString);
		return MartusFlexidate.toStoredDateFormat(mfd.getBeginDate());
	}


	public static String getEndDateRange(String storedFlexidateString)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromBulletinFlexidateFormat(storedFlexidateString);
		if (!mfd.hasDateRange())
			return "";
		return MartusFlexidate.toStoredDateFormat(mfd.getEndDate());
	}


	public static String getFirstOfThisYearInStoredFormat()
	{
		int thisYear = new MultiCalendar().getGregorianYear();
		MultiCalendar cal = MultiCalendar.createFromGregorianYearMonthDay(thisYear, 1, 1);
		return MartusFlexidate.toStoredDateFormat(cal);
	}



	public static String getTodayInStoredFormat()
	{
		GregorianCalendar realTodayInOurTimeZone = new GregorianCalendar();
		MultiCalendar mc = new MultiCalendar(realTodayInOurTimeZone);
		String result = MartusFlexidate.toStoredDateFormat(mc);
		return result;
	}
}
