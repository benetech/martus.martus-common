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
package org.martus.common.test;

import org.martus.common.packet.FieldSpecDetails;
import org.martus.common.packet.XmlFieldSpecDetailsLoader;
import org.martus.util.TestCaseEnhanced;


public class TestFieldSpecDetails extends TestCaseEnhanced
{
	public TestFieldSpecDetails(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		FieldSpecDetails details = createSampleDetails();
		assertEquals("Columns not 2?", 2, details.getColumnCount());
		assertEquals("label1 not returned", label1, details.getColumnLabel(0));
		assertEquals("label2 not returned", label2, details.getColumnLabel(1));
	}
	
	private FieldSpecDetails createSampleDetails()
	{
		FieldSpecDetails details = new FieldSpecDetails();
		assertEquals("Columns not zero?", 0, details.getColumnCount());
		details.addColumnLabel(label1);
		details.addColumnLabel(label2);
		return details;
	}

	public void testToXml() throws Exception
	{
		FieldSpecDetails details = createSampleDetails();
		String xml = details.toXml();
		XmlFieldSpecDetailsLoader loader = new XmlFieldSpecDetailsLoader();
		loader.parse(xml);
		FieldSpecDetails loaded = loader.getDetails();
		assertEquals("xml doesn't match?", xml, loaded.toXml());
	}
	
	public static String label1 = "Labe<l>& 1";
	public static String label2 = "Label 2";

}
