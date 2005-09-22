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
import java.util.GregorianCalendar;

import org.martus.util.TestCaseEnhanced;


public class TestMartusFlexidate extends TestCaseEnhanced
{

	public TestMartusFlexidate(String name)
	{
		super(name);
	}

	public void testToStoredDateFormat()
	{
		final int APRIL = 3;
		GregorianCalendar cal = new GregorianCalendar();

		cal.set(2005, APRIL, 7);
		Date goodDate = cal.getTime();
		assertEquals("2005-04-07", MartusFlexidate.toStoredDateFormat(goodDate));

		Date epoch = new Date(0);
		assertEquals("1969-12-31", MartusFlexidate.toStoredDateFormat(epoch));
		
		Date beforeEpochDate = new Date(-1234567);
		assertEquals("1969-12-31", MartusFlexidate.toStoredDateFormat(beforeEpochDate));

		cal.set(2548, APRIL, 3);
		Date thaiDate = cal.getTime();
		assertEquals("2548-04-03", MartusFlexidate.toStoredDateFormat(thaiDate));

		cal.set(9998, 17, 40);
		Date wayFutureDate = cal.getTime();
		assertEquals("9999-07-10", MartusFlexidate.toStoredDateFormat(wayFutureDate));

		cal.set(8, APRIL, 3);
		Date ancientDate = cal.getTime();
		assertEquals("0008-04-03", MartusFlexidate.toStoredDateFormat(ancientDate));

	}
	
	public void testRangesAndDaylightSavings() throws Exception
	{
		final int MAR = 2;
		final int APR = 3;
		final int MAY = 4;
		Date marDate = new GregorianCalendar(2005, MAR, 29, 12, 0, 0).getTime();
		Date aprDate1 = new GregorianCalendar(2005, APR, 1, 12, 0, 0).getTime();
		Date aprDate2 = new GregorianCalendar(2005, APR, 5, 12, 0, 0).getTime();
		Date mayDate = new GregorianCalendar(2005, MAY, 3, 12, 0, 0).getTime();
		String marToApr1String = MartusFlexidate.toStoredDateFormat(marDate, aprDate1);
		assertEquals("2005-03-29,20050329+3", marToApr1String);
		String marToApr2String = MartusFlexidate.toStoredDateFormat(marDate, aprDate2);
		assertEquals("2005-03-29,20050329+7", marToApr2String);
		String apr2ToMayString = MartusFlexidate.toStoredDateFormat(aprDate2, mayDate);
		assertEquals("2005-04-05,20050405+28", apr2ToMayString);
		String marToMayString = MartusFlexidate.toStoredDateFormat(marDate, mayDate);
		assertEquals("2005-03-29,20050329+35", marToMayString);
	}
}
