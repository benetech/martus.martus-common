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
package org.martus.common.test;

import org.martus.common.EnglishCommonStrings;
import org.martus.common.FieldSpec;
import org.martus.common.GridData;
import org.martus.common.GridFieldSpec;
import org.martus.common.MiniLocalization;
import org.martus.common.StandardFieldSpecs;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinHtmlGenerator;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.util.TestCaseEnhanced;


public class TestBulletinHtmlGenerator extends TestCaseEnhanced
{

	public TestBulletinHtmlGenerator(String name)
	{
		super(name);
	}
    public void setUp() throws Exception
    {
    	super.setUp();
		if(security == null)
			security = MockMartusSecurity.createClient();
		if(loc == null)
			loc = new MiniLocalization(EnglishCommonStrings.strings);
		if(store == null)
			store = new MockBulletinStore(this);

   }	
    
	public void testGetSectionHtmlString() throws Exception
	{
		FieldSpec[] standardPublicFields = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		FieldSpec[] standardPrivateFields = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		
		Bulletin b = new Bulletin(security, standardPublicFields, standardPrivateFields);
		String title = "My Title";
		b.set(Bulletin.TAGTITLE, title);
		
		BulletinHtmlGenerator generator = new BulletinHtmlGenerator(loc);
		String expectedHtml ="<tr><td width='15%' align='right' valign='top'>Language</td><td valign='top'>-Other-</td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Author</td><td valign='top'></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Organization</td><td valign='top'></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Title</td><td valign='top'><strong>My Title</strong></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Location</td><td valign='top'></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Keywords</td><td valign='top'></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Date of Event</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGEVENTDATE))+"</td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Date Created</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGENTRYDATE))+"</td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Summary</td><td valign='top'><p></p></td></tr>\n" +
		"<tr><td width='15%' align='right' valign='top'>Details</td><td valign='top'><p></p></td></tr>\n";
		assertEquals("Public Section HTML not correct?", expectedHtml, generator.getSectionHtmlString(b.getFieldDataPacket()));
		expectedHtml = "<tr><td width='15%' align='right' valign='top'>Private</td><td valign='top'><p></p></td></tr>\n";
		assertEquals("Private Section HTML not correct?",expectedHtml, generator.getSectionHtmlString(b.getPrivateFieldDataPacket()));
	}

	public void testGetHtmlString() throws Exception
	{
		FieldSpec[] standardPublicFields = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		FieldSpec[] standardPrivateFields = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		
		Bulletin b = new Bulletin(security, standardPublicFields, standardPrivateFields);
		String title = "My New Title";
		b.setAllPrivate(true);
		b.set(Bulletin.TAGTITLE,title);
		b.setSealed();
		
		BulletinHtmlGenerator generator = new BulletinHtmlGenerator(loc);

		store.saveBulletinForTesting(b);
		String expectedHtml = "<html><table width='80'><tr><td width='15%' align='right' valign='top'>Last Saved</td><td valign='top'>"+loc.formatDateTime(b.getLastSavedTime())+"</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Version</td><td valign='top'>1</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Bulletin Status:</td><td valign='top'>Sealed</td></tr>\n"+
			"<tr></tr><tr><td colspan='2'><tr><td width='15%' align='right' valign='top'><u><b>Private Information</b></u></td><td valign='top'></td></tr>\n"+
			"</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Keep ALL Information Private</td><td valign='top'>Yes</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Language</td><td valign='top'>-Other-</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Author</td><td valign='top'></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Organization</td><td valign='top'></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Title</td><td valign='top'><strong>"+title+"</strong></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Location</td><td valign='top'></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Keywords</td><td valign='top'></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Date of Event</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGEVENTDATE))+"</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Date Created</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGENTRYDATE))+"</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Summary</td><td valign='top'><p></p></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Details</td><td valign='top'><p></p></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Attachments</td><td valign='top'></td></tr>\n"+
			"<tr></tr><tr><td colspan='2'><tr><td width='15%' align='right' valign='top'><u><b>Private Information</b></u></td><td valign='top'></td></tr>\n"+
			"</td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Private</td><td valign='top'><p></p></td></tr>\n"+
			"<tr><td width='15%' align='right' valign='top'>Attachments</td><td valign='top'></td></tr>\n"+
			"<tr></tr><tr><td width='15%' align='right' valign='top'>Bulletin Id:</td><td valign='top'>"+b.getLocalId()+"</td></tr>\n"+
			"</table></html>";
		assertEquals("Entire Bulletin's HTML not correct", expectedHtml, generator.getHtmlString(b, store.getDatabase(), true, true));
	}

	public void testGetPublicOnlyHtmlString() throws Exception
	{

		FieldSpec[] standardPublicFields = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		FieldSpec[] standardPrivateFields = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		
		Bulletin b = new Bulletin(security, standardPublicFields, standardPrivateFields);
		String title = "My Title";
		b.setAllPrivate(false);
		b.set(Bulletin.TAGTITLE,title);
		b.setDraft();
		
		BulletinHtmlGenerator generator = new BulletinHtmlGenerator(loc);
		String expectedHtml = "<html><table width='80'><tr><td width='15%' align='right' valign='top'>Last Saved</td><td valign='top'>"+loc.formatDateTime(b.getLastSavedTime())+"</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Version</td><td valign='top'>1</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Bulletin Status:</td><td valign='top'>Draft</td></tr>\n"+
		"<tr></tr><tr></tr><tr><td width='15%' align='right' valign='top'>Field Desk Bulletin</td><td valign='top'></td></tr>\n"+
		"<tr></tr><tr><td colspan='2'><tr><td width='15%' align='right' valign='top'><u><b>Public Information</b></u></td><td valign='top'></td></tr>\n"+
		"</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Keep ALL Information Private</td><td valign='top'>No</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Language</td><td valign='top'>-Other-</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Author</td><td valign='top'></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Organization</td><td valign='top'></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Title</td><td valign='top'><strong>"+title+"</strong></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Location</td><td valign='top'></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Keywords</td><td valign='top'></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Date of Event</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGEVENTDATE))+"</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Date Created</td><td valign='top'>"+loc.convertStoredDateToDisplay(b.get(Bulletin.TAGENTRYDATE))+"</td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Summary</td><td valign='top'><p></p></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Details</td><td valign='top'><p></p></td></tr>\n"+
		"<tr><td width='15%' align='right' valign='top'>Attachments</td><td valign='top'></td></tr>\n"+
		"<tr></tr><tr><td width='15%' align='right' valign='top'>Bulletin Id:</td><td valign='top'>"+b.getLocalId()+"</td></tr>\n"+
		"</table></html>";
		assertEquals("Entire Bulletin's HTML not correct", expectedHtml, generator.getHtmlString(b, store.getDatabase(), false, false));
	}
	
	public void testGetHtmlStringWithGrids() throws Exception
	{
		GridData grid = TestGridData.createSampleGrid();
		GridFieldSpec gridSpec = new GridFieldSpec();
		gridSpec.addColumn("Column 1");
		gridSpec.addColumn("Column 2");
		FieldSpec[] gridSpecs = {gridSpec};
		FieldSpec[] standardPrivateFields = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		
		Bulletin b = new Bulletin(security, gridSpecs, standardPrivateFields);
		b.set(gridSpec.getTag(), grid.getXmlRepresentation());
		
		
		BulletinHtmlGenerator generator = new BulletinHtmlGenerator(loc);
		String expectedHtml ="<tr><td width='15%' align='right' valign='top'></td><td valign='top'><table border='1' align='left'><tr><th align='center'> </th><th align='center'>Column 1</th><th align='center'>Column 2</th></tr><tr><td align='left'>1</td><td align='left'>data1</td><td align='left'>&lt;&amp;data2&gt;</td></tr><tr><td align='left'>2</td><td align='left'>data3</td><td align='left'>data4</td></tr></table></td></tr>\n";
		assertEquals("HTML Grids not correct?", expectedHtml, generator.getSectionHtmlString(b.getFieldDataPacket()));
	}

	public void testGetHtmlStringWithEmptyGrids() throws Exception
	{
		GridData grid = TestGridData.createSampleEmptyGrid();
		GridFieldSpec gridSpec = new GridFieldSpec();
		gridSpec.addColumn("Column 1");
		gridSpec.addColumn("Column 2");
		FieldSpec[] gridSpecs = {gridSpec};
		FieldSpec[] standardPrivateFields = StandardFieldSpecs.getDefaultPrivateFieldSpecs();
		
		Bulletin b = new Bulletin(security, gridSpecs, standardPrivateFields);
		b.set(gridSpec.getTag(), grid.getXmlRepresentation());
		
		
		BulletinHtmlGenerator generator = new BulletinHtmlGenerator(loc);
		String expectedHtml ="<tr><td width='15%' align='right' valign='top'></td><td valign='top'></td></tr>\n";
		assertEquals("HTML Empty Grids not correct?", expectedHtml, generator.getSectionHtmlString(b.getFieldDataPacket()));
	}
	
	private static MockMartusSecurity security;
	private static MiniLocalization loc;
	private static MockBulletinStore store;
}
