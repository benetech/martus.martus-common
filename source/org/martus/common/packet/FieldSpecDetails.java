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
package org.martus.common.packet;

import java.util.Vector;

import org.martus.common.MartusUtilities;
import org.martus.common.MartusXml;


public class FieldSpecDetails
{
	public FieldSpecDetails()
	{
		columns = new Vector();
	}
	
	public int getColumnCount()
	{
		return columns.size();
	}
	
	public void addColumnLabel(String label)
	{
		columns.add(label);
	}
	
	public String getColumnLabel(int column)
	{
		return (String)columns.get(column);
	}
	
	public String toXml()
	{
		String xml = new String();
		xml += MartusXml.getTagStart(TAG_FIELDSPEC_DETAILS) + MartusXml.newLine;
		xml += MartusXml.getTagStart(TAG_GRIDSPEC_DETAILS) + MartusXml.newLine;
		for(int i = 0; i < columns.size(); ++i)
		{
			xml += MartusXml.getTagStart(TAG_GRIDSPEC_COLUMN) + MartusXml.newLine;
			xml += MartusXml.getTagStart(TAG_GRIDSPEC_LABEL);
			xml += MartusUtilities.getXmlEncoded(getColumnLabel(i));
			xml += MartusXml.getTagEnd(TAG_GRIDSPEC_LABEL);
			xml += MartusXml.getTagEnd(TAG_GRIDSPEC_COLUMN);
		}
		xml += MartusXml.getTagEnd(TAG_GRIDSPEC_DETAILS);
		xml += MartusXml.getTagEnd(TAG_FIELDSPEC_DETAILS);
		return xml;
	}
	
	Vector columns;
	public final static String TAG_FIELDSPEC_DETAILS = "FieldSpecDetails";
	public final static String TAG_GRIDSPEC_DETAILS = "GridSpecDetails";
	public final static String TAG_GRIDSPEC_COLUMN = "Column";
	public final static String TAG_GRIDSPEC_LABEL = "Label";
	
}
