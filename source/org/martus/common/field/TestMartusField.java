/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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

package org.martus.common.field;

import org.martus.common.fieldspec.FieldSpec;
import org.martus.util.TestCaseEnhanced;


public class TestMartusField extends TestCaseEnhanced
{
	public TestMartusField(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		FieldSpec spec = FieldSpec.createCustomField("tag", "label", FieldSpec.TYPE_NORMAL);
		MartusField f = new MartusField(spec);
		assertEquals("wrong tag?", spec.getTag(), f.getTag());
		assertEquals("wrong label?", spec.getLabel(), f.getLabel());
		assertEquals("wrong type?", spec.getType(), f.getType());
		assertEquals("not initially blank?", "", f.getData());
		
		assertEquals("wrong spec?", spec.toString(), f.getFieldSpec().toString());
		
		final String sampleData = "test data"; 
		f.setData(sampleData);
		assertEquals("didn't set data?", sampleData, f.getData());
	}
}
