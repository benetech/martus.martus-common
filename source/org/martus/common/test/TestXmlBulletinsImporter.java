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
package org.martus.common.test;

import java.io.InputStream;
import org.martus.common.FieldCollection;
import org.martus.common.bulletin.XmlBulletinsImporter;
import org.martus.common.field.MartusField;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.UnicodeReader;

public class TestXmlBulletinsImporter extends TestCaseEnhanced
{
	public TestXmlBulletinsImporter(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testImportXML() throws Exception
	{
		String xmlIn = getXMLFromResource("SampleXmlBulletin.xml");
		XmlBulletinsImporter importer = new XmlBulletinsImporter(xmlIn);
		FieldCollection mainFieldSpecs = importer.getMainFieldSpecs();
		assertNotNull(mainFieldSpecs);
		assertEquals(19, mainFieldSpecs.count());
		MartusField field = mainFieldSpecs.getField(0);
		assertTrue(field.getType().isLanguage());
		FieldCollection privateFieldSpecs = importer.getPrivateFieldSpecs();
		assertNotNull(privateFieldSpecs);
		assertEquals(1, privateFieldSpecs.count());
		field = privateFieldSpecs.getField(0);
		assertTrue(field.getType().isMultiline());

	}
	
	String getXMLFromResource(String resourceFile) throws Exception
	{
		InputStream in = getClass().getResource("SampleXmlBulletin.xml").openStream();
		assertNotNull(in);
		UnicodeReader reader = new UnicodeReader(in);
		String xmlRead = reader.readAll();
		reader.close();
		return xmlRead;
	}

}
