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
					add(MagicWords.normalizeMagicWord(line));
			}
			reader.close();
		}
		catch(FileNotFoundException nothingToWorryAbout)
		{
		}
	}
	
	public void remove(String magicWordEntry)
	{		
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);
			if(entry.getMagicWord().equals(normalizeMagicWord(magicWordEntry)))
				magicWordEntries.remove(entry);
		}
	}
	
	public static String filterActiveSign(String magicWord)
	{		
		if (magicWord.startsWith("#"))
			magicWord = magicWord.substring(1);
			
		return magicWord;
	}
	
	public void add(String magicWordFileEntry)
	{						
		add(new MagicWordEntry(magicWordFileEntry));				
	}
	
	public void add(MagicWordEntry wordEntry)
	{
		if(!contains(getMagicWordFromFileEntry(wordEntry.getMagicWord())))			
			magicWordEntries.add(wordEntry);
	}
	
	public boolean isValidMagicWord(String tryMagicWord)
	{
		return (contains(normalizeMagicWord(tryMagicWord)));
	}

	private boolean contains(String magicWordToFind)
	{
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);
			if(entry.getMagicWord().equals(magicWordToFind) && entry.isActive())
				return true;
		}
		return false;
	}
	
	public Vector getAllMagicWords()
	{
		Vector magicWords = new Vector();		
		for(int i = 0; i<magicWordEntries.size(); ++i)
		{
			MagicWordEntry entry = (MagicWordEntry)magicWordEntries.get(i);	
			magicWords.add(entry.getMagicWordWithActiveSign());
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
				magicWords.add(entry.getMagicWord());
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
				magicWords.add(entry.getMagicWord());
		}
		return magicWords;
	}
	
	public void writeMagicWords(File magicWordsFile, Vector newMagicWords) throws IOException
	{		
		try
		{
			UnicodeWriter writer = new UnicodeWriter(magicWordsFile);
			for (int i=0;i<newMagicWords.size();++i)
			{
				writer.writeln((String) newMagicWords.get(i));
			}								
			writer.close();			
		}
		catch (FileNotFoundException nothingToWorryAbout)
		{			
		}				
	}
	
	public int size()
	{
		return magicWordEntries.size();
	}
	
	static String normalizeMagicWord(String original)
	{
		return original.toLowerCase().trim().replaceAll("\\s", "");
	}

	public String getMagicWordFromFileEntry(String magicWordEntry)
	{
		return magicWordEntry;
	}	
		
	Vector magicWordEntries; 
	LoggerInterface logger;
}