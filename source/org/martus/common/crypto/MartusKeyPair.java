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

package org.martus.common.crypto;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.util.Base64;

public class MartusKeyPair
{
	public MartusKeyPair(SecureRandom randomGenerator) throws Exception
	{
		rand = randomGenerator;
		keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM_NAME, "BC");
		rsaCipherEngine = Cipher.getInstance(RSA_ALGORITHM, "BC");
	
	}
	
	public void createRSA(int publicKeyBits, SecureRandom rand)
	{
		keyPairGenerator.initialize(publicKeyBits, rand);
		setJceKeyPair(keyPairGenerator.genKeyPair());
	}
	
	public void setFromData(byte[] data) throws Exception
	{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		KeyPair candidatePair = (KeyPair)objectInputStream.readObject();
		if(!isKeyPairValid(candidatePair))
			throw (new AuthorizationFailedException());
		setJceKeyPair(candidatePair);
	}
	
	public void clear()
	{
		jceKeyPair = null;
	}
	
	public boolean isValid()
	{
		return (jceKeyPair != null);
	}
	
	public void setJceKeyPair(KeyPair jceKeyPair)
	{
		this.jceKeyPair = jceKeyPair;
	}

	public KeyPair getJceKeyPair()
	{
		return jceKeyPair;
	}

	public synchronized boolean isKeyPairValid(KeyPair candidatePair)
	{
		if(candidatePair == null)
			return false;

		try
		{
			rsaCipherEngine.init(Cipher.ENCRYPT_MODE, candidatePair.getPublic(), rand);
			byte[] samplePlainText = {1,2,3,4,127};
			byte[] cipherText = rsaCipherEngine.doFinal(samplePlainText);
			rsaCipherEngine.init(Cipher.DECRYPT_MODE, candidatePair.getPrivate(), rand);
			byte[] result = rsaCipherEngine.doFinal(cipherText);
			if(!Arrays.equals(samplePlainText, result))
				return false;
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}
	
	public byte[] encryptBytes(byte[] bytesToEncrypt, String publicKey) throws Exception
	{
		rsaCipherEngine.init(Cipher.ENCRYPT_MODE, extractPublicKey(publicKey), rand);
		return rsaCipherEngine.doFinal(bytesToEncrypt);
	}
	
	public byte[] decryptBytes(byte[] bytesToDecrypt) throws Exception
	{
		rsaCipherEngine.init(Cipher.DECRYPT_MODE, jceKeyPair.getPrivate(), rand);
		return rsaCipherEngine.doFinal(bytesToDecrypt);
	}

	public static PublicKey extractPublicKey(String base64PublicKey)
	{
		//System.out.println("key=" + base64PublicKey);
		try
		{
			EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(base64PublicKey));
			KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
			PublicKey publicKey = factory.generatePublic(keySpec);
			return publicKey;
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("MartusSecurity.extractPublicKey: " + e);
		}
		catch(InvalidKeySpecException e)
		{
			//System.out.println("MartusSecurity.extractPublicKey: " + e);
		}
		catch(Base64.InvalidBase64Exception e)
		{
			//System.out.println("MartusSecurity.extractPublicKey: " + e);
		}
	
		return null;
	}

	private KeyPair jceKeyPair;

	private SecureRandom rand;
	private KeyPairGenerator keyPairGenerator;
	private Cipher rsaCipherEngine;

	static final String RSA_ALGORITHM_NAME = "RSA";
	private static final String RSA_ALGORITHM = "RSA/NONE/PKCS1Padding";
}
