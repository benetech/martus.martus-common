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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.martus.swing.ParagraphLayout;
import org.martus.swing.UiWrappedTextArea;

public class UiSigninPanel extends JPanel implements VirtualKeyboardHandler
{
	public UiSigninPanel(UiBasicSigninDlg dialogToUse, int mode, String username, char[] password)
	{
		owner = dialogToUse;
		localization = owner.getLocalization();
		uiState = owner.getCurrentUiState();
		setLayout(new ParagraphLayout());
		
		ComponentOrientation orientation = localization.getComponentOrientation();
		if(mode == UiBasicSigninDlg.TIMED_OUT)
		{
			add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			JLabel timedOutNote1 = new JLabel(localization.getFieldLabel("timedout1"));
			add(timedOutNote1);
			if(owner.getCurrentUiState().isModifyingBulletin())
			{
				add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
				JLabel timedOutNote2 = new JLabel(localization.getFieldLabel("timedout2"));
				add(timedOutNote2);
			}
		}
		else if(mode == UiBasicSigninDlg.SECURITY_VALIDATE)
		{
			add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			JLabel securityServerConfigValidate = new JLabel(localization.getFieldLabel("securityServerConfigValidate"));
			add(securityServerConfigValidate);
		}
		else if(mode == UiBasicSigninDlg.RETYPE_USERNAME_PASSWORD)
		{
			add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			JLabel retypeUserNamePassword = new JLabel(localization.getFieldLabel("RetypeUserNameAndPassword"));
			add(retypeUserNamePassword);
		}
		else if(mode == UiBasicSigninDlg.CREATE_NEW)
		{
			add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			JLabel createNewUserNamePassword = new JLabel(localization.getFieldLabel("CreateNewUserNamePassword"));
			add(createNewUserNamePassword);
		
			add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			UiWrappedTextArea helpOnCreatingPassword = new UiWrappedTextArea(localization.getFieldLabel("HelpOnCreatingNewPassword"), 100, orientation);
			add(helpOnCreatingPassword);
		
		}
		
		userNameDescription = new JLabel("");
		passwordDescription = new JLabel("");
		
		add(new JLabel(localization.getFieldLabel("username")), ParagraphLayout.NEW_PARAGRAPH);
		nameField = new UiSingleTextField(20, orientation);
		nameField.setText(username);
		add(userNameDescription);
		add(nameField);
		
		add(new JLabel(localization.getFieldLabel("password")), ParagraphLayout.NEW_PARAGRAPH);
		passwordField = new UiPasswordField(20, orientation);
		passwordField.setPassword(password);
		switchToNormalKeyboard = new JButton(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));
		switchToNormalKeyboard.addActionListener(new SwitchKeyboardHandler());
		passwordArea = new JPanel();
		add(passwordArea);
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

		passwordArea.setLayout(new ParagraphLayout());
		passwordArea.setBorder(new LineBorder(Color.black, 2));
		passwordArea.add(new JLabel(""));
		passwordArea.add(passwordDescription);
		passwordArea.add(passwordField);

		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		passwordArea.add(virtualKeyboardPanel);

		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));
		passwordArea.add(switchToNormalKeyboard);
		refreshForNewVirtualMode();
		owner.sizeHasChanged();
	}

	public void displayPasswordAreaUsingNormalKeyboard()
	{
		passwordArea.removeAll();
		passwordArea.updateUI();
		userNameDescription.setText("");
		passwordDescription.setText("");
		passwordArea.setLayout(new ParagraphLayout());
		passwordArea.setBorder(new LineBorder(Color.black, 2));

		passwordField.setVirtualMode(false);
		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		passwordArea.add(passwordField);

		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		JLabel warningNormalKeyboard = new JLabel(localization.getFieldLabel("NormalKeyboardMsg1"));
		warningNormalKeyboard.setFont(warningNormalKeyboard.getFont().deriveFont(Font.BOLD));
		passwordArea.add(warningNormalKeyboard);
		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		passwordArea.add(new JLabel(localization.getFieldLabel("NormalKeyboardMsg2")));

		passwordArea.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToVirtual"));
		passwordArea.add(switchToNormalKeyboard);
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
	}
	
	public void virtualPasswordHasChanged()
	{
		passwordField.updateUI();
		owner.virtualPasswordHasChanged();
	}

	UiBasicSigninDlg owner;
	UiBasicLocalization localization;
	CurrentUiState uiState;
	private JLabel userNameDescription;
	private JLabel passwordDescription;
	private UiSingleTextField nameField;
	private UiPasswordField passwordField;
	private JPanel passwordArea;
	private JPanel virtualKeyboardPanel;
	private JButton switchToNormalKeyboard;
}
