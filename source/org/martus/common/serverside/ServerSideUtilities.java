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
package org.martus.common.serverside;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;

import org.martus.util.UnicodeReader;


public class ServerSideUtilities
{
	public static void writeSyncFile(File syncFile, String whoCallThisMethod) 
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(syncFile);
			out.write(0);
			out.close();
		} 
		catch(Exception e) 
		{
			System.out.println(whoCallThisMethod+": " + e);
			System.exit(6);
		}
	}


	public static char[] getPassphraseFromConsole(File triggerDirectory, String whoCallThisMethod)
	{
		System.out.print("Enter passphrase: ");
		System.out.flush();
	
		File waitingFile = new File(triggerDirectory, "waiting");
		waitingFile.delete();
		ServerSideUtilities.writeSyncFile(waitingFile, whoCallThisMethod);
	
		String passphrase = null;
		try
		{
			BufferedReader reader = new BufferedReader(new UnicodeReader(System.in));
			//TODO security issue here password is a string
			passphrase = reader.readLine();
		}
		catch(Exception e)
		{
			System.out.println(whoCallThisMethod+": " + e);
			System.exit(3);
		}
		return passphrase.toCharArray();
	}	
	
}
