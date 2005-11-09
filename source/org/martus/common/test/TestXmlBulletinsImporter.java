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
import java.util.HashMap;
import org.martus.common.bulletin.XmlBulletinsImporter;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;

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
		InputStream xmlIn = getXMLStreamFromResource("SampleXmlBulletin.xml");
		XmlBulletinsImporter importer = new XmlBulletinsImporter(xmlIn);
		FieldSpec[] mainFieldSpecs = importer.getMainFieldSpecs();
		assertNotNull(mainFieldSpecs);
		assertEquals(19, mainFieldSpecs.length);
		FieldSpec field = mainFieldSpecs[0];
		assertTrue(field.getType().isLanguage());
		FieldSpec[] privateFieldSpecs = importer.getPrivateFieldSpecs();
		assertNotNull(privateFieldSpecs);
		assertEquals(1, privateFieldSpecs.length);
		field = privateFieldSpecs[0];
		assertTrue(field.getType().isMultiline());
		HashMap tagValues = importer.getFieldTagValuesMap();
		assertEquals("Range:1980-02-15,1980-05-22", tagValues.get("InterviewDates"));
		assertEquals("Information we want kept private\n", tagValues.get("privateinfo"));
		assertFalse("Failed verification of good xml bulletin fields?", importer.didFieldSpecVerificationErrorOccur());
		assertEquals("", importer.getErrors());
	}
	
	public void testImportInvalidXML() throws Exception
	{
		String invalidXML = "<wrong xml field expected>jflskdf</wrong xml field expected>";
		StringInputStreamWithSeek xmlInvalid = new StringInputStreamWithSeek(invalidXML);
		try
		{
			new XmlBulletinsImporter(xmlInvalid);
			fail("should have thrown");
		}
		catch(Exception expectedException)
		{
		}
	}

	
	InputStream getXMLStreamFromResource(String resourceFile) throws Exception
	{
		InputStream in = getClass().getResource("SampleXmlBulletin.xml").openStream();
		assertNotNull(in);
		return in;
	}

}
