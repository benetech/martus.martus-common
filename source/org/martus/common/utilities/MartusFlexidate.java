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

package org.martus.common.utilities;

import java.util.Calendar;

import org.hrvd.util.date.Flexidate;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.util.MartusCalendar;

public class MartusFlexidate
{
	public MartusFlexidate(MartusCalendar beginDate, MartusCalendar endDate)
	{
		flexiDate = new Flexidate(beginDate.getCalendar(), endDate.getCalendar());
	}
		
	private MartusFlexidate(String dateStr)
	{		
		int plus = dateStr.indexOf(FLEXIDATE_RANGE_DELIMITER);
		String dateStr1 = dateStr;
		int range =0;
		if (plus > 0)
		{
			dateStr1 = dateStr.substring(0, plus);
			String rangeStr = dateStr.substring(plus+1);
			range = new Integer(rangeStr).intValue();			
		}							
		
		flexiDate = new Flexidate(new Long(dateStr1).longValue(), range);		
	}		

	public static MartusFlexidate createFromInternalMartusFlexidateString (String internalFormat)
	{
		return new MartusFlexidate(internalFormat);
	}

	/* this expects a string in one of these forms:
	 * 	1989-12-01
	 *  1989-12-01,19891201+300
 	 */
	public static MartusFlexidate createFromBulletinFlexidateFormat(String dateStr)
	{
		int comma = dateStr.indexOf(DATE_RANGE_SEPARATER);
		if (comma >= 0)
		{
			String beginDate = dateStr.substring(comma+1);
			return new MartusFlexidate(beginDate);
		}
		
		try
		{
			MartusCalendar cal = FieldSpec.yyyymmddWithDashesToCalendar(dateStr);
			return new MartusFlexidate(cal, cal);
		}
		catch(Exception e)
		{			
			return new MartusFlexidate("19000101+0");
		}
	}

	public static String toBulletinFlexidateFormat(MartusCalendar beginDate, MartusCalendar endDate)
	{
		return FieldSpec.calendarToYYYYMMDD(beginDate) + 
					DATE_RANGE_SEPARATER +
					toFlexidateFormat(beginDate, endDate);
	}

	/* this will convert a string in in one of these forms:
	 * 1989-12-01,1989-12-15
	 * 1989-12-15,1989-12-01
	 * and return it as a MartusFlexidate in the form 1989-12-01,19891201+15
 	 */
	public static String createMartusDateStringFromDateRange(String dateRange)
	{
		int comma = dateRange.indexOf(DATE_RANGE_SEPARATER);
		if (comma == -1)
			return null;
		String beginDate = dateRange.substring(0,comma);
		String endDate = dateRange.substring(comma+1);
		MartusCalendar calBeginDate = FieldSpec.yyyymmddWithDashesToCalendar(beginDate);
		MartusCalendar calEndDate = FieldSpec.yyyymmddWithDashesToCalendar(endDate);
		MartusFlexidate flexidate = new MartusFlexidate(calBeginDate, calEndDate);
		String startDate = beginDate;
		if(calBeginDate.after(calEndDate))
			startDate = endDate;
		return startDate+DATE_RANGE_SEPARATER+flexidate.getMartusFlexidateString();
	
	}
	
	public String getMartusFlexidateString() 
	{				
		return flexiDate.getDateAsNumber()+FLEXIDATE_RANGE_DELIMITER+flexiDate.getRange();
	}	
	
	public MartusCalendar getBeginDate()
	{
		Calendar flexidateCal = flexiDate.getCalendarLow(); 
		MartusCalendar cal = new MartusCalendar();
		cal.set(Calendar.YEAR, flexidateCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, flexidateCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, flexidateCal.get(Calendar.DAY_OF_MONTH));
		return cal;
	}
	
	public MartusCalendar getEndDate()
	{
		MartusCalendar endDate = new MartusCalendar(flexiDate.getCalendarHigh());
		return ((hasDateRange()) ? endDate : getBeginDate());
	}	
	
	public boolean hasDateRange()
	{
		return (flexiDate.getRange() > 0)? true:false;
	}

	public static String toStoredDateFormat(MartusCalendar date)
	{		
		return FieldSpec.calendarToYYYYMMDD(date);				
	}

	public static String toFlexidateFormat(MartusCalendar beginDate, MartusCalendar endDate)
	{		
		return new MartusFlexidate(beginDate, endDate).getMartusFlexidateString();
	}		
		
	Flexidate flexiDate;
	public static final String 	FLEXIDATE_RANGE_DELIMITER = "+";	
	public static final String	DATE_RANGE_SEPARATER = ",";
}
