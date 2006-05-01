/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.martus.common.fieldspec;

public class BulletinFieldSpecs
{
	public BulletinFieldSpecs()
	{
	}
	
	public FieldSpec[] getTopSectionSpecs()
	{
		return topSectionSpecs;
	}

	public FieldSpec[] getBottomSectionSpecs()
	{
		return bottomSectionSpecs;
	}
	
	public void setTopSectionSpecs(FieldSpec[] topSectionSpecsToUse)
	{
		topSectionSpecs = topSectionSpecsToUse;
	}

	public void setBottomSectionSpecs(FieldSpec[] bottomSectionSpecsToUse)
	{
		bottomSectionSpecs = bottomSectionSpecsToUse;
	}

	private FieldSpec[] topSectionSpecs;
	private FieldSpec[] bottomSectionSpecs ;
}
