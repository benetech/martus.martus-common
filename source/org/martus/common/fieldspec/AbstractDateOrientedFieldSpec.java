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
package org.martus.common.fieldspec;

import org.martus.common.MartusXml;
import org.martus.util.MultiCalendar;
import org.xml.sax.SAXParseException;

public class AbstractDateOrientedFieldSpec extends FieldSpec
{
	protected AbstractDateOrientedFieldSpec(FieldType typeToUse)
	{
		super(typeToUse);
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
	
	protected void validateDate(String fullFieldLabel, MultiCalendar candidateDate) throws DateTooEarlyException, DateTooLateException
	{
		if (!candidateDate.isUnknown())
		{
			if (getMinimumDate() != null && candidateDate.before(getMinimumDate()))
				throw new DateTooEarlyException(fullFieldLabel, getMinimumDate().toIsoDateString());

			if (getMaximumDate() != null && candidateDate.after(getMaximumDate()))
				throw new DateTooLateException(fullFieldLabel, getMaximumDate().toIsoDateString());
		}
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

	public static class XmlIsoDateLoaderWithSpec extends XmlIsoDateLoader
	{
		public XmlIsoDateLoaderWithSpec(AbstractDateOrientedFieldSpec specToUse, String tag)
		{
			super(tag);
			spec = specToUse;
		}
		
		AbstractDateOrientedFieldSpec spec;
	}
	
	public static class MinimumDateLoader extends XmlIsoDateLoaderWithSpec
	{
		public MinimumDateLoader(AbstractDateOrientedFieldSpec specToUse)
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
		public MaximumDateLoader(AbstractDateOrientedFieldSpec specToUse)
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
