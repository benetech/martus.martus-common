package org.martus.common.crypto;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.provider.JCERSAPrivateCrtKey;
import org.bouncycastle.jce.provider.JCERSAPublicKey;

public class MartusKeyPairLoader
{

	public MartusKeyPair readMartusKeyPair(DataInputStream in) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException 
	{
		nextHandle = INITIAL_HANDLE;
		
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
				assertEquals(expectedFieldsForPrivate[field], readBigIntegerFieldReference(in));
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
				assertEquals("modulus", readBigIntegerFieldReference(in));
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
				assertEquals("privateExponent", readBigIntegerFieldReference(in));
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
				String fieldName = readIntFieldDescription(in);
				assertEquals("bitCount", fieldName);
			}
			// Big Integer field 2
			{
				String fieldName = readIntFieldDescription(in);
				assertEquals("bitLength", fieldName);
			}
			// Big Integer field 3
			{
				String fieldName = readIntFieldDescription(in);
				assertEquals("firstNonzeroByteNum", fieldName);
			}
			// Big Integer field 4
			{
				String fieldName = readIntFieldDescription(in);
				assertEquals("lowestSetBit", fieldName);
			}
			// Big Integer field 5
			{
				String fieldName = readIntFieldDescription(in);
				assertEquals("signum", fieldName);
			}
			// Big Integer field 6
			{
				String fieldName = readByteArrayFieldDescription(in);
				assertEquals("magnitude", fieldName);
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
				
				int bitCount = in.readInt();
				assertEquals(-1, bitCount);
				int bitLength = in.readInt();
				assertEquals(-1, bitLength);
				int firstNonZeroByteNum = in.readInt();
				assertEquals(-2, firstNonZeroByteNum);
				int lowestSetBit = in.readInt();
				assertEquals(-2, lowestSetBit);
				int signum = in.readInt();
				assertEquals(1, signum);
								
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

				byte[] magnitude = new byte[arrayLength];
				in.read(magnitude);
				
				int arrayEndDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);
				
				modulus = new BigInteger(signum, magnitude);
				
			}
			
//				Hashtable
			{
				int objectForHashtable = in.readByte();
				assertEquals(ObjectStreamConstants.TC_OBJECT, objectForHashtable);
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
				String field1Name = readFloatFieldDescription(in);
				assertEquals("loadFactor", field1Name);
				
				// Hash Table field 2
				String field2Name = readIntFieldDescription(in);
				assertEquals("threshold", field2Name);

				int hashTableEndDataFieldFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, hashTableEndDataFieldFlag);
				int hashTableNullFieldFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, hashTableNullFieldFlag);
				// new handle
				nextHandle++;
				
				readEmptyHashTableData(in);
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
				String field1Name = readIntFieldDescription(in);
				assertEquals("capacityIncrement", field1Name);
				
				// Vector field 2
				String field2Name = readIntFieldDescription(in);
				assertEquals("elementCount", field2Name);
				 
				// Vector field 3
				String field3Name = readArrayFieldDecription(in);
				assertEquals("elementData", field3Name);
				
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
			
			//BigInt privateExponent (Private Key Field4)  
			publicExponentObjectHandle = readBigIntegerObjectHeader(in);
			privateExponent = readBigIntegerData(in);
			
			int EndPrivateKeyFlag = in.readByte();
			assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, EndPrivateKeyFlag);
			
			// BigInt crtCoefficient (PrivateCRTKey Field 1)
			readBigIntegerObjectHeader(in);
			crtCoefficient = readBigIntegerData(in);

			// BigInt primeExponentP (PrivateCRTKey Field 2)
			readBigIntegerObjectHeader(in);			
			primeExponentP = readBigIntegerData(in);
			
			// BigInt primeExponentQ (PrivateCRTKey Field 3)
			readBigIntegerObjectHeader(in);			
			primeExponentQ = readBigIntegerData(in);
					
			// BigInt primeP (PrivateCRTKey Field4) 
			readBigIntegerObjectHeader(in);		
			primeP = readBigIntegerData(in);
			
			// BigInt primeQ (PrivateCRTKey Field5)
			readBigIntegerObjectHeader(in);		
			primeQ = readBigIntegerData(in);
			
			// BigInt publicExponent (PrivateCRTKey Field6)
			publicExponentObjectHandle = readBigIntegerObjectHeader(in);
			publicExponent = readBigIntegerData(in);				
			
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
				
				for(int i = 0; i < expectedFieldsForPublic.length; ++i)
				{
					String fieldName = readBigIntegerFieldReference(in);
					assertEquals(expectedFieldsForPublic[i], fieldName);
				}
				
				int publicKeyEndDataFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, publicKeyEndDataFlag);
				int publicKeyNullFlag = in.readByte();
				assertEquals(ObjectStreamConstants.TC_NULL, publicKeyNullFlag);
			}
			
			//Public Key Data
			{
				// BigInt modulus (Field 1) 
				int refModulusObjectHandle = readObjectReference(in);
				assertEquals(modulusObjectHandle, refModulusObjectHandle);

				//BigInt publicExponent Reference (Field 2)
				int refPublicExponentObjectHandle = readObjectReference(in);
				assertEquals(publicExponentObjectHandle, refPublicExponentObjectHandle);
			}
		}
		
		// Reconstitute Keypair
		RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, publicExponent);
		RSAPrivateCrtKeySpec privateSpec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
		KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
		JCERSAPublicKey publicKey = (JCERSAPublicKey)factory.generatePublic(publicSpec);
		JCERSAPrivateCrtKey privateCRTKey = (JCERSAPrivateCrtKey)factory.generatePrivate(privateSpec);
		
		KeyPair keyPair = new KeyPair(publicKey, privateCRTKey);
		gotKeyPair = new MartusJceKeyPair(keyPair);

		return gotKeyPair;
	}

	private String readByteArrayFieldDescription(DataInputStream in) throws IOException {
		byte typeCode = in.readByte();
		assertEquals('[', typeCode);
		String fieldName = in.readUTF();
		int refFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_STRING, refFlag);
		String fieldClassName = in.readUTF();
		assertEquals("[B", fieldClassName);
		// new handle
		nextHandle++;
		return fieldName;
	}

	private String readFloatFieldDescription(DataInputStream in) throws IOException {
		byte typeCode = in.readByte();
		assertEquals('F', typeCode);
		String fieldName = in.readUTF();
		return fieldName;
	}

	private void readEmptyHashTableData(DataInputStream in) throws IOException {
		float loadFactor = in.readFloat();
		assertEquals("loadfactor wrong?", 0.75, loadFactor, 0.1f);
		
		int threshold = in.readInt();
		assertEquals("threshold wrong?", 8, threshold);
		
		byte hashTableBlockDataFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_BLOCKDATA, hashTableBlockDataFlag);
		
		byte blockDataByteCount = in.readByte();
		assertEquals("wrong block data byte count?", 8, blockDataByteCount);
		// originalLength
		in.readInt(); 
		int elements = in.readInt();
		assertEquals("Hashtable not empty?", 0, elements);
		
		int hashTableEndDataFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, hashTableEndDataFlag);
	}

	private String readArrayFieldDecription(DataInputStream in) throws IOException {
		byte typeCode = in.readByte();
		assertEquals('[', typeCode);
		String fieldName = in.readUTF();
		int vecString = in.readByte();
		assertEquals(ObjectStreamConstants.TC_STRING, vecString);
		String fieldClassName = in.readUTF();
		assertEquals("[Ljava/lang/Object;", fieldClassName);
		nextHandle++;
		return fieldName;
	}

	private String readIntFieldDescription(DataInputStream in) throws IOException {
		byte typeCode = in.readByte();
		assertEquals('I', typeCode);
		String fieldName = in.readUTF();
		return fieldName;
	}

	private int readBigIntegerObjectHeader(DataInputStream in) throws IOException {
		int publicExponentObjectFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_OBJECT, publicExponentObjectFlag);
		int refFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
		int refBigIntClassHandle = in.readInt();
		assertEquals(bigIntClassHandle, refBigIntClassHandle);
		int thisHandle = nextHandle++;
		return thisHandle;
	}

	private BigInteger readBigIntegerData(DataInputStream in) throws IOException {
		int bitCount = in.readInt();
		assertEquals(-1, bitCount);
		int bitLength = in.readInt();
		assertEquals(-1, bitLength);
		int firstNonZeroByteNum = in.readInt();
		assertEquals(-2, firstNonZeroByteNum);
		int lowestSetBit = in.readInt();
		assertEquals(-2, lowestSetBit);
		int signum = in.readInt();
		assertEquals(1, signum);
						
		byte typeCode = in.readByte();
		assertEquals(ObjectStreamConstants.TC_ARRAY, typeCode);
		byte typeCodeRefFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_REFERENCE, typeCodeRefFlag);
		int refbyteArrayClassHandle = in.readInt();
		assertEquals(byteArrayClassHandle, refbyteArrayClassHandle);
		nextHandle++;
		
		int arrayLength = in.readInt();
		
		byte[] magnitude = new byte[arrayLength];
		in.read(magnitude);
		
		int arrayEndDataFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, arrayEndDataFlag);

		BigInteger gotBigInteger = new BigInteger(signum, magnitude);
		return gotBigInteger;
	}

	private String readBigIntegerFieldReference(DataInputStream in) throws IOException {
		byte typeCode = in.readByte();
		String fieldName = in.readUTF();
		int refFlag = in.readByte();
		int refBigIntStringHandle = in.readInt();
		assertEquals('L', typeCode);
		assertEquals(ObjectStreamConstants.TC_REFERENCE, refFlag);
		assertEquals(bigIntStringHandle, refBigIntStringHandle);
		return fieldName;
	}

	private int readObjectReference(DataInputStream in) throws IOException {
		int modulusRefFlag = in.readByte();
		assertEquals(ObjectStreamConstants.TC_REFERENCE, modulusRefFlag);
		int refModulusObjectHandle = in.readInt();
		return refModulusObjectHandle;
	}
	
	void assertEquals(String text, Object expected, Object actual)
	{
		if(!expected.equals(actual))
			throw new RuntimeException(text + "expected " + expected + " but was " + actual);
	}
	
	void assertEquals(Object expected, Object actual)
	{
		if(!expected.equals(actual))
			throw new RuntimeException("expected " + expected + " but was " + actual);
	}
	
	void assertEquals(String text, int expected, int actual)
	{
		if(expected != actual)
			throw new RuntimeException(text + "expected " + expected + " but was " + actual);
	}
	
	void assertEquals(long expected, long actual)
	{
		if(expected != actual)
			throw new RuntimeException("expected " + expected + " but was " + actual);
	}
	
	void assertEquals(String text, double expected, double actual, double tolerance)
	{
		if(expected < actual - tolerance || expected > actual + tolerance)
			throw new RuntimeException(text + "expected " + expected + " but was " + actual);
	}
	
	private static final int INITIAL_HANDLE = 8257536;	
	
	int nextHandle;	
	int bigIntStringHandle;
	int bigIntClassHandle;
	int byteArrayClassHandle;
	int modulusObjectHandle;
	int publicExponentObjectHandle;
	
	BigInteger modulus;
	BigInteger publicExponent;
	BigInteger crtCoefficient;
	BigInteger primeExponentP;
	BigInteger primeExponentQ;
	BigInteger primeP;
	BigInteger primeQ;
	BigInteger privateExponent;

	MartusKeyPair gotKeyPair;
}
