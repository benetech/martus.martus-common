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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.martus.util.UnicodeReader;
import org.martus.util.UnicodeWriter;

public class MagicWords
{
	public MagicWords(LoggerInterface currentLogger)
	{
		logger = currentLogger;
		magicWordEntries = new Vector();		
	}
	
	public void loadMagicWords(File magicWordsFile) throws IOException
	{
		magicWordEntries.removeAllElements();
		try
		{
			UnicodeReader reader = new UnicodeReader(magicWordsFile);
			String line = null;
			while( (line = reader.readLine()) != null)
			{
				if(line.trim().length() == 0)
					logger.log("Warning: Found blank line in " + magicWordsFile.getPath());
				else
					add(line);
			}
			reader.close();
		}
		catch(FileNotFoundException nothingToWorryAbout)
		{
		}
	}

	public void writeMagicWords(File magicWordsFile, Vector newMagicWordsLineEntries) throws IOException
	{		
		UnicodeWriter writer = new UnicodeWriter(magicWordsFile);
		for (int i=0;i<newMagicWordsLineEntries.size();++i)
		{
			writer.writeln((String)newMagicWordsLineEntries.get(i));
		}								
		writer.close();			
	}
	
	public void add(String fileLineEntry)
	{
		String magicWord = getMagicWordWithActiveSignFromLineEntry(fileLineEntry);
		String group = getGroupNameFromLineEntry(fileLineEntry);
		add(magicWord, group);
	}
	
	public void add(String magicWordEntry, String group)
	{						
		add(new MagicWordEntry(magicWordEntry, group));				
	}
	
	public void add(MagicWordEntry wordEntry)
	{
		if(!contains(wordEntry.getMagicWord()))			
			magicWordEntries.add(wordEntry);
	}
	
	public void remove(String magicWord)
	{	
		magicWordEntries.remove(getMagicWordEntry(magicWord));
	}
	
	public boolean isValidMagicWord(String magicWordToFind)
	{
		MagicWordEntry entry = getMagicWordEntry(magicWordToFind);
		if(entry != null)
			return entry.isActive();
		return false;
	}
	
	public Vector getAllMagicWords()
	{
		Vector magicWords = new Vector();		
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			magicWords.add(getLineEntryFromMagicWordEntry((MagicWordEntry)magicWordEntries.get(i)));
		}
		return magicWords;		
	}
	
	public Vector getActiveMagicWords()
	{
		Vector magicWords = new Vector();		
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);
			if(entry.isActive())
				magicWords.add(getLineEntryFromMagicWordEntry(entry));
		}
		return magicWords;
	}
	
	public Vector getInactiveMagicWords()
	{
		Vector magicWords = new Vector();		
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);			
			if(!entry.isActive())
				magicWords.add(getLineEntryFromMagicWordEntry(entry));
		}
		return magicWords;
	}
	
	public int size()
	{
		return magicWordEntries.size();
	}
	
	private boolean contains(String magicWordToFind)
	{
		return (getMagicWordEntry(magicWordToFind) != null);
	}

	private MagicWordEntry getMagicWordEntry(String magicWordToFind)
	{
		String normalizedMagicWordToFind = normalizeMagicWord(magicWordToFind);
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);
			if(normalizeMagicWord(entry.getMagicWord()).equals(normalizedMagicWordToFind))
				return entry;
		}
		return null;
	}
	
	public static String normalizeMagicWord(String original)
	{
		return original.toLowerCase().trim().replaceAll("\\s", "");
	}

	public static String getMagicWordWithActiveSignFromLineEntry(String lineEntry)
	{
		if(lineEntry == null)
			return "";
		int index = lineEntry.indexOf(FIELD_DELIMITER);
		if(index == -1)
			return lineEntry;
		return lineEntry.substring(0,index);
	}

	public static String getGroupNameFromLineEntry(String lineEntry)
	{
		if(lineEntry == null)
			return "";
		int index = lineEntry.indexOf(FIELD_DELIMITER);
		if(index == -1)
			return filterActiveSign(lineEntry);
		return lineEntry.substring(index+1);
	}
	
	public static String getLineEntryFromMagicWordEntry(MagicWordEntry entry)
	{
		return entry.getMagicWordWithActiveSign() + FIELD_DELIMITER + entry.getGroupName();
	}
	
	public static String filterActiveSign(String magicWord)
	{		
		if (magicWord.startsWith("#"))
			magicWord = magicWord.substring(1);
		
		return magicWord;
	}
	
	public static final char FIELD_DELIMITER = '\t';
	
	Vector magicWordEntries; 
	LoggerInterface logger;
}