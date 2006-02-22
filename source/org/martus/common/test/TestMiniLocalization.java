/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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

package org.martus.common.test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.martus.common.MiniLocalization;
import org.martus.util.MultiCalendar;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.language.LanguageOptions;


public class TestMiniLocalization extends TestCaseEnhanced
{
	public TestMiniLocalization(String name)
	{
		super(name);
	}
	
	public void testCurrentCalendarSystem() throws Exception
	{
		MiniLocalization localization = new MiniLocalization();
		assertEquals("Didn't default to gregorian?", "Gregorian", localization.getCurrentCalendarSystem());
		
		localization.setCurrentCalendarSystem("Thai");
		assertEquals("Didn't set to Thai?", "Thai", localization.getCurrentCalendarSystem());
		
		try
		{
			localization.setCurrentCalendarSystem("oiwefjoiwef");
			fail("Should throw for unrecognized calendar system");
		} 
		catch (RuntimeException ignoreExpected)
		{
		}
	}
	
	public void testGetLocalizedDateFields() throws Exception
	{
		int year = 2005;
		int month = 10;
		int day = 20;
		MultiCalendar cal = MultiCalendar.createFromGregorianYearMonthDay(year, month, day);
		
		verifyGetLocalizedFields(MiniLocalization.GREGORIAN_SYSTEM, cal, year, month, day);
		verifyGetLocalizedFields(MiniLocalization.THAI_SYSTEM, cal, year + 243, month, day);
		
		
	}

	private void verifyGetLocalizedFields(String system, MultiCalendar cal, int expectedYear, int expectedMonth, int expectedDay)
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentCalendarSystem(system);
		assertEquals(system + " year wrong?", expectedYear, localization.getLocalizedYear(cal));
		assertEquals(system + " month wrong?", expectedMonth, localization.getLocalizedMonth(cal));
		assertEquals(system + "day wrong", expectedDay, localization.getLocalizedDay(cal));
	}

	public void testCreateCalendarFromLocalizedYearMonthDay() throws Exception
	{
		int year = 2005;
		int month = 10;
		int day = 20;
		MultiCalendar reference = MultiCalendar.createFromGregorianYearMonthDay(year, month, day);
		
		verifyCreateLocalizedCalendar(reference, MiniLocalization.GREGORIAN_SYSTEM, year, month, day);
		verifyCreateLocalizedCalendar(reference, MiniLocalization.THAI_SYSTEM, year + 243, month, day);
	}

	private void verifyCreateLocalizedCalendar(MultiCalendar reference, String system, int year, int month, int day)
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentCalendarSystem(system);
		MultiCalendar cal = localization.createCalendarFromLocalizedYearMonthDay(year, month, day);
		assertEquals(system + " Not the same date?", reference, cal);
	}
	
	public void testConvertStoredDateToDisplay()
	{
		verifyConvertStoredToDisplayDate(MiniLocalization.GREGORIAN_SYSTEM, "10/20/2005", "2005-10-20");
		verifyConvertStoredToDisplayDate(MiniLocalization.THAI_SYSTEM, "10/20/2248", "2005-10-20");
		
		
		LanguageOptions.setDirectionRightToLeft();
		verifyConvertStoredToDisplayDate(MiniLocalization.GREGORIAN_SYSTEM, "2005/20/10", "2005-10-20");
		LanguageOptions.setDirectionLeftToRight();
	}

	private void verifyConvertStoredToDisplayDate(String system, String expectedDate, String isoDate)
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentCalendarSystem(system);
		assertEquals(expectedDate, localization.convertStoredDateToDisplay(isoDate));
	}


	
	public void testConvertStoredDateToDisplayNoTimeZoneOffset() throws Exception
	{
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try
		{
			verifyConvertForTimeZoneOffsetHourly(0);
		}
		finally
		{
			TimeZone.setDefault(defaultTimeZone);
		}
		
	}
	public void testConvertStoredDateToDisplayWithAllHourlyTimeZones() throws Exception
	{
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try
		{
			verifyConvertForTimeZoneOffsetHourly(0);
			for(int offset = -12; offset < 12; ++offset)
				verifyConvertForTimeZoneOffsetHourly(offset);
		}
		finally
		{
			TimeZone.setDefault(defaultTimeZone);
		}
	}
	
	public void testConvertStoredDateToDisplayWithHalfHourTimeZones() throws Exception
	{
		TimeZone defaultTimeZone = TimeZone.getDefault();
		try
		{
			for(int offset = -24; offset < 24; ++offset)
				verifyConvertForTimeZoneOffsetHalfHour(offset);
		}
		finally
		{
			TimeZone.setDefault(defaultTimeZone);
		}
		
	}

	void verifyConvertForTimeZoneOffsetHourly(int offset)
	{
		MiniLocalization loc = new MiniLocalization();
		TimeZone thisTimeZone = new SimpleTimeZone(offset*1000*60*60, "martus");
		TimeZone.setDefault(thisTimeZone);
		assertEquals("didn't set time zone?", thisTimeZone, new GregorianCalendar().getTimeZone());
    	assertEquals("bad conversion UTC +" + Integer.toString(offset), "12/31/1987", loc.convertStoredDateToDisplay("1987-12-31"));
    	assertEquals("bad conversion before 1970 UTC " + Integer.toString(offset), "12/31/1947", loc.convertStoredDateToDisplay("1947-12-31"));
	}

	void verifyConvertForTimeZoneOffsetHalfHour(int offset)
	{
		MiniLocalization loc = new MiniLocalization();
		TimeZone thisTimeZone = new SimpleTimeZone(offset*1000*60*30, "martus");
		TimeZone.setDefault(thisTimeZone);
		assertEquals("didn't set 1/2 hour time zone?", thisTimeZone, new GregorianCalendar().getTimeZone());
    	assertEquals("bad conversion UTC 1/2 hour +" + Integer.toString(offset), "12/31/1987", loc.convertStoredDateToDisplay("1987-12-31"));
    	assertEquals("bad conversion before 1970 UTC 1/2 hour +" + Integer.toString(offset), "12/31/1947", loc.convertStoredDateToDisplay("1947-12-31"));
	}
	
	public void testFormatDateTime() throws Exception
	{
    	MiniLocalization loc = new MiniLocalization();
    	
    	final int june = 5;
    	GregorianCalendar leadingZeros = new GregorianCalendar(1996, june, 1);
    	leadingZeros.set(Calendar.HOUR_OF_DAY, 7);
    	leadingZeros.set(Calendar.MINUTE, 4);
    	assertEquals("06/01/1996 07:04", loc.formatDateTime(leadingZeros.getTimeInMillis()));
    	
    	final int december = 11;
    	GregorianCalendar afternoon = new GregorianCalendar(2004, december, 9);
    	afternoon.set(Calendar.HOUR_OF_DAY, 13);
    	afternoon.set(Calendar.MINUTE, 59);
    	assertEquals("12/09/2004 13:59", loc.formatDateTime(afternoon.getTimeInMillis()));
	}
    
    public void testFormatDateTimeRightToLeft() throws Exception
	{
    	MiniLocalization loc = new MiniLocalization();
    	String rightToLeftLanguageCode = "cc";
    	loc.addRightToLeftLanguage(rightToLeftLanguageCode);
		loc.setCurrentLanguageCode(rightToLeftLanguageCode);
    	final int june = 5;
    	GregorianCalendar leadingZeros = new GregorianCalendar(1996, june, 1);
    	leadingZeros.set(Calendar.HOUR_OF_DAY, 7);
    	leadingZeros.set(Calendar.MINUTE, 4);
    	assertEquals("07:04 1996/01/06", loc.formatDateTime(leadingZeros.getTimeInMillis()));
    	
    	final int december = 11;
    	GregorianCalendar afternoon = new GregorianCalendar(2004, december, 9);
    	afternoon.set(Calendar.HOUR_OF_DAY, 13);
    	afternoon.set(Calendar.MINUTE, 59);
    	assertEquals("13:59 2004/09/12", loc.formatDateTime(afternoon.getTimeInMillis()));
    	LanguageOptions.setDirectionLeftToRight();
	}

    public void testDateUnknown()
    {
    	MiniLocalization loc = new MiniLocalization();
    	assertEquals("Should return '' for an unknown date", "", loc.formatDateTime(MiniLocalization.DATE_UNKNOWN));
    }
    
	public void testGetMdyOrder()
	{
		MiniLocalization loc = new MiniLocalization();
		assertEquals("mdy", loc.getMdyOrder());
	}
	
	public void testSetDateFormatFromLanguage()
	{
		MiniLocalization loc = new MiniLocalization();
		assertEquals("wrong default mdy?", "mdy", loc.getMdyOrder());
		loc.setCurrentLanguageCode(MiniLocalization.RUSSIAN);
		loc.setDateFormatFromLanguage();
		assertEquals("didn't set russian mdy?", "dmy", loc.getMdyOrder());
	}
}
