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
package org.martus.common.utilities;

import java.util.Vector;
import org.martus.common.fieldspec.CustomFieldError;

public class ImportXmlBulletin
{
	public static void main(String[] args)
	{
	}
	
	public String getValidationErrorMessage(Vector errors)
	{
		StringBuffer validationErrorMessages = new StringBuffer();
		for(int i = 0; i<errors.size(); ++i)
		{
			CustomFieldError thisError = (CustomFieldError)errors.get(i);
			StringBuffer thisErrorMessage = new StringBuffer(thisError.getCode());
			thisErrorMessage.append(" : ");
			thisErrorMessage.append(thisError.getType());
			thisErrorMessage.append(" : ");
			thisErrorMessage.append(thisError.getTag());
			thisErrorMessage.append(" : ");
			thisErrorMessage.append(thisError.getLabel());
			validationErrorMessages.append(thisErrorMessage);
			validationErrorMessages.append('\n');
		}
		validationErrorMessages.append("\n\nTo see a list of the errors, please run Martus go to Options, Custom Fields and change <CustomFields> to <xCustomFields> and press OK.");
		return validationErrorMessages.toString();  
	}
	
}
