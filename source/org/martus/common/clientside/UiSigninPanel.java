/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.NotSerializableException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.martus.swing.UiButton;
import org.martus.swing.UiLabel;
import org.martus.swing.UiParagraphPanel;
import org.martus.swing.UiPasswordField;
import org.martus.swing.UiTextField;
import org.martus.swing.UiWrappedTextArea;
import org.martus.util.language.LanguageOptions;

public class UiSigninPanel extends UiParagraphPanel implements VirtualKeyboardHandler
{
	public UiSigninPanel(UiBasicSigninDlg dialogToUse, int mode, String username, char[] password)
	{
		owner = dialogToUse;
		localization = owner.getLocalization();
		uiState = owner.getCurrentUiState();

		if(mode == UiBasicSigninDlg.TIMED_OUT)
		{
			addOnNewLine(new UiLabel(localization.getFieldLabel("timedout1")));
			if(owner.getCurrentUiState().isModifyingBulletin())
				addOnNewLine(new UiLabel(localization.getFieldLabel("timedout2")));
		}
		else if(mode == UiBasicSigninDlg.SECURITY_VALIDATE)
		{
			addOnNewLine(new UiLabel(localization.getFieldLabel("securityServerConfigValidate")));
		}
		else if(mode == UiBasicSigninDlg.RETYPE_USERNAME_PASSWORD)
		{
			addOnNewLine(new UiLabel(localization.getFieldLabel("RetypeUserNameAndPassword")));
		}
		else if(mode == UiBasicSigninDlg.CREATE_NEW)
		{
			addOnNewLine(new UiLabel(localization.getFieldLabel("CreateNewUserNamePassword")));
			addOnNewLine(new UiWrappedTextArea(localization.getFieldLabel("HelpOnCreatingNewPassword"), 100));
			uiState.setCurrentDefaultKeyboardVirtual(true);
			uiState.save();
		}
		
		userNameDescription = new UiLabel("");
		passwordDescription = new UiLabel("");

		nameField = new UiTextField(20);
		nameField.setText(username);
		JLabel userNameLabel = new UiLabel(localization.getFieldLabel("username"));
		addComponents(userNameLabel, createPanel(userNameDescription, nameField));

		passwordField = new UiPasswordField(20);
		passwordField.setPassword(password);
		switchToNormalKeyboard = new UiButton(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));
		switchToNormalKeyboard.addActionListener(new SwitchKeyboardHandler());
		JLabel passwordLabel = new UiLabel(localization.getFieldLabel("password"));
		passwordArea = new UiParagraphPanel();
		addComponents(passwordLabel, passwordArea);

		new UiVirtualKeyboard(localization, this, passwordField);
		UpdatePasswordArea();
		
		if(username != null && username.length() > 0)
			passwordField.requestFocus();
	}
	
	public String getName()
	{
		return nameField.getText();
	}
	
	public char[] getPassword()
	{
		return passwordField.getPassword();
	}
	
	public void refreshForNewVirtualMode()
	{
		passwordArea.updateUI();
		userNameDescription.updateUI();
		nameField.requestFocus();
		owner.virtualPasswordHasChanged();
	}

	public void UpdatePasswordArea()
	{
		boolean viewingVirtualKeyboard = uiState.isCurrentDefaultKeyboardVirtual();
		if(viewingVirtualKeyboard)
			displayPasswordAreaUsingVirtualKeyboard();
		else
			displayPasswordAreaUsingNormalKeyboard();
	}

	public void addKeyboard(JPanel keyboard)
	{
		virtualKeyboardPanel = keyboard;
	}

	public void displayPasswordAreaUsingVirtualKeyboard()
	{
		passwordArea.removeAll();
		userNameDescription.setText(localization.getFieldLabel("VirtualUserNameDescription"));
		passwordDescription.setText(localization.getFieldLabel("VirtualPasswordDescription"));
		passwordField.setVirtualMode(true);
		passwordArea.setBorder(new LineBorder(Color.BLACK, 2));
		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));

		passwordArea.addOnNewLine(createPanel(passwordDescription, passwordField));
		passwordArea.addOnNewLine(virtualKeyboardPanel);
		passwordArea.addOnNewLine(switchToNormalKeyboard);

		refreshForNewVirtualMode();
		owner.sizeHasChanged();
	}

	public void displayPasswordAreaUsingNormalKeyboard()
	{
		passwordArea.removeAll();
		passwordArea.updateUI();
		userNameDescription.setText("");
		passwordDescription.setText("");
		passwordArea.setBorder(new LineBorder(Color.black, 2));

		passwordField.setVirtualMode(false);
		passwordArea.addOnNewLine(passwordField);

		JLabel warningNormalKeyboard = new UiLabel(localization.getFieldLabel("NormalKeyboardMsg1"));
		warningNormalKeyboard.setFont(warningNormalKeyboard.getFont().deriveFont(Font.BOLD));
		passwordArea.addOnNewLine(warningNormalKeyboard);
		passwordArea.addOnNewLine(new UiLabel(localization.getFieldLabel("NormalKeyboardMsg2")));

		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToVirtual"));
		passwordArea.addOnNewLine(switchToNormalKeyboard);
		
		refreshForNewVirtualMode();
		owner.sizeHasChanged();
	}

	public void switchKeyboards()
	{
		boolean viewingVirtualKeyboard = uiState.isCurrentDefaultKeyboardVirtual();
		if(viewingVirtualKeyboard)
		{				
			if(!UiUtilities.confirmDlg(localization, (JFrame)owner.getParent(), "WarningSwitchToNormalKeyboard"))
				return;
		}

		uiState.setCurrentDefaultKeyboardVirtual(!viewingVirtualKeyboard);
		uiState.save();
		UpdatePasswordArea();
	}

	public class SwitchKeyboardHandler extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			switchKeyboards();
		}

		private static final long serialVersionUID = 1;
		private void writeObject(java.io.ObjectOutputStream stream) throws IOException
		{
			throw new NotSerializableException();
		}

	}
	
	public void virtualPasswordHasChanged()
	{
		passwordField.updateUI();
		owner.virtualPasswordHasChanged();
	}

	private JPanel createPanel(Component component1, Component component2)
	{
		JPanel panel = new JPanel();
		if(LanguageOptions.isRightToLeftLanguage())
		{
			panel.add(component2);
			panel.add(component1);
		}
		else
		{
			panel.add(component1);
			panel.add(component2);
		}
		return panel;
	}
	

	private static final long serialVersionUID = 1;
	private void writeObject(java.io.ObjectOutputStream stream) throws IOException
	{
		throw new NotSerializableException();
	}

	UiBasicSigninDlg owner;
	UiBasicLocalization localization;
	CurrentUiState uiState;
	private JLabel userNameDescription;
	private JLabel passwordDescription;
	private UiTextField nameField;
	private UiPasswordField passwordField;
	private UiParagraphPanel passwordArea;
	private JPanel virtualKeyboardPanel;
	private JButton switchToNormalKeyboard;
}
