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

import java.io.IOException;
import java.io.InputStream;
import org.martus.common.clientside.Exceptions;
import org.martus.common.clientside.Exceptions.ServerNotAvailableException;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.common.crypto.MartusCrypto.InvalidKeyPairFileVersionException;


public class MartusBulletinRetriever
{
	public MartusBulletinRetriever(InputStream keyPair, char[] password) throws CryptoInitializationException, InvalidKeyPairFileVersionException, AuthorizationFailedException, IOException
	{
		security = new MartusSecurity();
		security.readKeyPair(keyPair, password);
	}
	
	public void setServer(String serverIPAddress, String serverPublicCode)
	{
		this.serverIPAddress = serverIPAddress;
		this.serverPublicCode = serverPublicCode;
	}
	
	
	public class ServerNotConfiguredException extends Exception{};

	public boolean pingServer() throws ServerNotAvailableException, ServerNotConfiguredException 
	{
		if(serverIPAddress == null || serverPublicCode==null)
			throw new ServerNotConfiguredException();
		throw new Exceptions.ServerNotAvailableException();
	}
	
	private MartusSecurity security;
	private String serverIPAddress;
	private String serverPublicCode;
}
