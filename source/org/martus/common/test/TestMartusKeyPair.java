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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ObjectStreamConstants;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Vector;

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
		rand = new SecureRandom(new byte[] {1,2,3,4,5,6,7,8});

		
		
		objects = new Vector();

		MartusJceKeyPair jceKeyPair = new MartusJceKeyPair(rand);
		objects.add(jceKeyPair);

//		MartusDirectCryptoKeyPair directKeyPair = new MartusDirectCryptoKeyPair(rand);
//		objects.add(directKeyPair);

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
	
	public void testKeyData() throws Exception
	{
		MartusJceKeyPair reader = new MartusJceKeyPair(rand);
		for(int i = 0; i < objects.size(); ++i)
		{
			MartusKeyPair keyOwner = (MartusKeyPair)objects.get(i);
			byte[] data = keyOwner.getKeyPairData();
			
//			ByteArrayInputStream rawIn = new ByteArrayInputStream(data);
//			ObjectInputStream in = new ObjectInputStream(rawIn);
//			KeyPair got  = (KeyPair)in.readObject();
//			PrivateKey gotPrivate = got.getPrivate();
//			PublicKey gotPublic = got.getPublic();
//			System.out.println(gotPrivate.getClass().getName());
//			System.out.println(gotPublic.getClass().getName());

			int nextHandle = 8257536;
			int bigIntHandle = 0;
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int magic = in.readShort();
			assertEquals(ObjectStreamConstants.STREAM_MAGIC, magic);
			int streamVersion = in.readShort();
			assertEquals(ObjectStreamConstants.STREAM_VERSION, streamVersion);
			int objectMarker = in.readByte();
			
			// KeyPair
			{
				assertEquals(ObjectStreamConstants.TC_OBJECT, objectMarker);
				int classDescMarker = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, classDescMarker);
				String className = in.readUTF();
				assertEquals("java.security.KeyPair", className);
				long classSerialUid = in.readLong();
				assertEquals(-7565189502268009837L, classSerialUid);
				// new handle assigned here
				nextHandle++;
				
				int classDescFlags = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, classDescFlags);
				int fieldCount = in.readShort();
				assertEquals(2, fieldCount);
				String[] expectedFields = {"privateKey", "publicKey"};
				String[] expectedClassNames = {"Ljava/security/PrivateKey;", "Ljava/security/PublicKey;"};
				int[] pairHandles = {0,0};
				for(int field=0; field < fieldCount; ++field)
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals(expectedFields[field], fieldName);
					int stringFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_STRING, stringFlag);
					// new handle
					pairHandles[field] = nextHandle++;
					
					String fieldClassName = in.readUTF();
					assertEquals(expectedClassNames[field], fieldClassName);
				}
				int endDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, endDataFlag);
				int noSuperClassFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, noSuperClassFlag);
				// new handle
				nextHandle++;
			}
			
			{
				int objectForPrivate = in.readByte();
				assertEquals(ObjectStreamConstants.TC_OBJECT, objectForPrivate);
				int classFlagForPrivate = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, classFlagForPrivate);
				String classNameForPrivate = in.readUTF();
				assertEquals("org.bouncycastle.jce.provider.JCERSAPrivateCrtKey", classNameForPrivate);
				long uidForPrivateKeyClass = in.readLong();
				assertEquals(7834723820638524718L, uidForPrivateKeyClass);
				// new handle
				nextHandle++;
				
				int classDescFlagsForPrivate = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, classDescFlagsForPrivate);
				int fieldCountForPrivate = in.readShort();
				assertEquals(6, fieldCountForPrivate);
				String[] expectedFieldsForPrivate = {
						"crtCoefficient", "primeExponentP", "primeExponentQ",
						"primeP", "primeQ", "publicExponent"};
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals(expectedFieldsForPrivate[0], fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_STRING, refFlag);
					String fieldClassName = in.readUTF();
					assertEquals("Ljava/math/BigInteger;", fieldClassName);
					// new handle
					bigIntHandle = nextHandle++;
				}
				
				for(int field=1; field < fieldCountForPrivate; ++field)
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals(expectedFieldsForPrivate[field], fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int handle = in.readInt();
					assertEquals(bigIntHandle, handle);
				}
				int endDataFlagForPrivate = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, endDataFlagForPrivate);
				int superClassFlagForPrivate = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, superClassFlagForPrivate);
				String classNameForPrivateSuper = in.readUTF();
				assertEquals("org.bouncycastle.jce.provider.JCERSAPrivateKey", classNameForPrivateSuper);
				long uidForPrivateKeyClassSuper = in.readLong();
				assertEquals(-5605421053708761770L, uidForPrivateKeyClassSuper);
				// new handle
				nextHandle++;
				
				int classDescFlagsForPrivateSuper = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, classDescFlagsForPrivateSuper);
				int fieldCountForPrivateSuper = in.readShort();
				assertEquals(4, fieldCountForPrivateSuper);
				// field 1
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("modulus", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int handle = in.readInt();
					assertEquals(bigIntHandle, handle);
				}
				// field 2
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("pkcs12Attributes", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_STRING, refFlag);
					String fieldClassName = in.readUTF();
					assertEquals("Ljava/util/Hashtable;", fieldClassName);
					// new handle
					bigIntHandle = nextHandle++;
				}
				// field 3
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("pkcs12Ordering", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_STRING, refFlag);
					String fieldClassName = in.readUTF();
					assertEquals("Ljava/util/Vector;", fieldClassName);
					// new handle
					bigIntHandle = nextHandle++;
				}
				// field 4
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("privateExponent", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int handle = in.readInt();
					assertEquals(8257541, handle);
				}
				int endDataFlagForPrivateSuper = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, endDataFlagForPrivateSuper);
				int superClassFlagForPrivateSuper = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, superClassFlagForPrivateSuper);
				// new handle
				nextHandle++;
			}			

			// BigInteger
			{
				int objectForBigInt = in.readByte();
				assertEquals(ObjectStreamConstants.TC_OBJECT, objectForBigInt);
				int classFlagForBigInt = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, classFlagForBigInt);
				String classNameForBigInt = in.readUTF();
				assertEquals("java.math.BigInteger", classNameForBigInt);
				long uidForBigIntClass = in.readLong();
				assertEquals(-8287574255936472291L, uidForBigIntClass);
				// new handle
				nextHandle++;
				
				int classDescFlags = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, classDescFlags);
				int fieldCount = in.readShort();
				assertEquals(6, fieldCount);
				// field 1
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("bitCount", fieldName);
				}
				// field 2
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("bitLength", fieldName);
				}
				// field 3
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("firstNonzeroByteNum", fieldName);
				}
				// field 4
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("lowestSetBit", fieldName);
				}
				// field 5
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("signum", fieldName);
				}
				// field 6
				{
					byte typeCode = in.readByte();
					assertEquals('[', typeCode);
					String fieldName = in.readUTF();
					assertEquals("magnitude", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_STRING, refFlag);
					String fieldClassName = in.readUTF();
					assertEquals("[B", fieldClassName);
					// new handle
					bigIntHandle = nextHandle++;
				}
				int endDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, endDataFlag);
				int superClassFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, superClassFlag);
				String superClassName = in.readUTF();
				assertEquals("java.lang.Number", superClassName);
				long superUid = in.readLong();
				assertEquals(-8742448824652078965L, superUid);
				int superClassDescFlags = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, superClassDescFlags);
				int superFieldCount = in.readShort();
				assertEquals(0, superFieldCount);
				int superEndDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, superEndDataFlag);
				int superNoSuperFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, superNoSuperFlag);
				// new handle
				nextHandle++;
			}
			
			// Why the heck am I hitting a long -1 here???
			
			
			
			
			reader.setFromData(data);
//			byte[] copiedData = reader.getKeyPairData();
//			System.out.println(Base64.encode(data));
//			System.out.println(Base64.encode(copiedData));
//			assertTrue("get data wrong? " + keyOwner.getClass().getName(), Arrays.equals(data, copiedData));
		}
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
	SecureRandom rand;
}
