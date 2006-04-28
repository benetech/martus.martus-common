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

import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;

public class FieldCollectionForTesting extends FieldCollection
{
	public static FieldCollection extendDefaultPublicFields(FieldSpec extraFieldSpec)
	{
		return extendDefaultPublicFields(new FieldSpec[] {extraFieldSpec});
	}

	public static FieldCollection extendDefaultPublicFields(FieldSpec[] extraFieldSpecs)
	{
		FieldSpec[] defaultFields = StandardFieldSpecs.getDefaultTopSectionFieldSpecs();
		FieldSpec[] allFields = new FieldSpec[defaultFields.length + extraFieldSpecs.length];
		System.arraycopy(defaultFields, 0, allFields, 0, defaultFields.length);
		System.arraycopy(extraFieldSpecs, 0, allFields, defaultFields.length, extraFieldSpecs.length);
		return new FieldCollection(allFields);
	}

	private FieldCollectionForTesting()
	{
		super(new FieldSpec[0]);
	}
}
