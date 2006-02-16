/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;

import org.bouncycastle.jce.provider.JCERSAPrivateCrtKey;

public class MartusKeyPairSaver
{
	public static void save(DataOutputStream out, MartusJceKeyPair keyPairToSave) throws Exception
	{		
		MartusKeyPairSaver saver = new MartusKeyPairSaver();		
		saver.writeKeyPair(out, keyPairToSave);
	}

	private MartusKeyPairSaver()
	{	
	}
	
	void writeKeyPair(DataOutputStream out, MartusJceKeyPair keyPair) throws Exception
	{
		nextHandle = MartusKeyPairDataConstants.INITIAL_HANDLE;
		
		out.writeShort(ObjectStreamConstants.STREAM_MAGIC);
		out.writeShort(ObjectStreamConstants.STREAM_VERSION);

		// KeyPair
		{
			writeObjectClassHeader(out, MartusKeyPairDataConstants.JAVA_SECURITY_KEY_PAIR_CLASS_NAME, MartusKeyPairDataConstants.KEY_PAIR_CLASS_UID);
			
			out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
			out.writeInt(MartusKeyPairDataConstants.KEY_PAIR_FIELD_NAMES.length);

			for(int field=0; field < MartusKeyPairDataConstants.KEY_PAIR_FIELD_CLASS_NAMES.length; ++field)
			{
				writeObjectFieldDescription(out, MartusKeyPairDataConstants.KEY_PAIR_FIELD_CLASS_NAMES[field], MartusKeyPairDataConstants.KEY_PAIR_FIELD_NAMES[field]);
			}
			
			writeClassFooter(out);
		}
	
		// JCERSAPrivateCrtKey
		{
			writeObjectClassHeader(out, MartusKeyPairDataConstants.BCE_JCE_PROVIDER_JCERSAPRIVATE_CRT_KEY_CLASS_NAME, MartusKeyPairDataConstants.BCE_JCE_RSA_PRIVATE_KEY_CLASS_UID);

			out.writeInt(ObjectStreamConstants.SC_SERIALIZABLE);
			out.writeInt(MartusKeyPairDataConstants.PRIVATE_CRT_KEY_FIELD_NAMES.length);

			bigIntStringHandle = writeObjectFieldDescription(out, MartusKeyPairDataConstants.LJAVA_MATH_BIG_INTEGER_CLASS_NAME, MartusKeyPairDataConstants.PRIVATE_CRT_KEY_FIELD_NAMES[0]);
			for(int field=1; field < MartusKeyPairDataConstants.PRIVATE_CRT_KEY_FIELD_NAMES.length; ++field)
			{
				writeBigIntegerFieldReference(out, MartusKeyPairDataConstants.PRIVATE_CRT_KEY_FIELD_NAMES[field]);
			}
			
			out.writeInt(ObjectStreamConstants.TC_ENDBLOCKDATA);
			
			// Super Class
			out.writeByte(ObjectStreamConstants.TC_CLASSDESC);
			out.writeUTF(MartusKeyPairDataConstants.BCE_JCE_PROVIDER_JCERSAPRIVATE_KEY_CLASS_NAME);
			out.writeLong(MartusKeyPairDataConstants.PRIVATE_KEY_SUPER_CLASS_UID);
			// new handle
			nextHandle++;
			
			out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
			out.writeInt(MartusKeyPairDataConstants.PRIVATE_KEY_FIELD_COUNT);

			// Private Key field 1
			writeBigIntegerFieldReference(out, MartusKeyPairDataConstants.MODULUS_FIELD_NAME);
						
			// Private Key field 2
			writeObjectFieldDescription(out, MartusKeyPairDataConstants.LJAVA_UTIL_HASHTABLE_CLASS_NAME, MartusKeyPairDataConstants.PKCS12ATTRIBUTES_FIELD_NAME);
			
			// Private Key field 3
			writeObjectFieldDescription(out, MartusKeyPairDataConstants.LJAVA_UTIL_VECTOR_CLASS_NAME, MartusKeyPairDataConstants.PKCS12ORDERING_FIELD_NAME);
			
			// Private Key field 4
			writeBigIntegerFieldReference(out, MartusKeyPairDataConstants.PRIVATE_EXPONENT_FIELD_NAME);
			writeClassFooter(out);
		}			

		// BigInteger
		{
			bigIntClassHandle = writeObjectClassHeader(out, MartusKeyPairDataConstants.JAVA_MATH_BIG_INTEGER_CLASS_NAME, MartusKeyPairDataConstants.BIG_INTEGER_CLASS_UID);
						
			out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD);
			out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_FIELD_COUNT);

			// Big Integer field 1
			writeIntFieldDescription(out, MartusKeyPairDataConstants.BIT_COUNT_FIELD_NAME);

			// Big Integer field 2
			writeIntFieldDescription(out, MartusKeyPairDataConstants.BIT_LENGTH_FIELD_NAME);
			
			// Big Integer field 3
			writeIntFieldDescription(out, MartusKeyPairDataConstants.FIRST_NONZERO_BYTE_NUM_FIELD_NAME);

			// Big Integer field 4
			writeIntFieldDescription(out, MartusKeyPairDataConstants.LOWEST_SET_BIT_FIELD_NAME);
			
			// Big Integer field 5
			writeIntFieldDescription(out, MartusKeyPairDataConstants.SIGNUM_FIELD_NAME);
			
			// Big Integer field 6
			writeByteArrayFieldDescription(out, MartusKeyPairDataConstants.MAGNITUDE_FIELD_NAME);

			out.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
			out.writeByte(ObjectStreamConstants.TC_CLASSDESC);
			out.writeUTF(MartusKeyPairDataConstants.JAVA_LANG_NUMBER_CLASS_NAME);
			out.writeLong(MartusKeyPairDataConstants.LANG_NUMBER_CLASS_UID);
			// new handle
			nextHandle++;
			
			out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
			//:TODO make constant
			out.writeShort(0);
//			int superFieldCount = in.readShort();
//			throwIfNotEqual(0, superFieldCount);
//			
			modulusObjectHandle = writeClassFooter(out);
			
			//BigInt Modulus Data
			{
				out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_BIT_COUNT);
				out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_BIT_LENGTH);
				out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_FIRST_NONZERO_BYTE_NUMBER);
				out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_LOWEST_SET_BIT);
				out.writeInt(MartusKeyPairDataConstants.BIGINTEGER_SIGNUM);
				
				byteArrayClassHandle = writeArrayClassHeader(out, MartusKeyPairDataConstants.BYTE_ARRAY_CLASS_NAME, MartusKeyPairDataConstants.ARRAY_CLASS_UID);
				out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
				out.writeByte(MartusKeyPairDataConstants.MAGNITUDE_FIELD_COUNT);		
				
				writeClassFooter(out);
				
				JCERSAPrivateCrtKey privateKey = (JCERSAPrivateCrtKey)keyPair.getPrivateKey();
				
				byte[] magnitude = privateKey.getModulus().toByteArray();
				
				out.writeInt(magnitude.length);
				out.write(magnitude);
				out.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);				
			}
//			
//			// Hashtable
//			{
//				readObjectClassHeader(in, MartusKeyPairDataConstants.JAVA_UTIL_HASHTABLE_CLASS_NAME, MartusKeyPairDataConstants.HASHTABLE_CLASS_UID);
//				
//				int hashTableClassDescFlags = in.readByte();
//				throwIfNotEqual(ObjectStreamConstants.SC_SERIALIZABLE | ObjectStreamConstants.SC_WRITE_METHOD, hashTableClassDescFlags);
//				short hashTableFieldCount = in.readShort();
//				throwIfNotEqual(MartusKeyPairDataConstants.HASHTABLE_FIELD_COUNT, hashTableFieldCount);
//				
//				// Hash Table field 1
//				throwIfNotEqual(MartusKeyPairDataConstants.LOAD_FACTOR_FIELD_NAME, readFloatFieldDescription(in));
//				
//				// Hash Table field 2
//				throwIfNotEqual(MartusKeyPairDataConstants.THRESHOLD_FIELD_NAME, readIntFieldDescription(in));
//
//				readClassFooter(in);
//				
//				readEmptyHashTableData(in);
//			}
//			
//			//Vector
//			{
//				readObjectClassHeader(in, MartusKeyPairDataConstants.JAVA_UTIL_VECTOR_CLASS_NAME, MartusKeyPairDataConstants.VECTOR_CLASS_UID);
//				
//				int vectorClassDescFlags = in.readByte();
//				throwIfNotEqual(ObjectStreamConstants.SC_SERIALIZABLE, vectorClassDescFlags & ObjectStreamConstants.SC_SERIALIZABLE);
//				boolean vectorHasWriteObject = hasFlag(vectorClassDescFlags, ObjectStreamConstants.SC_WRITE_METHOD);
//				short vectorFieldCount = in.readShort();
//				throwIfNotEqual(MartusKeyPairDataConstants.VECTOR_FIELD_COUNT, vectorFieldCount);
//				
//				// Vector field 1
//				throwIfNotEqual(MartusKeyPairDataConstants.CAPACITY_INCREMENT_FIELD_NAME, readIntFieldDescription(in));
//				
//				// Vector field 2
//				throwIfNotEqual(MartusKeyPairDataConstants.ELEMENT_COUNT_FIELD_NAME, readIntFieldDescription(in));
//				 
//				// Vector field 3
//				throwIfNotEqual(MartusKeyPairDataConstants.ELEMENT_DATA_FIELD_NAME, readArrayFieldDecription(in));
//				
//				readClassFooter(in);
//				
//				// Vector Field1 Data
//				int capacityIncrement = in.readInt();
//				throwIfNotEqual(MartusKeyPairDataConstants.VECTOR_CAPACITY_INCREMENT, capacityIncrement);
//				
//				//Vector Field2 Data
//				int elementCount = in.readInt();
//				throwIfNotEqual(MartusKeyPairDataConstants.VECTOR_ELEMENT_COUNT, elementCount);
//				
//				readArrayClassHeader(in, MartusKeyPairDataConstants.JAVA_LANG_OBJECT_CLASS_NAME, MartusKeyPairDataConstants.OBJECT_CLASS_UID);
//				
//				int vectorField3DescFlags = in.readByte();
//				throwIfNotEqual(ObjectStreamConstants.SC_SERIALIZABLE, vectorField3DescFlags);
//				short vectorField3Count = in.readShort();
//				throwIfNotEqual(0, vectorField3Count);
//				
//				readClassFooter(in);
//				
//				int arrayLength = in.readInt();
//				for(int b = 0; b < arrayLength; ++b)
//				{
//					byte nullObjectMarker = in.readByte();
//					throwIfNotEqual(ObjectStreamConstants.TC_NULL, nullObjectMarker);
//				}
//				
//				if(vectorHasWriteObject)
//				{
//					int arrayEndDataFlag = in.readByte();
//					throwIfNotEqual(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
//				}
//				
//			}
//			
//			//BigInt privateExponent (Private Key Field4)  
//			publicExponentObjectHandle = readBigIntegerObjectHeader(in);
//			privateExponent = readBigIntegerData(in);
//			
//			if(privateSuperHasWriteObject)
//			{
//				int endOfWriteObjectData = in.readByte();
//				throwIfNotEqual(ObjectStreamConstants.TC_ENDBLOCKDATA, endOfWriteObjectData);
//			}
//			
//			// BigInt crtCoefficient (PrivateCRTKey Field 1)
//			readBigIntegerObjectHeader(in);
//			crtCoefficient = readBigIntegerData(in);
//
//			// BigInt primeExponentP (PrivateCRTKey Field 2)
//			readBigIntegerObjectHeader(in);			
//			primeExponentP = readBigIntegerData(in);
//			
//			// BigInt primeExponentQ (PrivateCRTKey Field 3)
//			readBigIntegerObjectHeader(in);			
//			primeExponentQ = readBigIntegerData(in);
//					
//			// BigInt primeP (PrivateCRTKey Field4) 
//			readBigIntegerObjectHeader(in);		
//			primeP = readBigIntegerData(in);
//			
//			// BigInt primeQ (PrivateCRTKey Field5)
//			readBigIntegerObjectHeader(in);		
//			primeQ = readBigIntegerData(in);
//			
//			// BigInt publicExponent (PrivateCRTKey Field6)
//			publicExponentObjectHandle = readBigIntegerObjectHeader(in);
//			publicExponent = readBigIntegerData(in);				
//			
//			// Public Key Description
//			{
//				readObjectClassHeader(in, MartusKeyPairDataConstants.BCE_JCE_PROVIDER_JCERSAPUBLIC_KEY_CLASS_NAME, MartusKeyPairDataConstants.BCE_JCE_RSA_PUBLIC_KEY_CLASS_UID);
//				
//				int classDescFlagsForPublic = in.readByte();
//				throwIfNotEqual(ObjectStreamConstants.SC_SERIALIZABLE, classDescFlagsForPublic);
//				int fieldCountForPublic = in.readShort();
//				throwIfNotEqual(2, fieldCountForPublic);
//				
//				for(int i = 0; i < MartusKeyPairDataConstants.PUBLIC_KEY_FIELD_NAMES.length; ++i)
//				{
//					String fieldName = readBigIntegerFieldReference(in);
//					throwIfNotEqual(MartusKeyPairDataConstants.PUBLIC_KEY_FIELD_NAMES[i], fieldName);
//				}
//				
//				readClassFooter(in);
//				
//			}
//			
//			//Public Key Data
//			{
//				// BigInt modulus (Field 1) 
//				int refModulusObjectHandle = readObjectReference(in);
//				throwIfNotEqual(modulusObjectHandle, refModulusObjectHandle);
//
//				//BigInt publicExponent Reference (Field 2)
//				int refPublicExponentObjectHandle = readObjectReference(in);
//				throwIfNotEqual(publicExponentObjectHandle, refPublicExponentObjectHandle);
//			}
		}
//		
//		// Reconstitute Keypair
//		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, publicExponent);
//		RSAPrivateCrtKeySpec privateSpec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
//		KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
//		JCERSAPublicKey publicKey = (JCERSAPublicKey)factory.generatePublic(publicSpec);
//		JCERSAPrivateCrtKey privateCRTKey = (JCERSAPrivateCrtKey)factory.generatePrivate(privateSpec);
//		
//		KeyPair keyPair = new KeyPair(publicKey, privateCRTKey);
//		return keyPair;
	}

//		private boolean hasFlag(int variable, int flag)
//		{
//			return (variable & flag) == flag;
//		}
	
		private int writeClassFooter(DataOutputStream out) throws IOException
		{
			out.write(ObjectStreamConstants.TC_ENDBLOCKDATA);
			out.writeInt(ObjectStreamConstants.TC_NULL);
			// new handle		
			return nextHandle++;
		}

		private int writeObjectClassHeader(DataOutputStream out, String className, long serialUid) throws IOException
		{
			return writeClassHeader(out, ObjectStreamConstants.TC_OBJECT, className, serialUid);
		}
		
		private int writeArrayClassHeader(DataOutputStream out, String className, long serialUid) throws IOException
		{
			return writeClassHeader(out, ObjectStreamConstants.TC_ARRAY, className, serialUid);
		}

		private int writeClassHeader(DataOutputStream out, byte classType, String className, long serialUid) throws IOException
		{
			out.writeByte(classType);
//			throwIfNotEqual(classType, objectForPublic);
			out.writeByte(ObjectStreamConstants.TC_CLASSDESC);
			out.writeUTF(className);
			out.writeLong(serialUid);
			// new handle
			return nextHandle++;
		}

		private int writeObjectFieldDescription(DataOutputStream out, String expectedClassName, String expectedFieldName) throws IOException
		{
			out.writeByte(MartusKeyPairDataConstants.FIELD_TYPE_CODE_OBJECT);
			out.writeUTF(expectedFieldName);
			out.writeByte(ObjectStreamConstants.TC_STRING);
			out.writeUTF(expectedClassName);
			// new handle
			return nextHandle++;
		}

		private void writeByteArrayFieldDescription(DataOutputStream out, String fieldName) throws IOException
		{
			out.writeByte(MartusKeyPairDataConstants.FIELD_TYPE_CODE_ARRAY);
			out.writeUTF(fieldName);
			out.writeByte(ObjectStreamConstants.TC_STRING);
			out.writeUTF(MartusKeyPairDataConstants.BYTE_ARRAY_FIELD_NAME);
			// new handle
			nextHandle++;
		}
//
//		private String readFloatFieldDescription(DataInputStream in) throws IOException
//		{
//			byte typeCode = in.readByte();
//			throwIfNotEqual(MartusKeyPairDataConstants.FIELD_TYPE_CODE_FLOAT, typeCode);
//			String fieldName = in.readUTF();
//			return fieldName;
//		}
//
//		private void readEmptyHashTableData(DataInputStream in) throws IOException
//		{
//			float loadFactor = in.readFloat();
//			throwIfNotEqual("loadfactor wrong?", MartusKeyPairDataConstants.HASHTABLE_LOADFACTOR, loadFactor, MartusKeyPairDataConstants.HASHTABLE_LOADFACTOR/100);
//			
//			int threshold = in.readInt();
//			throwIfNotEqual("threshold wrong?", MartusKeyPairDataConstants.HASHTABLE_THRESHOLD, threshold);
//			
//			byte hashTableBlockDataFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_BLOCKDATA, hashTableBlockDataFlag);
//			
//			byte blockDataByteCount = in.readByte();
//			throwIfNotEqual("wrong block data byte count?", MartusKeyPairDataConstants.HASHTABLE_BYTE_COUNT, blockDataByteCount);
//			// originalLength
//			in.readInt(); 
//			int elements = in.readInt();
//			throwIfNotEqual("Hashtable not empty?", MartusKeyPairDataConstants.HASHTABLE_NUMBER_OF_ELEMENTS, elements);
//			
//			int hashTableEndDataFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_ENDBLOCKDATA, hashTableEndDataFlag);
//		}
//
//		private String readArrayFieldDecription(DataInputStream in) throws IOException
//		{
//			byte typeCode = in.readByte();
//			throwIfNotEqual(MartusKeyPairDataConstants.FIELD_TYPE_CODE_ARRAY, typeCode);
//			String fieldName = in.readUTF();
//			int vecString = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_STRING, vecString);
//			String fieldClassName = in.readUTF();
//			throwIfNotEqual(MartusKeyPairDataConstants.LJAVA_LANG_OBJECT_CLASS_NAME, fieldClassName);
//			nextHandle++;
//			return fieldName;
//		}
//
		private void writeIntFieldDescription(DataOutputStream out, String fieldName) throws IOException
		{
			out.writeByte(MartusKeyPairDataConstants.FIELD_TYPE_CODE_INTEGER);
			out.writeUTF(fieldName);
		}

//		private int readBigIntegerObjectHeader(DataInputStream in) throws IOException
//		{
//			int publicExponentObjectFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_OBJECT, publicExponentObjectFlag);
//			int refFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_REFERENCE, refFlag);
//			int refBigIntClassHandle = in.readInt();
//			throwIfNotEqual(bigIntClassHandle, refBigIntClassHandle);
//			int thisHandle = nextHandle++;
//			return thisHandle;
//		}
//
//		private BigInteger readBigIntegerData(DataInputStream in) throws IOException
//		{
//			int bitCount = in.readInt();
//			throwIfNotEqual(MartusKeyPairDataConstants.BIGINTEGER_BIT_COUNT, bitCount);
//			int bitLength = in.readInt();
//			throwIfNotEqual(MartusKeyPairDataConstants.BIGINTEGER_BIT_LENGTH, bitLength);
//			int firstNonZeroByteNum = in.readInt();
//			throwIfNotEqual(MartusKeyPairDataConstants.BIGINTEGER_FIRST_NONZERO_BYTE_NUMBER, firstNonZeroByteNum);
//			int lowestSetBit = in.readInt();
//			throwIfNotEqual(MartusKeyPairDataConstants.BIGINTEGER_LOWEST_SET_BIT, lowestSetBit);
//			int signum = in.readInt();
//			throwIfNotEqual(MartusKeyPairDataConstants.BIGINTEGER_SIGNUM, signum);
//
//			byte typeCode = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_ARRAY, typeCode);
//			byte typeCodeRefFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
//			int refbyteArrayClassHandle = in.readInt();
//			throwIfNotEqual(byteArrayClassHandle, refbyteArrayClassHandle);
//			nextHandle++;
//			
//			int arrayLength = in.readInt();
//			
//			byte[] magnitude = new byte[arrayLength];
//			in.read(magnitude);
//			
//			int arrayEndDataFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
//
//			BigInteger gotBigInteger = new BigInteger(signum, magnitude);
//			return gotBigInteger;
//		}
//
		private void writeBigIntegerFieldReference(DataOutputStream out, String fieldName) throws IOException
		{
			out.writeByte(MartusKeyPairDataConstants.FIELD_TYPE_CODE_OBJECT);
			out.writeUTF(fieldName);
			out.writeInt(ObjectStreamConstants.TC_REFERENCE);
			out.writeInt(bigIntStringHandle);
		}
//
//		private int readObjectReference(DataInputStream in) throws IOException
//		{
//			int modulusRefFlag = in.readByte();
//			throwIfNotEqual(ObjectStreamConstants.TC_REFERENCE, modulusRefFlag);
//			int refModulusObjectHandle = in.readInt();
//			return refModulusObjectHandle;
//		}
//		
//		void throwIfNotEqual(String text, Object expected, Object actual)
//		{
//			if(!expected.equals(actual))
//				throw new RuntimeException(text + "expected " + expected + " but was " + actual);
//		}
//		
//		void throwIfNotEqual(Object expected, Object actual)
//		{
//			if(!expected.equals(actual))
//				throw new RuntimeException("expected " + expected + " but was " + actual);
//		}
//		
//		void throwIfNotEqual(String text, int expected, int actual)
//		{
//			if(expected != actual)
//				throw new RuntimeException(text + "expected " + expected + " but was " + actual);
//		}
//		
//		void throwIfNotEqual(long expected, long actual)
//		{
//			if(expected != actual)
//				throw new RuntimeException("expected " + expected + " but was " + actual);
//		}
//		
//		void throwIfNotEqual(String text, double expected, double actual, double tolerance)
//		{
//			if(expected < actual - tolerance || expected > actual + tolerance)
//				throw new RuntimeException(text + "expected " + expected + " but was " + actual);
//		}
//		
		int nextHandle;	
		int bigIntStringHandle;
		int bigIntClassHandle;
		int modulusObjectHandle;
		int byteArrayClassHandle;
//		int publicExponentObjectHandle;
//
//		
//		BigInteger modulus;
//		BigInteger publicExponent;
//		BigInteger crtCoefficient;
//		BigInteger primeExponentP;
//		BigInteger primeExponentQ;
//		BigInteger primeP;
//		BigInteger primeQ;
//		BigInteger privateExponent;
	//MartusKeyPair gotKeyPair;
}
