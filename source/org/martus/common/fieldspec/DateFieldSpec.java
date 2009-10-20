/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2009, Beneficent
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
package org.martus.common.fieldspec;

import java.util.GregorianCalendar;

import org.martus.common.MartusXml;
import org.martus.common.MiniLocalization;
import org.martus.util.MultiCalendar;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;

public class DateFieldSpec extends FieldSpec
{
	protected DateFieldSpec()
	{
		super(new FieldTypeDate());
	}
	
	void setMinimumDate(MultiCalendar newMinimumDate)
	{
		minimumDate = newMinimumDate;
	}
	
	public MultiCalendar getMinimumDate()
	{
		return minimumDate;
	}
	
	void setMaximumDate(MultiCalendar newMaximumDate)
	{
		maximumDate = newMaximumDate;
	}
	
	public MultiCalendar getMaximumDate()
	{
		return maximumDate;
	}
	
	@Override
	public void validate(String fullFieldLabel, String candidateValue,
			MiniLocalization localization) throws DataInvalidException
	{
		MultiCalendar candidateDate = MultiCalendar.createFromIsoDateString(candidateValue);
		if(!candidateDate.isUnknown())
		{
			if(minimumDate != null && candidateDate.before(minimumDate))
				throw new DateTooEarlyException(fullFieldLabel, minimumDate.toIsoDateString());
			
			if(maximumDate != null && candidateDate.after(maximumDate))
				throw new DateTooLateException(fullFieldLabel, maximumDate.toIsoDateString());
		}
		
		super.validate(fullFieldLabel, candidateValue, localization);
	}

	public String getDetailsXml()
	{
		StringBuffer xml = new StringBuffer();
		xml.append(getDetailsXml(MINIMUM_DATE, minimumDate));
		xml.append(getDetailsXml(MAXIMUM_DATE, maximumDate));
		return xml.toString();
	}
	
	private String getDetailsXml(String tag, MultiCalendar date)
	{
		if(date == null)
			return "";

		return MartusXml.getTagStart(tag) + date.toIsoDateString() + MartusXml.getTagEnd(tag);
	}

	private static class XmlIsoDateLoader extends SimpleXmlStringLoader
	{
		public XmlIsoDateLoader(String tag)
		{
			super(tag);
		}
		
		MultiCalendar getDate()
		{
			String text = getText();
			if(text.length() > 0)
				return MultiCalendar.createFromIsoDateString(text);
			return new MultiCalendar(new GregorianCalendar());
		}
	}
	
	public static class XmlIsoDateLoaderWithSpec extends XmlIsoDateLoader
	{
		public XmlIsoDateLoaderWithSpec(DateFieldSpec specToUse, String tag)
		{
			super(tag);
			spec = specToUse;
		}
		
		DateFieldSpec spec;
	}
	
	public static class MinimumDateLoader extends XmlIsoDateLoaderWithSpec
	{
		public MinimumDateLoader(DateFieldSpec specToUse)
		{
			super(specToUse, MINIMUM_DATE);
		}

		public void endDocument() throws SAXParseException
		{
			spec.setMinimumDate(getDate());
			super.endDocument();
		}

		
	}

	public static class MaximumDateLoader extends XmlIsoDateLoaderWithSpec
	{
		public MaximumDateLoader(DateFieldSpec specToUse)
		{
			super(specToUse, MAXIMUM_DATE);
		}

		public void endDocument() throws SAXParseException
		{
			spec.setMaximumDate(getDate());
			super.endDocument();
		}

		
	}

	public static final String MINIMUM_DATE = "MinimumDate";
	public static final String MAXIMUM_DATE = "MaximumDate";

	private MultiCalendar minimumDate;
	private MultiCalendar maximumDate;
}
