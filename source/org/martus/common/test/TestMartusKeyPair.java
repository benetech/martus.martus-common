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

import org.martus.common.crypto.MartusDirectCryptoKeyPair;
import org.martus.common.crypto.MartusJceKeyPair;
import org.martus.common.crypto.MartusKeyPair;
import org.martus.util.Base64;
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

		MartusJceKeyPair jceKeyPair = new MartusJceKeyPair(rand);
		
		MartusDirectCryptoKeyPair directKeyPair = new MartusDirectCryptoKeyPair(rand);
		
		objects = new Vector();
		objects.add(jceKeyPair);
		objects.add(directKeyPair);

		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair p = (MartusKeyPair)objects.get(i);
			assertFalse("has initial key? " + p.getClass().getName(), p.hasKeyPair());
			
			p.createRSA(512);
			assertTrue("has key failed? " + p.getClass().getName(), p.hasKeyPair());
			String publicKeyString = p.getPublicKeyString();
			Base64.decode(publicKeyString);
		}
//		System.out.println("JCE:");
//		System.out.println(((RSAPublicKey)jceKeyPair.getPublicKey()).getModulus());
//		System.out.println(((RSAPublicKey)jceKeyPair.getPublicKey()).getPublicExponent());
	}
	
	public void tearDown() throws Exception
	{
		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair p = (MartusKeyPair)objects.get(i);
			assertTrue("lost key? " + p.getClass().getName(), p.hasKeyPair());
			p.clear();
			assertFalse("clear failed? " + p.getClass().getName(), p.hasKeyPair());
		}
	}

	public void testBasics() throws Exception
	{
	}
	
	public void testEncryption() throws Exception
	{
		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair encryptor = (MartusKeyPair)objects.get(i);
			MartusKeyPair self = encryptor;
			verifyEncryptDecrypt(encryptor, self);
			
// The following test should be valid, but doesn't work yet
//			int next = (i+1)%objects.size();
//			MartusKeyPair decryptor = (MartusKeyPair)objects.get(next);
//			verifyEncryptDecrypt(encryptor, decryptor);
		}		
	}
	
	private void verifyEncryptDecrypt(MartusKeyPair encryptor, MartusKeyPair decryptor) throws Exception
	{
		byte[] sampleBytes = new byte[] {55, 99, 13, 23, };
		byte[] encrypted = encryptor.encryptBytes(sampleBytes, decryptor.getPublicKeyString());
		byte[] decrypted = decryptor.decryptBytes(encrypted);
		String label = " encryptor: " + encryptor.getClass().getName() +
						" decryptor: " + decryptor.getClass().getName();
		assertTrue("bad decrypt? " + label, Arrays.equals(sampleBytes, decrypted));
		
	}
	
	Vector objects;
}
