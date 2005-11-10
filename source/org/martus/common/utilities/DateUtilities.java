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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.martus.common.fieldspec.ChoiceItem;
import org.martus.util.MartusCalendar;


public class DateUtilities
{
	public static String getMdyOrder(String format)
	{
		String result = "";
		format = format.toLowerCase();
		for(int i = 0; i < format.length(); ++i)
		{
			char c = format.charAt(i);
			if( (c == 'm' || c == 'd' || c == 'y') && (result.indexOf(c) < 0) )
				result += c;
		}

		return result;
	}

	public static String getDefaultDateFormatCode()
	{
		return DateUtilities.MDY_SLASH.getCode();
	}

	public static ChoiceItem[] getDateFormats()
	{
		return new ChoiceItem[]
		{
			DateUtilities.MDY_SLASH,
			DateUtilities.DMY_SLASH,
			DateUtilities.DMY_DOT
		};
	}


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


	public static String getFirstOfThisYear()
	{
		MartusCalendar cal = new MartusCalendar();
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		return MartusFlexidate.toStoredDateFormat(cal);
	}


	public static String getLastDayOfThisYear()
	{
		MartusCalendar cal = new MartusCalendar();
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 31);
		return MartusFlexidate.toStoredDateFormat(cal);
	}


	public static String getToday()
	{
		Calendar realTodayInOurTimeZone = new GregorianCalendar();
		MartusCalendar mc = new MartusCalendar(realTodayInOurTimeZone);
		String result = MartusFlexidate.toStoredDateFormat(mc);
		return result;
	}


	public static ChoiceItem DMY_SLASH = new ChoiceItem("dd/MM/yyyy", "dd/mm/yyyy");
	public static ChoiceItem MDY_SLASH = new ChoiceItem("MM/dd/yyyy", "mm/dd/yyyy");
	public static ChoiceItem DMY_DOT = new ChoiceItem("dd.MM.yyyy", "dd.mm.yyyy");	
}
