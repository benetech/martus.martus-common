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
package org.martus.common.clientside;

import java.util.Vector;
import org.martus.common.MartusUtilities;
import org.martus.common.MartusUtilities.PublicInformationInvalidException;
import org.martus.common.MartusUtilities.ServerErrorException;
import org.martus.common.clientside.Exceptions.ServerNotAvailableException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkInterfaceForNonSSL;
import org.martus.common.network.NetworkResponse;


public class ServerUtilities
{

	public static String getServerPublicKey(NetworkInterfaceForNonSSL server, MartusCrypto verifier) throws ServerNotAvailableException, PublicInformationInvalidException
	{
		if(server.ping() == null)
			throw new ServerNotAvailableException();
	
		Vector serverInformation = server.getServerInformation();
		if(serverInformation == null)
			throw new ServerNotAvailableException();
	
		if(serverInformation.size() != 3)
			throw new PublicInformationInvalidException();
	
		String accountId = (String)serverInformation.get(1);
		String sig = (String)serverInformation.get(2);
		MartusUtilities.validatePublicInfo(accountId, sig, verifier);
		return accountId;
	}

	public static Vector downloadFieldOfficeAccountIds(ClientSideNetworkGateway networkInterfaceGateway, MartusCrypto security, String myAccountId) throws ServerErrorException
	{
		try
		{
			NetworkResponse response = networkInterfaceGateway.getFieldOfficeAccountIds(security, myAccountId);
			String resultCode = response.getResultCode();
			if(!resultCode.equals(NetworkInterfaceConstants.OK))
				throw new ServerErrorException(resultCode);
			return response.getResultVector();
		}
		catch(MartusCrypto.MartusSignatureException e)
		{
			System.out.println("ServerUtilities.getFieldOfficeAccounts: " + e);
			throw new ServerErrorException();
		}
	}
}
