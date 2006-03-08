/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2006, Beneficent
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

import org.hrvd.util.date.Flexidate;
import org.martus.util.MultiCalendar;

public class MartusFlexidate
{
	public MartusFlexidate(MultiCalendar beginDate, MultiCalendar endDate)
	{
		flexiDate = new Flexidate(beginDate.getTime(), endDate.getTime());
	}
		
	public MartusFlexidate(String isoBeginDate, int range)
	{
		MultiCalendar cal = MultiCalendar.createFromIsoDateString(isoBeginDate);
		flexiDate = new Flexidate(cal.getGregorianYear(), cal.getGregorianMonth(), cal.getGregorianDay(), range);		
	}
	
	/* this will convert a string in in one of these forms:
	 * 1989-12-01,1989-12-15
	 * 1989-12-15,1989-12-01
	 * and return it as a MartusFlexidate in the form 1989-12-01,19891201+15
 	 */
	public static String createMartusDateStringFromBeginAndEndDateString(String dateRange)
	{
		int comma = dateRange.indexOf(DATE_RANGE_SEPARATER);
		if (comma == -1)
			return null;
		String beginDate = dateRange.substring(0,comma);
		String endDate = dateRange.substring(comma+1);
		MultiCalendar calBeginDate = MultiCalendar.createFromIsoDateString(beginDate);
		MultiCalendar calEndDate = MultiCalendar.createFromIsoDateString(endDate);
		MartusFlexidate flexidate = new MartusFlexidate(calBeginDate, calEndDate);
		String startDate = beginDate;
		if(calBeginDate.after(calEndDate))
			startDate = endDate;
		return startDate+DATE_RANGE_SEPARATER+flexidate.getMartusFlexidateString();
	
	}
	
	
	
	public static String extractIsoDateFromStoredDate(String storedDate)
	{
		String internalFlexidateString = MartusFlexidate.extractInternalFlexidateFromStoredDate(storedDate);
		String year = internalFlexidateString.substring(0, 4);
		String month = internalFlexidateString.substring(4, 6);
		String day = internalFlexidateString.substring(6, 8);
		return year + "-" + month + "-" + day;
	}

	public static boolean isFlexidateString(String dateStr)
	{
		return dateStr.indexOf(DATE_RANGE_SEPARATER) >= 0;
	}

	public static int extractRangeFromStoredDate(String storedDate)
	{
		String internalFlexidateString = MartusFlexidate.extractInternalFlexidateFromStoredDate(storedDate);
		int plusAt = internalFlexidateString.indexOf(FLEXIDATE_RANGE_DELIMITER);
		if (plusAt < 0)
			return 0;
		
		String rangeStr = internalFlexidateString.substring(plusAt+1);
		return Integer.parseInt(rangeStr);			
	}

	public static String toBulletinFlexidateFormat(MultiCalendar beginDate, MultiCalendar endDate)
	{
		return beginDate.toIsoDateString() + 
					DATE_RANGE_SEPARATER +
					toFlexidateFormat(beginDate, endDate);
	}

	public String getMartusFlexidateString() 
	{				
		return flexiDate.getDateAsNumber()+FLEXIDATE_RANGE_DELIMITER+flexiDate.getRange();
	}	
	
	public MultiCalendar getBeginDate()
	{
		MultiCalendar cal = new MultiCalendar((GregorianCalendar)flexiDate.getCalendarLow());
		return cal;
	}
	
	public MultiCalendar getEndDate()
	{
		MultiCalendar endDate = new MultiCalendar((GregorianCalendar)flexiDate.getCalendarHigh());
		return ((hasDateRange()) ? endDate : getBeginDate());
	}	
	
	public boolean hasDateRange()
	{
		return (flexiDate.getRange() > 0)? true:false;
	}

	public static String toStoredDateFormat(MultiCalendar date)
	{		
		return date.toIsoDateString();				
	}

	public static String toFlexidateFormat(MultiCalendar beginDate, MultiCalendar endDate)
	{		
		return new MartusFlexidate(beginDate, endDate).getMartusFlexidateString();
	}		
		
	private static String extractInternalFlexidateFromStoredDate(String dateStr)
	{
		return dateStr.substring(dateStr.indexOf(DATE_RANGE_SEPARATER)+1);
	}

	Flexidate flexiDate;
	public static final String 	FLEXIDATE_RANGE_DELIMITER = "+";	
	public static final String	DATE_RANGE_SEPARATER = ",";
}
