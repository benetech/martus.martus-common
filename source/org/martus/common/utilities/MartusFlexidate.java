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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hrvd.util.date.Flexidate;
import org.martus.common.bulletin.Bulletin;

public class MartusFlexidate
{
	/*
	 * this appears to take a string in the form: 19891201+300
	 */
	public MartusFlexidate(String dateStr)
	{		
		parseString(dateStr);		
	}		
	
	public MartusFlexidate(Date beginDate, Date endDate)
	{
		setDateRange(beginDate, endDate);
	}
		
	private void setDateRange(Date beginDate, Date endDate)
	{	
		flexiDate = new Flexidate(normalizeDate(beginDate), normalizeDate(endDate));	
	}
	
	public Date normalizeDate(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(GregorianCalendar.YEAR);
		int month = calendar.get(GregorianCalendar.MONTH);
		int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		int NOON = 12;
		calendar.set(year, month, day, NOON, 0, 0);
		return calendar.getTime();
	}
	
	private void parseString(String flexiDateStr)
	{
		int plus = flexiDateStr.indexOf(FLEXIDATE_RANGE_DELIMITER);
		String dateStr = flexiDateStr;
		int range =0;
		if (plus > 0)
		{
			dateStr = flexiDateStr.substring(0, plus);
			String rangeStr = flexiDateStr.substring(plus+1);
			range = new Integer(rangeStr).intValue();			
		}							
		
		flexiDate = new Flexidate(new Long(dateStr).longValue(), range);
	}	
		
	public String getMatusFlexidate() 
	{				
		return flexiDate.getDateAsNumber()+FLEXIDATE_RANGE_DELIMITER+flexiDate.getRange();
	}	
	
	public Date getBeginDate()
	{		
		return flexiDate.getCalendarLow().getTime();
	}
	
	/* this expects a string in one of these forms:
	 * 	1989-12-01
	 *  1989-12-01,19891201+300
 	 */
	public static MartusFlexidate createFromMartusDateString(String dateStr)
	{
		DateFormat df = Bulletin.getStoredDateFormat();
		Date d = null;
		int comma = dateStr.indexOf(DATE_RANGE_SEPARATER);
		if (comma >= 0)
		{
			String beginDate = dateStr.substring(comma+1);
			return new MartusFlexidate(beginDate);
		}
		
		try
		{
			d = df.parse(dateStr);			
		}
		catch(ParseException e)
		{			
			return new MartusFlexidate("19000101+0");
		}				
		return new MartusFlexidate(d,d);
	}
	
	public Date getEndDate()
	{					
		return ((hasDateRange())? flexiDate.getCalendarHigh().getTime(): getBeginDate());
	}	
	
	public boolean hasDateRange()
	{
		return (flexiDate.getRange() > 0)? true:false;
	}

	public static String toStoredDateFormat(Date date)
	{		
		return Bulletin.getStoredDateFormat().format(date);				
	}

	public static String toFlexidateFormat(Date beginDate, Date endDate)
	{		
		return new MartusFlexidate(beginDate, endDate).getMatusFlexidate();
	}		
		
	Flexidate flexiDate;
	public static final String 	FLEXIDATE_RANGE_DELIMITER = "+";	
	public static final String	DATE_RANGE_SEPARATER = ",";
}
