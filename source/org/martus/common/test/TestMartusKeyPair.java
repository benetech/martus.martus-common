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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Vector;

import org.martus.common.crypto.MartusJceKeyPair;
import org.martus.common.crypto.MartusKeyPair;
import org.martus.util.TestCaseEnhanced;

public class TestMartusKeyPair extends TestCaseEnhanced
{
	public TestMartusKeyPair(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		SecureRandom rand = new SecureRandom();
		objects = new Vector();
		objects.add(new MartusJceKeyPair(rand));
	}

	public void testBasics() throws Exception
	{
		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair p = (MartusKeyPair)objects.get(i);
			assertFalse("has initial key? " + p.getClass().getName(), p.hasKeyPair());
			
			p.createRSA(512);
			assertTrue("has key failed? " + p.getClass().getName(), p.hasKeyPair());
			
			p.clear();
			assertFalse("clear failed? " + p.getClass().getName(), p.hasKeyPair());
		}
	}
	
	public void testEncryption() throws Exception
	{
		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair p = (MartusKeyPair)objects.get(i);
			p.createRSA(512);
			
			byte[] sampleBytes = new byte[] {55, 99, 13, 23, };
			byte[] encrypted = p.encryptBytes(sampleBytes, p.getPublicKeyString());
			byte[] decrypted = p.decryptBytes(encrypted);
			assertTrue("bad decrypt? " + p.getClass().getName(), Arrays.equals(sampleBytes, decrypted));
		}		
	}
	
	Vector objects;
}
