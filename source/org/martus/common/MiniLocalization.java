/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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
package org.martus.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.utilities.DatePreference;
import org.martus.common.utilities.DateUtilities;
import org.martus.common.utilities.MartusFlexidate;
import org.martus.util.MultiCalendar;
import org.martus.util.language.LanguageOptions;


public class MiniLocalization
{
	static public class NoDateSeparatorException extends Exception
	{
	}
	
	public MiniLocalization(String[] englishStrings)
	{
		this();
		addEnglishTranslations(englishStrings);
	}

	public MiniLocalization()
	{
		textResources = new TreeMap();
		rightToLeftLanguages = new Vector();
		currentDateFormat = new DatePreference();
	}
	
	public void addEnglishTranslations(String[] translations)
	{
		for(int i=0; i < translations.length; ++i)
		{
			String mtfEntry = translations[i];
			addEnglishTranslation(mtfEntry);
		}
	}
	
	private void addEnglishTranslation(String entry)
	{
		addTranslation(ENGLISH, entry);
	}

	public void addTranslation(String languageCode, String entryText)
	{
		if(entryText == null)
			return;
		
		if(entryText.indexOf('=') < 0)
			return;
		
		String key = extractKeyFromEntry(entryText);
		Map availableTranslations = getAvailableTranslations(key);
		if(availableTranslations == null)
		{
			if(!languageCode.equals(ENGLISH))
				return;
			availableTranslations = new TreeMap();
			textResources.put(key, availableTranslations);
		}
		
		String translatedText = extractValueFromEntry(entryText);
		availableTranslations.put(languageCode, translatedText);
	}

	public String extractKeyFromEntry(String entryText)
	{
		int splitAt = entryText.indexOf('=', 0);
		if(splitAt < 0)
			splitAt = 0;
		return entryText.substring(0, splitAt);
	}
	
	public String extractValueFromEntry(String entryText)
	{
		int keyEnd = entryText.indexOf('=');
		if(keyEnd < 0)
			return "";
		
		String value = entryText.substring(keyEnd+1);
		value = value.replaceAll("\\\\n", "\n");
		return value;
	}

	protected SortedSet getAllKeysSorted()
	{
		Set allKeys = textResources.keySet();
		SortedSet sorted = new TreeSet(allKeys);
		return sorted;
	}

	protected String formatAsUntranslated(String value)
	{
		if(value.startsWith("<"))
			return value;
		return "<" + value + ">";
	}
	
	protected Map getAvailableTranslations(String key)
	{
		
		return (Map)textResources.get(key);
	}

	public String getCurrentLanguageCode()
	{
		return currentLanguageCode;
	}

	public void setCurrentLanguageCode(String newLanguageCode)
	{
		currentLanguageCode = newLanguageCode;
		if(isRightToLeftLanguage())
			LanguageOptions.setDirectionRightToLeft();
		else
			LanguageOptions.setDirectionLeftToRight();
		if(doesLanguageRequirePadding(currentLanguageCode))
			LanguageOptions.setLanguagePaddingRequired();
		else
			LanguageOptions.setLanguagePaddingNotRequired();
	}
	
	public boolean doesLanguageRequirePadding(String languageCode)
	{	
		boolean paddingRequired = languageCode.equals(ARABIC) || languageCode.equals(FARSI);
		return paddingRequired;
	}
	
	public String getCurrentDateFormatCode()
	{
		return currentDateFormat.getDateTemplate();
	}

	public void setCurrentDateFormatCode(String code)
	{
		currentDateFormat.setDateTemplate(code);
	}
	
	public String getMdyOrder()
	{
		return currentDateFormat.getMdyOrder();
	}

	public String getLabel(String languageCode, String key)
	{
		Map availableTranslations = getAvailableTranslations(key);
		if(availableTranslations == null)
			return formatAsUntranslated(key);
	
		String translatedText = (String)availableTranslations.get(languageCode);
		if(translatedText != null)
			return translatedText;
	
		String englishText = (String)availableTranslations.get(ENGLISH);
		if(englishText == null)
		{
			System.out.println("Error, probably an invalid Martus-en.mtf file in C:\\Martus, try removing this file.");
			System.out.println("Possibly obsolete key: " + key);
			englishText = key;
		}
		return formatAsUntranslated(englishText);
	}

	public String getLabel(String languageCode, String category, String tag)
	{
		return getLabel(languageCode, category + ":" + tag);
	}

	public String getFieldLabel(String fieldName)
	{
		return getFieldLabel(getCurrentLanguageCode(), fieldName);
	}

	public String getFieldLabel(String languageCode, String fieldName) 
	{
		return getLabel(languageCode, "field", fieldName);
	}


	public String getLanguageName(String code)
	{
		return getLabel(getCurrentLanguageCode(), "language", code);
	}

	public String getButtonLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "button", code);
	}

	public String getStatusLabel(String code)
	{
		return getLabel(getCurrentLanguageCode(), "status", code);
	}

	private static Map getDefaultDateFormats()
	{
		Map defaultLanguageDateFormat = new HashMap();
		defaultLanguageDateFormat.put(ENGLISH, DateUtilities.getDefaultDateFormatCode());
		defaultLanguageDateFormat.put(SPANISH, DateUtilities.DMY_SLASH.getCode());
		defaultLanguageDateFormat.put(RUSSIAN, DateUtilities.DMY_DOT.getCode());
		defaultLanguageDateFormat.put(THAI, DateUtilities.DMY_SLASH.getCode());
		defaultLanguageDateFormat.put(ARABIC, DateUtilities.DMY_SLASH.getCode());
		defaultLanguageDateFormat.put(FARSI, DateUtilities.DMY_SLASH.getCode());
		return defaultLanguageDateFormat;
	}
	
	public static String getDefaultDateFormatForLanguage(String languageCode)
	{
		Map defaultLanguageDateFormat = getDefaultDateFormats();
		if(!defaultLanguageDateFormat.containsKey(languageCode))
			return DateUtilities.getDefaultDateFormatCode();
		return (String)defaultLanguageDateFormat.get(languageCode);
	}

	public static char getDateSeparator(String date) throws NoDateSeparatorException
	{
		for(int i = 0; i < date.length(); ++i)
		{
			if(!Character.isDigit(date.charAt(i)))
				return date.charAt(i);
		}
		throw new NoDateSeparatorException();
	}

	private String reverseDate(String dateToReverse)
	{
		StringBuffer reversedDate= new StringBuffer();
		try
		{
			char dateSeparator = getDateSeparator(dateToReverse);
			int beginningIndex = dateToReverse.indexOf(dateSeparator);
			int endingIndex = dateToReverse.lastIndexOf(dateSeparator);
			String dateField1 = dateToReverse.substring(0, beginningIndex);
			String dateField2 = dateToReverse.substring(beginningIndex+1, endingIndex);
			String dateField3 = dateToReverse.substring(endingIndex+1);
			reversedDate.append(dateField3);
			reversedDate.append(dateSeparator);
			reversedDate.append(dateField2);
			reversedDate.append(dateSeparator);
			reversedDate.append(dateField1);
			return reversedDate.toString();
		}
		catch(NoDateSeparatorException e)
		{
			return dateToReverse;
		}
	}

	/////////////////////////////////////////////////////////////////
	// Date-oriented stuff

	public String convertStoredDateToDisplay(String storedDate)
	{
		DateFormat dfDisplay = new SimpleDateFormat(getCurrentDateFormatCode());
		String result = "";
		try
		{
			MultiCalendar cal = MultiCalendar.createFromIsoDateString(storedDate);
			result = dfDisplay.format(cal.getTime());
		}
		catch(Exception e)
		{
			// unparsable dates simply become blank strings,
			// so we don't want to do anything for this exception
			//System.out.println(e);
		}
		return result;
	}
	
	public String convertStoredDateToDisplayReverseIfNecessary(String date)
	{
		String displayDate = convertStoredDateToDisplay(date);
		return reverseDisplayDateIfRequired(displayDate);
	}

	private String reverseDisplayDateIfRequired(String displayDate)
	{
		if(LanguageOptions.isRightToLeftLanguage())
			return reverseDate(displayDate);
		return displayDate;
	}

	public String getViewableDateRange(String newText)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromBulletinFlexidateFormat(newText);
		String rawBeginDate = MartusFlexidate.toStoredDateFormat(mfd.getBeginDate());
	
		if (!mfd.hasDateRange())
			return convertStoredDateToDisplayReverseIfNecessary(rawBeginDate);
	
		String rawEndDate = MartusFlexidate.toStoredDateFormat(mfd.getEndDate());
	
		//Strange quirk with Java and displaying RToL languages with dates.
		//When there is a string with mixed RtoL and LtoR characters 
		//if there is .'s separating numbers then the date is not reversed,
		//but if the date is separated by /'s, then the date is reversed.
		String beginDate = convertStoredDateToDisplayReverseIfNecessary(rawBeginDate);
		String endDate = convertStoredDateToDisplayReverseIfNecessary(rawEndDate);
			
		String display = beginDate + " - " + endDate;
		if(LanguageOptions.isRightToLeftLanguage())
			display = endDate + " - " + beginDate;
		return display;
	}

	private boolean isRightToLeftLanguage()
	{
		return rightToLeftLanguages.contains(getCurrentLanguageCode());
	}

	public void addRightToLeftLanguage(String languageCode)
	{
		if(rightToLeftLanguages.contains(languageCode))
			return;
		rightToLeftLanguages.add(languageCode);
	}

	public String formatDateTime(long dateTime)
	{
		if(dateTime == DATE_UNKNOWN)
			return "";
		DateFormat dateShort = new SimpleDateFormat(getCurrentDateFormatCode());
		DateFormat time24hour = new SimpleDateFormat("HH:mm");
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(dateTime);
		
		String date = reverseDisplayDateIfRequired(dateShort.format(cal.getTime()));
		String time = time24hour.format(cal.getTime());
		if(isRightToLeftLanguage())
			return time + SPACE + date;
		return date + SPACE + time;
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

	public static final String ENGLISH = "en";
	public static final String LANGUAGE_OTHER = "?";
	public static final String FRENCH = "fr";
	public static final String SPANISH = "es";
	public static final String RUSSIAN = "ru";
	public static final String THAI = "th";
	public static final String ARABIC = "ar";
	public static final String FARSI = "fa";
	public static final String[] ALL_LANGUAGE_CODES = {
				LANGUAGE_OTHER, ENGLISH, ARABIC,
				"az", "bg", "bn", "km","my","zh", "nl", "eo", FARSI, FRENCH, "de","gu","ha","he","hi","hu",
				"it", "ja","jv","kn","kk","ky","ko","ku","ml","mr","ne","or","pa","ps","pl","pt","ro",RUSSIAN,
				"sr", "sd","si",SPANISH,"ta","tg","te",THAI,"tr","tk","uk","ur","uz","vi"};

	static public final String SPACE = " ";
	static public final long DATE_UNKNOWN = -1;

	protected Map textResources;
	protected Vector rightToLeftLanguages;
	private String currentLanguageCode;
	private DatePreference currentDateFormat;
	
}
