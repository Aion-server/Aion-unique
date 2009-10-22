/*
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.itemengine;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Avol
 * 
 */

public class ItemTemplateLoader
{

	private static final Logger log = Logger.getLogger(ItemTemplateLoader.class);

	public static String itemName;
	public static int itemId;
	public static int itemEffectType;
	public static int itemEffectDeletable;
	public static int itemEffectValue;
	public static int itemEffectValue2;
	public static int itemEffectTimerEnd;
	public static int itemEffectTimerInterval;
	public static int itemIdSel;

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	////TODO: remove this code under and make a startup xml loader to hash.

	public static void loadFromXml() {
 
  	try {
		File fXmlFile = new File("data/static_data/items/item_effects.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("item");

		for(int i=0;i<nList.getLength();i++)
		{
 		 NodeList nameNlc = doc.getElementsByTagName("item_id");
 		 Element nameElements1=(Element)nameNlc.item(i);
 		 String itemIdXml=nameElements1.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc2 = doc.getElementsByTagName("type");
 		 Element nameElements2=(Element)nameNlc2.item(i);
 		 String itemTypeXml=nameElements2.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc3 = doc.getElementsByTagName("deletable");
 		 Element nameElements3=(Element)nameNlc3.item(i);
 		 String itemDeletableXml=nameElements3.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc4 = doc.getElementsByTagName("value");
 		 Element nameElements4=(Element)nameNlc4.item(i);
 		 String itemValueXml=nameElements4.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc5 = doc.getElementsByTagName("value2");
 		 Element nameElements5=(Element)nameNlc5.item(i);
 		 String itemValue2Xml=nameElements5.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc6 = doc.getElementsByTagName("timer_end");
 		 Element nameElements6=(Element)nameNlc6.item(i);
 		 String timerEndXml=nameElements6.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc7 = doc.getElementsByTagName("timer_interval");
 		 Element nameElements7=(Element)nameNlc7.item(i);
 		 String timerIntervalXml=nameElements5.getChildNodes().item(0).getNodeValue();

 		 NodeList nameNlc8 = doc.getElementsByTagName("name");
 		 Element nameElements8=(Element)nameNlc8.item(i);
 		 String itemNameXml=nameElements8.getChildNodes().item(0).getNodeValue();

		 int itemIdXmlInt = Integer.parseInt(itemIdXml);
		 int itemDeletableXmlInt = Integer.parseInt(itemDeletableXml);
		 int itemValueXmlInt = Integer.parseInt(itemValueXml);
		 int itemValue2XmlInt = Integer.parseInt(itemValue2Xml);
		 int itemTimerEndXmlInt = Integer.parseInt(timerEndXml);
		 int itemTimerIntervalXmlInt = Integer.parseInt(timerIntervalXml);
		 int itemTypeXmlInt = Integer.parseInt(itemTypeXml);
		 


		 if (itemIdXmlInt==itemId) {
			itemName = itemNameXml;
			itemEffectType = itemTypeXmlInt;
			itemEffectDeletable = itemDeletableXmlInt;
			itemEffectValue = itemValueXmlInt;
			itemEffectValue2 = itemValue2XmlInt;
			itemEffectTimerEnd = itemTimerEndXmlInt;
			itemEffectTimerInterval = itemTimerIntervalXmlInt;
			i=nList.getLength();
			itemIdSel = 1;
		 } else {
			itemIdSel = 0;
		 }

		}
 	 } catch (Exception e) {
    	e.printStackTrace();
 	 }
 
 	}

	///////////////////////

	public int getCheckTemplate() {
		return itemIdSel;
	}
	public String getItemName() {
		return itemName;
	}
	public int getType() {
		return itemEffectType;
	}
	public int getDeletable() {
		return itemEffectDeletable;
	}
	public int getValue() {
		return itemEffectValue;
	}
	public int getValue2() {
		return itemEffectValue2;
	}
	public int getTimerEnd() {
		return itemEffectTimerEnd;
	}
	public int getTimerInterval() {
		return itemEffectTimerInterval;
	}
}
