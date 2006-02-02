/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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

import org.martus.util.TestCaseEnhanced;

public class TestDatePreference extends TestCaseEnhanced
{
	public TestDatePreference(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		DatePreference pref = new DatePreference();
		assertEquals("Doesn't default to Gregorian?", DatePreference.GREGORIAN, pref.getCalendarType());
		assertEquals("Doesn't default to mdy?", "mdy", pref.getMdyOrder());
		assertEquals("Doesn't default to slash delimiter?", '/', pref.getDelimiter());
		assertEquals("Wrong date format?", "MM/dd/yyyy", pref.getDateTemplate());
	}
	
	public void testSetDelimiter() throws Exception
	{
		DatePreference pref = new DatePreference();
		pref.setDelimiter('-');
		assertEquals("didn't set delimiter?", '-', pref.getDelimiter());
		assertEquals("didn't use delimiter?", "MM-dd-yyyy", pref.getDateTemplate());
	}
	
	public void testSetMdyOrder() throws Exception
	{
		DatePreference pref = new DatePreference();
		pref.setMdyOrder("dmy");
		assertEquals("didn't set dmy?", "dmy", pref.getMdyOrder());
		assertEquals("didn't use dmy?", "dd/MM/yyyy", pref.getDateTemplate());
	}
	
	public void testSetDateTemplate() throws Exception
	{
		DatePreference pref = new DatePreference();
		pref.setDateTemplate("dd.MM.yyyy");
		assertEquals("wrong dmy order?", "dmy", pref.getMdyOrder());
		assertEquals("wrong delimiter?", '.', pref.getDelimiter());
	}
}
