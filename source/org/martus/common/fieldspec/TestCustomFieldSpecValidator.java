/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
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

import java.util.Vector;

import org.martus.common.FieldCollection;
import org.martus.common.FieldSpecCollection;
import org.martus.common.LegacyCustomFields;
import org.martus.common.ReusableChoices;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.util.TestCaseEnhanced;

public class TestCustomFieldSpecValidator extends TestCaseEnhanced
{
	public TestCustomFieldSpecValidator(String name)
	{
		super(name);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();

		specsTopSection = new FieldSpecCollection(StandardFieldSpecs.getDefaultTopSetionFieldSpecs().asArray());
		specsBottomSection = new FieldSpecCollection(StandardFieldSpecs.getDefaultBottomSectionFieldSpecs().asArray());
	}
	
	public void testAllValid() throws Exception
	{
		String tag = "_A.-_AllValid0123456789";
		String label = "my Label";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tag+","+label));
		String tagB = "_B.-_AllValid0123456789";
		String labelB = "my Label B";
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tagB+","+labelB));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertTrue("not valid?", checker.isValid());
	}

	public void testIllegalTagCharactersTopSection() throws Exception
	{
		String label = "anything";
		int[] nepaliByteValuesWithNonBreakingSpace = new int[] {
				0xe0, 0xa5, 0x8b, 0xc2, 
				0xa0, 
				0xe0, 0xa4, 0xb6, 
				};
		byte[] nepaliBytesWithNonBreakingSpace = new byte[nepaliByteValuesWithNonBreakingSpace.length];
		for(int i = 0; i < nepaliBytesWithNonBreakingSpace.length; ++i)
			nepaliBytesWithNonBreakingSpace[i] = (byte)nepaliByteValuesWithNonBreakingSpace[i];
		String nepaliWithNonBreakingSpace = new String(nepaliBytesWithNonBreakingSpace, "UTF-8");
		String[] variousIllegalTags = {"a tag", "a&amp;b", "a=b", "a'b", ".a", nepaliWithNonBreakingSpace};
		for(int i=0; i < variousIllegalTags.length; ++i)
		{
			String thisTag = variousIllegalTags[i];
			FieldSpec thisSpec = FieldSpec.createCustomField(thisTag, label, new FieldTypeNormal());
			specsTopSection = addFieldSpec(specsTopSection, thisSpec);
		}
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("didn't catch all errors?", variousIllegalTags.length, errors.size());
		for(int i=0; i < errors.size(); ++i)
		{
			verifyExpectedError("IllegalTagCharactersTop", CustomFieldError.CODE_ILLEGAL_TAG, variousIllegalTags[i], label, null, (CustomFieldError)errors.get(i));
		}
	}
	
	public void testIllegalTagCharactersBottomSection() throws Exception
	{
		String label = "anything";
		String[] variousIllegalTags = {"a tag", "a&amp;b", "a=b", "a'b", ".a"};
		for(int i=0; i < variousIllegalTags.length; ++i)
		{
			String thisTag = variousIllegalTags[i];
			FieldSpec thisSpec = FieldSpec.createCustomField(thisTag, label, new FieldTypeNormal());
			specsBottomSection = addFieldSpec(specsBottomSection, thisSpec);
		}
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("didn't catch all errors?", variousIllegalTags.length, errors.size());
		for(int i=0; i < errors.size(); ++i)
		{
			verifyExpectedError("IllegalTagCharactersBottom", CustomFieldError.CODE_ILLEGAL_TAG, variousIllegalTags[i], label, null, (CustomFieldError)errors.get(i));
		}
	}

	public void testMissingRequiredFields() throws Exception
	{
		FieldSpecCollection emptySpecs = new FieldSpecCollection();
		
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(emptySpecs, emptySpecs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		int numberOfRequiredFields = 4;
		assertEquals("Should require 4 fields", numberOfRequiredFields , errors.size());
		for (int i = 0; i<numberOfRequiredFields; ++i)
		{
			assertEquals("Incorrect Error code required "+i, CustomFieldError.CODE_REQUIRED_FIELD, ((CustomFieldError)errors.get(i)).getCode());
		}
		Vector errorFields = new Vector();
		for (int i = 0; i<numberOfRequiredFields; ++i)
		{
			errorFields.add(((CustomFieldError)errors.get(i)).getTag());
		}
		assertContains(BulletinConstants.TAGAUTHOR, errorFields);
		assertContains(BulletinConstants.TAGLANGUAGE, errorFields);
		assertContains(BulletinConstants.TAGENTRYDATE, errorFields);
		assertContains(BulletinConstants.TAGTITLE, errorFields);
	}
	
	public void testReservedFields() throws Exception
	{
		String tagStatus = BulletinConstants.TAGSTATUS;
		String labelStatus ="status";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tagStatus+","+labelStatus));
		
		String tagSent = BulletinConstants.TAGWASSENT;
		String labelSent ="sent";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tagSent+","+labelSent));
		
		String tagSaved = BulletinConstants.TAGLASTSAVED;
		String labelSaved ="saved";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tagSaved+","+labelSaved));

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have found 3 errors", 3 , errors.size());
		verifyExpectedError("Reserved Fields", CustomFieldError.CODE_RESERVED_TAG, tagStatus, labelStatus, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Reserved Fields", CustomFieldError.CODE_RESERVED_TAG, tagSent, labelSent, null, (CustomFieldError)errors.get(1));
		verifyExpectedError("Reserved Fields", CustomFieldError.CODE_RESERVED_TAG, tagSaved, labelSaved, null, (CustomFieldError)errors.get(2));
		
		
		specsTopSection = StandardFieldSpecs.getDefaultTopSetionFieldSpecs();
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tagStatus+","+labelStatus));
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tagSent+","+labelSent));
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tagSaved+","+labelSaved));

		checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		errors = checker.getAllErrors();
		assertEquals("Should have found 3 errors", 3 , errors.size());
		verifyExpectedError("Reserved Fields Bottom Section", CustomFieldError.CODE_RESERVED_TAG, tagStatus, labelStatus, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Reserved Fields Bottom Section", CustomFieldError.CODE_RESERVED_TAG, tagSent, labelSent, null, (CustomFieldError)errors.get(1));
		verifyExpectedError("Reserved Fields Bottom Section", CustomFieldError.CODE_RESERVED_TAG, tagSaved, labelSaved, null, (CustomFieldError)errors.get(2));
	}

	public void testMartusFieldsInBottomSection() throws Exception
	{
		FieldSpecCollection specsRequiredOnlyTopSection = getRequiredOnlyTopSectionFieldSpecs();
		FieldSpecCollection specsNonRequiredBottomSection = getAllNonRequiredMartusFieldSpecs();
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsRequiredOnlyTopSection, specsNonRequiredBottomSection);
		assertFalse("Valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		int numberOfMartusFields = 6;
		assertEquals("Should require 6 fields", numberOfMartusFields, errors.size());
		for (int i = 0; i<numberOfMartusFields; ++i)
		{
			assertEquals("Incorrect Error code required "+i, CustomFieldError.CODE_MARTUS_FIELD_IN_BOTTOM_SECTION, ((CustomFieldError)errors.get(i)).getCode());
		}
		Vector errorFields = new Vector();
		for (int i = 0; i<numberOfMartusFields; ++i)
		{
			errorFields.add(((CustomFieldError)errors.get(i)).getTag());
		}
		assertContains(BulletinConstants.TAGORGANIZATION, errorFields);
		assertContains(BulletinConstants.TAGLOCATION, errorFields);
		assertContains(BulletinConstants.TAGEVENTDATE, errorFields);
		assertContains(BulletinConstants.TAGKEYWORDS, errorFields);
		assertContains(BulletinConstants.TAGSUMMARY, errorFields);
		assertContains(BulletinConstants.TAGPUBLICINFO, errorFields);
	}

	public void testPrivateFieldInTopSection() throws Exception
	{
		FieldSpecCollection specsTopSectionRequirePlusPrivate = getRequiredOnlyTopSectionFieldSpecs();
		FieldSpecCollection specsEmptyBottomSection = new FieldSpecCollection();
		
		specsTopSectionRequirePlusPrivate.add(FieldSpec.createStandardField(BulletinConstants.TAGPRIVATEINFO, new FieldTypeMultiline()));

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSectionRequirePlusPrivate, specsEmptyBottomSection);
		assertFalse("Valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals(1, errors.size());
		assertEquals("Incorrect Error code required ", CustomFieldError.CODE_PRIVATE_FIELD_IN_TOP_SECTION, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals(BulletinConstants.TAGPRIVATEINFO, ((CustomFieldError)errors.get(0)).getTag());
	}

	public void testMissingTag() throws Exception
	{
		String label = "my Label";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(","+label));
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(","+label));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 errors", 2, errors.size());
		verifyExpectedError("Missing Tags", CustomFieldError.CODE_MISSING_TAG, null, label, new FieldTypeNormal(), (CustomFieldError)errors.get(0));
		verifyExpectedError("Missing Tags Bottom Section", CustomFieldError.CODE_MISSING_TAG, null, label, new FieldTypeNormal(), (CustomFieldError)errors.get(1));
	}

	public void testDuplicateTags() throws Exception
	{
		String tag = "a";
		String label ="b";
		FieldSpecCollection withA = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tag+","+label));
		FieldSpecCollection withATwice = addFieldSpec(withA, LegacyCustomFields.createFromLegacy(tag+","+label));
		String tag2 = "a2";
		String label2 ="b2";
		FieldSpecCollection withA2 = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tag2+","+label2));
		FieldSpecCollection withA2Twice = addFieldSpec(withA2, LegacyCustomFields.createFromLegacy(tag2+","+label2));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(withATwice, withA2Twice);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("Duplicate Tags", CustomFieldError.CODE_DUPLICATE_FIELD, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Duplicate Tags Bottom Section", CustomFieldError.CODE_DUPLICATE_FIELD, tag2, label2, null, (CustomFieldError)errors.get(1));

		//TODO duplicate tag from top found in bottom
		FieldSpecCollection bottomWithA = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tag+","+label));
		checker = new CustomFieldSpecValidator(withA, bottomWithA);
		assertFalse("valid?", checker.isValid());
		errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		verifyExpectedError("Duplicate Tags not found in Bottom?", CustomFieldError.CODE_DUPLICATE_FIELD, tag, label, null, (CustomFieldError)errors.get(0));
		
	}

	public void testDuplicateDropDownEntry() throws Exception
	{
		String tag = "dd";
		String label ="cc";
		ChoiceItem[] choicesNoDups = {new ChoiceItem("no Dup", "first item"), new ChoiceItem("second", "second item")};
		DropDownFieldSpec dropDownSpecNoDuplicates = new DropDownFieldSpec(choicesNoDups);
		dropDownSpecNoDuplicates.setTag(tag);
		dropDownSpecNoDuplicates.setLabel(label);
		specsTopSection = addFieldSpec(specsTopSection, dropDownSpecNoDuplicates);
		
		String tag2 = "dd2";
		String label2 ="cc2";
		ChoiceItem[] choicesNoDups2 = {new ChoiceItem("no Dup2", "first item2"), new ChoiceItem("second", "second item")};
		DropDownFieldSpec dropDownSpecNoDuplicates2 = new DropDownFieldSpec(choicesNoDups2);
		dropDownSpecNoDuplicates2.setTag(tag2);
		dropDownSpecNoDuplicates2.setLabel(label2);
		specsBottomSection = addFieldSpec(specsBottomSection, dropDownSpecNoDuplicates2);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertTrue("invalid?", checker.isValid());
		
		specsTopSection = StandardFieldSpecs.getDefaultTopSetionFieldSpecs();
		specsBottomSection = StandardFieldSpecs.getDefaultBottomSectionFieldSpecs();

		ChoiceItem[] choicesWithDuplicate = {new ChoiceItem("duplicate", "duplicate"), new ChoiceItem("duplicate", "duplicate")};
		DropDownFieldSpec dropDownSpecWithDuplicates = new DropDownFieldSpec(choicesWithDuplicate);
		dropDownSpecWithDuplicates.setTag(tag);
		dropDownSpecWithDuplicates.setLabel(label);
		specsTopSection = addFieldSpec(specsTopSection, dropDownSpecWithDuplicates);

		ChoiceItem[] choicesWithDuplicate2 = {new ChoiceItem("duplicate2", "duplicate2"), new ChoiceItem("duplicate2", "duplicate2")};
		DropDownFieldSpec dropDownSpecWithDuplicates2 = new DropDownFieldSpec(choicesWithDuplicate2);
		dropDownSpecWithDuplicates2.setTag(tag2);
		dropDownSpecWithDuplicates2.setLabel(label2);
		specsBottomSection = addFieldSpec(specsBottomSection, dropDownSpecWithDuplicates2);
		
		checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("Duplicate Dropdown Entry", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Duplicate Dropdown Entry Bottom Section", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, tag2, label2, null, (CustomFieldError)errors.get(1));
	}

	public void testDuplicateDropDownEntryInSideOfAGrid() throws Exception
	{
		String tag = "dd";
		String label ="cc";

		String tag2 = "dd2";
		String label2 ="cc2";

		ChoiceItem[] choicesNoDups = {new ChoiceItem("no Dup", "first item"), new ChoiceItem("second", "second item")};
		DropDownFieldSpec dropDownSpecNoDuplicates = new DropDownFieldSpec(choicesNoDups);
		dropDownSpecNoDuplicates.setLabel("dropdown column label");
		GridFieldSpec gridWithNoDuplicateDropdownEntries = new GridFieldSpec();
		gridWithNoDuplicateDropdownEntries.setTag(tag);
		gridWithNoDuplicateDropdownEntries.setLabel(label);
		gridWithNoDuplicateDropdownEntries.addColumn(dropDownSpecNoDuplicates);
		specsTopSection = addFieldSpec(specsTopSection, gridWithNoDuplicateDropdownEntries);

		ChoiceItem[] choicesNoDups2 = {new ChoiceItem("no Dup2", "first item2"), new ChoiceItem("second2", "second item2")};
		DropDownFieldSpec dropDownSpecNoDuplicates2 = new DropDownFieldSpec(choicesNoDups2);
		dropDownSpecNoDuplicates2.setLabel("label");
		GridFieldSpec gridWithNoDuplicateDropdownEntries2 = new GridFieldSpec();
		gridWithNoDuplicateDropdownEntries2.setTag(tag2);
		gridWithNoDuplicateDropdownEntries2.setLabel(label2);
		gridWithNoDuplicateDropdownEntries2.addColumn(dropDownSpecNoDuplicates2);
		specsBottomSection = addFieldSpec(specsBottomSection, gridWithNoDuplicateDropdownEntries2);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertTrue("invalid?", checker.isValid());
		
		specsTopSection = StandardFieldSpecs.getDefaultTopSetionFieldSpecs();
		ChoiceItem[] choicesWithDuplicate = {new ChoiceItem("duplicate", "duplicate"), new ChoiceItem("duplicate", "duplicate")};
		DropDownFieldSpec dropDownSpecWithDuplicates = new DropDownFieldSpec(choicesWithDuplicate);
		dropDownSpecWithDuplicates.setLabel("dropdown column label with dups");
		GridFieldSpec gridWithDuplicateDropdownEntries = new GridFieldSpec();
		gridWithDuplicateDropdownEntries.setTag(tag);
		gridWithDuplicateDropdownEntries.setLabel(label);
		gridWithDuplicateDropdownEntries.addColumn(dropDownSpecWithDuplicates);
		specsTopSection = addFieldSpec(specsTopSection, gridWithDuplicateDropdownEntries);
		
		specsBottomSection = StandardFieldSpecs.getDefaultBottomSectionFieldSpecs();
		ChoiceItem[] choicesWithDuplicate2 = {new ChoiceItem("duplicate2", "duplicate2"), new ChoiceItem("duplicate2", "duplicate2")};
		DropDownFieldSpec dropDownSpecWithDuplicates2 = new DropDownFieldSpec(choicesWithDuplicate2);
		dropDownSpecWithDuplicates2.setLabel("Dropdown label");
		GridFieldSpec gridWithDuplicateDropdownEntries2 = new GridFieldSpec();
		gridWithDuplicateDropdownEntries2.setTag(tag2);
		gridWithDuplicateDropdownEntries2.setLabel(label2);
		gridWithDuplicateDropdownEntries2.addColumn(dropDownSpecWithDuplicates2);
		specsBottomSection = addFieldSpec(specsBottomSection, gridWithDuplicateDropdownEntries2);

		checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("Duplicate Dropdown Entry", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Duplicate Dropdown Entry Bottom Section", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, tag2, label2, null, (CustomFieldError)errors.get(1));
	}

	public void testDropDownWithMissingReusableChoices() throws Exception
	{
		String reusableChoicesName = "a";
		ReusableChoices reusableChoices = new ReusableChoices(reusableChoicesName, "whatever");
		specsTopSection.addReusableChoiceList(reusableChoices);
		
		CustomDropDownFieldSpec dropdown = new CustomDropDownFieldSpec();
		dropdown.setTag("tag");
		dropdown.setLabel("Label:");
		dropdown.addReusableChoicesCode(reusableChoicesName);
		dropdown.addReusableChoicesCode("Doesn't exist");
		dropdown.addReusableChoicesCode(null);
		specsTopSection.add(dropdown);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("Should be invalid due to missing reusable choices", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Not just the missing and null reusable choices errors?", 2, errors.size());
		CustomFieldError missingError = (CustomFieldError)errors.get(0);
		assertEquals("Wrong missing error code?", CustomFieldError.CODE_MISSING_REUSABLE_CHOICES, missingError.getCode());
		assertContains("Wrong missing tag?", dropdown.getTag(), missingError.getTag());
		assertContains("Wrong missing tag?", "Doesn't exist", missingError.getTag());
		assertEquals("Wrong missing label?", dropdown.getLabel(), missingError.getLabel());
		CustomFieldError nullError = (CustomFieldError)errors.get(1);
		assertEquals("Wrong null error code?", CustomFieldError.CODE_NULL_REUSABLE_CHOICES, nullError.getCode());
		assertEquals("Wrong null tag?", dropdown.getTag(), nullError.getTag());
		assertEquals("Wrong null label?", dropdown.getLabel(), nullError.getLabel());
	}
	
	public void testDropDownWithMissingReusableChoicesInsideGrid() throws Exception
	{
		String reusableChoicesName = "a";
		ReusableChoices reusableChoices = new ReusableChoices(reusableChoicesName, "whatever");

		CustomDropDownFieldSpec dropdown = new CustomDropDownFieldSpec();
		dropdown.setTag("tag");
		dropdown.setLabel("Label:");
		dropdown.addReusableChoicesCode(reusableChoicesName);
		dropdown.addReusableChoicesCode("Doesn't exist");

		GridFieldSpec gridWithDropDownWithMissingReusableChoices = new GridFieldSpec();
		gridWithDropDownWithMissingReusableChoices.setTag("grid");
		gridWithDropDownWithMissingReusableChoices.setLabel("Grid");
		gridWithDropDownWithMissingReusableChoices.addColumn(dropdown);
		specsTopSection = addFieldSpec(specsTopSection, gridWithDropDownWithMissingReusableChoices);
		specsTopSection.addReusableChoiceList(reusableChoices);
		
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		
		assertFalse("Should be invalid due to missing reusable choices", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should just be the missing reusable choices error", 1, errors.size());
		CustomFieldError error = (CustomFieldError)errors.get(0);
		assertEquals("Wrong error code?", CustomFieldError.CODE_MISSING_REUSABLE_CHOICES, error.getCode());
	}
	
	
	public void testNoDropDownEntries() throws Exception
	{
		String tag = "dd";
		String label ="cc";

		String tag2 = "dd2";
		String label2 ="cc2";

		DropDownFieldSpec dropDownSpecNoEntries = new DropDownFieldSpec();
		dropDownSpecNoEntries.setTag(tag);
		dropDownSpecNoEntries.setLabel(label);
		specsTopSection = addFieldSpec(specsTopSection, dropDownSpecNoEntries);

		DropDownFieldSpec dropDownSpecNoEntries2 = new DropDownFieldSpec();
		dropDownSpecNoEntries2.setTag(tag2);
		dropDownSpecNoEntries2.setLabel(label2);
		specsBottomSection = addFieldSpec(specsBottomSection, dropDownSpecNoEntries2);
		
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("No Dropdown Entries", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("No Dropdown Entries Bottom Section", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, tag2, label2, null, (CustomFieldError)errors.get(1));
	}

	public void testNoDropDownEntriesInsideOfAGrid() throws Exception
	{
		String tag = "dd";
		String label ="cc";

		String tag2 = "dd2";
		String label2 ="cc2";

		DropDownFieldSpec dropDownSpecNoEntries = new DropDownFieldSpec();
		dropDownSpecNoEntries.setLabel("dropdown label");
		GridFieldSpec gridWithNoDropdownEntries = new GridFieldSpec();
		gridWithNoDropdownEntries.setTag(tag);
		gridWithNoDropdownEntries.setLabel(label);
		gridWithNoDropdownEntries.addColumn(dropDownSpecNoEntries);
		specsTopSection = addFieldSpec(specsTopSection, gridWithNoDropdownEntries);

		DropDownFieldSpec dropDownSpecNoEntries2 = new DropDownFieldSpec();
		dropDownSpecNoEntries2.setLabel("dropdown label 2");
		GridFieldSpec gridWithNoDropdownEntries2 = new GridFieldSpec();
		gridWithNoDropdownEntries2.setTag(tag2);
		gridWithNoDropdownEntries2.setLabel(label2);
		gridWithNoDropdownEntries2.addColumn(dropDownSpecNoEntries2);
		specsBottomSection = addFieldSpec(specsBottomSection, gridWithNoDropdownEntries2);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("No Dropdown Entries In Grids", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("No Dropdown Entries In Grids Bottom Section", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, tag2, label2, null, (CustomFieldError)errors.get(1));
	}
	
	public void testBlankLabelsInsideOfAGrid() throws Exception
	{
		String columnTag = "dd";
		String columnEmptyLabel ="";
		String columnSpaceLabel =" ";

		
		String gridTag = "Grid";
		String gridLabel = "Grid Label";
		GridFieldSpec gridWithEmptyColumnLabel = new GridFieldSpec();
		gridWithEmptyColumnLabel.setTag(gridTag);
		gridWithEmptyColumnLabel.setLabel(gridLabel);
		
		gridWithEmptyColumnLabel.addColumn(LegacyCustomFields.createFromLegacy(columnTag+","+columnEmptyLabel));
		gridWithEmptyColumnLabel.addColumn(LegacyCustomFields.createFromLegacy(columnTag+","+columnSpaceLabel));
		specsTopSection = addFieldSpec(specsTopSection, gridWithEmptyColumnLabel);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("Empty Column Label in Grids", CustomFieldError.CODE_MISSING_LABEL, gridTag, null, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Space Column Label in Grids", CustomFieldError.CODE_MISSING_LABEL, gridTag, null, null, (CustomFieldError)errors.get(1));
	}

	public void testMissingCustomLabel() throws Exception
	{
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy("a,label"));
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy("a2,label2"));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertTrue("not valid?", checker.isValid());
		String tag = "b";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tag));
		String tag1 = "ab";
		String spaceLabel = " ";
		specsTopSection = addFieldSpec(specsTopSection, LegacyCustomFields.createFromLegacy(tag1+","+spaceLabel));
		String tag2 = "b2";
		specsBottomSection = addFieldSpec(specsBottomSection, LegacyCustomFields.createFromLegacy(tag2));
		CustomFieldSpecValidator checker2 = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("valid?", checker2.isValid());
		Vector errors = checker2.getAllErrors();
		assertEquals("Should have 3 error", 3, errors.size());
		verifyExpectedError("Missing Label", CustomFieldError.CODE_MISSING_LABEL, tag, null, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Label with spaces Only", CustomFieldError.CODE_MISSING_LABEL, tag1, null, null, (CustomFieldError)errors.get(1));
		verifyExpectedError("Missing Label Bottom Section", CustomFieldError.CODE_MISSING_LABEL, tag2, null, null, (CustomFieldError)errors.get(2));
	}
	
	public void testUnknownType() throws Exception
	{
		String tag = "weirdTag";
		String label = "weird Label";
		String xmlFieldUnknownType = "<CustomFields><Field><Tag>"+tag+"</Tag>" +
			"<Label>" + label + "</Label><Type>xxx</Type>" +
			"</Field></CustomFields>";
		FieldSpec badSpecTopSection = FieldCollection.parseXml(xmlFieldUnknownType).get(0); 
		specsTopSection = addFieldSpec(specsTopSection, badSpecTopSection);
		
		String tag2 = "weirdTag2";
		String label2 = "weird Label2";
		String xmlFieldUnknownType2 = "<CustomFields><Field><Tag>"+tag2+"</Tag>" +
			"<Label>" + label2 + "</Label><Type>xxx</Type>" +
			"</Field></CustomFields>";
		FieldSpec badSpecBottomSection = FieldCollection.parseXml(xmlFieldUnknownType2).get(0); 
		specsBottomSection = addFieldSpec(specsBottomSection, badSpecBottomSection);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("didn't detect unknown?", checker.isValid());

		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("Unknown Type", CustomFieldError.CODE_UNKNOWN_TYPE, tag, label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("Unknown Type Bottom Section", CustomFieldError.CODE_UNKNOWN_TYPE, tag2, label2, null, (CustomFieldError)errors.get(1));
	}

	public void testUnknownTypeInsideGrids() throws Exception
	{
		String gridTag = "Tag";
		String gridLabel = "Label";

		TestGridFieldSpec gridWithUnknownColumnType = new TestGridFieldSpec();
		gridWithUnknownColumnType.setTag(gridTag);
		gridWithUnknownColumnType.setLabel(gridLabel);
		String columnTag = "weirdTag2";
		String columnLabel = "weird Label2";
		String xmlFieldUnknownType2 = "<CustomFields><Field><Tag>"+columnTag+"</Tag>" +
			"<Label>" + columnLabel + "</Label><Type>xxx</Type>" +
			"</Field></CustomFields>";
		FieldSpec badSpecBottomSection = FieldCollection.parseXml(xmlFieldUnknownType2).get(0); 
		
		gridWithUnknownColumnType.addColumn(badSpecBottomSection);

		
		specsTopSection = addFieldSpec(specsTopSection, gridWithUnknownColumnType);
		
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specsTopSection, specsBottomSection);
		assertFalse("didn't detect unknown?", checker.isValid());

		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		verifyExpectedError("Unknown Type", CustomFieldError.CODE_UNKNOWN_TYPE, gridTag, columnLabel, null, (CustomFieldError)errors.get(0));
	}

	public void testStandardFieldWithLabel() throws Exception
	{
		FieldSpec[] rawSpecsTop = specsTopSection.asArray();
		String tag = rawSpecsTop[3].getTag();
		String illegal_label = "Some Label";
		rawSpecsTop[3] = LegacyCustomFields.createFromLegacy(tag + ","+ illegal_label);
		FieldSpecCollection top = new FieldSpecCollection(rawSpecsTop);

		FieldSpec[] rawSpecsBottom = specsBottomSection.asArray();
		String tag2 = rawSpecsBottom[0].getTag();
		String illegal_label2 = "Some Label2";
		rawSpecsBottom[0] = LegacyCustomFields.createFromLegacy(tag2 + ","+ illegal_label2);
		FieldSpecCollection bottom = new FieldSpecCollection(rawSpecsBottom);

		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(top, bottom);
		assertFalse("valid?", checker.isValid());

		Vector errors = checker.getAllErrors();
		assertEquals("Should have 2 error", 2, errors.size());
		verifyExpectedError("StandardField with Label", CustomFieldError.CODE_LABEL_STANDARD_FIELD, tag, illegal_label, null, (CustomFieldError)errors.get(0));
		verifyExpectedError("StandardField with Label Bottom Section", CustomFieldError.CODE_LABEL_STANDARD_FIELD, tag2, illegal_label2, null, (CustomFieldError)errors.get(1));
	}

	public void testParseXmlError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorParseXml();
		assertEquals("Incorrect Error code for parse XML error", CustomFieldError.CODE_PARSE_XML, xmlError.getCode());
	}

	public void testIOError() throws Exception
	{
		String errorMessage = "io message";
		CustomFieldError xmlError = CustomFieldError.errorIO(errorMessage);
		assertEquals("Incorrect Error code for IO error", CustomFieldError.CODE_IO_ERROR, xmlError.getCode());
		assertEquals("Incorrect error message for IO error", errorMessage, xmlError.getType());
	}

	public void testSignatureError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorSignature();
		assertEquals("Incorrect Error code for signature error", CustomFieldError.CODE_SIGNATURE_ERROR, xmlError.getCode());
	}

	public void testUnauthorizedKeyError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorUnauthorizedKey();
		assertEquals("Incorrect Error code for parse XML error", CustomFieldError.CODE_UNAUTHORIZED_KEY, xmlError.getCode());
	}

	static public FieldSpecCollection addFieldSpec(FieldSpecCollection existingFieldSpecs, FieldSpec newFieldSpec)
	{
		FieldSpecCollection newCollection = new FieldSpecCollection(existingFieldSpecs.asArray());
		newCollection.add(newFieldSpec);
		return newCollection;
	}

	private void verifyExpectedError(String reportingErrorMsg, String expectedErrorCode, String expectedTag, String expectedLabel, FieldType expectedType, CustomFieldError errorToVerify) 
	{
		assertEquals("Incorrect Error code: " + reportingErrorMsg, expectedErrorCode, (errorToVerify).getCode());
		if(expectedTag != null)
			assertEquals("Incorrect tag: " + reportingErrorMsg, expectedTag, errorToVerify.getTag());
		if(expectedLabel != null)
			assertEquals("Incorrect label: " + reportingErrorMsg, expectedLabel, errorToVerify.getLabel());
		if(expectedType != null)
			assertEquals("Incorrect type: " + reportingErrorMsg , FieldSpec.getTypeString(expectedType), errorToVerify.getType());
	}

	public static FieldSpecCollection getRequiredOnlyTopSectionFieldSpecs()
	{
		FieldSpec[] requiredOnlyTopSectionFieldSpecs = new FieldSpec[] 
			{
				FieldSpec.createStandardField(BulletinConstants.TAGLANGUAGE, new FieldTypeLanguage()),
				FieldSpec.createStandardField(BulletinConstants.TAGAUTHOR, new FieldTypeNormal()),
				FieldSpec.createStandardField(BulletinConstants.TAGTITLE, new FieldTypeNormal()),
				FieldSpec.createStandardField(BulletinConstants.TAGENTRYDATE, new FieldTypeDate()),
			};
		
		return new FieldSpecCollection(requiredOnlyTopSectionFieldSpecs);
		
	}
	
	public static FieldSpecCollection getAllNonRequiredMartusFieldSpecs()
	{
		FieldSpec[] allNonRequiredMartusFieldSpecs = new FieldSpec[] 
			{
				FieldSpec.createStandardField(BulletinConstants.TAGORGANIZATION, new FieldTypeLanguage()),
				FieldSpec.createStandardField(BulletinConstants.TAGLOCATION, new FieldTypeNormal()),
				FieldSpec.createStandardField(BulletinConstants.TAGKEYWORDS, new FieldTypeNormal()),
				FieldSpec.createStandardField(BulletinConstants.TAGEVENTDATE, new FieldTypeDate()),
				FieldSpec.createStandardField(BulletinConstants.TAGSUMMARY, new FieldTypeDate()),
				FieldSpec.createStandardField(BulletinConstants.TAGPUBLICINFO, new FieldTypeDate()),
			};
		
		return new FieldSpecCollection(allNonRequiredMartusFieldSpecs);
		
	}
	
	class TestGridFieldSpec extends GridFieldSpec
	{

		public boolean isValidColumnType(FieldType columnType)
		{
			return true;
		}
	}
	
	private FieldSpecCollection specsTopSection;
	private FieldSpecCollection specsBottomSection;
}
