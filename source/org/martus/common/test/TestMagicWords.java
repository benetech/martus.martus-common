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
package org.martus.common.test;

import java.io.File;
import java.util.Vector;

import org.martus.common.LoggerToConsole;
import org.martus.common.MagicWords;
import org.martus.util.UnicodeWriter;


public class TestMagicWords extends TestCaseEnhanced
{
	public TestMagicWords(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		tempFile = createTempFileFromName("$$$MartusTestFileMagicWords");
		UnicodeWriter writer = new UnicodeWriter(tempFile);
		writer.writeln("Test1");
		writer.writeln("test2");
		writer.writeln("#test3");
		writer.close();
		
		magicWords = new MagicWords( new LoggerToConsole());	
		magicWords.loadMagicWords(tempFile);	
	}

	public void tearDown() throws Exception
	{		
		tempFile.delete();
		super.tearDown();
	}

	public void testMagicWords() throws Exception
	{
		assertTrue("available active magic words?", magicWords.getActiveMagicWords().size()==2);
		assertTrue("available inactive magic words?", magicWords.getInactiveMagicWords().size()==1);
		
		assertFalse("Not a valid magic word?", magicWords.isValidMagicWord("Test4"));
		assertTrue("A valid magic word", magicWords.isValidMagicWord("Test1"));
				
	}
	
	public void testActiveAndInActiveMagicWords() throws Exception
	{				
		Vector activeWord = magicWords.getActiveMagicWords();
		assertTrue("Contain size of active magic words", activeWord.size()==2);
		Vector inActiveWord = magicWords.getInactiveMagicWords();
		assertTrue("COntain size of inactive magic words", inActiveWord.size()==1);
	
	}
	
	public void testRemoveMagicWords() throws Exception
	{				
		String removeString = "Test1";
		
		assertTrue("Contain this matic word", magicWords.isValidMagicWord(removeString));
		assertTrue("Current magic words size", magicWords.size()== 3); 
		magicWords.remove(removeString);
		assertTrue("Current magic words size after remove", magicWords.size()== 2); 			
	
	}
	
	public void testNullMagicWord() throws Exception
	{
		String duplicateString = null;
		assertTrue("Current size?", magicWords.size()==3);
		magicWords.add(duplicateString);
		assertFalse("Same size?", magicWords.size()==3);
	}
	
	public void testDuplicateMagicWords() throws Exception
	{
		String duplicateString = "test2";
		assertTrue("Current size?", magicWords.size()==3);
		magicWords.add(duplicateString);
		assertTrue("Same size?", magicWords.size()==3);
	}
	
	public void testWriteoutMagicWordsToFile() throws Exception
	{			
		Vector words = magicWords.getAllMagicWords();
		magicWords.writeMagicWords(tempFile, words);
		
		magicWords = new MagicWords( new LoggerToConsole());	
		magicWords.loadMagicWords(tempFile);
		
		words = magicWords.getInactiveMagicWords();
		assertEquals("test3", words.get(0));	
	}	
		
	File tempFile;
	MagicWords magicWords;
}
