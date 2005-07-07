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

package org.martus.common.bulletin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.martus.common.GridData;
import org.martus.common.HQKeys;
import org.martus.common.MiniLocalization;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.database.Database.RecordHiddenException;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;
import org.martus.common.packet.FieldDataPacket;
import org.martus.common.packet.UniversalId;
import org.martus.util.Base64.InvalidBase64Exception;
import org.martus.util.language.LanguageOptions;
import org.martus.util.xml.XmlUtilities;

public class BulletinHtmlGenerator
{
	public BulletinHtmlGenerator(MiniLocalization localizationToUse)
	{
		this(80, localizationToUse);
	}
	
	public BulletinHtmlGenerator(int widthToUse, MiniLocalization localizationToUse)
	{
		width = widthToUse;
		localization = localizationToUse;
	}

	public String getHtmlString(Bulletin b, ReadableDatabase database, boolean includePrivateData, boolean yourBulletin)
	{
		bulletin = b;
		StringBuffer html = new StringBuffer(1000);
		html.append("<html>");
		
		html.append("<table width='");
		html.append(Integer.toString(width));
		html.append("'>\n");
		int leftColumnWidthPercentage = LABEL_COLUMN_WIDTH_PERCENTAGE;
		if(LanguageOptions.isRightToLeftLanguage())
			leftColumnWidthPercentage = (100-leftColumnWidthPercentage);
		int rightColumnWidthPercentage = (100-leftColumnWidthPercentage);
		html.append("<tr>");
		html.append("<td width='" + leftColumnWidthPercentage + "%'></td>");
		html.append("<td width='" + rightColumnWidthPercentage + "%'></td>");
		html.append("</tr>\n");
		appendHeadHtml(html, b);
		if(!yourBulletin)
		{
			html.append("<tr></tr>\n");
			html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("BulletinNotYours"),""));		
		}

		String publicSectionTitle =  localization.getFieldLabel("publicsection");
		String allPrivateValueTag = "no";
		if(b.isAllPrivate())
		{	
			allPrivateValueTag = "yes";				
			publicSectionTitle =  localization.getFieldLabel("privatesection");
			if (!includePrivateData)
			{
				appendTitleOfSection(html, publicSectionTitle);	
				appendTailHtml(html, b);
				return html.toString();
			}
		}
		

		appendTitleOfSection(html, publicSectionTitle);
		html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("allprivate"), localization.getButtonLabel(allPrivateValueTag)));

		html.append(getSectionHtmlString(b.getFieldDataPacket()));
		html.append(getAttachmentsHtmlString(b.getPublicAttachments(), database));

		if (includePrivateData)
		{	
			appendTitleOfSection(html, localization.getFieldLabel("privatesection"));
			html.append(getSectionHtmlString(b.getPrivateFieldDataPacket()));
			html.append(getAttachmentsHtmlString(b.getPrivateAttachments(), database));
		}
		appendHQs(html, b);
		appendTailHtml(html, b);
		return html.toString();
	}
	
	private void appendTitleOfSection(StringBuffer html, String title)
	{
		html.append("<tr></tr>\n");
		String align = "left";
		if(LanguageOptions.isRightToLeftLanguage())
			align = "right";
		html.append("<tr><td colspan='2' align='" + align + "'>");
		html.append("<u><b>"+title+"</b></u>");
		html.append("</td></tr>");
		html.append("\n");
	}	
	
	private void appendHeadHtml(StringBuffer html, Bulletin b )
	{
		html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("BulletinLastSaved"), localization.formatDateTime(b.getLastSavedTime())));
		html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("BulletinVersionNumber"), (new Integer(b.getVersion())).toString()));
		html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("BulletinStatus"), localization.getStatusLabel(b.getStatus())));
	}
	
	private void appendTailHtml(StringBuffer html, Bulletin b )
	{
		html.append("<tr></tr>\n");
		html.append(getHtmlEscapedFieldHtmlString(localization.getFieldLabel("BulletinId"),b.getLocalId()));
		html.append("</table>");
		html.append("</html>");
	}
	
	private void appendHQs(StringBuffer html, Bulletin b )
	{
		appendTitleOfSection(html, localization.getFieldLabel("HQSummaryLabel"));

		HQKeys keys = b.getAuthorizedToReadKeys();
		int size = keys.size();
		if(size==0)
		{
			html.append(getFieldHtmlString("",localization.getFieldLabel("NoHQsConfigured")));
			return;
		}

		for(int i = 0; i < size; ++i)
		{
			String label = keys.get(i).getLabel();
			if(label.length() == 0)
			{
				try 
				{
					label = keys.get(i).getPublicCode();
				} 
				catch (InvalidBase64Exception e) 
				{
					e.printStackTrace();
				}
			}
			html.append(getHtmlEscapedFieldHtmlString("",label));
			html.append("<p></p>");
		}
	}
	

	public String getSectionHtmlString(FieldDataPacket fdp)
	{
		FieldSpec[] fieldTags = fdp.getFieldSpecs();
		String sectionHtml = "";
		for(int fieldNum = 0; fieldNum < fieldTags.length; ++fieldNum)
		{
			FieldSpec spec = fieldTags[fieldNum];
			String tag = spec.getTag();
			String label = getHTMLEscaped(spec.getLabel());			
			String value = getHTMLEscaped(fdp.get(tag));
			if(tag.equals(Bulletin.TAGTITLE))
				value = "<strong>" + value + "</strong>";
			if(spec.getType() == FieldSpec.TYPE_DATE)
				value = localization.convertStoredDateToDisplayReverseIfNecessary(value);
			else if(spec.getType() == FieldSpec.TYPE_LANGUAGE)
				value = getHTMLEscaped(localization.getLanguageName(value));
			else if(spec.getType() == FieldSpec.TYPE_MULTILINE)
				value = insertNewlines(value);
			else if(spec.getType() == FieldSpec.TYPE_DATERANGE)
				value = localization.getViewableDateRange(value);
			else if(spec.getType() == FieldSpec.TYPE_BOOLEAN)
			{
				value = getPrintableBooleanValue(value);
			}
			else if(spec.getType() == FieldSpec.TYPE_GRID)
			{
				value = getGridHTML(fdp, spec, tag);
			}
			
			if(StandardFieldSpecs.isStandardFieldTag(tag))
				label = getHTMLEscaped(localization.getFieldLabel(tag));
							
			String fieldHtml = getFieldHtmlString(label, value);
			sectionHtml += fieldHtml;
		}
		return sectionHtml;
	}

	private String getPrintableBooleanValue(String value)
	{
		if(value.equals(FieldSpec.TRUESTRING))
			value = getHTMLEscaped(localization.getButtonLabel("yes"));
		else
			value = getHTMLEscaped(localization.getButtonLabel("no"));
		return value;
	}

	private String getGridHTML(FieldDataPacket fdp, FieldSpec spec, String tag)
	{
		String gridXMLData = fdp.get(tag);
		if(gridXMLData.length()==0)
			return "";
		
		String value;
		GridFieldSpec grid = (GridFieldSpec)spec;
		value = "<table border='1' align='left'><tr>";
		String justification = "center";
		if(!LanguageOptions.isRightToLeftLanguage())
			value += getItemToAddForTable(grid.getColumnZeroLabel(),TABLE_HEADER, justification);
		int columnCount = grid.getColumnCount();
		for(int i = 0; i < columnCount; ++i)
		{
			String data = grid.getColumnLabel(i);
			if(LanguageOptions.isRightToLeftLanguage())
				data = grid.getColumnLabel((columnCount-1)-i);
			value += getItemToAddForTable(data,TABLE_HEADER, justification);
		}
		if(LanguageOptions.isRightToLeftLanguage())
			value += getItemToAddForTable(grid.getColumnZeroLabel(),TABLE_HEADER, justification);
		value += "</tr>";
		try
		{
			GridData gridData = new GridData(grid);
			gridData.setFromXml(gridXMLData);
			int rowCount = gridData.getRowCount();

			justification = "left";
			if(LanguageOptions.isRightToLeftLanguage())
				justification = "right";
			
			for(int r =  0; r<rowCount; ++r)
			{
				value += "<tr>";
				if(!LanguageOptions.isRightToLeftLanguage())
					value += getItemToAddForTable(Integer.toString(r+1),TABLE_DATA, justification);
				for(int c = 0; c<columnCount; ++c)
				{
					String data = gridData.getValueAt(r, c);
					if(LanguageOptions.isRightToLeftLanguage())
						data = gridData.getValueAt(r, ((columnCount-1)-c));
					if(grid.getColumnType(c) == FieldSpec.TYPE_BOOLEAN)
						data = getPrintableBooleanValue(data);
					value += getItemToAddForTable(data, TABLE_DATA, justification);
				}
				if(LanguageOptions.isRightToLeftLanguage())
					value += getItemToAddForTable(Integer.toString(r+1),TABLE_DATA, justification);
				value += "</tr>";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		value += "</table>";
		return value;
	}

	private String getItemToAddForTable(String data, String type, String justification)
	{
		return "<"+type+" align='"+justification+"'>"+getHTMLEscaped(data)+"</"+type+">";
	}

	private String getSizeInKb(int sizeBytes)
	{
		int sizeInKb = sizeBytes / 1024;
		if (sizeInKb == 0)
			sizeInKb = 1;
		return Integer.toString(sizeInKb);
	}

	private String getAttachmentSize(ReadableDatabase db, UniversalId uid)
	{
		// TODO :This is a duplicate code from AttachmentTableModel.java. 
		// Ideally, the AttachmentProxy should self-describe of file size and file description.

		String size = "";
		try
		{
			int rawSize = 0;
			if (bulletin.getStatus().equals(Bulletin.STATUSDRAFT))
				rawSize = db.getRecordSize(DatabaseKey.createDraftKey(uid));
			else
				rawSize = db.getRecordSize(DatabaseKey.createSealedKey(uid));

			rawSize -= 1024;//Public code & overhead
			rawSize = rawSize * 3 / 4;//Base64 overhead
			size = getSizeInKb(rawSize);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (RecordHiddenException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	private String getAttachmentsHtmlString(AttachmentProxy[] attachments, ReadableDatabase db)
	{
		String attachmentList = "";
	
		for(int i = 0 ; i < attachments.length ; ++i)
		{
			AttachmentProxy aProxy = attachments[i];
			String label = aProxy.getLabel();
			String size = "( " + getAttachmentSize(db, aProxy.getUniversalId())+ " " + localization.getFieldLabel("attachmentSizeForPrinting")+ " )";
			if(LanguageOptions.isRightToLeftLanguage())
			{
				String tmp = label;
				label = size;
				size = tmp;
			}

			attachmentList += "<p>" + getHTMLEscaped(label) + "    " + getHTMLEscaped(size) + "</p>";
		}
		return getFieldHtmlString(localization.getFieldLabel("attachments"), attachmentList);
	}

	private String getHtmlEscapedFieldHtmlString(String label, String value)
	{
		return getFieldHtmlString(getHTMLEscaped(label), getHTMLEscaped(value));
	}

	private String getFieldHtmlString(String label, String value)
	{
		String leftData = label;
		String rightData = value;
		
		if(LanguageOptions.isRightToLeftLanguage())
		{
			leftData = value;
			rightData = label;
		}

		StringBuffer fieldHtml = new StringBuffer(label.length() + value.length() + 100);
		fieldHtml.append("<tr><td align='right' valign='top'>");
		fieldHtml.append(leftData);
		fieldHtml.append("</td>");
		fieldHtml.append("<td valign='top'>");
		fieldHtml.append(rightData);
		fieldHtml.append("</td></tr>");
		fieldHtml.append("\n");
		return new String(fieldHtml);
	}

	private String insertNewlines(String value)
	{
		final String P_TAG_BEGIN = "<p>";
		final String P_TAG_END = "</p>";
		StringBuffer html = new StringBuffer(value.length() + 100);
		html.append(P_TAG_BEGIN);

		try
		{
			BufferedReader reader = new BufferedReader(new StringReader(getHTMLEscaped(value)));
			String thisParagraph = null;
			while((thisParagraph = reader.readLine()) != null)
			{
				html.append(thisParagraph);
				html.append(P_TAG_END);
				html.append(P_TAG_BEGIN);
			}
		}
		catch (IOException e)
		{
			html.append("...?");
		}

		html.append(P_TAG_END);
		return new String(html);
	}
	
	private String getHTMLEscaped(String text)
	{
		return XmlUtilities.getXmlEncoded(text);
	}

	int width;
	MiniLocalization localization;
	Bulletin bulletin;

	private static final int LABEL_COLUMN_WIDTH_PERCENTAGE = 15;
	private static final String TABLE_HEADER = "th";
	private static final String TABLE_DATA = "td";
	
}
