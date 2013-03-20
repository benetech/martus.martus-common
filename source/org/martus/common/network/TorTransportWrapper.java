/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2013, Beneficent
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
package org.martus.common.network;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;


public class TorTransportWrapper
{
	public TorTransportWrapper()
	{
//		createRealTorClient();
	}

	public void start()
	{
//		startRealTorClient();
	}

	public XmlRpcTransportFactory createTransport(XmlRpcClient client, SimpleX509TrustManager tm)	throws Exception 
	{
		XmlRpcTransportFactory transportFactory = null;
//		transportFactory = createRealTorTransportFactory(client, tm);
		return transportFactory;
	}

//	private void createRealTorClient()
//	{
//		tor = new TorClient();
//	}
//	
//	private void startRealTorClient()
//	{
//		tor.start();
//	}
//
//	private XmlRpcTransportFactory createRealTorTransportFactory(XmlRpcClient client, SimpleX509TrustManager tm) throws Exception
//	{
//		return new JTorXmlRpcTransportFactory(client, tor, MartusUtilities.createSSLContext(tm));
//	}
//
//	private TorClient tor;
}
