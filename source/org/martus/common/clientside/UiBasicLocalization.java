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

package org.martus.common.clientside;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import org.martus.common.VersionBuildDate;
import org.martus.util.UnicodeWriter;


public class UiBasicLocalization extends Localization
{
    public void exportTranslations(String languageCode, String versionLabel, UnicodeWriter writer)
		throws IOException 
	{
		setCurrentLanguageCode("en");
		String byteOrderMark = new String(new char[] {0xFEFF});
		writer.writeln(byteOrderMark + "# Martus Client Translation File");
		writer.writeln("# Language code:  " + languageCode);
		writer.writeln("# Language name:  " + getLanguageName(languageCode));
		writer.writeln("# Exported date:  " + new Date().toString());
		writer.writeln("# Client version: " + versionLabel);
		writer.writeln("# Client build:   " + VersionBuildDate.getVersionBuildDate());
		writer.writeln("#");
		writer.writeln("# This file MUST be saved in UTF-8 format!");
		writer.writeln("# Lines beginning with # are comments and are ignored by Martus");
		writer.writeln("# Each entry has two lines. The first line is the current English");
		writer.writeln("#    text, and the second line consists of: KEY=VALUE");
		writer.writeln("#    (e.g. -c47a-button:customHelp=<Help> )");
		writer.writeln("# Do not modify any KEY. Do localize every VALUE.");
		writer.writeln("# Each VALUE that needs to be translated has <> around it.");
		writer.writeln("#    As you translate each VALUE, remove its <>");
		writer.writeln("# When a non-English VALUE appears inside <> it is because the English");
		writer.writeln("#    translation has changed. Verify or update the translation to ");
		writer.writeln("#    match the new English text, and remove the <>");
		writer.writeln("# If there is an English VALUE inside <> , it is because it is a field"); 
		writer.writeln("#    added after the last release. Remove the english, insert/update the"); 
		writer.writeln("#    VALUE translation to match the new English text which is in the"); 
		writer.writeln("#    comment line directly above it, and remove the <>");
		writer.writeln("# If you name it Martus-" + languageCode + ".mtf and put the file in");
		writer.writeln("#     c:\\Martus, then Martus will automatically read it.");
		writer.writeln("# In Martus, to choose a language, pick one from the drop-down list");
		writer.writeln("#    in the lower left-hand corner of the signin screen, or after you");
		writer.writeln("#    are logged in, go to Options/Preferences.");
		writer.writeln("#");
		writer.writeln("#  1.  do NOT translate \"\\n\" (used for new lines)");
		writer.writeln("#  2.  do NOT create any user-entered new-lines/carriage-returns");
		writer.writeln("#      (by using the enter key) in any mtf entry");
		writer.writeln("#      (from the field:xxx to the end of the translated text),");
		writer.writeln("#      since a continuous block of text is needed.");
		writer.writeln("#  3.  do NOT translate \"#N#\" or \"#M#\" (though move them as appropriate");
		writer.writeln("#      grammatically for the language to make \"#N# of #M#\" [eg \"2 of 5\"] make");
		writer.writeln("#      sense for creating/restoring multiple key backup files).");
		writer.writeln("#  4.  in field:VirtualKeyboardKeys, keep the english alphabet, but include any");
		writer.writeln("#      non-english characters at the end of the english alphabet/numbers/special");
		writer.writeln("#      characters (e.g. attach entire Thai alphabet at the end of the line)");
		writer.writeln("#  5.  in field:SearchBulletinRules, make sure to translate the english \"and\"");
		writer.writeln("#      and \"or\" in the text to exactly match the keyword:and= and ");
		writer.writeln("#      keyword:or= translations.  Also, the translation of the first sentence");
		writer.writeln("#      should not exactly match the english version because Martus can handle");
		writer.writeln("#      both english \"and\"/\"or\" and the translations of those keywords for");
		writer.writeln("#      bulletin searching, since for some languages keyboards are not available");
		writer.writeln("#      to all users in those alphabets. So please translate the following for the");
		writer.writeln("#      first sentence: \"When searching for bulletins you can add key words either");
		writer.writeln("#      in english or the-language-you-are-translating (or/or-translated,");
		writer.writeln("#      and/and-translated) between multiple search term words");
		writer.writeln("#      (e.g. prison or jail, prison and assault).");
		writer.writeln("#  6.  in CreateCustomFieldsHelp2/3 fields, you can translate tags into foreign");
		writer.writeln("#      characters (but without punctuation or spaces)");
		writer.writeln("#  7.  when there are file or directory names, do not translate them");
		writer.writeln("#      (e.g.  \"acctmap.txt\" and \"packets\"");
		writer.writeln("#      in field:confirmWarnMissingAccountMapFilecause=Warning: acctmap.txt");
		writer.writeln("#      file in your account's packets directory...)");
		writer.writeln("#  8.  do not translate the words \"Martus\" or \"Benetech\"");
		writer.writeln("#  9.  do NOT translate \"#S#\" (used for search string entry)");
		writer.writeln("# 10.  do not translate \"#L#\" as is it used to populate lists of values on");
		writer.writeln("#      various dialogs.");
		writer.writeln("# 11.  do not translate \"#Title#\" (used for bulletin title entry)");
		writer.writeln("# 12.  do not translate \"#A#\" (used for bulletin details: Author Public Code)");
		writer.writeln("# 13.  do not translate \"#I#\" (used for bulletin details: Bulletin ID)");
		writer.writeln("# 14.  do not translate \"#H#\" (used for bulletin details: Headquarter entries)");
		writer.writeln("# 15.  do not translate \"#NumberOfHQs#\" in various dialogs");
		writer.writeln("# 16.  Leave standard field tags in English in CreateCustomFieldsHelp1 and");
		writer.writeln("#      CreateCustomFieldsHelp2, but put translation in parentheses after");
		writer.writeln("#      english : e.g.  'author' (autor), so users know what they refer to");
		writer.writeln("# 17.  Leave field types in English in CreateCustomFieldsHelp2 (e.g. BOOLEAN,");
		writer.writeln("#      DATE), but put translation in parentheses after english, so users know");
		writer.writeln("#      what they refer to."); 
		writer.writeln("# 18.  Leave field types in English in CreateCustomFieldsHelp3 examples");
		writer.writeln("#      (e.g. BOOLEAN, DATE)");
		writer.writeln("# 19.  Do not translate words used in XML for custom fields such as:");  
		writer.writeln("#      <Field type='GRID'>, </Field>, <Tag>, </Tag>, <Label>, </Label>,");
		writer.writeln("#      <GridSpecDetails>, </GridSpecDetails>, <Column>, </Column>");
		writer.writeln("# 20.  If you want your language to appear right to left,");
		writer.writeln("#      Uncomment the next line (by removing the # infront of #!right-to-left)");
		writer.writeln("#!right-to-left");
		writer.writeln("#");
		
		SortedSet sorted = getAllKeysSorted();
		Iterator it = sorted.iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			String mtfEntry = getMtfEntry(languageCode, key);
			String englishMtfEntry = getMtfEntry(ENGLISH, key);
			int keyEnd = mtfEntry.indexOf('=');
			char[] filler = new char[keyEnd];
			Arrays.fill(filler, '_');
			filler[0] = '#';
			String result = new String(filler) + englishMtfEntry.substring(keyEnd);

			writer.writeln();
			writer.writeln(result);
			writer.writeln(mtfEntry);
			
		}
	}

	public UiBasicLocalization (File directoryToUse, String[] englishTranslations)
	{
		super(directoryToUse);
		for(int i=0; i < englishTranslations.length; ++i)
		{
			String mtfEntry = englishTranslations[i];
			addEnglishTranslation(mtfEntry);
		}

		setCurrentDateFormatCode(DateUtilities.MDY_SLASH.getCode());
	}

	public String getLabel(String languageCode, String category, String tag)
	{
		return getLabel(languageCode, category + ":" + tag);
	}

	protected ChoiceItem getLanguageChoiceItem(String filename)
	{
		String code = getLanguageCodeFromFilename(filename);
		String name = getLabel(ENGLISH, "language", code);
		return new ChoiceItem(code, name);
	}

	public ChoiceItem[] getUiLanguages()
	{
		Vector languages = new Vector();
		languages.addElement(new ChoiceItem(ENGLISH, getLabel(ENGLISH, "language", ENGLISH)));
		languages.addAll(getAllCompiledLanguageResources());
		languages.addAll(getNonDuplicateLanguageResourcesInDirectory(languages, directory));
		return (ChoiceItem[])(languages.toArray(new ChoiceItem[0]));
	}
	
	protected Vector getAllCompiledLanguageResources()
	{
		return new Vector();
	}
	
	
	Vector getNonDuplicateLanguageResourcesInDirectory(Vector currentLanguages, File languageDirectory)
	{
		Vector nonDuplicateLanguages = new Vector();
		String[] languageFiles = languageDirectory.list(new LanguageFilenameFilter());
		for(int i=0;i<languageFiles.length;++i)
		{
			if(includeOfficialLanguagesOnly && !isOfficialTranslationFile(new File(languageDirectory, languageFiles[i])))
				continue;
			ChoiceItem languageChoiceItem = getLanguageChoiceItem(languageFiles[i]);
			String languageCodeToAdd = languageChoiceItem.getCode();
			boolean nonDuplicateLanguage = true;
			for(int j=0; j<currentLanguages.size(); ++j)
			{
				ChoiceItem languageChoiceAlreadyAdded = (ChoiceItem)currentLanguages.get(j);
				if(languageChoiceAlreadyAdded.getCode().equalsIgnoreCase(languageCodeToAdd))
				{
					nonDuplicateLanguage = false;
					break;
				}
			}
			if(nonDuplicateLanguage)
				nonDuplicateLanguages.addElement(languageChoiceItem);
		}
		return nonDuplicateLanguages;
	}
	
	public String getLocalizedFolderName(String folderName)
	{
		return getLabel(getCurrentLanguageCode(), "folder", folderName);
	}

	public String getFieldLabel(String fieldName)
	{
		return getLabel(getCurrentLanguageCode(), "field", fieldName);
	}

	public String getLanguageName(String code)
	{
		return getLabel(getCurrentLanguageCode(), "language", code);
	}

	public String getWindowTitle(String code)
	{
		return getLabel(getCurrentLanguageCode(), "wintitle", code);
	}

	public String getButtonLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "button", code);
	}

	public String getMenuLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "menu", code);
	}

	public String getStatusLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "status", code);
	}

	public String getKeyword(String code)
	{
		return getLabel(getCurrentLanguageCode(), "keyword", code);
	}

	public String getMonthLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "month", code);
	}

	public String[] getMonthLabels()
	{
		final String[] tags = {"jan","feb","mar","apr","may","jun",
							"jul","aug","sep","oct","nov","dec"};

		String[] labels = new String[tags.length];
		for(int i = 0; i < labels.length; ++i)
		{
			labels[i] = getMonthLabel(tags[i]);
		}

		return labels;
	}

	public ChoiceItem[] getLanguageNameChoices()
	{
		return getLanguageNameChoices(ALL_LANGUAGE_CODES);
	}

	public ChoiceItem[] getLanguageNameChoices(String[] languageCodes)
	{
		if(languageCodes == null)
			return null;
		ChoiceItem[] tempChoicesArray = new ChoiceItem[languageCodes.length];
		for(int i = 0; i < languageCodes.length; i++)
		{
			tempChoicesArray[i] =
				new ChoiceItem(languageCodes[i], getLanguageName(languageCodes[i]));
		}
		Arrays.sort(tempChoicesArray);
		return tempChoicesArray;
	}

	public Vector getAllTranslationStrings(String languageCode)
	{
		SortedSet sorted = getAllKeysSorted();
		Vector strings = new Vector();
		Iterator it = sorted.iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			String mtfEntry = getMtfEntry(languageCode, key);
			strings.add(mtfEntry);
		}
		return strings;
	}

	public static class LanguageFilenameFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			return isLanguageFile(name);
		}
	}
	
}
