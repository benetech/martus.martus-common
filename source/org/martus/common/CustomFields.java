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

import java.util.Vector;

import org.martus.common.bulletin.BulletinConstants;


public class CustomFields
{

	static public FieldSpec[] addFieldSpec(FieldSpec[] existingFieldSpecs, FieldSpec newFieldSpec)
	{
		int oldCount = existingFieldSpecs.length;
		FieldSpec[] tempFieldTags = new FieldSpec[oldCount + 1];
		System.arraycopy(existingFieldSpecs, 0, tempFieldTags, 0, oldCount);
		tempFieldTags[oldCount] = newFieldSpec;
		return tempFieldTags;
	}

	public static FieldSpec[] getDefaultPublicFieldSpecs()
	{
		if(defaultPublicFieldSpecs == null)
		{
			defaultPublicFieldSpecs = new FieldSpec[] 
			{
				new FieldSpec(BulletinConstants.TAGLANGUAGE, FieldSpec.TYPE_CHOICE),
				new FieldSpec(BulletinConstants.TAGAUTHOR, FieldSpec.TYPE_NORMAL),
				new FieldSpec(BulletinConstants.TAGORGANIZATION, FieldSpec.TYPE_NORMAL),
				new FieldSpec(BulletinConstants.TAGTITLE, FieldSpec.TYPE_NORMAL),
				new FieldSpec(BulletinConstants.TAGLOCATION, FieldSpec.TYPE_NORMAL), 
				new FieldSpec(BulletinConstants.TAGKEYWORDS, FieldSpec.TYPE_NORMAL),
				new FieldSpec(BulletinConstants.TAGEVENTDATE, FieldSpec.TYPE_DATERANGE),
				new FieldSpec(BulletinConstants.TAGENTRYDATE, FieldSpec.TYPE_DATE),
				new FieldSpec(BulletinConstants.TAGSUMMARY, FieldSpec.TYPE_MULTILINE),
				new FieldSpec(BulletinConstants.TAGPUBLICINFO, FieldSpec.TYPE_MULTILINE),
			};
		}
		
		return defaultPublicFieldSpecs;
	}

	public static FieldSpec[] getDefaultPrivateFieldSpecs()
	{
		if(defaultPrivateFieldSpecs == null)
		{
			defaultPrivateFieldSpecs = new FieldSpec[]
			{
				new FieldSpec(BulletinConstants.TAGPRIVATEINFO, FieldSpec.TYPE_MULTILINE),
			};
		}
		
		return defaultPrivateFieldSpecs;
	}

	public static Vector getDefaultPublicFieldTags()
	{
		Vector tags = new Vector();
		FieldSpec[] defaultFields = CustomFields.getDefaultPublicFieldSpecs();
		for (int i = 0; i < defaultFields.length; i++)
			tags.add(defaultFields[i].getTag());
		return tags;
	}

	public static int getStandardType(String tag)
	{
		FieldSpec[] publicSpecs = CustomFields.getDefaultPublicFieldSpecs();
		for(int i=0; i < publicSpecs.length; ++i)
			if(publicSpecs[i].getTag().equals(tag))
				return publicSpecs[i].getType();
				
		FieldSpec[] privateSpecs = CustomFields.getDefaultPrivateFieldSpecs();
		for(int i=0; i < privateSpecs.length; ++i)
			if(privateSpecs[i].getTag().equals(tag))
				return privateSpecs[i].getType();
				
		return FieldSpec.TYPE_UNKNOWN;
	}

	public static boolean isCustomFieldTag(String tag)
	{
		FieldSpec[] publicSpecs = CustomFields.getDefaultPublicFieldSpecs();
		for(int i=0; i < publicSpecs.length; ++i)
			if(publicSpecs[i].getTag().equals(tag))
				return false;
				
		FieldSpec[] privateSpecs = CustomFields.getDefaultPrivateFieldSpecs();
		for(int i=0; i < privateSpecs.length; ++i)
			if(privateSpecs[i].getTag().equals(tag))
				return false;
				
		return true;
	}
	
	
	private static FieldSpec[] defaultPublicFieldSpecs;
	private static FieldSpec[] defaultPrivateFieldSpecs;
}
