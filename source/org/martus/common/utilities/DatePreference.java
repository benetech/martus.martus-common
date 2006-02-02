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

public class DatePreference
{
	public DatePreference()
	{
		setMdyOrder("mdy");
		setDelimiter('/');
	}
	
	public int getCalendarType()
	{
		return GREGORIAN;
	}
	
	public String getMdyOrder()
	{
		return mdyOrder;
	}
	
	public void setMdyOrder(String newOrder)
	{
		mdyOrder = newOrder;
	}
	
	public char getDelimiter()
	{
		return delimiter;
	}
	
	public void setDelimiter(char newDelimiter)
	{
		delimiter = newDelimiter;
	}
	
	public String getDateTemplate()
	{
		char[] mdy = getMdyOrder().toCharArray();
		int at = 0;
		StringBuffer template = new StringBuffer();

		template.append(getTemplateField(mdy[at++]));
		template.append(getDelimiter());
		template.append(getTemplateField(mdy[at++]));
		template.append(getDelimiter());
		template.append(getTemplateField(mdy[at++]));
		
		return template.toString();
	}
	
	public void setDateTemplate(String template)
	{
		setMdyOrder(detectMdyOrder(template));
		setDelimiter(detectDelimiter(template));
	}
	
	private static String detectMdyOrder(String template)
	{
		String result = "";
		template = template.toLowerCase();
		for(int i = 0; i < template.length(); ++i)
		{
			char c = template.charAt(i);
			if( (c == 'm' || c == 'd' || c == 'y') && (result.indexOf(c) < 0) )
				result += c;
		}

		return result;
	}

	private static char detectDelimiter(String template)
	{
		int at = 0;
		while(Character.isLetter(template.charAt(at)))
			++at;
		
		return template.charAt(at);
	}

	private static String getTemplateField(char fieldId)
	{
		switch(fieldId)
		{
			case 'y':	return "yyyy";
			case 'm':	return "MM";
			case 'd':	return "dd";
		}
		throw new RuntimeException("Unknown date field id: " + fieldId);
	}
	
	public static int GREGORIAN = 0;
	
	private char delimiter;
	private String mdyOrder;
}