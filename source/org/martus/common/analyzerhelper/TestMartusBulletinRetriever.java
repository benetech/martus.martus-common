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
package org.martus.common.analyzerhelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.martus.common.analyzerhelper.MartusBulletinRetriever.ServerErrorException;
import org.martus.common.analyzerhelper.MartusBulletinRetriever.ServerPublicCodeDoesNotMatchException;
import org.martus.common.clientside.ClientSideNetworkHandlerUsingXmlRpcForNonSSL;
import org.martus.common.clientside.Exceptions.ServerNotAvailableException;
import org.martus.common.clientside.test.NoServerNetworkInterfaceForNonSSLHandler;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkInterfaceForNonSSL;
import org.martus.util.Base64;
import org.martus.util.TestCaseEnhanced;


public class TestMartusBulletinRetriever extends TestCaseEnhanced
{
	public TestMartusBulletinRetriever(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
	  	super.setUp();

	  	if(security == null)
	  	{
			security = new MartusSecurity();
			security.createKeyPair(512);
			serverSecurity = new MartusSecurity();
			serverSecurity.createKeyPair(512);
	  	}
		streamOut = new ByteArrayOutputStream();
		security.writeKeyPair(streamOut, password);
		streamOut.close();
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
	
	private class TestServerNetworkInterfaceForNonSSLHandler implements NetworkInterfaceForNonSSL
	{

		public String ping()
		{
			return ClientSideNetworkHandlerUsingXmlRpcForNonSSL.MARTUS_SERVER_PING_RESPONSE;
		}

		public Vector getServerInformation()
		{
			Vector result = new Vector();
			try
			{
				byte[] publicKeyBytes = Base64.decode(publicKeyString);
				ByteArrayInputStream in = new ByteArrayInputStream(publicKeyBytes);
				byte[] sigBytes = serverSecurity.createSignatureOfStream(in);
				
				result.add(NetworkInterfaceConstants.OK);
				result.add(publicKeyString);
				result.add(Base64.encode(sigBytes));
			}
			catch(Exception e)
			{
			}
			return result;
		}
		
		public String publicKeyString;
	}
	
	public void testPingServer() throws Exception
	{
		ByteArrayInputStream streamIn = new ByteArrayInputStream(streamOut.toByteArray());
		MartusBulletinRetriever retriever = new MartusBulletinRetriever(streamIn, password );
		streamIn.close();
		
		try
		{
			retriever.pingServer();
			fail("server hasn't been configured yet");
		}
		catch(MartusBulletinRetriever.ServerNotConfiguredException expected)
		{
		}
		
		retriever.initalizeServer("1.2.3.4", "some random public key");
		retriever.serverNonSSL = new NoServerNetworkInterfaceForNonSSLHandler();
		assertFalse(retriever.pingServer());
		retriever.serverNonSSL = new TestServerNetworkInterfaceForNonSSLHandler();
		assertTrue(retriever.pingServer());
	}
	
	public void testGetServerPublicKey() throws Exception
	{
		
		ByteArrayInputStream streamIn = new ByteArrayInputStream(streamOut.toByteArray());
		MartusBulletinRetriever retriever = new MartusBulletinRetriever(streamIn, password );
		streamIn.close();
		NetworkInterfaceForNonSSL noServer = new NoServerNetworkInterfaceForNonSSLHandler();
		try
		{
			retriever.getServerPublicKey("Some Random code", noServer);
			fail("Server exists?");
		}
		catch(ServerNotAvailableException expected)
		{
		}
		
		TestServerNetworkInterfaceForNonSSLHandler testServerForNonSSL = new TestServerNetworkInterfaceForNonSSLHandler();
		testServerForNonSSL.publicKeyString = "some invalid keystring";
		try
		{
			retriever.getServerPublicKey("Some Random code", testServerForNonSSL);
			fail("Invalid public key strings should throw an exception");
		}
		catch(ServerErrorException expected)
		{
		}
		String serverPublicKeyString = serverSecurity.getPublicKeyString();
		testServerForNonSSL.publicKeyString = serverPublicKeyString;
		try
		{
			retriever.getServerPublicKey("Invalid code", testServerForNonSSL);
			fail("Incorrect public code.");
		}
		catch(ServerPublicCodeDoesNotMatchException expected)
		{
		}
		retriever.getServerPublicKey(MartusCrypto.computePublicCode(serverPublicKeyString), testServerForNonSSL);
		retriever.getServerPublicKey(MartusCrypto.computeFormattedPublicCode(serverPublicKeyString), testServerForNonSSL);
	}
	
	private static MartusSecurity security;
	static MartusSecurity serverSecurity;
	private static char[] password = "the password".toCharArray();
	private ByteArrayOutputStream streamOut;

}
