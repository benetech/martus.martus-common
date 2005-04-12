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
import java.util.Date;
import java.util.GregorianCalendar;

import org.martus.util.TestCaseEnhanced;

import com.ghasemkiani.util.icu.PersianCalendar;


public class TestPersianCalendar extends TestCaseEnhanced
{
	public TestPersianCalendar(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		final int APRIL = 3;
		final int JUNE = 5;
		
		GregorianCalendar greg = new GregorianCalendar();
		greg.set(2005, APRIL, 7);
		Date goodDate = greg.getTime();
		PersianCalendar persian = new PersianCalendar(goodDate);
		assertEquals(1384, persian.get(Calendar.YEAR));
		assertEquals(0, persian.get(Calendar.MONTH));
		assertEquals(18, persian.get(Calendar.DAY_OF_MONTH));
		
		persian.set(1383, 2, 12);
		Date otherDate = persian.getTime();
		greg.setTime(otherDate);
		assertEquals(2004, greg.get(Calendar.YEAR));
		assertEquals(JUNE, greg.get(Calendar.MONTH));
		assertEquals(1, greg.get(Calendar.DAY_OF_MONTH));
	}
}
