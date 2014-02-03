/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import org.martus.common.MartusAccountAccessToken;
import org.martus.common.MartusAccountAccessToken.TokenInvalidException;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.UnicodeWriter;
import org.miradi.utils.EnhancedJsonArray;
import org.miradi.utils.EnhancedJsonObject;

public class TestMartusAccountAccessToken extends TestCaseEnhanced
{
	public TestMartusAccountAccessToken(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		String invalidToken = "12345678";
		try
		{
			new MartusAccountAccessToken(invalidToken);
			fail("Should have thrown TokenInvalidException");
		}
		catch( TokenInvalidException expectedException)
		{
		}

		String validToken = "34482187";
		String copyOfValidToken = "34482187";
		MartusAccountAccessToken accessTokenValidData1;
		try
		{
			accessTokenValidData1 = new MartusAccountAccessToken(validToken);
			assertEquals("accessToken should be valid since the token given was valid", validToken, accessTokenValidData1.getToken());

			MartusAccountAccessToken annotherAccessTokenValidData1 = new MartusAccountAccessToken(copyOfValidToken);
			assertTrue("Two different Token objects with same token data should be equal", accessTokenValidData1.equals(annotherAccessTokenValidData1));
			
			
			String validToken2 = "11223344";
			MartusAccountAccessToken tokenWithDifferentData = new MartusAccountAccessToken(validToken2);
			assertFalse("Two different Token objects with different token data should not be equal", accessTokenValidData1.equals(tokenWithDifferentData));
			
			assertEquals("toString should return the same code", validToken2, tokenWithDifferentData.toString());
			
			assertEquals("HashCodes should also match", validToken2.hashCode(), tokenWithDifferentData.hashCode());
			assertEquals("Two different token objects with same token HashCodes should not also match", accessTokenValidData1.hashCode(), annotherAccessTokenValidData1.hashCode());
			assertNotEquals("Two different token objects with different tokens HashCodes should not also match", accessTokenValidData1.hashCode(), tokenWithDifferentData.hashCode());
		} 
		catch (TokenInvalidException e)
		{
			fail("Should not have thrown for a valid Token");
		}
	}

	public void testLoadFromFile() throws Exception
	{
		try
		{
			File invalidFile = createTempFile();
			MartusAccountAccessToken.loadFromFile(invalidFile);
			fail("Should have thrown invalid file");
		}
		catch (Exception expectedException)
		{
		}

		File tokenInvalidFile = createTempFile();
		tokenInvalidFile.deleteOnExit();
		FileOutputStream outputStream = new FileOutputStream(tokenInvalidFile);
		DataOutputStream out = new DataOutputStream(outputStream);
		out.writeUTF(invalidMartusAccessJsonTokenString);
		out.flush();
		out.close();
		try
		{
			MartusAccountAccessToken.loadFromFile(tokenInvalidFile);
			fail("Should have thown TokenInvalidException");
		} 
		catch (TokenInvalidException expectedException)
		{
		}
		tokenInvalidFile.delete();

		File tokenValidFile = createTempFile();
		tokenValidFile.deleteOnExit();
		FileOutputStream outputStream2 = new FileOutputStream(tokenValidFile);
		UnicodeWriter out2 = new UnicodeWriter(outputStream2);
		out2.write(validMartusAccessJsonTokenString);
		out2.flush();
		out2.close();
		try
		{
			MartusAccountAccessToken loadedToken = MartusAccountAccessToken.loadFromFile(tokenValidFile);
			assertEquals("Token retrieved from file didn't match?", validMartusAccessTokenString, loadedToken.getToken());
		} 
		catch (Exception expectedException)
		{
			fail("Should not have thown any exceptions, valid file with valid token");
		}
		tokenValidFile.delete();
	}
	
	public void testMartusAccessJsonTokenResponse() throws Exception
	{

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(2014,01,15);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 45);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();

		String tokendate = date.toString();
		EnhancedJsonObject jsonInner = new EnhancedJsonObject();
		jsonInner.put(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG, validMartusAccessTokenString);
		jsonInner.put(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG, tokendate);
		
		EnhancedJsonObject jsonOutter = new EnhancedJsonObject();
		jsonOutter.put(MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG, jsonInner);
		EnhancedJsonObject jsonRetrievedInnter = (EnhancedJsonObject) jsonOutter.get(MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG);
		
		assertEquals("didn't get inner JSON response?", jsonInner, jsonRetrievedInnter);
		
		assertEquals("didn't get token?", validMartusAccessTokenString, jsonRetrievedInnter.get(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG));
		assertEquals("didn't get token date?", tokendate, jsonRetrievedInnter.get(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG));
		
		String expectedJsonInnerAsString = "{\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG+"\":\"Sat Feb 15 01:30:45 PST 2014\",\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG+"\":\""+validMartusAccessTokenString+"\"}";
		assertEquals("Json Inner object not correct?", expectedJsonInnerAsString, jsonInner.toString());
		
		assertEquals("Json Outer object not correct?", validMartusAccessJsonTokenString, jsonOutter.toString());
		
		EnhancedJsonArray innerAsAnArray = new EnhancedJsonArray();
		innerAsAnArray.put(jsonInner);
		EnhancedJsonObject bigObject = new EnhancedJsonObject();
		bigObject.put(MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG, innerAsAnArray);
		
		EnhancedJsonArray gotArray = bigObject.getJsonArray(MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG);
		EnhancedJsonObject gotInner = gotArray.getJson(0);
		assertEquals(gotInner.get(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG), jsonInner.get(MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG));
		
		String expectedJsonOuterArrayAsString = "{\""+MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG+"\":[{\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG+"\":\"Sat Feb 15 01:30:45 PST 2014\",\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG+"\":\""+validMartusAccessTokenString+"\"}]}";
		assertEquals("Json Array Outer object not correct?", expectedJsonOuterArrayAsString, bigObject.toString());
	}
	
	static final String validMartusAccessTokenString = "34482187";
	static final String validMartusAccessJsonTokenString = "{\""+MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG+"\":{\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG+"\":\"Sat Feb 15 01:30:45 PST 2014\",\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG+"\":\""+validMartusAccessTokenString+"\"}}";
	static final String invalidMartusAccessTokenString = "1111111";
	static final String invalidMartusAccessJsonTokenString = "{\""+MartusAccountAccessToken.MARTUS_ACCOUNT_ACCESS_TOKEN_JSON_TAG+"\":{\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_CREATION_DATE_JSON_TAG+"\":\"Sat Feb 15 01:30:45 PST 2014\",\""+MartusAccountAccessToken.MARTUS_ACCESS_TOKEN_JSON_TAG+"\":\""+invalidMartusAccessTokenString+"\"}}";
	
}
