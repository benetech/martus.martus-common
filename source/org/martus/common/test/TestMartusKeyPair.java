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
		// This test is under development, comments and code will
		// be cleaned up later
		
		//MartusJceKeyPair reader = new MartusJceKeyPair(rand);
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
			int bigIntStringHandle = 0;
			int bigIntClassHandle = 0;
			int byteArrayClassHandle = 0;
			int modulusObjectHandle = 0;
			int publicExponentObjectHandle = 0;
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int magic = in.readShort();
			assertEquals(ObjectStreamConstants.STREAM_MAGIC, magic);
			int streamVersion = in.readShort();
			assertEquals(ObjectStreamConstants.STREAM_VERSION, streamVersion);
						
			// KeyPair
			{
				int objectMarker = in.readByte();
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
					bigIntStringHandle = nextHandle++;
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
					assertEquals(bigIntStringHandle, handle);
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
				// Private Key field 1
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("modulus", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int handle = in.readInt();
					assertEquals(bigIntStringHandle, handle);
				}
				// Private Key field 2
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
					nextHandle++;
				}
				// Private Key field 3
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
					nextHandle++;
				}
				// Private Key field 4
				{
					byte typeCode = in.readByte();
					assertEquals('L', typeCode);
					String fieldName = in.readUTF();
					assertEquals("privateExponent", fieldName);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int handle = in.readInt();
					assertEquals(bigIntStringHandle, handle);
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
				bigIntClassHandle = nextHandle++;
				
				int classDescFlags = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, classDescFlags);
				int fieldCount = in.readShort();
				assertEquals(6, fieldCount);
				// Big Integer field 1
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("bitCount", fieldName);
				}
				// Big Integer field 2
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("bitLength", fieldName);
				}
				// Big Integer field 3
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("firstNonzeroByteNum", fieldName);
				}
				// Big Integer field 4
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("lowestSetBit", fieldName);
				}
				// Big Integer field 5
				{
					byte typeCode = in.readByte();
					assertEquals('I', typeCode);
					String fieldName = in.readUTF();
					assertEquals("signum", fieldName);
				}
				// Big Integer field 6
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
					nextHandle++;
				}
				
				int endDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, endDataFlag);
				int superClassFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_CLASSDESC, superClassFlag);
				String superClassName = in.readUTF();
				assertEquals("java.lang.Number", superClassName);
				long superUid = in.readLong();
				assertEquals(-8742448824652078965L, superUid);
				// new handle
				nextHandle++;
				
				int superClassDescFlags = in.readByte();
				assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, superClassDescFlags);
				int superFieldCount = in.readShort();
				assertEquals(0, superFieldCount);
				int superEndDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, superEndDataFlag);
				int superNoSuperFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, superNoSuperFlag);
				// new handle				
				modulusObjectHandle = nextHandle++;
				
				//BigInt Data
				{			
					int field1 = in.readInt();
					assertEquals(-1, field1);
					int field2 = in.readInt();
					assertEquals(-1, field2);
					int field3 = in.readInt();
					assertEquals(-2, field3);
					int field4 = in.readInt();
					assertEquals(-2, field4);
					int field5 = in.readInt();
					assertEquals(1, field5);
									
					byte typeCode = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
					int magnitudeClassDescFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_CLASSDESC, magnitudeClassDescFlag);
					String magnitudeClassName = in.readUTF();
					assertEquals("[B", magnitudeClassName);
					long magnitudeUid = in.readLong();
					assertEquals(-5984413125824719648L, magnitudeUid);
					byteArrayClassHandle = nextHandle++;
					
					int magnitudeClassDescFlags = in.readByte();
					assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, magnitudeClassDescFlags);
					//int superClassDescFlags = in.readByte();
					int magnitudeFieldCount = in.readShort();
					assertEquals(0, magnitudeFieldCount);
					int magnitudeEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, magnitudeEndDataFlag);
					int magnitudeNullFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_NULL, magnitudeNullFlag);
					// new handle
					nextHandle++;
					int arrayLength = in.readInt();
					for(int b = 0; b < arrayLength; ++b)
					{
						in.readByte();
					}
					int arrayEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
				}
				
//				Hash Map
				{
					int objectForHashMap = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, objectForHashMap);
					int hashMapClassDescFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_CLASSDESC, hashMapClassDescFlag);
					String className = in.readUTF();
					assertEquals("java.util.Hashtable", className);
					long classSerialUid = in.readLong();
					assertEquals(1421746759512286392L, classSerialUid);
					// new handle
					nextHandle++;
					
					int hashTableClassDescFlags = in.readByte();
					assertEquals(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, hashTableClassDescFlags);
					short hashTableFieldCount = in.readShort();
					assertEquals(2, hashTableFieldCount);
					// Hash Table field 1
					{
						byte typeCode = in.readByte();
						assertEquals('F', typeCode);
						String fieldName = in.readUTF();
						assertEquals("loadFactor", fieldName);
					}
					
					// Hash Table field 2
					{
						byte typeCode = in.readByte();
						assertEquals('I', typeCode);
						String fieldName = in.readUTF();
						assertEquals("threshold", fieldName);
					}
					int hashTableEndDataFieldFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, hashTableEndDataFieldFlag);
					int hashTableNullFieldFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_NULL, hashTableNullFieldFlag);
					// new handle
					nextHandle++;
					
					float hashMapDF1 = in.readFloat();
					assertEquals("loadfactor wrong?", 0.75, hashMapDF1, 0.1f);
					int hashMapDF2 = in.readInt();
					assertEquals("threshold wrong?", 8, hashMapDF2);
					
					byte hashTableBlockDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_BLOCKDATA, hashTableBlockDataFlag);
					byte numBytes = in.readByte();
					for(int b = 0; b < numBytes; b++)
					{
						in.readByte();
					}
					
					int hashTableEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, hashTableEndDataFlag);					
				}
				//Vector
				{
					int vectorTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, vectorTableObjectFlag);
					int vectorClassDescFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_CLASSDESC, vectorClassDescFlag);
					String className = in.readUTF();
					assertEquals("java.util.Vector", className);
					long classSerialUid = in.readLong();
					assertEquals(-2767605614048989439L, classSerialUid);
					// new handle
					nextHandle++;
					
					int hashTableClassDescFlags = in.readByte();
					assertEquals(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, hashTableClassDescFlags);
					short vectorFieldCount = in.readShort();
					assertEquals(3, vectorFieldCount);
					
					// Vector field 1
					{
						byte typeCode = in.readByte();
						assertEquals('I', typeCode);
						String fieldName = in.readUTF();
						assertEquals("capacityIncrement", fieldName);
					}
					
					// Vector field 2
					{
						byte typeCode = in.readByte();
						assertEquals('I', typeCode);
						String fieldName = in.readUTF();
						assertEquals("elementCount", fieldName);
					}
					 
					// Vector field 3
					{
						byte typeCode = in.readByte();
						assertEquals('[', typeCode);
						String fieldName = in.readUTF();
						assertEquals("elementData", fieldName);
						int vecString = in.readByte();
						assertEquals(ObjectStreamConstants.TC_STRING, vecString);
						String fieldClassName = in.readUTF();
						assertEquals("[Ljava/lang/Object;", fieldClassName);
						nextHandle++;
						
					}
					
					int vectorEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, vectorEndDataFlag);
					int vectorNullFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_NULL, vectorNullFlag);
					// new handle
					//vectorHandle = 
					nextHandle++;
					
					int vectorField1Data = in.readInt();
					assertEquals(0, vectorField1Data);
					
					int vectorField2Data = in.readInt();
					assertEquals(0, vectorField2Data);
					
					byte vectorField3TypeCode = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ARRAY, vectorField3TypeCode);
					int vectorField3ClassDescFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_CLASSDESC, vectorField3ClassDescFlag);
					String vectorField3SuperClassName = in.readUTF();
					assertEquals("[Ljava.lang.Object;", vectorField3SuperClassName);
					long vectorField3SerialUid = in.readLong();
					assertEquals(-8012369246846506644L, vectorField3SerialUid);
					nextHandle++;
					
					int vectorField3DescFlags = in.readByte();
					assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, vectorField3DescFlags);
					short vectorField3Count = in.readShort();
					assertEquals(0, vectorField3Count);
					int vectorField3EndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, vectorField3EndDataFlag);
					int vectorField3NullFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_NULL, vectorField3NullFlag);
					nextHandle++;
					
					int arrayLength = in.readInt();
					for(int b = 0; b < arrayLength; ++b)
					{
						byte nullObjectMarker = in.readByte();
						assertEquals(ObjectStreamConstants.TC_NULL, nullObjectMarker);
					}
					int arrayEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
				}
				
//				Unknown Custom Data
				{ 
					int unknownCustomObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, unknownCustomObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					publicExponentObjectHandle = nextHandle++;
					
				}
//				BigInt Data
				{			
					int field1 = in.readInt();
					assertEquals(-1, field1);
					int field2 = in.readInt();
					assertEquals(-1, field2);
					int field3 = in.readInt();
					assertEquals(-2, field3);
					int field4 = in.readInt();
					assertEquals(-2, field4);
					int field5 = in.readInt();
					assertEquals(1, field5);
									
					byte typeCode = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
					byte typeCodeRefFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
					int refbyteArrayClassHandle = in.readInt();
					assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
					nextHandle++;
					
					int arrayLength = in.readInt();
					for(int b = 0; b < arrayLength; ++b)
					{
						in.readByte();
					}
					int arrayEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
				}
				int EndCustomDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, EndCustomDataFlag);
				
				{
					//Private CRTKEY Field One Data 
					
					// BigInt
					int crtCoefficientTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, crtCoefficientTableObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}

				}
				
				// Private CRT Key Field2 Data
				{
					int primeExponentPTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, primeExponentPTableObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}
					
				}
				
				//Private CRT Field3 Data
				{ 
					int primeExponentQTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, primeExponentQTableObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}
				}
				
//				Private CRT Field4 Data
				{ 
					int primePTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, primePTableObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}
				}
				
//				Private CRT Field5 Data
				{ 
					int primeQTableObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, primeQTableObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}
				}
				
//				Private CRT Field6 Data
				{ 
					int publicExponentObjectFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, publicExponentObjectFlag);
					int refFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
					int refBigIntClassHandle = in.readInt();
					assertEquals(bigIntClassHandle, refBigIntClassHandle);
					publicExponentObjectHandle = nextHandle++;
					
//					BigInt Data
					{			
						int field1 = in.readInt();
						assertEquals(-1, field1);
						int field2 = in.readInt();
						assertEquals(-1, field2);
						int field3 = in.readInt();
						assertEquals(-2, field3);
						int field4 = in.readInt();
						assertEquals(-2, field4);
						int field5 = in.readInt();
						assertEquals(1, field5);
										
						byte typeCode = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
						byte typeCodeRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
						int refbyteArrayClassHandle = in.readInt();
						assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
						nextHandle++;
						
						int arrayLength = in.readInt();
						for(int b = 0; b < arrayLength; ++b)
						{
							in.readByte();
						}
						int arrayEndDataFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
					}
				}
				


				// Public Key Description
				{
					int objectForPublic = in.readByte();
					assertEquals(ObjectStreamConstants.TC_OBJECT, objectForPublic);
					int classFlagForPublic = in.readByte();
					assertEquals(ObjectStreamConstants.TC_CLASSDESC, classFlagForPublic);
					String classNameForPublic = in.readUTF();
					assertEquals("org.bouncycastle.jce.provider.JCERSAPublicKey", classNameForPublic);
					long uidForPublicKeyClass = in.readLong();
					assertEquals(2675817738516720772L, uidForPublicKeyClass);
					// new handle
					nextHandle++;
					
					int classDescFlagsForPublic = in.readByte();
					assertEquals(ObjectStreamConstants.SC_SERIALIZABLE, classDescFlagsForPublic);
					int fieldCountForPublic = in.readShort();
					assertEquals(2, fieldCountForPublic);
					String[] expectedFieldsForPublic = {"modulus", "publicExponent"};
					
					{
						byte typeCode = in.readByte();
						assertEquals('L', typeCode);
						String fieldName = in.readUTF();
						assertEquals(expectedFieldsForPublic[0], fieldName);
						int refFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
						int refBigIntStringHandle = in.readInt();
						assertEquals(bigIntStringHandle, refBigIntStringHandle);
						
					}
					{
						byte typeCode = in.readByte();
						assertEquals('L', typeCode);
						String fieldName = in.readUTF();
						assertEquals(expectedFieldsForPublic[1], fieldName);
						int refFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
						int refBigIntStringHandle = in.readInt();
						assertEquals(bigIntStringHandle, refBigIntStringHandle);
					}
					int publicKeyEndDataFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, publicKeyEndDataFlag);
					int publicKeyNullFlag = in.readByte();
					assertEquals(ObjectStreamConstants.TC_NULL, publicKeyNullFlag);
				}
				
				//Public Key Data
				{
					// Field 1 
					{
						int modulusRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, modulusRefFlag);
						int refModulusObjectHandle = in.readInt();
						assertEquals(modulusObjectHandle, refModulusObjectHandle);
					}
					
					{
						int publicExponentRefFlag = in.readByte();
						assertEquals(ObjectStreamConstants.TC_REFERENCE, publicExponentRefFlag);
						int refPublicExponentObjectHandle = in.readInt();
						assertEquals(publicExponentObjectHandle, refPublicExponentObjectHandle);
					}
				}
				
				

			}
//			http://www.macchiato.com/columns/Durable4.html
//			URL For serialized data structure
			

//			File tmpFile = createTempFile();
//			FileOutputStream out = new FileOutputStream(tmpFile);
//			out.write(data);
//			out.close();
//			System.out.println(tmpFile.getAbsolutePath());
//			while(in.available() > 0)
//			{
//				System.out.println(in.readByte());
//			}
			
			
			
//			reader.setFromData(data);
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
