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

import java.util.GregorianCalendar;
import org.martus.common.MiniLocalization;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.language.LanguageOptions;


public class TestMiniLocalization extends TestCaseEnhanced
{
	public TestMiniLocalization(String name)
	{
		super(name);
	}

    public void testFormatDateTime() throws Exception
	{
    	MiniLocalization loc = new MiniLocalization();
    	
    	final int june = 5;
    	GregorianCalendar leadingZeros = new GregorianCalendar(1996, june, 1, 7, 4);
    	assertEquals("06/01/1996 07:04", loc.formatDateTime(leadingZeros.getTimeInMillis()));
    	
    	final int december = 11;
    	GregorianCalendar afternoon = new GregorianCalendar(2004, december, 9, 13, 59);
    	assertEquals("12/09/2004 13:59", loc.formatDateTime(afternoon.getTimeInMillis()));
	}
    
    public void testFormatDateTimeRightToLeft() throws Exception
	{
    	MiniLocalization loc = new MiniLocalization();
    	String rightToLeftLanguageCode = "cc";
    	loc.addRightToLeftLanguage(rightToLeftLanguageCode);
		loc.setCurrentLanguageCode(rightToLeftLanguageCode);
    	final int june = 5;
    	GregorianCalendar leadingZeros = new GregorianCalendar(1996, june, 1, 7, 4);
    	assertEquals("07:04 1996/01/06", loc.formatDateTime(leadingZeros.getTimeInMillis()));
    	
    	final int december = 11;
    	GregorianCalendar afternoon = new GregorianCalendar(2004, december, 9, 13, 59);
    	assertEquals("13:59 2004/09/12", loc.formatDateTime(afternoon.getTimeInMillis()));
    	LanguageOptions.setDirectionLeftToRight();
	}

    public void testDateUnknown()
    {
    	MiniLocalization loc = new MiniLocalization();
    	assertEquals("Should return '' for an unknown date", "", loc.formatDateTime(MiniLocalization.DATE_UNKNOWN));
    }
    
	
}
