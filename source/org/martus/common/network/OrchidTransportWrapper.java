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

import java.io.File;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

import javax.net.ssl.TrustManager;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.martus.common.MartusLogger;
import org.martus.common.MartusUtilities;
import org.martus.common.ProgressMeterInterface;

import com.subgraph.orchid.TorClient;
import com.subgraph.orchid.TorInitializationListener;
import com.subgraph.orchid.xmlrpc.OrchidXmlRpcTransportFactory;


public class OrchidTransportWrapper extends TransportWrapperWithOfflineMode
{
	public static OrchidTransportWrapper createWithoutPersistentStore() throws Exception
	{
		return create(new MartusOrchidDirectoryStore());
	}
	
	public static OrchidTransportWrapper create(MartusOrchidDirectoryStore storeToUse) throws Exception
	{
		return new OrchidTransportWrapper(storeToUse);
	}
	
	private OrchidTransportWrapper(MartusOrchidDirectoryStore storeToUse) throws Exception
	{
		isTorActive = new SimpleBooleanProperty();
		isTorReady = false;

		createRealTorClient(storeToUse);
	}
	
	public void setTorDataDirectory(File directory)
	{
		tor.getConfig().setDataDirectory(directory);
	}

	public void setProgressMeter(ProgressMeterInterface initializationProgressMeterToUse)
	{
		progressMeter = initializationProgressMeterToUse;
	}

	public void startTor()
	{
		isTorActive.setValue(true);
		updateStatus();
		if(!isTorReady)
			new TorInitializer().start();
	}

	public void startTorInSameThread()
	{
		isTorActive.setValue(true);
		if(!isTorReady)
			getTor().start();
	}
	
	public void stopTor()
	{
		isTorActive.setValue(false);
		updateStatus();
	}
	
	protected class TorInitializer extends Thread
	{
		@Override
		public void run()
		{
			getTor().start();
		}

	}
	
	protected TorClient getTor()
	{
		return tor;
	}
	
	public Property <Boolean> getIsTorActiveProperty()
	{
		return isTorActive;
	}
	
	@Override
	public boolean isTorEnabled()
	{
		return isTorActive.getValue();
	}
	
	@Override
	public boolean isReady()
	{
		if(!super.isReady())
			return false;
		
		if(!isTorActive.getValue())
			return true;
		
		return isTorReady;
	}

	public void updateStatus()
	{
		if(progressMeter == null)
			return;
		
		if(isTorActive.getValue())
		{
			if(isTorReady)
			{
				progressMeter.setStatusMessage("TorStatusActive");
				progressMeter.updateProgressMeter(100, 100);		
				progressMeter.hideProgressMeter();
			}
			else
			{
				progressMeter.setStatusMessage("TorStatusInitializing");
			}
		}
		else
		{
			progressMeter.setStatusMessage("TorStatusDisabled");
			progressMeter.updateProgressMeter(100, 100);		
			progressMeter.hideProgressMeter();
		}
	}
	
	@Override
	public XmlRpcTransportFactory createTransport(XmlRpcClient client, TrustManager tm)	throws Exception 
	{
		if(!isTorActive.getValue())
			return null;
		
		if(!isReady())
			throw new RuntimeException("Tor not initialized yet");
		
		return createRealTorTransportFactory(client, tm);
	}

	void updateProgress(String message, int percent)
	{
		MartusLogger.log("Tor initialization: " + percent + "% - " + message);
		if(progressMeter != null)
		{
			progressMeter.setStatusMessage(message);
			progressMeter.updateProgressMeter(percent, 100);
			updateStatus();
		}
	}

	void updateProgressComplete()
	{
		MartusLogger.log("Tor initialization complete");
		isTorReady = true;
		if(progressMeter != null)
			progressMeter.updateProgressMeter(100, 100);		
		updateStatus();
	}

	private void createRealTorClient(MartusOrchidDirectoryStore storeToUse) throws Exception
	{
		tor = new TorClient(storeToUse);

		class TorInitializationHandler implements TorInitializationListener
		{
			@Override
			public void initializationProgress(String message, int percent)
			{
				updateProgress(message, percent);
			}
			
			@Override
			public void initializationCompleted()
			{
				updateProgressComplete();
			}

		}
		
		tor.addInitializationListener(new TorInitializationHandler());
	}
	
	private XmlRpcTransportFactory createRealTorTransportFactory(XmlRpcClient client, TrustManager tm) throws Exception
	{
		XmlRpcTransportFactory factory = null;
		factory = new OrchidXmlRpcTransportFactory(client, tor, MartusUtilities.createSSLContext(tm));
		return factory;
	}
	

	private TorClient tor;
	private ProgressMeterInterface progressMeter;

	private Property <Boolean> isTorActive;
	private boolean isTorReady;
}
