/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
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

import java.util.Vector;

import org.martus.common.ContactKey;
import org.martus.common.ContactKeys;
import org.martus.common.ExternalPublicKeys;
import org.martus.common.MartusXml;
import org.martus.util.TestCaseEnhanced;
import org.martus.util.xml.XmlUtilities;

public class TestContactKeys extends TestCaseEnhanced
{
	public TestContactKeys(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		
		ContactKeys contactKeys = new ContactKeys();
		assertTrue(contactKeys.isEmpty());
		assertEquals(0, contactKeys.size());
		String publicKey1 = "123";
		ContactKey key = new ContactKey(publicKey1);
		contactKeys.add(key);
		assertEquals(1, contactKeys.size());
		assertTrue(contactKeys.containsKey(publicKey1));
		ContactKey retrieved = contactKeys.get(0);
		assertEquals(key.getPublicKey(), retrieved.getPublicKey());
		assertEquals(key.getLabel(), retrieved.getLabel());
		contactKeys.remove(0);
		assertEquals(0, contactKeys.size());

		String publicKey2 = "123";
		String label2 = "abc";
		ContactKey key2 = new ContactKey(publicKey2, label2);
		contactKeys.add(key2);
		assertEquals(label2, contactKeys.getLabelIfPresent(key2));
		
		key2.setCanSendTo(true);
		key2.setSendToByDefault(true);
		assertTrue(key2.getCanSendTo());
		assertTrue(key2.getSendToByDefault());
		assertFalse(key2.getCanReceiveFrom());

		key2.setSendToByDefault(false);
		assertFalse(key2.getSendToByDefault());
		assertTrue(key2.getCanSendTo());
		assertFalse(key2.getCanReceiveFrom());
		
		key2.setSendToByDefault(true);
		assertTrue(key2.getCanSendTo());
		assertTrue(key2.getSendToByDefault());
		assertFalse(key2.getCanReceiveFrom());

		key2.setCanSendTo(false);
		assertFalse(key2.getCanSendTo());
		assertFalse("Setting this HQ to CanSendTo also must not be able to sendToByDefault", key2.getSendToByDefault());
		assertFalse(key2.getCanReceiveFrom());
		
		key2.setCanReceiveFrom(true);
		assertFalse(key2.getCanSendTo());
		assertFalse(key2.getSendToByDefault());
		assertTrue(key2.getCanReceiveFrom());
		
		
	}
	
	public void testAddKeys()
	{
		ContactKeys contactKeys = new ContactKeys();
		String publicKey1 = "123";
		ContactKey key = new ContactKey(publicKey1);
		contactKeys.add(key);
		String publicKey2 = "123";
		String label2 = "abc";
		ContactKey key2 = new ContactKey(publicKey2, label2);
		contactKeys.add(key2);
		assertEquals(2, contactKeys.size());
		
		ContactKeys newKeys = new ContactKeys(contactKeys);
		assertEquals(2, newKeys.size());
		assertTrue(newKeys.containsKey(publicKey1));
		assertTrue(newKeys.containsKey(publicKey2));
		
		ContactKeys newKeys2 = new ContactKeys();
		newKeys2.add(contactKeys);
		assertEquals(2, newKeys.size());
		assertTrue(newKeys2.containsKey(publicKey1));
		assertTrue(newKeys2.containsKey(publicKey2));
	}
	
	public void testEmpty()
	{
		ContactKeys contactKeys = new ContactKeys();
		String xmlExpected = MartusXml.getTagStartWithNewline(ContactKeys.CONTACT_KEYS_TAG) +
		MartusXml.getTagEnd(ContactKeys.CONTACT_KEYS_TAG);
		assertEquals(xmlExpected, contactKeys.toString());
	}
	
	public void testXmlRepresentation()
	{
		Vector keys = new Vector();
		String key1 = "key 1";
		String label1 = "label 1";
		String key2 = "key 2";
		String label2 = "label 2 with <icky &xml stuff>";
		String key3 = "key 2";
		String label3 = "label 3";
		ContactKey contactKey1 = new ContactKey(key1, label1);
		contactKey1.setCanReceiveFrom(true);
		contactKey1.setVerification(ContactKey.NOT_VERIFIED);
		keys.add(contactKey1);
		ContactKey contactKey2 = new ContactKey(key2, label2);
		contactKey2.setCanSendTo(true);
		contactKey2.setVerification(ContactKey.VERIFIED_ENTERD_20_DIGITS);
		keys.add(contactKey2);
		ContactKey contactKey3 = new ContactKey(key3, label3);
		contactKey3.setCanSendTo(true);
		contactKey3.setSendToByDefault(true);
		contactKey3.setVerification(ContactKey.VERIFIED_VISUALLY);
		keys.add(contactKey3);
		ContactKeys contactKeys = new ContactKeys(keys);
		String xmlExpected = MartusXml.getTagStartWithNewline(ContactKeys.CONTACT_KEYS_TAG) +
		 MartusXml.getTagStart(ContactKeys.CONTACT_KEY_TAG) + 
		 MartusXml.getTagStart(ExternalPublicKeys.PUBLIC_KEY_TAG) + 
		 XmlUtilities.getXmlEncoded(key1) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.PUBLIC_KEY_TAG) +
		 MartusXml.getTagStart(ExternalPublicKeys.LABEL_TAG) + 
		 XmlUtilities.getXmlEncoded(label1) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.LABEL_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_SEND_TO_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.NO_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_SEND_TO_TAG) +
		 MartusXml.getTagStart(ContactKeys.SEND_TO_BY_DEFAULT_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.NO_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.SEND_TO_BY_DEFAULT_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_RECEIVE_FROM_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.YES_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_RECEIVE_FROM_TAG) +
		 MartusXml.getTagStart(ContactKeys.VERIFICATION_TAG) + 
		 XmlUtilities.getXmlEncoded(Integer.toString(ContactKey.NOT_VERIFIED)) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.VERIFICATION_TAG) +
		 MartusXml.getTagEnd(ContactKeys.CONTACT_KEY_TAG) +

		 MartusXml.getTagStart(ContactKeys.CONTACT_KEY_TAG) + 
		 MartusXml.getTagStart(ExternalPublicKeys.PUBLIC_KEY_TAG) + 
		 XmlUtilities.getXmlEncoded(key2) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.PUBLIC_KEY_TAG) +
		 MartusXml.getTagStart(ExternalPublicKeys.LABEL_TAG) + 
		 XmlUtilities.getXmlEncoded(label2) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.LABEL_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_SEND_TO_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.YES_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_SEND_TO_TAG) +
		 MartusXml.getTagStart(ContactKeys.SEND_TO_BY_DEFAULT_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.NO_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.SEND_TO_BY_DEFAULT_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_RECEIVE_FROM_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.NO_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_RECEIVE_FROM_TAG) +
		 MartusXml.getTagStart(ContactKeys.VERIFICATION_TAG) + 
		 XmlUtilities.getXmlEncoded(Integer.toString(ContactKey.VERIFIED_ENTERD_20_DIGITS)) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.VERIFICATION_TAG) +
		 MartusXml.getTagEnd(ContactKeys.CONTACT_KEY_TAG) +

		 MartusXml.getTagStart(ContactKeys.CONTACT_KEY_TAG) + 
		 MartusXml.getTagStart(ExternalPublicKeys.PUBLIC_KEY_TAG) + 
		 XmlUtilities.getXmlEncoded(key3) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.PUBLIC_KEY_TAG) +
		 MartusXml.getTagStart(ExternalPublicKeys.LABEL_TAG) + 
		 XmlUtilities.getXmlEncoded(label3) +
		 MartusXml.getTagEndWithoutNewline(ExternalPublicKeys.LABEL_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_SEND_TO_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.YES_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_SEND_TO_TAG) +
		 MartusXml.getTagStart(ContactKeys.SEND_TO_BY_DEFAULT_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.YES_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.SEND_TO_BY_DEFAULT_TAG) +
		 MartusXml.getTagStart(ContactKeys.CAN_RECEIVE_FROM_TAG) + 
		 XmlUtilities.getXmlEncoded(ContactKeys.NO_DATA) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.CAN_RECEIVE_FROM_TAG) +
		 MartusXml.getTagStart(ContactKeys.VERIFICATION_TAG) + 
		 XmlUtilities.getXmlEncoded(Integer.toString(ContactKey.VERIFIED_VISUALLY)) +
		 MartusXml.getTagEndWithoutNewline(ContactKeys.VERIFICATION_TAG) +
		 MartusXml.getTagEnd(ContactKeys.CONTACT_KEY_TAG) +
		 
		 MartusXml.getTagEnd(ContactKeys.CONTACT_KEYS_TAG);
		
		assertEquals(xmlExpected, contactKeys.toString());
	}

	
	public void testParseXml() throws Exception
	{
		Vector keys = new Vector();
		String key1 = "key 1";
		String label1 = "label 1";
		String key2 = "key 2";
		String label2 = "label 2";
		boolean key1CanSendTo = true;
		boolean key1CanReceiveFrom = false;
		ContactKey contactKey1 = new ContactKey(key1, label1);
		contactKey1.setCanReceiveFrom(key1CanReceiveFrom);
		contactKey1.setCanSendTo(key1CanSendTo);
		contactKey1.setVerification(ContactKey.NOT_VERIFIED);
		assertEquals("Key1 CanSendTo not same?", key1CanSendTo, contactKey1.getCanSendTo());
		assertEquals("Key1 CanReceiveFrom not same?", key1CanReceiveFrom, contactKey1.getCanReceiveFrom());
		assertEquals("Key1 not verified?", ContactKey.NOT_VERIFIED, contactKey1.getVerification());
		keys.add(contactKey1);
		boolean key2CanSendTo = false;
		boolean key2CanReceiveFrom = true;
		
		ContactKey contactKey2 = new ContactKey(key2, label2);
		contactKey2.setCanReceiveFrom(key2CanReceiveFrom);
		contactKey2.setCanSendTo(key2CanSendTo);
		contactKey2.setVerification(ContactKey.VERIFIED_VISUALLY);
		keys.add(contactKey2);
		assertEquals("Key2 CanSendTo not same?", key2CanSendTo, contactKey2.getCanSendTo());
		assertEquals("Key2 CanReceiveFrom not same?", key2CanReceiveFrom, contactKey2.getCanReceiveFrom());
		assertEquals("Key2 Verified not VERIFIED_VISUALLY?", ContactKey.VERIFIED_VISUALLY, contactKey2.getVerification());
		ContactKeys contactKeys = new ContactKeys(keys);

		
		Vector newKeys = new ContactKeys().parseXml(contactKeys.toString());
		ContactKeys contactKeys2 = new ContactKeys(newKeys);
		
		assertEquals(contactKeys.toString(), contactKeys2.toString());
		
		ContactKey retrieved = contactKeys2.get(0);
		assertEquals("retrieved Key1 label not same?", contactKey1.getLabel(), retrieved.getLabel());
		assertEquals("retrieved Key1 CanSendTo not same?", key1CanSendTo, retrieved.getCanSendTo());
		assertEquals("retrieved Key1 CanReceiveFrom not same?", key1CanReceiveFrom, retrieved.getCanReceiveFrom());
		assertEquals("retrieved Key1 not verified?", ContactKey.NOT_VERIFIED, retrieved.getVerification());
		
		ContactKey retrieved2 = contactKeys2.get(1);
		assertEquals("retrieved Key2 label not same?", contactKey2.getLabel(), retrieved2.getLabel());
		assertEquals("retrieved Key2 CanSendTo not same?", key2CanSendTo, retrieved2.getCanSendTo());
		assertEquals("retrieved Key2 CanReceiveFrom not same?", key2CanReceiveFrom, retrieved2.getCanReceiveFrom());
		assertEquals("retrieved Key2 Verified not VERIFIED_VISUALLY?", ContactKey.VERIFIED_VISUALLY, retrieved2.getVerification());
	}
}
