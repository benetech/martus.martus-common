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
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.martus.swing.ParagraphLayout;
import org.martus.swing.UiLanguageDirection;
import org.martus.swing.UiWrappedTextArea;

public class UiSigninPanel extends JPanel implements VirtualKeyboardHandler
{
	public UiSigninPanel(UiBasicSigninDlg dialogToUse, int mode, String username, char[] password)
	{
		owner = dialogToUse;
		localization = owner.getLocalization();
		uiState = owner.getCurrentUiState();
		setLayout(new ParagraphLayout());

		if(mode == UiBasicSigninDlg.TIMED_OUT)
		{
			addComponentToPanel(this, new JLabel(localization.getFieldLabel("timedout1")));
			if(owner.getCurrentUiState().isModifyingBulletin())
				addComponentToPanel(this, new JLabel(localization.getFieldLabel("timedout2")));
		}
		else if(mode == UiBasicSigninDlg.SECURITY_VALIDATE)
		{
			addComponentToPanel(this, new JLabel(localization.getFieldLabel("securityServerConfigValidate")));
		}
		else if(mode == UiBasicSigninDlg.RETYPE_USERNAME_PASSWORD)
		{
			addComponentToPanel(this, new JLabel(localization.getFieldLabel("RetypeUserNameAndPassword")));
		}
		else if(mode == UiBasicSigninDlg.CREATE_NEW)
		{
			addComponentToPanel(this, new JLabel(localization.getFieldLabel("CreateNewUserNamePassword")));
			addComponentToPanel(this, new UiWrappedTextArea(localization.getFieldLabel("HelpOnCreatingNewPassword"), 100));
		}
		
		userNameDescription = new JLabel("");
		passwordDescription = new JLabel("");

		nameField = new UiSingleTextField(20);
		nameField.setText(username);
		JLabel userNameLabel = new JLabel(localization.getFieldLabel("username"));
		addComponentsToPanel(this, userNameLabel, createPanel(userNameDescription, nameField));

		passwordField = new UiPasswordField(20);
		passwordField.setPassword(password);
		switchToNormalKeyboard = new JButton(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));
		switchToNormalKeyboard.addActionListener(new SwitchKeyboardHandler());
		JLabel passwordLabel = new JLabel(localization.getFieldLabel("password"));
		passwordArea = new JPanel();
		addComponentsToPanel(this, passwordLabel, passwordArea);

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
		passwordArea.setBorder(new LineBorder(Color.BLACK, 2));
		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToNormal"));

		addComponentToPanel(passwordArea, createPanel(passwordDescription, passwordField));
		addComponentToPanel(passwordArea, virtualKeyboardPanel);
		addComponentToPanel(passwordArea, switchToNormalKeyboard);

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
		addComponentToPanel(passwordArea, passwordField);

		JLabel warningNormalKeyboard = new JLabel(localization.getFieldLabel("NormalKeyboardMsg1"));
		warningNormalKeyboard.setFont(warningNormalKeyboard.getFont().deriveFont(Font.BOLD));
		addComponentToPanel(passwordArea, warningNormalKeyboard);
		addComponentToPanel(passwordArea, new JLabel(localization.getFieldLabel("NormalKeyboardMsg2")));

		switchToNormalKeyboard.setText(localization.getButtonLabel("VirtualKeyboardSwitchToVirtual"));
		addComponentToPanel(passwordArea, switchToNormalKeyboard);
		
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

	private JPanel createPanel(Component component1, Component component2)
	{
		JPanel panel = new JPanel();
		addComponentsToPanel(panel, component1, component2);
		return panel;
	}

	private void addComponentToPanel(JPanel panel, Component itemToAdd)
	{
		addComponentsToPanel(panel, new JLabel(""), itemToAdd);
	}

	private void addComponentsToPanel(JPanel panel, Component item1, Component item2)
	{
		if(UiLanguageDirection.isRightToLeftLanguage())
		{
			panel.add(item2, ParagraphLayout.NEW_PARAGRAPH);
			panel.add(item1);
		}
		else
		{
			panel.add(item1, ParagraphLayout.NEW_PARAGRAPH);
			panel.add(item2);
		}
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
