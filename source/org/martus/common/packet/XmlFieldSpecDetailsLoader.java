/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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
package org.martus.common.packet;

import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;


public class XmlFieldSpecDetailsLoader extends SimpleXmlDefaultLoader
{
	public XmlFieldSpecDetailsLoader()
	{
		super(FieldSpecDetails.TAG_FIELDSPEC_DETAILS);
	}
	
	
	public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
	{
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_DETAILS))
			return new XmlGridSpecDetailsLoader();
		return super.startElement(tag);
	}

	public void endElement(SimpleXmlDefaultLoader ended)
			throws SAXParseException
	{
		String tag = ended.getTag();
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_DETAILS))
			details = ((XmlGridSpecDetailsLoader)ended).getDetails();
		super.endElement(ended);
	}
	
	public FieldSpecDetails getDetails()
	{
		return details;
	}
	
	private FieldSpecDetails details; 
}

class XmlGridSpecDetailsLoader extends SimpleXmlDefaultLoader
{
	public XmlGridSpecDetailsLoader()
	{
		super(FieldSpecDetails.TAG_GRIDSPEC_DETAILS);
		details = new FieldSpecDetails();
	}

	public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
	{
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_COLUMN))
			return new XmlGridSpecColumnLoader();
		return super.startElement(tag);
	}

	public void endElement(SimpleXmlDefaultLoader ended)
			throws SAXParseException
	{
		String tag = ended.getTag();
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_COLUMN))
			details.addColumnLabel(((XmlGridSpecColumnLoader)ended).getLabel());
		super.endElement(ended);
	}
	
	public FieldSpecDetails getDetails()
	{
		return details;
	}

	private FieldSpecDetails details;
}

class XmlGridSpecColumnLoader extends SimpleXmlDefaultLoader
{
	public XmlGridSpecColumnLoader()
	{
		super(FieldSpecDetails.TAG_GRIDSPEC_COLUMN);
	}

	public SimpleXmlDefaultLoader startElement(String tag)
			throws SAXParseException
	{
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_LABEL))
			return new SimpleXmlStringLoader(tag);
		return super.startElement(tag);
	}

	public void endElement(SimpleXmlDefaultLoader ended)
			throws SAXParseException
	{
		String tag = ended.getTag();
		if(tag.equals(FieldSpecDetails.TAG_GRIDSPEC_LABEL))
			label = ((SimpleXmlStringLoader)ended).getText();
		super.endElement(ended);
	}
	public String getLabel()
	{
		return label;
	}
	private String label;
}

