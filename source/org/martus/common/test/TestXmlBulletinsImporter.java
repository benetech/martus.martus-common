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
import java.util.Vector;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.XmlBulletinsImporter;
import org.martus.common.bulletin.XmlBulletinsImporter.FieldSpecVerificationException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.fieldspec.CustomFieldError;
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
		if(security == null)
		{
			security = MockMartusSecurity.createClient();
		}

	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testImportXML() throws Exception
	{
		InputStream xmlIn = getXMLStreamFromResource("SampleXmlBulletin.xml");
		XmlBulletinsImporter importer = new XmlBulletinsImporter(security, xmlIn);
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
		Bulletin[] bulletinReturned = importer.getBulletins();
		assertNotNull("No bulletin returned?", bulletinReturned);
		assertEquals(1, bulletinReturned.length);
		Bulletin b = bulletinReturned[0];
		assertEquals("Charles.", b.get(Bulletin.TAGAUTHOR));
		assertEquals("no keywords", b.get(Bulletin.TAGKEYWORDS));
		assertEquals("1970-01-01,1970-01-02", b.get(Bulletin.TAGEVENTDATE));
		assertEquals("1980-02-15,1980-05-22", b.get("InterviewDates"));
		assertEquals("en", b.get(Bulletin.TAGLANGUAGE));
		assertEquals("2005-11-01", b.get(Bulletin.TAGENTRYDATE));
	}

	public void testImportInvalidMainFieldSpecs() throws Exception
	{
		InputStream xmlIn = getXMLStreamFromResource("SampleInvalidFieldSpecsXmlBulletin.xml");
		try
		{
			 new XmlBulletinsImporter(security, xmlIn);
			fail("Should have thrown an exception");
		}
		catch(FieldSpecVerificationException expectedException)
		{
			Vector errors = expectedException.getErrors();
			StringBuffer validationErrorMessages = new StringBuffer();
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
			assertEquals(expectedErrorMessage, validationErrorMessages.toString());
			assertEquals("Calling the getErrors twice changed the results?", expectedErrorMessage, validationErrorMessages.toString());
		}
	}

	public void testImportInvalidXML() throws Exception
	{
		String invalidXML = "<wrong xml field expected>jflskdf</wrong xml field expected>";
		StringInputStreamWithSeek xmlInvalid = new StringInputStreamWithSeek(invalidXML);
		try
		{
			new XmlBulletinsImporter(security, xmlInvalid);
			fail("should have thrown");
		}
		catch(Exception expectedException)
		{
		}
	}

	
	InputStream getXMLStreamFromResource(String resourceFile) throws Exception
	{
		InputStream in = getClass().getResource(resourceFile).openStream();
		assertNotNull(in);
		return in;
	}

	final String expectedErrorMessage = "100 :  : author : \n" +
			"100 :  : title : \n" +
			"102 : BOOLEAN : DuplicateTag : Does interviewee wish to remain anonymous?\n" +
			"108 : DROPDOWN : BulletinSourceDuplicateEntries : Source of bulletin information\n";

	MartusCrypto security;
}
