/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.martus.swing.Utilities;



public class UiVirtualKeyboard
{

	public UiVirtualKeyboard(UiBasicLocalization localization, VirtualKeyboardHandler uiHandler, UiPasswordField passwordFieldToUse)
	{
		handler = uiHandler;
		passwordField = passwordFieldToUse;
		String keys = localization.getFieldLabel("VirtualKeyboardKeys");
		space = localization.getFieldLabel("VirtualKeyboardSpace");
		delete = localization.getFieldLabel("VirtualKeyboardBackSpace");

		UpdateHandler updateHandler = new UpdateHandler();

		Container vKeyboard = new Container();
		int columns = 13;
		if(Utilities.isMacintosh())
			columns = 10;
		int rows = keys.length() / columns;
		vKeyboard.setLayout(new GridLayout(rows, columns));
		for(int i = 0; i < keys.length(); ++i)
		{
			JButton key = new JButton(keys.substring(i,i+1));
			key.setFocusPainted(false);
			key.addActionListener(updateHandler);
			vKeyboard.add(key);
		}

		Container bottomRow = new Container();
		bottomRow.setLayout(new GridLayout(1,3));
		JButton spaceButton = new JButton(space);
		spaceButton.addActionListener(updateHandler);
		JButton deleteButton = new JButton(delete);
		deleteButton.addActionListener(updateHandler);
		bottomRow.add(spaceButton);
		bottomRow.add(new JLabel(""));
		bottomRow.add(deleteButton);

		Container entireKeyboard = new Container();
		entireKeyboard.setLayout(new BorderLayout());
		entireKeyboard.add(vKeyboard, BorderLayout.NORTH);
		entireKeyboard.add(bottomRow, BorderLayout.SOUTH);

		JPanel virtualKeyboard = new JPanel();
		virtualKeyboard.add(entireKeyboard);
		virtualKeyboard.setBorder(new LineBorder(Color.black, 1));

		handler.addKeyboard(virtualKeyboard);
	}

	public class UpdateHandler extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			JButton buttonPressed = (JButton)(e.getSource());
			String passChar = buttonPressed.getText();
			if(passChar.equals(space))
				passwordField.appendChar(' ');
			else if(passChar.equals(delete))
			{
				passwordField.deleteLastChar();
			}
			else
				passwordField.appendChar(passChar.charAt(0));
			handler.virtualPasswordHasChanged();


		}
	}
	VirtualKeyboardHandler handler;
	String space;
	String delete;
	UiPasswordField passwordField;
}
