/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.ItemSlot;

/**
 * @author Luno
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item_template")
public class ItemTemplate
{
	
	private int			itemId;

	@XmlAttribute(name = "target_slot")
	private ItemSlot	itemSlot;

	public int getItemId()
	{
		return itemId;
	}

	public ItemSlot getItemSlot()
	{
		return itemSlot;
	}
	
	@SuppressWarnings("unused")
	@XmlID
	@XmlAttribute(name = "item_id", required = true)
	private void setXmlUid(String uid)
	{
		/*
		 * This method is used only by JAXB unmarshaller.
		 * I couldn't set annotations at field, because
		 * ID must be a string. 
		 */
		itemId = Integer.parseInt(uid);
	}
}
