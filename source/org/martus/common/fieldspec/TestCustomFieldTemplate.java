/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.martus.common.FieldCollection;
import org.martus.common.FieldCollectionForTesting;
import org.martus.common.FieldSpecCollection;
import org.martus.common.LegacyCustomFields;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.fieldspec.CustomFieldTemplate.FutureVersionException;
import org.martus.util.StreamableBase64;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.UnicodeUtilities;
import org.martus.util.UnicodeWriter;
import org.martus.util.inputstreamwithseek.ByteArrayInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.FileInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;

public class TestCustomFieldTemplate extends TestCaseEnhanced
{
	public TestCustomFieldTemplate(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		if(security == null)
		{
			security = new MockMartusSecurity();
			security.createKeyPair(512);
		}
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testBasics()
	{
		assertEquals("Version number not correct?", 3, CustomFieldTemplate.exportVersionNumber);
	}
	
	public void testValidateXml() throws Exception
	{
		FieldCollection defaultTopSectionFields = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		FieldCollection defaultBottomSectionFields = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
		CustomFieldTemplate template = new CustomFieldTemplate();
		assertTrue("not valid?", template.isvalidTemplateXml(defaultTopSectionFields.toString(), defaultBottomSectionFields.toString()));
		assertEquals(0, template.getErrors().size());
		
		FieldSpec invalidTopSectionField = FieldSpec.createCustomField("myTag", "", new FieldTypeNormal());
		FieldCollection fields = FieldCollectionForTesting.extendFields(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray(), invalidTopSectionField);
		assertFalse("Should not be a valid template", template.isvalidTemplateXml(fields.toString(), defaultBottomSectionFields.toString()));
		assertEquals(1, template.getErrors().size());
		assertEquals(CustomFieldError.CODE_MISSING_LABEL,((CustomFieldError)template.getErrors().get(0)).getCode());

		CustomFieldTemplate template2 = new CustomFieldTemplate();
		FieldSpec invalidBottomSectionField = FieldSpec.createCustomField("myTag", "", new FieldTypeNormal());
		fields = FieldCollectionForTesting.extendFields(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray(), invalidBottomSectionField);
		assertFalse("Should not be a valid template", template2.isvalidTemplateXml(defaultTopSectionFields.toString(), fields.toString()));
		assertEquals(1, template2.getErrors().size());
		assertEquals(CustomFieldError.CODE_MISSING_LABEL,((CustomFieldError)template2.getErrors().get(0)).getCode());
	}
	
	public void testExportXml() throws Exception
	{
		CustomFieldTemplate template = new CustomFieldTemplate();
		File exportFile = createTempFileFromName("$$$testExportXml");
		exportFile.deleteOnExit();
		String formTemplateTitle = "New Form Title";
		String formTemplateDescription = "New Form Description";
		FieldCollection defaultFieldsTopSection = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		FieldCollection defaultFieldsBottomSection = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
		assertTrue(template.exportTemplate(security, exportFile, defaultFieldsTopSection.toString(), defaultFieldsBottomSection.toString(), formTemplateTitle, formTemplateDescription));
		assertTrue(exportFile.exists());
		CustomFieldTemplate importedTemplate = new CustomFieldTemplate();
		importTemplate(importedTemplate, exportFile);
		File exportFile2 = createTempFileFromName("$$$testExportXml2");
		exportFile2.deleteOnExit();
		assertTrue(importedTemplate.exportTemplate(security, exportFile2));
		CustomFieldTemplate importedTemplate2 = new CustomFieldTemplate();
		importTemplate(importedTemplate2, exportFile2);
		assertEquals("imported file 1 does not match imported file 2?", importedTemplate.getExportedTemplateAsBase64String(security), importedTemplate2.getExportedTemplateAsBase64String(security));
		
		exportFile.delete();
		exportFile2.delete();
		
		

		FieldSpec invalidField = FieldSpec.createCustomField("myTag", "", new FieldTypeNormal());
		FieldCollection withInvalid = FieldCollectionForTesting.extendFields(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray(), invalidField);
		FieldCollection bottomSectionFields = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
		assertFalse(exportFile.exists());
		assertFalse(template.exportTemplate(security, exportFile, withInvalid.toString(), bottomSectionFields.toString(), formTemplateTitle, formTemplateDescription));
		assertFalse(exportFile.exists());
		exportFile.delete();
	}
	
	public void testImportedTemplateWithDifferentSignedSections() throws Exception
	{
		String formTemplateTitle = "New Form Title";
		String formTemplateDescription = "New Form Description";
		FieldCollection defaultFieldsTopSection = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		FieldCollection defaultFieldsBottomSection = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());

		File exportMultipleSignersCFTFile = createTempFileFromName("$$$testExportMultipleSignersXml");
		exportMultipleSignersCFTFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(exportMultipleSignersCFTFile);		
		DataOutputStream dataOut = new DataOutputStream(out);
		dataOut.write(CustomFieldTemplate.versionHeader.getBytes());
		dataOut.writeInt(CustomFieldTemplate.exportVersionNumber);
		byte[] signedBundleTopSection = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(defaultFieldsTopSection.getSpecsXml()));
		byte[] signedBundleBottomSection = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(defaultFieldsBottomSection.getSpecsXml()));
		byte[] signedBundleTitle = security.createSignedBundle(UnicodeUtilities.toUnicodeBytes(formTemplateTitle));

		MockMartusSecurity otherSecurity = new MockMartusSecurity();
		otherSecurity.createKeyPair(512);

		byte[] signedBundleDescription = otherSecurity.createSignedBundle(UnicodeUtilities.toUnicodeBytes(formTemplateDescription));
		dataOut.writeInt(signedBundleTopSection.length);
		dataOut.writeInt(signedBundleBottomSection.length);
		dataOut.writeInt(signedBundleTitle.length);
		dataOut.writeInt(signedBundleDescription.length);
		dataOut.write(signedBundleTopSection);
		dataOut.write(signedBundleBottomSection);
		dataOut.write(signedBundleTitle);
		dataOut.write(signedBundleDescription);
		dataOut.flush();
		dataOut.close();
		out.flush();
		out.close();
		
		CustomFieldTemplate template = new CustomFieldTemplate(formTemplateTitle, formTemplateDescription, defaultFieldsTopSection, defaultFieldsBottomSection);
		assertFalse(importTemplate(template, exportMultipleSignersCFTFile));
		assertEquals(CustomFieldError.CODE_SIGNATURE_ERROR, ((CustomFieldError)template.getErrors().get(0)).getCode());
	}
	
	public void testGetExportedTemplateAsString() throws Exception
	{
		String formTemplateTitle = "New Form Title";
		String formTemplateDescription = "New Form Description";
		FieldCollection defaultFieldsTopSection = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		FieldCollection defaultFieldsBottomSection = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
		CustomFieldTemplate template = new CustomFieldTemplate(formTemplateTitle, formTemplateDescription, defaultFieldsTopSection, defaultFieldsBottomSection);
		
		String exportedTemplateAsStringBase64= template.getExportedTemplateAsBase64String(security);
		byte[] decodedBytes = StreamableBase64.decode(exportedTemplateAsStringBase64);
		assertNotEquals(0, decodedBytes.length);
		
		File exportFile = createTempFileFromName("$$$testExportedTemplateAsString");
		exportFile.delete();
		assertFalse(exportFile.exists());
		FileOutputStream output = new FileOutputStream(exportFile);
		output.write(decodedBytes);
		output.flush();
		output.close();
		
		CustomFieldTemplate templateRetrieved = new CustomFieldTemplate();
		assertTrue("Failed to import Template from exportedString?", importTemplate(templateRetrieved, exportFile));
		assertEquals(formTemplateTitle, templateRetrieved.getTitle());
		assertEquals(formTemplateDescription, templateRetrieved.getDescription());
		assertEquals(defaultFieldsTopSection.toString(), templateRetrieved.getImportedTopSectionText());
		assertEquals(defaultFieldsBottomSection.toString(), templateRetrieved.getImportedBottomSectionText());
	}

	public void testImportXmlLegacy() throws Exception
	{
		FieldCollection fieldsTopSection = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		File exportFile = createTempFileFromName("$$$testImportXmlLegacy");
		exportFile.delete();
		
		FileOutputStream out = new FileOutputStream(exportFile);
		byte[] signedBundle = security.createSignedBundle(fieldsTopSection.toString().getBytes("UTF-8"));
		out.write(signedBundle);
		out.flush();
		out.close();

		CustomFieldTemplate template = new CustomFieldTemplate();
		assertEquals("", template.getImportedTopSectionText());
		assertTrue(importTemplate(template, exportFile));
		FieldCollection defaultBottomSectionFields = new FieldCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
		assertEquals(fieldsTopSection.toString(), template.getImportedTopSectionText());
		assertEquals(defaultBottomSectionFields.toString(), template.getImportedBottomSectionText());
		assertEquals(0, template.getErrors().size());
		
		UnicodeWriter writer = new UnicodeWriter(exportFile,UnicodeWriter.APPEND);
		writer.write("unauthorizedTextAppended Should not be read.");
		writer.close();
		
		assertTrue(importTemplate(template, exportFile));
		assertEquals(fieldsTopSection.toString(), template.getImportedTopSectionText());
		assertEquals(0, template.getErrors().size());

		exportFile.delete();
		out = new FileOutputStream(exportFile);
		byte[] tamperedBundle = security.createSignedBundle(fieldsTopSection.toString().getBytes("UTF-8"));
		tamperedBundle[tamperedBundle.length-2] = 'j';
		out.write(tamperedBundle);
		out.flush();
		out.close();
		
		assertFalse(importTemplate(template, exportFile));
		assertEquals("", template.getImportedTopSectionText());
		assertEquals(1, template.getErrors().size());
		assertEquals(CustomFieldError.CODE_SIGNATURE_ERROR, ((CustomFieldError)template.getErrors().get(0)).getCode());
		
		exportFile.delete();
		assertFalse(importTemplate(template, exportFile));
		assertEquals("", template.getImportedTopSectionText());
		assertEquals(1, template.getErrors().size());
		assertEquals(CustomFieldError.CODE_IO_ERROR, ((CustomFieldError)template.getErrors().get(0)).getCode());
	}

	public void testImportXmlFuture() throws Exception
	{
		File exportFile = createTempFileFromName("$$$testImportXmlFuture");
		exportFile.delete();
		
		FileOutputStream out = new FileOutputStream(exportFile);
		DataOutputStream dataOut = new DataOutputStream(out);
		dataOut.write(CustomFieldTemplate.versionHeader.getBytes());
		dataOut.writeInt(CustomFieldTemplate.exportVersionNumber + 1);
		dataOut.writeInt(16);
		dataOut.writeInt(0);
		dataOut.write("Some future data".getBytes());
		dataOut.flush();
		dataOut.close();

		CustomFieldTemplate template = new CustomFieldTemplate();
		try
		{
			importTemplate(template, exportFile);
			fail("Should have thrown future version Exception");
		}
		catch(CustomFieldTemplate.FutureVersionException expected)
		{
		}
	}

	public void testImportXml() throws Exception
	{
		FieldCollection fieldsTopSection = new FieldCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		FieldSpecCollection fieldSpecsBottomSection = StandardFieldSpecs.getDefaultBottomSectionFieldSpecs();
		String privateTag = "a2";
		String privateLabel ="b2";
		fieldSpecsBottomSection = TestCustomFieldSpecValidator.addFieldSpec(fieldSpecsBottomSection, LegacyCustomFields.createFromLegacy(privateTag+","+privateLabel));
		FieldCollection fieldsBottomSection = new FieldCollection(fieldSpecsBottomSection);
		
		CustomFieldTemplate template = new CustomFieldTemplate();
		File exportFile = createTempFileFromName("$$$testImportXml");
		String formTemplateTitle = "Form Title";
		String formTemplateDescription = "Form Description";
		exportFile.delete();
		template.exportTemplate(security, exportFile, fieldsTopSection.toString(), fieldsBottomSection.toString(), formTemplateTitle, formTemplateDescription);
		assertEquals("", template.getImportedTopSectionText());
		assertTrue(importTemplate(template, exportFile));
		assertEquals(fieldsTopSection.toString(), template.getImportedTopSectionText());
		assertEquals(fieldsBottomSection.toString(), template.getImportedBottomSectionText());
		assertEquals(formTemplateTitle, template.getTitle());
		assertEquals(formTemplateDescription, template.getDescription());
		assertEquals(0, template.getErrors().size());
		
		UnicodeWriter writer = new UnicodeWriter(exportFile,UnicodeWriter.APPEND);
		writer.write("unauthorizedTextAppended Should not be read.");
		writer.close();
		
		assertTrue(importTemplate(template, exportFile));
		assertEquals(fieldsTopSection.toString(), template.getImportedTopSectionText());
		assertEquals(fieldsBottomSection.toString(), template.getImportedBottomSectionText());
		assertEquals(formTemplateTitle, template.getTitle());
		assertEquals(formTemplateDescription, template.getDescription());
		assertEquals(0, template.getErrors().size());

		exportFile.delete();
		FileOutputStream out = new FileOutputStream(exportFile);
		byte[] tamperedBundle = security.createSignedBundle(fieldsTopSection.toString().getBytes("UTF-8"));
		tamperedBundle[tamperedBundle.length-2] = 'j';
		out.write(tamperedBundle);
		out.flush();
		out.close();
		
		assertFalse(importTemplate(template, exportFile));
		assertEquals("", template.getImportedTopSectionText());
		assertEquals("", template.getImportedBottomSectionText());
		assertEquals("", template.getTitle());
		assertEquals("", template.getDescription());
		assertEquals(1, template.getErrors().size());
		assertEquals(CustomFieldError.CODE_SIGNATURE_ERROR, ((CustomFieldError)template.getErrors().get(0)).getCode());
		
		exportFile.delete();
		assertFalse(importTemplate(template, exportFile));
		assertEquals("", template.getImportedTopSectionText());
		assertEquals("", template.getImportedBottomSectionText());
		assertEquals("", template.getTitle());
		assertEquals("", template.getDescription());
		assertEquals(1, template.getErrors().size());
		assertEquals(CustomFieldError.CODE_IO_ERROR, ((CustomFieldError)template.getErrors().get(0)).getCode());
	}

	private boolean importTemplate(CustomFieldTemplate template, File exportFile) throws FutureVersionException, IOException
	{
		//FIXME these tests are temporarily here so tests passing in deleted files pass
		InputStreamWithSeek inputStreamWithSeek;
		if (exportFile.exists())
			inputStreamWithSeek = new FileInputStreamWithSeek(exportFile);
		else 
			inputStreamWithSeek = new ByteArrayInputStreamWithSeek(new byte[0]);
		
		return template.importTemplate(security, inputStreamWithSeek);
	}
	
	static MockMartusSecurity security;
}
