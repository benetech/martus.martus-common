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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.util.Base64;

public class MartusKeyPair
{
	public MartusKeyPair(SecureRandom randomGenerator) throws Exception
	{
		rand = randomGenerator;
	}
	
	public KeyPair getJceKeyPair()
	{
		return jceKeyPair;
	}

	public void clear()
	{
		jceKeyPair = null;
	}
	
	public boolean isValid()
	{
		return (jceKeyPair != null);
	}
	
	public void createRSA(int publicKeyBits, SecureRandom rand) throws Exception
	{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM_NAME, "BC");
		keyPairGenerator.initialize(publicKeyBits, rand);
		setJceKeyPair(keyPairGenerator.genKeyPair());
	}
	
	public byte[] getKeyPairData() throws IOException
	{
		KeyPair jceKeyPairToWrite = getJceKeyPair();
		return getKeyPairData(jceKeyPairToWrite);
	}

	public static byte[] getKeyPairData(KeyPair jceKeyPairToWrite) throws IOException
	{
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(data);
		objectOutputStream.writeObject(jceKeyPairToWrite);
		return data.toByteArray();
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
	
	public synchronized boolean isKeyPairValid(KeyPair candidatePair)
	{
		if(candidatePair == null)
			return false;

		try
		{
			byte[] samplePlainText = {1,2,3,4,127};

			PublicKey encryptWithKey = candidatePair.getPublic();
			byte[] cipherText = encryptBytes(samplePlainText, encryptWithKey);
			
			PrivateKey decryptWithKey = candidatePair.getPrivate();
			byte[] result = decryptBytes(cipherText, decryptWithKey);
			
			if(!Arrays.equals(samplePlainText, result))
				return false;
		}
		catch(Exception e)
		{
			return false;
		}

		return true;
	}
	
	public byte[] encryptBytes(byte[] bytesToEncrypt, String recipientPublicKeyX509) throws Exception
	{
		PublicKey publicKey = extractPublicKey(recipientPublicKeyX509);
		return encryptBytes(bytesToEncrypt, publicKey);
	}
	
	public byte[] decryptBytes(byte[] bytesToDecrypt) throws Exception
	{
		PrivateKey privateKey = jceKeyPair.getPrivate();
		return decryptBytes(bytesToDecrypt, privateKey);
	}

	public static PublicKey extractPublicKey(String publicKeyX509)
	{
		//System.out.println("key=" + base64PublicKey);
		try
		{
			EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKeyX509));
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

	private void setJceKeyPair(KeyPair jceKeyPair)
	{
		this.jceKeyPair = jceKeyPair;
	}

	private Cipher createRSAEncryptor(PublicKey key) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException
	{
		return createRSAEngine(key, Cipher.ENCRYPT_MODE);
	}

	private Cipher createRSADecryptor(PrivateKey key) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException
	{
		return createRSAEngine(key, Cipher.DECRYPT_MODE);
	}

	private Cipher createRSAEngine(Key key, int mode) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException
	{
		Cipher rsaCipherEngine = Cipher.getInstance(RSA_ALGORITHM, "BC");
		rsaCipherEngine.init(mode, key, rand);
		return rsaCipherEngine;
	}

	private byte[] encryptBytes(byte[] bytesToEncrypt, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher rsaCipherEngine = createRSAEncryptor(publicKey);
		return rsaCipherEngine.doFinal(bytesToEncrypt);
	}

	private byte[] decryptBytes(byte[] bytesToDecrypt, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher rsaCipherEngine = createRSADecryptor(privateKey);
		return rsaCipherEngine.doFinal(bytesToDecrypt);
	}

	private SecureRandom rand;
	private KeyPair jceKeyPair;


	static final String RSA_ALGORITHM_NAME = "RSA";
	private static final String RSA_ALGORITHM = "RSA/NONE/PKCS1Padding";
}
