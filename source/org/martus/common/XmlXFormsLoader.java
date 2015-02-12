/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2015, Beneficent
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
package org.martus.common;

import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.SAXParseException;

//FIXME this is simple string loader and needs to use JavaRosa to load the data into a xforms model/instance
public class XmlXFormsLoader extends SimpleXmlDefaultLoader
{
	public XmlXFormsLoader()
	{
		super(MartusXml.XFormsElementName);
		
		pendingText = new StringBuffer();
	}

	public String getText()
	{
		return pendingText.toString();
	}

	public void addText(char[] ch, int start, int length) throws SAXParseException
	{
		pendingText.append(ch, start, length);
	}

	private StringBuffer pendingText;
}
