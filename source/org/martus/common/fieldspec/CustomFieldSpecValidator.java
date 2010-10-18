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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.martus.common.FieldCollection;
import org.martus.common.FieldSpecCollection;
import org.martus.common.PoolOfReusableChoicesLists;
import org.martus.common.ReusableChoices;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.util.xml.SimpleXmlDefaultLoader;
import org.martus.util.xml.SimpleXmlParser;

public class CustomFieldSpecValidator
{
	public CustomFieldSpecValidator(FieldCollection specsToCheckTopSection, FieldCollection specsToCheckBottomSection, boolean allowSpaceOnlyCustomLabels)
	{
		this(specsToCheckTopSection.getSpecs(), specsToCheckBottomSection.getSpecs(), allowSpaceOnlyCustomLabels);
	}
	
	public CustomFieldSpecValidator(FieldSpecCollection specsToCheckTopSection, FieldSpecCollection specsToCheckBottomSection)
	{
		this(specsToCheckTopSection, specsToCheckBottomSection, false);
	}
	
	public CustomFieldSpecValidator(FieldSpecCollection specsToCheckTopSection, FieldSpecCollection specsToCheckBottomSection, boolean allowSpaceOnlyCustomLabels)
	{
		allowSpaceOnlyCustomFieldLabels = allowSpaceOnlyCustomLabels;
		errors = new Vector();
		
		FieldSpec[] rawSpecsToCheckTopSection = specsToCheckTopSection.asArray();
		FieldSpec[] rawSpecsToCheckBottomSection = specsToCheckBottomSection.asArray();
		
		HashMap topGridFieldSpecs = scanForGrids(rawSpecsToCheckTopSection);
		HashMap bottomGridFieldSpecs = scanForGrids(rawSpecsToCheckBottomSection);
	
		checkForDuplicatesInResuableChoiceLists(specsToCheckTopSection);
		checkForDuplicatesInResuableChoiceLists(specsToCheckBottomSection);
		
		checkForRequiredTopSectionFields(rawSpecsToCheckTopSection);
		checkForPrivateField(rawSpecsToCheckTopSection);

		checkCommonErrors(specsToCheckTopSection);
		checkCommonErrors(specsToCheckBottomSection);
		
		checkForCommonErrorsInsideGrids(specsToCheckTopSection, topGridFieldSpecs);
		checkForCommonErrorsInsideGrids(specsToCheckBottomSection, bottomGridFieldSpecs);

		checkForDuplicateFields(rawSpecsToCheckTopSection, rawSpecsToCheckBottomSection);
		
		checkForMartusFieldsBottomSectionFields(rawSpecsToCheckBottomSection);

		checkDataDrivenDropDowns(rawSpecsToCheckTopSection, topGridFieldSpecs);
		checkDataDrivenDropDowns(rawSpecsToCheckBottomSection, bottomGridFieldSpecs);
	}

	private void checkForDuplicatesInResuableChoiceLists(FieldSpecCollection specsToCheckBottomSection)
	{
		PoolOfReusableChoicesLists reusableChoicesLists = specsToCheckBottomSection.getAllReusableChoiceLists();
		Set choicesListNames = reusableChoicesLists.getAvailableNames();
		Iterator iter = choicesListNames.iterator();
		while(iter.hasNext())
		{
			String name = (String) iter.next();
			ReusableChoices choices = reusableChoicesLists.getChoices(name);
			checkForDuplicatesInReusableChoices(choices);
		}
	}

	private void checkForDuplicatesInReusableChoices(ReusableChoices choices)
	{
		HashSet codes = new HashSet();
		HashMap labelToCodesMap = new HashMap();
		for(int i = 0; i < choices.size(); ++i)
		{
			ChoiceItem choice = choices.get(i);
			String code = choice.getCode();
			String label = choice.getLabel();

			if(codes.contains(code))
				errors.add(CustomFieldError.errorDuplicateDropDownEntryInReusableChoices(code, label));
			codes.add(code);
			
			if(!labelToCodesMap.containsKey(label))
				labelToCodesMap.put(label, new Vector());
			Vector codesForLabel = (Vector) labelToCodesMap.get(label);
			codesForLabel.add(code);
		}
		
		Iterator iter = labelToCodesMap.keySet().iterator();
		while(iter.hasNext())
		{
			String label = (String)iter.next();
			Vector codesForLabel = (Vector) labelToCodesMap.get(label);
			for(int i = 0; i < codesForLabel.size(); ++i)
			{
				for(int j = i+1; j < codesForLabel.size(); ++j)
				{
					String code1 = (String) codesForLabel.get(i);
					String code2 = (String) codesForLabel.get(j);
					int lastDotAt1 = code1.lastIndexOf('.');
					int lastDotAt2 = code2.lastIndexOf('.');
					if(lastDotAt1 < 0 || lastDotAt2 < 0)
					{
						errors.add(CustomFieldError.errorDuplicateDropDownEntryInReusableChoices(code2, label));
						continue;
					}
					String prefix1 = code1.substring(0, lastDotAt1);
					String prefix2 = code2.substring(0, lastDotAt2);
					if(prefix1.equals(prefix2))
						errors.add(CustomFieldError.errorDuplicateDropDownEntryInReusableChoices(code2, label));
				}
			}
		}
	}

	private HashMap scanForGrids(FieldSpec[] specsToCheck)
	{
		HashMap grids = new HashMap();
		for (int i = 0; i < specsToCheck.length; i++)
		{
			FieldSpec thisSpec = specsToCheck[i];
			if(thisSpec.getType().isGrid())
				grids.put(thisSpec.getTag(), thisSpec);
		}

		return grids;
	}

	private void checkCommonErrors(FieldSpecCollection specsToCheck) 
	{
		checkForReservedTags(specsToCheck.asArray());
		checkForLabelsOnStandardFields(specsToCheck.asArray());

		checkForDropdownsWithDuplicatedOrZeroEntries(specsToCheck);
		checkForIllegalTagCharacters(specsToCheck.asArray());
		checkForBlankTags(specsToCheck.asArray());
		checkForMissingCustomLabels(specsToCheck.asArray());
		checkForUnknownTypes(specsToCheck.asArray());
		checkForInvalidDefaultValuesInDropdowns(specsToCheck);
		
		checkReusableChoicesListLabels(specsToCheck.getAllReusableChoiceLists());
		checkReusableChoicesHaveCodesAndLabels(specsToCheck.getAllReusableChoiceLists());
	}
		
	private void checkReusableChoicesListLabels(PoolOfReusableChoicesLists allReusableChoiceLists)
	{
		Set labels = new HashSet();
		Iterator iter = allReusableChoiceLists.getAvailableNames().iterator();
		while(iter.hasNext())
		{
			String name = (String)iter.next();
			ReusableChoices choices = allReusableChoiceLists.getChoices(name);
			String thisLabel = choices.getLabel();

			if(thisLabel.length() == 0)
				errors.add(CustomFieldError.errorMissingLabel(choices.getCode(), CustomFieldError.TYPE_STRING_FOR_REUSABLE_LISTS));
			
			if(labels.contains(thisLabel))
				errors.add(CustomFieldError.errorDuplicateReusableChoicesListLabel(thisLabel));
			labels.add(thisLabel);
		}
	}

	private void checkReusableChoicesHaveCodesAndLabels(PoolOfReusableChoicesLists allReusableChoiceLists)
	{
		Iterator iter = allReusableChoiceLists.getAvailableNames().iterator();
		while(iter.hasNext())
		{
			String name = (String)iter.next();
			ReusableChoices choices = allReusableChoiceLists.getChoices(name);
			for(int i = 0; i < choices.size(); ++i)
			{
				ChoiceItem choice = choices.get(i);
				if(choice.getCode() == null || choice.toString() == null)
					errors.add(CustomFieldError.errorInvalidReusableChoice(name, i));
			}
		}
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
	
	private void checkForPrivateField(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			String tag = specsToCheck[i].getTag();
			if(tag.equals(BulletinConstants.TAGPRIVATEINFO))
				errors.add(CustomFieldError.errorBottomSectionFieldInTopSection(tag));
		}		
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

			String xmlTag = "Field-" + thisTag;
			SimpleXmlDefaultLoader loader = new SimpleXmlDefaultLoader(xmlTag);
			String xml = "<" + xmlTag + "/>";
			try 
			{
				SimpleXmlParser.parse(loader, xml);
			} 
			catch (Exception e) 
			{
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
		int topLength = specsToCheckTopSection.length;
		int bottomLength = specsToCheckBottomSection.length;
		FieldSpec[] allSpecs = new FieldSpec[topLength + bottomLength];
		System.arraycopy(specsToCheckTopSection, 0, allSpecs, 0, topLength);
		System.arraycopy(specsToCheckBottomSection, 0, allSpecs, topLength, bottomLength);
		checkForDuplicateFields(allSpecs);
	}

	private void checkForInvalidDefaultValuesInDropdowns(FieldSpecCollection specsToCheck)
	{
		for (int i = 0; i < specsToCheck.size(); i++)
		{
			FieldSpec thisSpec = specsToCheck.get(i);
			if(thisSpec.getType().isDropdown())
			{
				DropDownFieldSpec dropdownSpec = (DropDownFieldSpec)thisSpec;
				checkForInvalidDefaultValueInDropdown(dropdownSpec, specsToCheck.getAllReusableChoiceLists());
			}
		}
	}

	private void checkForInvalidDefaultValueInDropdown(DropDownFieldSpec dropdownSpec, PoolOfReusableChoicesLists reusableChoicesLists)
	{
		String defaultValue = dropdownSpec.getDefaultValue();
		if(defaultValue == null || defaultValue.length() == 0)
			return;
		
		Object candidateError = CustomFieldError.errorInvalidDefaultValue(dropdownSpec.getTag(), dropdownSpec.getLabel(), getType(dropdownSpec));

		if(dropdownSpec.hasDataSource())
		{
			errors.add(candidateError);
			return;
		}
		
		if(dropdownSpec.hasReusableCodes())
		{
			ChoiceItem match = reusableChoicesLists.findChoiceFromFullOrPartialCode(dropdownSpec.getReusableChoicesCodes(), defaultValue);
			if(match == null)
				errors.add(candidateError);
			return;
		}
		
		if(dropdownSpec.findCode(defaultValue) >= 0)
			return;
		
		errors.add(candidateError);
	}

	private void checkForDropdownsWithDuplicatedOrZeroEntries(FieldSpecCollection specsToCheck)
	{
		for (int i = 0; i < specsToCheck.size(); i++)
		{
			FieldSpec thisSpec = specsToCheck.get(i);
			if(thisSpec.getType().isDropdown())
			{
				DropDownFieldSpec dropdownSpec = (DropDownFieldSpec)thisSpec;
				String tag = thisSpec.getTag();
				String label = thisSpec.getLabel();
				checkForDuplicateEntriesInDropDownSpec(dropdownSpec, tag, label);
				checkForNoDropdownChoices(dropdownSpec, tag, label);
				checkForMissingReusableChoices(dropdownSpec, tag, label, specsToCheck.getReusableChoiceNames());
				checkForDataSourceReusableOrNested(dropdownSpec, specsToCheck);
			}
		}
	}
	
	private void checkForDataSourceReusableOrNested(DropDownFieldSpec dropdownSpec, FieldSpecCollection specsToCheck)
	{
		String gridTag = dropdownSpec.getDataSourceGridTag();
		if(gridTag == null || gridTag.length() == 0)
			return;
		
		FieldSpec rawGridSpec = specsToCheck.findBytag(gridTag);
		if(rawGridSpec == null)
			return;

		GridFieldSpec gridSpec = (GridFieldSpec)rawGridSpec;
		String gridColumnLabel = dropdownSpec.getDataSourceGridColumn();
		FieldSpec rawColumnSpec = gridSpec.findColumnSpecByLabel(gridColumnLabel);
		if(!rawColumnSpec.getType().isDropdown())
			return;
		
		DropDownFieldSpec columnDropdownSpec = (DropDownFieldSpec) rawColumnSpec;
		if(columnDropdownSpec.getReusableChoicesCodes().length > 0)
			errors.add(CustomFieldError.errorDataSourceReusableDropdown(dropdownSpec.getTag(), dropdownSpec.getLabel()));
	}

	private void checkForCommonErrorsInsideGrids(FieldSpecCollection specsToCheck, HashMap otherGrids)
	{
		for (int i = 0; i < specsToCheck.size(); i++)
		{
			FieldSpec thisSpec = specsToCheck.get(i);
			if(thisSpec.getType().isGrid())
			{
				GridFieldSpec gridSpec = (GridFieldSpec)thisSpec;
				for(int columns = 0; columns < gridSpec.getColumnCount(); ++columns)
				{
					FieldSpec columnSpec = gridSpec.getFieldSpec(columns);
					String gridTag = gridSpec.getTag();
					checkForMissingCustomLabel(columnSpec, gridTag);
					checkForUnknownType(columnSpec, gridTag);
					if(columnSpec.getType().isDropdown())
					{
						String gridLabel = gridSpec.getLabel();
						DropDownFieldSpec dropdownSpec = (DropDownFieldSpec)columnSpec;
						checkForDuplicateEntriesInDropDownSpec(dropdownSpec, gridTag, gridLabel);
						checkForNoDropdownChoices(dropdownSpec, gridTag, gridLabel);
						checkDataDrivenDropDown(dropdownSpec, otherGrids);
						checkForMissingReusableChoices(dropdownSpec, gridTag, gridLabel, specsToCheck.getReusableChoiceNames());
						checkForDataSourceReusableOrNested(dropdownSpec, specsToCheck);
						checkForInvalidDefaultValueInDropdown(dropdownSpec, specsToCheck.getAllReusableChoiceLists());
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
		if(dropdownSpec.getDataSourceGridTag() != null)
			return;
		
		if(dropdownSpec.getReusableChoicesCodes().length > 0)
			return;
		
		if(dropdownSpec.getCount() == 0)
			errors.add(CustomFieldError.noDropDownEntries(tag, label));				
	}
	
	private void checkForMissingReusableChoices(DropDownFieldSpec dropdownSpec, String tag, String label, Set reusableChoiceNames)
	{
		String[] reusableChoicesCodes = dropdownSpec.getReusableChoicesCodes();

		for(int i = 0; i < reusableChoicesCodes.length; ++i)
		{
			String reusableChoicesCode = reusableChoicesCodes[i];
			if(reusableChoicesCode == null)
			{
				String typeName = dropdownSpec.getType().getTypeName();
				errors.add(CustomFieldError.errorNullReusableChoices(tag, label, typeName));
			}
			else if(!reusableChoiceNames.contains(reusableChoicesCode))
			{
				String typeName = dropdownSpec.getType().getTypeName();
				String fullTag = tag + "." + reusableChoicesCode;
				errors.add(CustomFieldError.errorMissingReusableChoices(fullTag, label, typeName));
			}
		}
	}

	private void checkForMissingCustomLabels(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			checkForMissingCustomLabel(specsToCheck[i], specsToCheck[i].getTag());
		}
	}

	private void checkForMissingCustomLabel(FieldSpec thisSpec, String tag)
	{
		String label = thisSpec.getLabel();
		if(!allowSpaceOnlyCustomFieldLabels)
			label = label.trim();
		if(StandardFieldSpecs.isCustomFieldTag(tag) && label.equals(""))
			errors.add(CustomFieldError.errorMissingLabel(tag, getType(thisSpec)));
	}

	private void checkForUnknownTypes(FieldSpec[] specsToCheck)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			checkForUnknownType(specsToCheck[i], specsToCheck[i].getTag());				
		}
	}

	private void checkForUnknownType(FieldSpec thisSpec, String tag)
	{
		if(thisSpec.getType().isUnknown())
			errors.add(CustomFieldError.errorUnknownType(tag, thisSpec.getLabel()));
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
	
	private void checkDataDrivenDropDowns(FieldSpec[] specsToCheck, HashMap availableGrids)
	{
		for (int i = 0; i < specsToCheck.length; i++)
		{
			if(!specsToCheck[i].getType().isDropdown())
				continue;
			
			checkDataDrivenDropDown((DropDownFieldSpec)specsToCheck[i], availableGrids);
		}
		
	}
	
	private void checkDataDrivenDropDown(DropDownFieldSpec specToCheck, HashMap availableGrids)
	{
		String tag = specToCheck.getTag();
		
		String gridTag = specToCheck.getDataSourceGridTag();
		if(gridTag == null)
			return;
		
		String label = specToCheck.getLabel();
		String typeString = getType(specToCheck);
		if(specToCheck.getCount() > 0)
			errors.add(CustomFieldError.errorDropDownHasChoicesAndDataSource(tag, label, typeString));
		
		if(!availableGrids.containsKey(gridTag))
		{
			errors.add(CustomFieldError.errorDataSourceNoGridTag(tag, label, typeString));				
			return;
		}
		
		String gridColumn = specToCheck.getDataSourceGridColumn();
		GridFieldSpec grid = (GridFieldSpec)availableGrids.get(gridTag);
		if(!grid.hasColumnLabel(gridColumn))
			errors.add(CustomFieldError.errorDataSourceNoGridColumn(tag, label, typeString));				

	}

	private String getType(FieldSpec thisSpec)
	{
		return FieldSpec.getTypeString( thisSpec.getType());
	}
	
	public String toString() 
	{
		String result = "CustomFieldSpecValidator: \n";
		for(int i = 0; i < errors.size(); ++i)
			result += errors.get(i).toString() + "\n";
		return result;
	}



	private boolean allowSpaceOnlyCustomFieldLabels;
	private Vector errors;
}
