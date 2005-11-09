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
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.martus.common.FieldCollection;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.fieldspec.CustomFieldSpecValidator;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.xml.sax.SAXParseException;

public class XmlBulletinsFileLoader extends SimpleXmlDefaultLoader
{
	public XmlBulletinsFileLoader(MartusCrypto cryptoToUse)
	{
		super(MartusBulletinSElementName);
		security = cryptoToUse;
		bulletins = new Vector();
		fieldspecVerificationErrorOccurred = false;
		fieldSpecValidationErrors = new Vector();
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
			if(didFieldSpecVerificationErrorOccur())
				return;
			privateFields = currentBulletinLoader.getPrivateFieldSpecs();
			fieldTagValuesMap = currentBulletinLoader.getFieldTagValuesMap();
			
			bulletins.add(createBulletin());			
		}
		else
			super.endElement(tag, ended);
	}

	private Bulletin createBulletin()
	{
		Bulletin bulletin = new Bulletin(security, mainFields.getSpecs(), privateFields.getSpecs());
		for (Iterator iter = fieldTagValuesMap.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry element = (Map.Entry) iter.next();
			String fieldTag = (String)element.getKey();
			String value = (String)element.getValue();
			if(currentBulletinLoader.isDateField(fieldTag) || currentBulletinLoader.isDateRangeField(fieldTag))
				value = extractRealDateValue(value);
			bulletin.set(fieldTag, value);
		}
		return bulletin;
	}
	
	public Bulletin[] getBulletins()
	{
		int size = bulletins.size();
		if(size == 0)
			return null;
		Bulletin[] bulletinArray = new Bulletin[size];
		bulletins.toArray(bulletinArray);
		return bulletinArray ;
	}
	
	public boolean didFieldSpecVerificationErrorOccur()
	{
		return fieldspecVerificationErrorOccurred;
	}
	
	public Vector getErrors()
	{
		return fieldSpecValidationErrors;
	}
	
	private String extractRealDateValue(String xmlValue)
	{
		if(xmlValue.startsWith(DateSimple))
			return xmlValue.substring(DateSimple.length());
		if(xmlValue.startsWith(DateRange))
			return xmlValue.substring(DateRange.length());
		return xmlValue;
	}

	private void validateMainFields(FieldCollection fields)
	{
		CustomFieldSpecValidator validator = new CustomFieldSpecValidator(fields);
		if(!validator.isValid())
		{
			fieldspecVerificationErrorOccurred = true;
			fieldSpecValidationErrors.addAll(validator.getAllErrors());
		}
	}

	public static String MartusBulletinSElementName = "MartusBulletins";
	public static String DateSimple = "Simple:";
	public static String DateRange = "Range:";
	private XmlBulletinLoader currentBulletinLoader;
	public FieldCollection mainFields;
	public FieldCollection privateFields;
	public HashMap fieldTagValuesMap;
	Vector bulletins;
	boolean fieldspecVerificationErrorOccurred;
	Vector fieldSpecValidationErrors;
	private MartusCrypto security;
}
