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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.martus.common.MartusUtilities.PublicInformationInvalidException;
import org.martus.common.MartusUtilities.ServerErrorException;
import org.martus.common.clientside.ClientSideNetworkGateway;
import org.martus.common.clientside.ClientSideNetworkHandlerUsingXmlRpcForNonSSL;
import org.martus.common.clientside.Exceptions.ServerNotAvailableException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.common.crypto.MartusCrypto.InvalidKeyPairFileVersionException;
import org.martus.common.crypto.MartusCrypto.MartusSignatureException;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkResponse;
import org.martus.common.network.NonSSLNetworkAPI;
import org.martus.common.packet.UniversalId;
import org.martus.util.Base64.InvalidBase64Exception;


public class MartusBulletinRetriever
{
	public MartusBulletinRetriever(InputStream keyPair, char[] password) throws CryptoInitializationException, InvalidKeyPairFileVersionException, AuthorizationFailedException, IOException
	{
		security = new MartusSecurity();
		security.readKeyPair(keyPair, password);
	}
	
	public void initalizeServer(String serverIPAddress, String serverPublicKey)
	{
		this.serverPublicKey = serverPublicKey;
		serverNonSSL = new ClientSideNetworkHandlerUsingXmlRpcForNonSSL(serverIPAddress);
		serverSLL = ClientSideNetworkGateway.buildGateway(serverIPAddress, serverPublicKey);
	}

	public boolean isServerAvailable() throws ServerNotConfiguredException 
	{
		if(serverPublicKey==null)
			throw new ServerNotConfiguredException();
		return ClientSideNetworkHandlerUsingXmlRpcForNonSSL.isNonSSLServerAvailable(serverNonSSL);
	}

	public String getServerPublicKey(String serverIPAddress, String serverPublicCode) throws ServerPublicCodeDoesNotMatchException, ServerNotAvailableException, ServerErrorException
	{
		ClientSideNetworkHandlerUsingXmlRpcForNonSSL serverNonSSL = new ClientSideNetworkHandlerUsingXmlRpcForNonSSL(serverIPAddress);
		return getServerPublicKey(serverPublicCode, serverNonSSL);
	}
	
	public List getListOfNewBulletinIds(List bulletinIdsAlreadyRetrieved) throws ServerNotConfiguredException, MartusSignatureException, ServerErrorException
	{
		if(!isServerAvailable())
			return new ArrayList();
		List allBulletins = getAllFieldOfficeBulletinUniversalIds();
		allBulletins.removeAll(bulletinIdsAlreadyRetrieved);
		return allBulletins;
	}
	
	public List getAllFieldOfficeBulletinUniversalIds() throws ServerErrorException, MartusSignatureException
	{
		List allBulletins = new ArrayList();
		Vector fieldOffices = serverSLL.downloadFieldOfficeAccountIds(security, security.getPublicKeyString());
		Vector noTags = new Vector();
		for(int a = 0; a < fieldOffices.size(); ++a)
		{
			String fieldOfficeAccountId = (String)fieldOffices.get(a);
			NetworkResponse response = serverSLL.getSealedBulletinIds(security,fieldOfficeAccountId, noTags);
			allBulletins.addAll(getListOfBulletinUniversalIds(fieldOfficeAccountId, response));

			response = serverSLL.getDraftBulletinIds(security,fieldOfficeAccountId, noTags);
			allBulletins.addAll(getListOfBulletinUniversalIds(fieldOfficeAccountId, response));
		}
		return allBulletins;
	}
	
	public MartusBulletinWrapper getBulletin(UniversalId uid) throws ServerErrorException
	{
		try
		{
			File bulletinZipFile = serverSLL.retrieveBulletin(uid, security, NetworkInterfaceConstants.MAX_CHUNK_SIZE, null);
			MartusBulletinWrapper bulletin = new MartusBulletinWrapper(uid, bulletinZipFile, security);
			return bulletin;
		}
		catch(Exception e)
		{
			throw new ServerErrorException(e.getMessage());
		}
	}
	
	public Vector getListOfBulletinUniversalIds(String fieldOfficeAccountId, NetworkResponse response) throws ServerErrorException
	{
		if(!response.getResultCode().equals(NetworkInterfaceConstants.OK))
			throw new ServerErrorException();
		Vector result = response.getResultVector();
		Vector bulletinIds = new Vector();
		for(int i = 0; i < result.size(); ++i)
		{
			String summary = (String)result.get(i);
			String[] data = summary.split("=");
			bulletinIds.add(UniversalId.createFromAccountAndLocalId(fieldOfficeAccountId,data[0]));
		}
		return bulletinIds;
	}

	public class ServerNotConfiguredException extends Exception{};
	public class ServerPublicCodeDoesNotMatchException extends Exception {};
	
	public String getServerPublicKey(String serverPublicCode, NonSSLNetworkAPI serverNonSSL) throws ServerNotAvailableException, ServerPublicCodeDoesNotMatchException, ServerErrorException
	{
		String ServerPublicKey;
		try
		{
			ServerPublicKey = serverNonSSL.getServerPublicKey(security);
			String serverPublicCodeToTest = MartusSecurity.computePublicCode(ServerPublicKey);
			
			if(!MartusCrypto.removeNonDigits(serverPublicCode).equals(serverPublicCodeToTest))
				throw new ServerPublicCodeDoesNotMatchException();
			return ServerPublicKey;
		}
		catch(PublicInformationInvalidException e)
		{
			throw new ServerErrorException();
		}
		catch(InvalidBase64Exception e)
		{
			e.printStackTrace();
			throw new ServerErrorException();
		}
	}
	
	public void setSSLServerForTests(ClientSideNetworkGateway sslServerToUse)
	{
		serverSLL = sslServerToUse;
	}

	public NonSSLNetworkAPI serverNonSSL;
	private ClientSideNetworkGateway serverSLL;
	private MartusSecurity security;
	private String serverPublicKey;
}
