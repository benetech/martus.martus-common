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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusCrypto;
import org.martus.util.UnicodeReader;

public class Localization
{
	public Localization(File directoryToUse)
	{
		directory = directoryToUse;
		textResources = new TreeMap();
	}
	
	/////////////////////////////////////////////////////////////////
	// Text-oriented stuff
	public String getCurrentLanguageCode()
	{
		return currentLanguageCode;
	}

	public void setCurrentLanguageCode(String newLanguageCode)
	{
		loadTranslationFile(newLanguageCode);
		currentLanguageCode = newLanguageCode;
	}
	
	protected String getLabel(String languageCode, String key)
	{
		Map availableTranslations = getAvailableTranslations(key);
		if(availableTranslations == null)
			return formatAsUntranslated(key);

		String translatedText = (String)availableTranslations.get(languageCode);
		if(translatedText != null)
			return translatedText;

		String englishText = (String)availableTranslations.get(ENGLISH);
		if(englishText == null)
			System.out.println("Error, probably an invalid Martus-en.mtf file in C:\\Martus, try removing this file.");
		return formatAsUntranslated(englishText);
	}
	
	protected String getMtfEntry(String languageCode, String key)
	{
		String value = getLabel(languageCode, key);
		String hash = getHashOfEnglish(key);
		value = value.replaceAll("\\n", "\\\\n");
		return "-" + hash + "-" + key + "=" + value;
	}

	protected void addEnglishTranslation(String mtfEntry)
	{
		addTranslation(ENGLISH, mtfEntry);
	}

	public void addTranslation(String languageCode, String mtfEntryText)
	{
		if(mtfEntryText == null)
			return;
			
		if(mtfEntryText.startsWith("#"))
			return;
		
		if(mtfEntryText.indexOf('=') < 0)
			return;
	
		String key = extractKeyFromMtfEntry(mtfEntryText);
		Map availableTranslations = getAvailableTranslations(key);
		if(availableTranslations == null)
		{
			if(!languageCode.equals(ENGLISH))
				return;
			availableTranslations = new TreeMap();
			textResources.put(key, availableTranslations);
		}
		
		String translatedText = extractValueFromMtfEntry(mtfEntryText);
		String hash = extractHashFromMtfEntry(mtfEntryText);
		if(hash != null && !hash.equals(getHashOfEnglish(key)))
			translatedText = formatAsUntranslated(translatedText);
		availableTranslations.put(languageCode, translatedText);
	}
	
	private String extractKeyFromMtfEntry(String mtfEntryText)
	{
		int keyStart = HASH_LENGTH + 2;
		if(!mtfEntryText.startsWith("-"))
			keyStart = 0;
		
		int splitAt = mtfEntryText.indexOf('=', keyStart);
		if(splitAt < 0)
			splitAt = 0;
		return mtfEntryText.substring(keyStart, splitAt);
	}
	
	private String extractValueFromMtfEntry(String mtfEntryText)
	{
		int keyEnd = mtfEntryText.indexOf('=');
		if(keyEnd < 0)
			return "";
		
		String value = mtfEntryText.substring(keyEnd+1);
		value = value.replaceAll("\\\\n", "\n");
		return value;
	}

	private String extractHashFromMtfEntry(String mtfEntryText)
	{
		if(!mtfEntryText.startsWith("-"))
			return null;
		
		return mtfEntryText.substring(1, HASH_LENGTH + 1);
	}
	
	public void loadTranslations(String languageCode, InputStream inputStream)
	{
		try
		{
			UnicodeReader reader = new UnicodeReader(inputStream);
			while(true)
			{
				String mtfEntry = reader.readLine();
				if(mtfEntry == null)
					break;
				addTranslation(languageCode, mtfEntry);
			}
			reader.close();
		}
		catch (IOException e)
		{
			System.out.println("BulletinDisplay.loadTranslations " + e);
		}
	}
	
	protected SortedSet getAllKeysSorted()
	{
		Set allKeys = textResources.keySet();
		SortedSet sorted = new TreeSet(allKeys);
		return sorted;
	}

	private Map getAvailableTranslations(String key)
	{
		return (Map)textResources.get(key);
	}

	private String formatAsUntranslated(String value)
	{
		if(value.startsWith("<"))
			return value;
		return "<" + value + ">";
	}

	public String getHashOfEnglish(String key)
	{
		return MartusCrypto.getHexDigest(getLabel(ENGLISH, key)).substring(0,HASH_LENGTH);
	}


	/////////////////////////////////////////////////////////////////
	// File-oriented stuff
	public void loadTranslationFile(String languageCode)
	{
		InputStream transStream = null;
		String fileShortName = getMtfFilename(languageCode);
		File file = new File(directory, fileShortName);
		try
		{
			if(file.exists())
			{
				transStream = new FileInputStream(file);
			}
			else
			{
				transStream = getClass().getResourceAsStream(fileShortName);
			}
			if(transStream == null)
			{
				return;
			}
			loadTranslations(languageCode, transStream);
		}
	
		catch (IOException e)
		{
			System.out.println("BulletinDisplay.loadTranslationFile " + e);
		}
	}

	public static String getLanguageCodeFromFilename(String filename)
	{
		if(!isLanguageFile(filename))
			return "";
	
		int codeStart = filename.indexOf('-') + 1;
		int codeEnd = filename.indexOf('.');
		return filename.substring(codeStart, codeEnd);
	}

	public static boolean isLanguageFile(String filename)
	{
		String filenameLower = filename.toLowerCase();
		String martusLanguageFilePrefixLower = MARTUS_LANGUAGE_FILE_PREFIX.toLowerCase();
		String martusLanguageFileSufixLower = MARTUS_LANGUAGE_FILE_SUFFIX.toLowerCase();
		return (filenameLower.startsWith(martusLanguageFilePrefixLower) && filenameLower.endsWith(martusLanguageFileSufixLower));
	}

	public static String getMtfFilename(String languageCode)
	{
		String filename;
		filename = MARTUS_LANGUAGE_FILE_PREFIX + languageCode + MARTUS_LANGUAGE_FILE_SUFFIX;
		return filename;
	}

	/////////////////////////////////////////////////////////////////
	// Language-oriented stuff

	public static boolean isRecognizedLanguage(String testLanguageCode)
	{
		for(int i = 0 ; i < ALL_LANGUAGE_CODES.length; ++i)
		{
			if(ALL_LANGUAGE_CODES[i].equals(testLanguageCode))
				return true;
		}
		return false;
	}
	
	public boolean isRightToLeftLanguage()
	{
		for(int i = 0 ; i < RIGHT_TO_LEFT_LANGUAGES.length; ++i)
		{
			if(RIGHT_TO_LEFT_LANGUAGES[i].equals(currentLanguageCode))
				return true;
		}
//TODO return false for 2.x release
return languageDirectionRightToLeft;
	}


	/////////////////////////////////////////////////////////////////
	// Date-oriented stuff
	public String getCurrentDateFormatCode()
	{
		return currentDateFormat;
	}

	public void setCurrentDateFormatCode(String code)
	{
		currentDateFormat = code;
	}

	public String convertStoredDateToDisplay(String storedDate)
	{
		DateFormat dfStored = Bulletin.getStoredDateFormat();
		DateFormat dfDisplay = new SimpleDateFormat(getCurrentDateFormatCode());
		String result = "";
		try
		{
			Date d = dfStored.parse(storedDate);
			result = dfDisplay.format(d);
		}
		catch(ParseException e)
		{
			// unparsable dates simply become blank strings,
			// so we don't want to do anything for this exception
			//System.out.println(e);
		}
	
		return result;
	}
	
	public String convertStoredDateTimeToDisplay(String storedDate)
	{		
		DateFormat dfStored = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		DateFormat dfDisplay = new SimpleDateFormat(getCurrentDateFormatCode());
		String result = "";
		try
		{
			Date date = dfStored.parse(storedDate);
			String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);		
			result = dfDisplay.format(date)+" "+time;
		}
		catch(ParseException e)
		{
			// unparsable dates simply become blank strings,
			// so we don't want to do anything for this exception
			//System.out.println(e);
		}
	
		return result;
	}

	private static Map getDefaultDateFormats()
	{
		Map defaultLanguageDateFormat = new HashMap();
		defaultLanguageDateFormat.put(ENGLISH, DateUtilities.getDefaultDateFormatCode());
		defaultLanguageDateFormat.put(SPANISH, DateUtilities.DMY_SLASH.getCode());
		defaultLanguageDateFormat.put(RUSSIAN, DateUtilities.DMY_DOT.getCode());
		defaultLanguageDateFormat.put(THAI, DateUtilities.DMY_SLASH.getCode());
		return defaultLanguageDateFormat;
	}
	
	public static String getDefaultDateFormatForLanguage(String languageCode)
	{
		Map defaultLanguageDateFormat = getDefaultDateFormats();
		if(!defaultLanguageDateFormat.containsKey(languageCode))
			return DateUtilities.getDefaultDateFormatCode();
		return (String)defaultLanguageDateFormat.get(languageCode);
	}
	

	public File directory;
	public Map textResources;
	public String currentLanguageCode;
	public String currentDateFormat;
	public boolean languageDirectionRightToLeft;

	public static final String UNUSED_TAG = "";
	public static final String MARTUS_LANGUAGE_FILE_PREFIX = "Martus-";
	public static final String MARTUS_LANGUAGE_FILE_SUFFIX = ".mtf";
	
	public static final int HASH_LENGTH = 4;
	
	public static final String LANGUAGE_OTHER = "?";
	public static final String ENGLISH = "en";
	public static final String FRENCH = "fr";
	public static final String SPANISH = "es";
	public static final String RUSSIAN = "ru";
	public static final String THAI = "th";
	public static final String ARABIC = "ar";
	public static final String[] ALL_LANGUAGE_CODES = {
				LANGUAGE_OTHER, ENGLISH, ARABIC,
				"az", "bn", "km","my","zh", "nl", "eo", "fa", FRENCH, "de","gu","ha","he","hi","hu",
				"it", "ja","jv","kn","kk","ky","ko","ml","mr","ne","or","pa","ps","pl","pt","ro",RUSSIAN,"sr",
				"sr", "sd","si",SPANISH,"ta","tg","te",THAI,"tr","tk","uk","ur","uz","vi"};
	
	public static final String[] RIGHT_TO_LEFT_LANGUAGES = {
			ARABIC};
}
