/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.common.fieldspec;

import org.martus.common.FieldSpecCollection;

public class BulletinFieldSpecs extends FormTemplate
{
	public BulletinFieldSpecs()
	{
	}
	
	public FieldSpecCollection getTopSectionSpecs()
	{
		return getTopFields();
	}

	public FieldSpecCollection getBottomSectionSpecs()
	{
		return getBottomFields();
	}
	
	public String getTitleOfSpecs()
	{
		return getTitle();
	}
	
	public String getDescriptionOfSpecs()
	{
		return getDescription();
	}
	
	public void setTopSectionSpecs(FieldSpec[] topSectionSpecsToUse)
	{
		setTopSectionSpecs(new FieldSpecCollection(topSectionSpecsToUse));
	}
	
	public void setTopSectionSpecs(FieldSpecCollection topSectionSpecsToUse)
	{
		setTopFields(topSectionSpecsToUse);
	}

	public void setBottomSectionSpecs(FieldSpec[] bottomSectionSpecsToUse)
	{
		setBottomSectionSpecs(new FieldSpecCollection(bottomSectionSpecsToUse));
	}
	
	public void setBottomSectionSpecs(FieldSpecCollection bottomSectionSpecsToUse)
	{
		setBottomFields(bottomSectionSpecsToUse);
	}
	
	public void setTitleOfSpecs(String titleToUse)
	{
		setTitle(titleToUse);
	}

	public void setDescriptionOfSpecs(String descriptionToUse)
	{
		setDescriptionOfSpecs(descriptionToUse);
	}
}
