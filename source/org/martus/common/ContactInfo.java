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

package org.martus.common;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.util.Base64;


public class ContactInfo
{

	public static Vector encodeContactInfoVector(Vector unencodedContactInfo) throws UnsupportedEncodingException
	{
		Vector encoded = new Vector();
		encoded.add(NetworkInterfaceConstants.BASE_64_ENCODED);
		encoded.add(unencodedContactInfo.get(0));
		encoded.add(unencodedContactInfo.get(1));
		int start = 2;
		int i = start;
		int stringsToEncode = ((Integer)(unencodedContactInfo.get(1))).intValue();
		for(; i < start + stringsToEncode ; ++i)
			encoded.add(Base64.encode((String)unencodedContactInfo.get(i)));
		encoded.add(unencodedContactInfo.get(i));
		return encoded;
	}
	
	private static boolean isEncoded(Vector possiblyEncodedContactInfo)
	{
		return possiblyEncodedContactInfo.get(0).equals(NetworkInterfaceConstants.BASE_64_ENCODED);
	}
	
	static public Vector decodeContactInfoVectorIfNecessary(Vector possiblyEncodedContactInfo) throws UnsupportedEncodingException, Base64.InvalidBase64Exception
	{
		if (!isEncoded(possiblyEncodedContactInfo))
			return possiblyEncodedContactInfo;
		Vector decodedContactInfo = new Vector();
		decodedContactInfo.add(possiblyEncodedContactInfo.get(1));
		decodedContactInfo.add(possiblyEncodedContactInfo.get(2));
		int start = 3;
		int i = start;
		int stringsToDecode = ((Integer)(possiblyEncodedContactInfo.get(2))).intValue();
		for(; i < start + stringsToDecode ; ++i)
		{	
			String encodedData = (String)possiblyEncodedContactInfo.get(i);
			decodedContactInfo.add(new String(Base64.decode(encodedData),"UTF-8"));
		}
		decodedContactInfo.add(possiblyEncodedContactInfo.get(i));
		return decodedContactInfo;
		
	}
}
