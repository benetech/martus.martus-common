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

import java.text.ParseException;
import java.util.Calendar;

import org.hrvd.util.date.Flexidate;
import org.martus.common.fieldspec.FieldSpec;

public class MartusFlexidate
{
	/*
	 * this appears to take a string in the form: 19891201+300
	 */
	public MartusFlexidate(String dateStr)
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
	
	public MartusFlexidate(Calendar beginDate, Calendar endDate)
	{
		flexiDate = new Flexidate(beginDate, endDate);
	}
		
	public String getMartusFlexidateString() 
	{				
		return flexiDate.getDateAsNumber()+FLEXIDATE_RANGE_DELIMITER+flexiDate.getRange();
	}	
	
	public Calendar getBeginDate()
	{		
		return flexiDate.getCalendarLow();
	}
	
	/* this expects a string in one of these forms:
	 * 	1989-12-01
	 *  1989-12-01,19891201+300
 	 */
	public static MartusFlexidate createFromMartusDateString(String dateStr)
	{
		int comma = dateStr.indexOf(DATE_RANGE_SEPARATER);
		if (comma >= 0)
		{
			String beginDate = dateStr.substring(comma+1);
			return new MartusFlexidate(beginDate);
		}
		
		try
		{
			Calendar cal = FieldSpec.yyyymmddWithDashesToCalendar(dateStr);
			return new MartusFlexidate(cal, cal);
		}
		catch(ParseException e)
		{			
			return new MartusFlexidate("19000101+0");
		}
	}
	
	public Calendar getEndDate()
	{					
		return ((hasDateRange()) ? flexiDate.getCalendarHigh() : getBeginDate());
	}	
	
	public boolean hasDateRange()
	{
		return (flexiDate.getRange() > 0)? true:false;
	}

	public static String toStoredDateFormat(Calendar date)
	{		
		return FieldSpec.calendarToYYYYMMDD(date);				
	}

	public static String toFlexidateFormat(Calendar beginDate, Calendar endDate)
	{		
		return new MartusFlexidate(beginDate, endDate).getMartusFlexidateString();
	}		
		
	public static String toStoredDateFormat(Calendar beginDate, Calendar endDate)
	{
		return FieldSpec.calendarToYYYYMMDD(beginDate) + 
					DATE_RANGE_SEPARATER +
					toFlexidateFormat(beginDate, endDate);
	}

	Flexidate flexiDate;
	public static final String 	FLEXIDATE_RANGE_DELIMITER = "+";	
	public static final String	DATE_RANGE_SEPARATER = ",";
}
