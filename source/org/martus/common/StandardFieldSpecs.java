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

import org.martus.common.bulletin.BulletinConstants;


public class StandardFieldSpecs
{

	public static FieldSpec[] getDefaultPublicFieldSpecs()
	{
		if(defaultPublicFieldSpecs == null)
		{
			defaultPublicFieldSpecs = new FieldSpec[] 
			{
				FieldSpec.createStandardField(BulletinConstants.TAGLANGUAGE, FieldSpec.TYPE_LANGUAGE),
				FieldSpec.createStandardField(BulletinConstants.TAGAUTHOR, FieldSpec.TYPE_NORMAL),
				FieldSpec.createStandardField(BulletinConstants.TAGORGANIZATION, FieldSpec.TYPE_NORMAL),
				FieldSpec.createStandardField(BulletinConstants.TAGTITLE, FieldSpec.TYPE_NORMAL),
				FieldSpec.createStandardField(BulletinConstants.TAGLOCATION, FieldSpec.TYPE_NORMAL), 
				FieldSpec.createStandardField(BulletinConstants.TAGKEYWORDS, FieldSpec.TYPE_NORMAL),
				FieldSpec.createStandardField(BulletinConstants.TAGEVENTDATE, FieldSpec.TYPE_DATERANGE),
				FieldSpec.createStandardField(BulletinConstants.TAGENTRYDATE, FieldSpec.TYPE_DATE),
				FieldSpec.createStandardField(BulletinConstants.TAGSUMMARY, FieldSpec.TYPE_MULTILINE),
				FieldSpec.createStandardField(BulletinConstants.TAGPUBLICINFO, FieldSpec.TYPE_MULTILINE),
			};
		}
		
		return (FieldSpec[])defaultPublicFieldSpecs.clone();
	}

	public static FieldSpec[] getDefaultPrivateFieldSpecs()
	{
		if(defaultPrivateFieldSpecs == null)
		{
			defaultPrivateFieldSpecs = new FieldSpec[]
			{
				FieldSpec.createStandardField(BulletinConstants.TAGPRIVATEINFO, FieldSpec.TYPE_MULTILINE),
			};
		}
		
		return (FieldSpec[])defaultPrivateFieldSpecs.clone();
	}

	public static int getStandardType(String tag)
	{
		FieldSpec thisSpec = findStandardFieldSpec(tag);
		if(thisSpec == null)
			return FieldSpec.TYPE_UNKNOWN;
		return thisSpec.getType();
	}

	public static boolean isCustomFieldTag(String tag)
	{
		FieldSpec thisSpec = findStandardFieldSpec(tag);
		if(thisSpec == null)
			return true;
		return false;
	}
	
	public static boolean isStandardFieldTag(String tag)
	{
		if (findStandardFieldSpec(tag) != null)
			return true;
		return false;	
	}
	
	private static FieldSpec findStandardFieldSpec(String tag)
	{
		FieldSpec[] publicSpecs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		for(int i=0; i < publicSpecs.length; ++i)
			if(publicSpecs[i].getTag().equals(tag))
			{
				return publicSpecs[i];
			}
				
		FieldSpec[] privateSpecs = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		for(int i=0; i < privateSpecs.length; ++i)
			if(privateSpecs[i].getTag().equals(tag))
			{
				return privateSpecs[i];
			}
				
		return null;
	}
	
	
	private static FieldSpec[] defaultPublicFieldSpecs;
	private static FieldSpec[] defaultPrivateFieldSpecs;
}
