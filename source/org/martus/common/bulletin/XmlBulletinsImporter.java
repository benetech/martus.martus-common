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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.util.UnicodeReader;
import org.xml.sax.SAXException;

public class XmlBulletinsImporter
{
	public XmlBulletinsImporter(InputStream xmlIn) throws IOException, ParserConfigurationException, SAXException
	{
		UnicodeReader reader = new UnicodeReader(xmlIn);
		String xmlRead = reader.readAll();
		reader.close();

		bulletinsLoader = new XmlBulletinsFileLoader();
		bulletinsLoader.parse(xmlRead);
	}
	
	//Todo remove these and create real bulletins which this can return
	public FieldSpec[] getMainFieldSpecs()
	{
		return bulletinsLoader.mainFields.getSpecs();
	}

	public FieldSpec[] getPrivateFieldSpecs()
	{
		return bulletinsLoader.privateFields.getSpecs();
	}
	
	public HashMap getFieldTagValuesMap()
	{
		return bulletinsLoader.fieldTagValuesMap;
	}

	XmlBulletinsFileLoader bulletinsLoader;
}
