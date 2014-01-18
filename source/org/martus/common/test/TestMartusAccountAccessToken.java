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

import org.martus.common.MartusAccountAccessToken;
import org.martus.common.MartusAccountAccessToken.TokenInvalidException;
import org.martus.util.TestCaseEnhanced;

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
}
