/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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

import java.util.Arrays;

import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeMessage;
import org.martus.util.TestCaseEnhanced;

public class TestFieldCollectionMemoryUsage extends TestCaseEnhanced
{
	public TestFieldCollectionMemoryUsage(String name)
	{
		super(name);
	}

	public void testMemoryUsage()
	{
		byte[] bigArray = new byte[30000000];
		bigArray[0] = 0;
		
		int bulletinCount = 1000;
		int fieldCount = 10;
		int fieldSize = 10000;
		
		FieldCollection[] packets = new FieldCollection[bulletinCount];
		for(int b = 0; b < packets.length; ++b)
		{
			FieldSpecCollection fields = new FieldSpecCollection(fieldCount);
			for(int f = 0; f < fields.size(); ++f)
			{
				fields.set(f, createLongMessageFieldSpec(fieldSize));
			}
			packets[b] = new FieldCollection(fields);
		}
		bigArray = null;
	}

	private FieldSpec createLongMessageFieldSpec(int length)
	{
		char[] labelChars = new char[length];
		Arrays.fill(labelChars, 'x');
		String longLabel = new String(labelChars);
		FieldSpec longMessage = FieldSpec.createFieldSpec(longLabel, new FieldTypeMessage());
		return longMessage;
	}
}
