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
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.BufferedAsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.martus.util.Base64;
import org.martus.util.Base64.InvalidBase64Exception;

/*
 * NOTE!!!!!!!
 * 
 * This class is basically a non-functional stub! Do not use it!
 * 
 */
public class MartusDirectCryptoKeyPair extends MartusKeyPair
{
	public MartusDirectCryptoKeyPair(SecureRandom rand)
	{
		this.rand = rand;
	}

	public PrivateKey getPrivateKey()
	{
		// TODO Auto-generated method stub
		//RSAprivKey = (RSAPrivateCrtKeyParameters) keyPair.getPrivate();
		return null;
	}

	public PublicKey getPublicKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getPublicKeyString()
	{
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DEROutputStream dOut = new DEROutputStream(bOut);
		RSAKeyParameters publicKeyParameters = (RSAKeyParameters)bcKeyPair.getPublic();
		BigInteger modulus = publicKeyParameters.getModulus();
		BigInteger exponent = publicKeyParameters.getExponent();
		RSAPublicKeyStructure rsaKeyStruct = new RSAPublicKeyStructure(modulus, exponent); 
		DERObject derPublicKey = rsaKeyStruct.getDERObject();
		AlgorithmIdentifier algorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull());
		SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(algorithm, derPublicKey);
		
		try
		{
			dOut.writeObject(info);
			dOut.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error encoding RSA public key");
		}
		
		byte[] bytes = bOut.toByteArray();
		return Base64.encode(bytes);
	}

	public void clear()
	{
		bcKeyPair = null;
	}

	public boolean hasKeyPair()
	{
		return (bcKeyPair != null);
	}

	public boolean isKeyPairValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void createRSA(int publicKeyBits) throws Exception
	{
		bcKeyPair = null;
		BigInteger pubExp = new BigInteger("10001", 16);
		RSAKeyGenerationParameters RSAKeyGenPara =
			new RSAKeyGenerationParameters(pubExp, rand, publicKeyBits, 80);
		RSAKeyPairGenerator RSAKeyPairGen = new RSAKeyPairGenerator();
		RSAKeyPairGen.init(RSAKeyGenPara);
		bcKeyPair = RSAKeyPairGen.generateKeyPair();
		
//		System.out.println("Direct Crypto:");
//		System.out.println(((RSAKeyParameters)bcKeyPair.getPublic()).getModulus());
//		System.out.println(((RSAKeyParameters)bcKeyPair.getPublic()).getExponent());
	}	
	
	public byte[] getKeyPairData() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setFromData(byte[] data) throws Exception
	{
		// TODO Auto-generated method stub

	}

	public byte[] encryptBytes(byte[] bytesToEncrypt,
			String recipientPublicKeyX509) throws Exception
	{
		CipherParameters parameters = extractPublicKey(recipientPublicKeyX509);

		BufferedAsymmetricBlockCipher engine = createEncryptor(parameters);
		return encryptOrDecryptBytes(engine, bytesToEncrypt);
	}

	public byte[] decryptBytes(byte[] bytesToDecrypt) throws Exception
	{
		RSAKeyParameters parameters = (RSAKeyParameters)bcKeyPair.getPrivate();
		
		BufferedAsymmetricBlockCipher engine = createDecryptor(parameters);
		return encryptOrDecryptBytes(engine, bytesToDecrypt);
	}

	public byte[] getDigestOfPartOfPrivateKey() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private CipherParameters extractPublicKey(String recipientPublicKeyX509) throws InvalidBase64Exception, IOException
	{
		byte[] publicKeyBytes = Base64.decode(recipientPublicKeyX509);
		ByteArrayInputStream rawIn = new ByteArrayInputStream(publicKeyBytes);
		ASN1InputStream in = new ASN1InputStream(rawIn);
		SubjectPublicKeyInfo info = new SubjectPublicKeyInfo((ASN1Sequence)(in.readObject()));
		RSAPublicKeyStructure pubKey = new RSAPublicKeyStructure((ASN1Sequence)info.getPublicKey());
		
		BigInteger modulus = pubKey.getModulus();
		BigInteger exponent = pubKey.getPublicExponent();
//System.out.println("MartusDirectCryptoKeyPair.extractPublicKey");
//System.out.println(modulus);
//System.out.println(exponent);
		boolean isPrivate = false;
		CipherParameters parameters = new RSAKeyParameters(isPrivate, modulus, exponent);
		return parameters;
	}

	private byte[] encryptOrDecryptBytes(BufferedAsymmetricBlockCipher engine, byte[] bytes) throws InvalidCipherTextException
	{
		engine.processBytes(bytes, 0, bytes.length);
		return engine.doFinal();
	}

	private BufferedAsymmetricBlockCipher createEncryptor(CipherParameters parameters)
	{
		boolean isEncrypting = true;
		BufferedAsymmetricBlockCipher engine = new BufferedAsymmetricBlockCipher(new RSAEngine());
		engine.init(isEncrypting, parameters);
		return engine;
	}

	private BufferedAsymmetricBlockCipher createDecryptor(RSAKeyParameters parameters)
	{
		boolean isEncrypting = false;
		BufferedAsymmetricBlockCipher engine = new BufferedAsymmetricBlockCipher(new RSAEngine());
		engine.init(isEncrypting, parameters);
		return engine;
	}

	
	

	SecureRandom rand;
	AsymmetricCipherKeyPair bcKeyPair;
}
