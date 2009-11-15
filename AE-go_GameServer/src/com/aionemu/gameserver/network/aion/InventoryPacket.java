/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.network.aion;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author ATracer
 *
 */
public abstract class InventoryPacket extends AionServerPacket
{
	private static Logger log= Logger.getLogger(InventoryPacket.class);
	
	/**
	 *  The header of every item block
	 * @param buf
	 * @param item
	 */
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getItemId());
		writeH(buf, 0x24);
		writeD(buf, Integer.parseInt(itemTemplate.getDescription()));
		writeH(buf, 0);
	}
	
	/**
	 *  All misc items
	 * @param buf
	 * @param item
	 */
	protected void writeGeneralItemInfo(ByteBuffer buf, Item item)
	{
		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeC(buf, 0x3E); //or can be 0x1E
		writeC(buf, 0x63); // ?
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, item.getEquipmentSlot()); // not equipable items
		writeC(buf, 0);
	}
	
	/**
	 * 
	 * @param buf
	 * @param item
	 */
	protected void writeKinah(ByteBuffer buf, Item item)
	{
		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeC(buf, 0x1E); //or can be 0x1E
		writeC(buf, 0x63); // ?
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 255); // FF FF equipment
		writeC(buf, 0);
	}
	
	/**
	 *  For all weapon. Weapon is identified by weapon_type in xml
	 *  
	 * @param buf
	 * @param item
	 */
	protected void writeWeaponInfo(ByteBuffer buf, Item item)
	{
		int itemSlotId = item.getEquipmentSlot();
		writeH(buf, 0x46);
		writeC(buf, 0x06);	
		writeD(buf, item.isEquipped() ? itemSlotId : 0);
		writeC(buf, 0x01);
		writeD(buf,	ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot()).get(0).getSlotIdMask());
		writeD(buf, 0x02);
		writeC(buf, 0x0B); //? some details separator
		writeH(buf, 0);
		writeD(buf, item.getItemTemplate().getItemId());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x3E);
		writeC(buf, 0x0A);
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
		writeC(buf,  0);//item.isEquipped() ? 1 : 0
	}
	
	/**
	 *  For all armor. Armor is identified by armor_type in xml
	 * @param buf
	 * @param item
	 */
	protected void writeArmorInfo(ByteBuffer buf, Item item)
	{
		int itemSlotId = item.getEquipmentSlot();
		writeH(buf, 0x4A);
		writeC(buf, 0x06);		
		writeD(buf, item.isEquipped() ? itemSlotId : 0);
		writeC(buf, 0x02);
		writeD(buf,	ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot()).get(0).getSlotIdMask());
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x0B); //? some details separator
		writeH(buf, 0);
		writeD(buf, item.getItemTemplate().getItemId());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x3E);
		writeC(buf, 0x02);
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
		writeC(buf,  1);//item.isEquipped() ? 1 : 0
	}
}
