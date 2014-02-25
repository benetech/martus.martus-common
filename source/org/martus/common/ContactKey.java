/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
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
public class ContactKey extends ExternalPublicKey
{
	public ContactKey(String publicKey)
	{
		this(publicKey, "");
	}

	public ContactKey(String publicKey, String label)
	{
		super(publicKey, label);
	}
	
	public boolean getCanSendTo()
	{
		return canSendTo;
	}

	public void setCanSendTo(boolean canSendTo)
	{
		this.canSendTo = canSendTo;
		if(!canSendTo)
			this.sendToByDefault = false;
	}

	public boolean getCanReceiveFrom()
	{
		return canReceiveFrom;
	}

	public void setCanReceiveFrom(boolean canReceiveFrom)
	{
		this.canReceiveFrom = canReceiveFrom;
	}

	public boolean getSendToByDefault()
	{
		return sendToByDefault;
	}

	public void setSendToByDefault(boolean sendToByDefault)
	{
		this.sendToByDefault = sendToByDefault;
	}

	private boolean canSendTo;
	private boolean sendToByDefault;
	private boolean canReceiveFrom;
	
	final static public int NOT_VERIFIED = 0;
	final static public int VERIFIED_VISUALLY = 1;
	final static public int VERIFIED_ENTERED_MANUALLY = 2;
}

