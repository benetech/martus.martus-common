/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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

package org.martus.common.fieldspec;

import java.util.HashMap;
import java.util.Vector;

import org.martus.common.FieldCollection;
import org.martus.common.bulletin.BulletinConstants;

public class CustomFieldSpecValidator
{
	public CustomFieldSpecValidator(FieldCollection specsToCheckTopSection, FieldCollection specsToCheckBottomSection)
	{
		this(specsToCheckTopSection.getSpecs(), specsToCheckBottomSection.getSpecs());
	}
	
	public CustomFieldSpecValidator(FieldSpec[] specsToCheckTopSection, FieldSpec[] specsToCheckBottomSection)
	{
		errors = new Vector();
		if(specsToCheckTopSection == null || specsToCheckBottomSection == null)
		{
			errors.add(CustomFieldError.errorNoSpecs());
			return;
		}
		
		checkForReservedTags(specsToCheckTopSection);
		checkForRequiredTopSectionFields(specsToCheckTopSection);
		checkForIllegalTagCharacters(specsToCheckTopSection);
		checkForBlankTags(specsToCheckTopSection);
		checkForDuplicateFields(specsToCheckTopSection, specsToCheckBottomSection);
		checkForMissingCustomLabels(specsToCheckTopSection);
		checkForUnknownTypes(specsToCheckTopSection);
		checkForLabelsOnStandardFields(specsToCheckTopSection);
		checkForDropdownsWithDuplicatedOrZeroEntries(specsToCheckTopSection);
		checkForDropdownsWithDuplicatedOrZeroEntriesInsideGrids(specsToCheckTopSection);
		
		checkForReservedTags(specsToCheckBottomSection);
		checkForMartusFieldsBottomSectionFields(specsToCheckBottomSection);
		checkForIllegalTagCharacters(specsToCheckBottomSection);
		checkForBlankTags(specsToCheckBottomSection);
		checkForMissingCustomLabels(specsToCheckBottomSection);
		checkForUnknownTypes(specsToCheckBottomSection);
		checkForLabelsOnStandardFields(specsToCheckBottomSection);
		checkForDropdownsWithDuplicatedOrZeroEntries(specsToCheckBottomSection);
		checkForDropdownsWithDuplicatedOrZeroEntriesInsideGrids(specsToCheckBottomSection);

	}
		
	public boolean isValid()
	{
		if(errors.size()>0)
			return false;
		return true;
	}
	
	public Vector getAllErrors()
	{
		return errors;
	}
	
	public void addMissingCustomSpecError(String tag)
	{
		errors.add(CustomFieldError.errorMissingCustomSpec(tag));
	}

	private void checkForRequiredTopSectionFields(FieldSpec[] specsToCheck)
	{
		Vector missingTags = new Vector();
		missingTags.add(BulletinConstants.TAGAUTHOR);
		missingTags.add(BulletinConstants.TAGLANGUAGE);
		missingTags.add(BulletinConstants.TAGENTRYDATE);
		missingTags.add(BulletinConstants.TAGTITLE);
		for (int i = 0; i < specsToCheck.length; i++)
		{
			String tag = specsToCheck[i].getTag();
			if(missingTags.contains(tag))
			missingTags.remove(tag);
		}
		
		for (int j = 0; j < missingTags.size(); j++)
			errors.add(CustomFieldError.errorRequiredField((String)missingTags.get(j)));
	}
	
	
	private void checkForMartusFieldsBottomSectionFields(FieldSpec[] specsToCheck)
	{
		Vector topSectionOnlyTags = new Vector();
		topSectionOnlyTags.add(BulletinConstants.TAGLANGUAGE);
		topSectionOnlyTags.add(BulletinConstants.TAGAUTHOR);
		topSectionOnlyTags.add(BulletinConstants.TAGORGANIZATION);
		topSectionOnlyTags.add(BulletinConstants.TAGTITLE);
		topSectionOnlyTags.add(BulletinConstants.TAGLOCATION);
		topSectionOnlyTags.add(BulletinConstants.TAGEVENTDATE);
		topSectionOnlyTags.add(BulletinConstants.TAGENTRYDATE);
		topSectionOnlyTags.add(BulletinConstants.TAGKEYWORDS);
		topSectionOnlyTags.add(BulletinConstants.TAGSUMMARY);
		topSectionOnlyTags.add(BulletinConstants.TAGPUBLICINFO);
		for (int i = 0; i < specsToCheck.length; i++)
		{
			String tag = specsToCheck[i].getTag();
			if(topSectionOnlyTags.contains(tag))
				errors.add(CustomFieldError.errorTopSectionFieldInBottomSection(tag));
		}		
	}
	
	private void checkForReservedTags(FieldSpec[] specsToCheck)
	{
		Vector reservedTags = new Vector();
		reservedTags.add(BulletinConstants.TAGSTATUS);
		reservedTags.add(BulletinConstants.TAGWASSENT);
		reservedTags.add(BulletinConstants.TAGLASTSAVED);
		for (int i = 0; i < specsToCheck.length; i++)
		{
			String tag = specsToCheck[i].getTag();
			if(reservedTags.contains(tag))
				errors.add(CustomFieldError.errorReservedTag(tag, specsToCheck[i].getLabel()));
		}		
	}
	
	private void checkForBlankTags(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			String tag = thisSpec.getTag();
			if(tag.length() == 0)
				errors.add(CustomFieldError.errorBlankTag(thisSpec.getLabel(), getType(thisSpec)));				
		}
	}
	
	private void checkForIllegalTagCharacters(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			boolean allValid = true;
			String thisTag = thisSpec.getTag();
			if(thisTag.length() < 1)
				continue;
			char[] tagChars = thisTag.toCharArray();
			if(!isValidFirstTagCharacter(tagChars[0]))
				allValid = false;
			for(int j = 1; j < tagChars.length; ++j)
			{
				if(!isValidTagCharacter(tagChars[j]))
					allValid = false;
			}
			if(!allValid)
				errors.add(CustomFieldError.errorIllegalTag(thisTag, thisSpec.getLabel(), getType(thisSpec)));
		}
	}
	
	private boolean isValidTagCharacter(char c)
	{
		if(isValidFirstTagCharacter(c))
			return true;
		if(c > 128)
			return true;
		if(c == '-' || c == '.')
			return true;
		return false;
	}
	
	private boolean isValidFirstTagCharacter(char c)
	{
		if(Character.isLetterOrDigit(c))
			return true;
		if(c == '_')
			return true;
		return false;
	}
	
	private void checkForDuplicateFields(FieldSpec[] specsToCheck)
	{
		Vector foundTags = new Vector();
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			String tag = thisSpec.getTag();
			if(tag.length() > 0)
			{
				if(foundTags.contains(tag))
					errors.add(CustomFieldError.errorDuplicateFields(thisSpec.getTag(), thisSpec.getLabel(), getType(thisSpec)));				
				foundTags.add(tag);
			}
		}
	}
	
	private void checkForDuplicateFields(FieldSpec[] specsToCheckTopSection, FieldSpec[] specsToCheckBottomSection)
	{
		FieldCollection allSpecs = new FieldCollection(specsToCheckTopSection);
		for(int i = 0; i < specsToCheckBottomSection.length; ++i)
		{
			allSpecs.add(specsToCheckBottomSection[i]);
		}
		checkForDuplicateFields(allSpecs.getSpecs());
	}

	private void checkForDropdownsWithDuplicatedOrZeroEntries(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			if(thisSpec.getType().isDropdown())
			{
				DropDownFieldSpec dropdownSpec = (DropDownFieldSpec)thisSpec;
				String tag = thisSpec.getTag();
				String label = thisSpec.getLabel();
				checkForDuplicateEntriesInDropDownSpec(dropdownSpec, tag, label);
				checkForNoDropdownChoices(dropdownSpec, tag, label);
			}
		}
	}
	
	private void checkForDropdownsWithDuplicatedOrZeroEntriesInsideGrids(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			if(thisSpec.getType().isGrid())
			{
				GridFieldSpec gridSpec = (GridFieldSpec)thisSpec;
				for(int columns = 0; columns < gridSpec.getColumnCount(); ++columns)
				{
					FieldSpec columnSpec = gridSpec.getFieldSpec(columns);
					if(columnSpec.getType().isDropdown())
					{
						checkForDuplicateEntriesInDropDownSpec((DropDownFieldSpec)columnSpec, gridSpec.getTag(), gridSpec.getLabel());
						checkForNoDropdownChoices((DropDownFieldSpec)columnSpec, gridSpec.getTag(), gridSpec.getLabel());
					}
				}
			}
		}
	}
	
	private void checkForDuplicateEntriesInDropDownSpec(DropDownFieldSpec dropdownSpec, String tag, String label)
	{
		HashMap labelEntries = new HashMap();
		for(int choice = 0; choice < dropdownSpec.getCount(); ++choice)
		{
			String choiceEntryLabel = dropdownSpec.getValue(choice);
			if(labelEntries.containsKey(choiceEntryLabel))
				errors.add(CustomFieldError.errorDuplicateDropDownEntry(tag, label));				
			labelEntries.put(choiceEntryLabel, choiceEntryLabel);
		}
	}
	
	private void checkForNoDropdownChoices(DropDownFieldSpec dropdownSpec, String tag, String label)
	{
		if(dropdownSpec.getCount() == 0)
			errors.add(CustomFieldError.noDropDownEntries(tag, label));				
	}
	
	private void checkForMissingCustomLabels(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i]; 
			String tag = thisSpec.getTag();
			if(StandardFieldSpecs.isCustomFieldTag(tag) && thisSpec.getLabel().equals(""))
				errors.add(CustomFieldError.errorMissingLabel(thisSpec.getTag(), getType(thisSpec)));				
		}
	}

	private void checkForUnknownTypes(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i]; 
			if(thisSpec.getType().isUnknown())
				errors.add(CustomFieldError.errorUnknownType(thisSpec.getTag(), thisSpec.getLabel()));				
		}
	}
	
	private void checkForLabelsOnStandardFields(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i]; 
			String tag = thisSpec.getTag();
			if(!StandardFieldSpecs.isCustomFieldTag(tag) && !thisSpec.getLabel().equals(""))
				errors.add(CustomFieldError.errorLabelOnStandardField(thisSpec.getTag(), thisSpec.getLabel(), getType(thisSpec)));				
		}
	}
	
	private String getType(FieldSpec thisSpec)
	{
		return FieldSpec.getTypeString( thisSpec.getType());
	}

	private Vector errors;
}
