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
package org.martus.common;

import org.martus.util.xml.SimpleXmlStringLoader;
import org.xml.sax.SAXParseException;



public class MessageFieldSpec extends FieldSpec
{
	public MessageFieldSpec()
	{
		super(TYPE_MESSAGE);
	}
	
	public String getDefaultValue()
	{
		return getMessage();
	}

	public String getDetailsXml()
	{
		String xml = MartusXml.getTagStart(MESSAGE_SPEC_MESSAGE_TAG);
		xml += getMessage();
		xml += MartusXml.getTagEnd(MESSAGE_SPEC_MESSAGE_TAG);
		return xml;
	}

	public String getMessage()
	{
		return message;
	}
	
	public void putMessage(String newMessage)
	{
		message = newMessage;
	}
	
	static class MessageSpecLoader extends SimpleXmlStringLoader
	{
		public MessageSpecLoader(MessageFieldSpec spec)
		{
			super(MESSAGE_SPEC_MESSAGE_TAG);
			this.spec = spec;
		}

		MessageFieldSpec spec;

		public void endDocument() throws SAXParseException
		{
			spec.putMessage(getText());
			super.endDocument();
		}

	}
	
	private String message;
	
	public final static String MESSAGE_SPEC_MESSAGE_TAG = "Message";
}
