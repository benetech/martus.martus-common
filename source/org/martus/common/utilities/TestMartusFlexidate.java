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

import java.util.Date;

import org.martus.util.MultiCalendar;
import org.martus.util.TestCaseEnhanced;


public class TestMartusFlexidate extends TestCaseEnhanced
{

	public TestMartusFlexidate(String name)
	{
		super(name);
	}

	public void testToStoredDateFormat()
	{
		final int APRIL = 4;
		MultiCalendar cal = new MultiCalendar();

		cal.setGregorian(2005, APRIL, 7);
		MultiCalendar goodDate = cal;
		assertEquals("2005-04-07", MartusFlexidate.toStoredDateFormat(goodDate));

		Date epoch = new Date(0);
		cal.setTime(epoch);
		assertEquals("1970-01-01", MartusFlexidate.toStoredDateFormat(cal));
		
		Date beforeEpochDate = new Date(-1234567890);
		cal.setTime(beforeEpochDate);
		assertEquals("1970-01-01", MartusFlexidate.toStoredDateFormat(cal));

		cal.setGregorian(2548, APRIL, 3);
		MultiCalendar thaiDate = cal;
		assertEquals("2548-04-03", MartusFlexidate.toStoredDateFormat(thaiDate));

		cal.setGregorian(9998, 18, 40);
		MultiCalendar wayFutureDate = cal;
		assertEquals("9999-07-10", MartusFlexidate.toStoredDateFormat(wayFutureDate));

		cal.setGregorian(8, APRIL, 3);
		MultiCalendar ancientDate = cal;
		assertEquals("0008-04-03", MartusFlexidate.toStoredDateFormat(ancientDate));

	}
	
	public void testRangesAndDaylightSavings() throws Exception
	{
		final int MAR = 3;
		final int APR = 4;
		final int MAY = 5;
		MultiCalendar marDate = MultiCalendar.createFromGregorianYearMonthDay(2005, MAR, 29);
		MultiCalendar aprDate1 = MultiCalendar.createFromGregorianYearMonthDay(2005, APR, 1);
		MultiCalendar aprDate2 = MultiCalendar.createFromGregorianYearMonthDay(2005, APR, 5);
		MultiCalendar mayDate = MultiCalendar.createFromGregorianYearMonthDay(2005, MAY, 3);
		String marToApr1String = MartusFlexidate.toBulletinFlexidateFormat(marDate, aprDate1);
		assertEquals("2005-03-29,20050329+3", marToApr1String);
		String marToApr2String = MartusFlexidate.toBulletinFlexidateFormat(marDate, aprDate2);
		assertEquals("2005-03-29,20050329+7", marToApr2String);
		String apr2ToMayString = MartusFlexidate.toBulletinFlexidateFormat(aprDate2, mayDate);
		assertEquals("2005-04-05,20050405+28", apr2ToMayString);
		String marToMayString = MartusFlexidate.toBulletinFlexidateFormat(marDate, mayDate);
		assertEquals("2005-03-29,20050329+35", marToMayString);
	}
}
