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
package org.martus.common.clientside;

import java.awt.Dimension;
import javax.swing.JTextField;
import org.martus.swing.UiLanguageDirection;
import org.martus.util.language.LanguageOptions;


public class UiTextField extends JTextField
{
	public UiTextField()
	{
		super();
		initalizeOrientation();
	}
	
	public UiTextField (int columns)
	{
		super(columns);
		initalizeOrientation();
	}
	
	public UiTextField(String text)
	{
		super(text);
		initalizeOrientation();
	}

	private void initalizeOrientation()
	{
		setComponentOrientation(UiLanguageDirection.getComponentOrientation());
		setHorizontalAlignment(UiLanguageDirection.getHorizontalAlignment());
	}
	
	/* 
	 * NOTE: This is a horrible hack to work around the fact that a JTextArea
	 * seems to completely ignore any calls to setBorder or setMargin.
	 * We need to add some space to the bottom of the field to avoid Arabic
	 * (and even English) characters from being chopped off. 
	 * The number of pixels is arbitrary and may need to be adjusted.
	 */
	public Dimension getPreferredSize()
	{
		if(!LanguageOptions.needsLanguagePadding())
			return super.getPreferredSize();

		final int EXTRA_PIXELS = 14;
		Dimension d = super.getPreferredSize();
		d.setSize(d.getWidth(), d.getHeight() + EXTRA_PIXELS);
		return d;
	}
}
