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
package org.martus.common.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.util.TestCaseEnhanced;


public class TestMartusBulletinRetriever extends TestCaseEnhanced
{
	public TestMartusBulletinRetriever(String name)
	{
		super(name);
	}
	
	public void testInvalidIOStream() throws Exception
	{
		char[] password = "test".toCharArray();
		try
		{
			InputStream stream = new FileInputStream("");
			new MartusBulletinRetriever(stream, password);
			fail("Should have thrown IO exception on null input stream");
		}
		catch(IOException expectedException)
		{
		}
	}

	public void testPassword() throws Exception
	{
		char[] password = "the password".toCharArray();
		MartusSecurity security = new MartusSecurity();
		security.createKeyPair(512);
		ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
		security.writeKeyPair(streamOut, password);
		streamOut.close();
		
		ByteArrayInputStream streamIn = new ByteArrayInputStream(streamOut.toByteArray());
		
		try
		{
			new MartusBulletinRetriever(streamIn, "invalid".toCharArray() );
			fail("Should have thrown AuthorizationFailedException on invalid password");
		}
		catch(AuthorizationFailedException expected)
		{
		}
		streamIn.reset();
		new MartusBulletinRetriever(streamIn, password );
		streamIn.close();
	}
	
}
