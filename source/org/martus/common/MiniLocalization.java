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
package org.martus.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.utilities.DateUtilities;
import org.martus.common.utilities.MartusFlexidate;
import org.martus.swing.UiLanguageDirection;


public class MiniLocalization
{
	static public class NoDateSeparatorException extends Exception{}
	public MiniLocalization(String[] englishStrings)
	{
		this();
		addEnglishTranslations(englishStrings);
	}

	public MiniLocalization()
	{
		textResources = new TreeMap();
		rightToLeftLanguages = new Vector();
		setCurrentLanguageCode(ENGLISH);
		setCurrentDateFormatCode(DateUtilities.MDY_SLASH.getCode());
	}
	
	protected void addEnglishTranslations(String[] translations)
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
			UiLanguageDirection.setDirection(UiLanguageDirection.RIGHT_TO_LEFT);
		else
			UiLanguageDirection.setDirection(UiLanguageDirection.LEFT_TO_RIGHT);
	}
	
	public String getCurrentDateFormatCode()
	{
		return currentDateFormat;
	}

	public void setCurrentDateFormatCode(String code)
	{
		currentDateFormat = code;
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
			System.out.println("Error, probably an invalid Martus-en.mtf file in C:\\Martus, try removing this file.");
		return formatAsUntranslated(englishText);
	}

	public String getLabel(String languageCode, String category, String tag)
	{
		return getLabel(languageCode, category + ":" + tag);
	}

	public String getFieldLabel(String fieldName)
	{
		return getLabel(getCurrentLanguageCode(), "field", fieldName);
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

	public static String getDefaultDateFormatForLanguage(String languageCode)
	{
		return DateUtilities.getDefaultDateFormatCode();
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

	public String convertStoredDateToDisplayReverseIfNecessary(String date)
	{
		String displayDate = convertStoredDateToDisplay(date);
		if(UiLanguageDirection.isRightToLeftLanguage())
			return reverseDate(displayDate);
		return displayDate;
	}

	public String getViewableDateRange(String newText)
	{
		MartusFlexidate mfd = MartusFlexidate.createFromMartusDateString(newText);
		String rawBeginDate = MartusFlexidate.toStoredDateFormat(mfd.getBeginDate());
	
		if (!mfd.hasDateRange())
			return convertStoredDateToDisplayReverseIfNecessary(rawBeginDate);
	
		String rawEndDate = MartusFlexidate.toStoredDateFormat(mfd.getEndDate());
	
		String beginDate = convertStoredDateToDisplay(rawBeginDate);
		String endDate = convertStoredDateToDisplay(rawEndDate);
		try
		{
			//Strange quirk with Java and displaying RToL languages with dates.
			//When there is a string with mixed RtoL and LtoR characters 
			//if there is .'s separating numbers then the date is not reversed,
			//but if the date is separated by /'s, then the date is reversed.
			if(getDateSeparator(beginDate) == '.')
			{
				beginDate = convertStoredDateToDisplayReverseIfNecessary(rawBeginDate);
				endDate = convertStoredDateToDisplayReverseIfNecessary(rawEndDate);
			}
		}
		catch(NoDateSeparatorException e)
		{
			e.printStackTrace();
			return "";
		}
			
		String display = getFieldLabel("DateRangeFrom")+ SPACE + 
			beginDate + SPACE + getFieldLabel("DateRangeTo")+
			SPACE + endDate;
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

	public static final String ENGLISH = "en";
	public static final String LANGUAGE_OTHER = "?";
	public static final String FRENCH = "fr";
	public static final String SPANISH = "es";
	public static final String RUSSIAN = "ru";
	public static final String THAI = "th";
	public static final String ARABIC = "ar";
	public static final String[] ALL_LANGUAGE_CODES = {
				LANGUAGE_OTHER, ENGLISH, ARABIC,
				"az", "bg", "bn", "km","my","zh", "nl", "eo", "fa", FRENCH, "de","gu","ha","he","hi","hu",
				"it", "ja","jv","kn","kk","ky","ko","ml","mr","ne","or","pa","ps","pl","pt","ro",RUSSIAN,"sr",
				"sr", "sd","si",SPANISH,"ta","tg","te",THAI,"tr","tk","uk","ur","uz","vi"};

	static public final String SPACE = " ";

	protected Map textResources;
	protected Vector rightToLeftLanguages;
	private String currentLanguageCode;
	private String currentDateFormat;
	
}
