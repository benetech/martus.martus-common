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

package org.martus.common.crypto;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MartusDirectCryptoKeyPair extends MartusKeyPair
{

	public PrivateKey getPrivateKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public PublicKey getPublicKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getPublicKeyString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void clear()
	{
		// TODO Auto-generated method stub

	}

	public boolean hasKeyPair()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isKeyPairValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void createRSA(int publicKeyBits)
			throws Exception
	{
		// TODO Auto-generated method stub

	}

	public byte[] getKeyPairData() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setFromData(byte[] data) throws Exception
	{
		// TODO Auto-generated method stub

	}

	public byte[] encryptBytes(byte[] bytesToEncrypt,
			String recipientPublicKeyX509) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] decryptBytes(byte[] bytesToDecrypt) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getDigestOfPartOfPrivateKey() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
