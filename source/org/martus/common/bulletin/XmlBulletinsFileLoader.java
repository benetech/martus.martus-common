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
package org.martus.common.bulletin;

import java.util.HashMap;
import java.util.Vector;
import org.martus.common.FieldCollection;
import org.martus.common.fieldspec.CustomFieldError;
import org.martus.common.fieldspec.CustomFieldSpecValidator;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.SAXParseException;

public class XmlBulletinsFileLoader extends SimpleXmlDefaultLoader
{
	public XmlBulletinsFileLoader()
	{
		super(MartusBulletinSElementName);
		bulletins = new Vector();
		validationErrorMessages = new StringBuffer();
		fieldspecVerificationError = false;
	}
	
	public SimpleXmlDefaultLoader startElement(String tag)
		throws SAXParseException
	{
		if(tag.equals(XmlBulletinLoader.MartusBulletinElementName))
		{
			currentBulletinLoader = new XmlBulletinLoader();
			return currentBulletinLoader;
		}
		return super.startElement(tag);
	}

	public void endElement(String tag, SimpleXmlDefaultLoader ended)
		throws SAXParseException
	{
		if(tag.equals(XmlBulletinLoader.MartusBulletinElementName))
		{
			//Todo here actually create a bulletin based on the currentBulletinLoader's data
			mainFields = currentBulletinLoader.getMainFieldSpecs();
		
			validateMainFields(mainFields);
			{
				privateFields = currentBulletinLoader.getPrivateFieldSpecs();
				fieldTagValuesMap = currentBulletinLoader.getFieldTagValuesMap();
			}
		}
		else
			super.endElement(tag, ended);
	}
	
	public boolean didFieldSpecVerificationErrorOccur()
	{
		return fieldspecVerificationError;
	}
	
	public String getErrors()
	{
		return validationErrorMessages.toString();
	}

	private void validateMainFields(FieldCollection fields)
	{
		CustomFieldSpecValidator validator = new CustomFieldSpecValidator(fields);
		if(!validator.isValid())
		{
			fieldspecVerificationError = true;
			Vector errors = validator.getAllErrors();
			for(int i = 0; i<errors.size(); ++i)
			{
				CustomFieldError thisError = (CustomFieldError)errors.get(i);
				StringBuffer thisErrorMessage = new StringBuffer(thisError.getCode());
				thisErrorMessage.append(" : ");
				thisErrorMessage.append(thisError.getType());
				thisErrorMessage.append(" : ");
				thisErrorMessage.append(thisError.getTag());
				thisErrorMessage.append(" : ");
				thisErrorMessage.append(thisError.getLabel());
				validationErrorMessages.append(thisErrorMessage);
				validationErrorMessages.append('\n');
			}
		}
	}

	public static String MartusBulletinSElementName = "MartusBulletins";
	private XmlBulletinLoader currentBulletinLoader;
	public FieldCollection mainFields;
	public FieldCollection privateFields;
	public HashMap fieldTagValuesMap;
	Vector bulletins;
	StringBuffer validationErrorMessages;
	boolean fieldspecVerificationError;
}
