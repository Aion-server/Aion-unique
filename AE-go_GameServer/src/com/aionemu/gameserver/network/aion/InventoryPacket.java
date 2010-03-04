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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.SimpleModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 *
 */
public abstract class InventoryPacket extends AionServerPacket
{	
	/**
	 *  The header of every item block
	 * @param buf
	 * @param item
	 */
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getTemplateId());
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
	}

	/**
	 *  All misc items
	 * @param buf
	 * @param item
	 */
	protected void writeGeneralItemInfo(ByteBuffer buf, Item item, boolean isQuest, boolean privateStore)
	{
		writeH(buf, 0x16); //length of details
		writeC(buf, 0);

		if(isQuest)
		{
			writeC(buf, 0x20);
			writeC(buf, 0x65); // ?
		}else
		{
			writeC(buf, 0x3E); //or can be 0x1E 0x3E 0x20 (quest)
			writeC(buf, 0x63); // ?
		}

		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		if(!privateStore)
			writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, item.getEquipmentSlot()); // not equipable items		
	}

	/**
	 * 
	 * @param buf
	 * @param item
	 */
	protected void writeKinah(ByteBuffer buf, Item item, boolean isInventory)
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
		if(isInventory)
			writeC(buf, 0);
	}

	/**
	 * Write weapon info for non weapon switch items
	 * 
	 * @param buf
	 * @param item
	 * @param isInventory
	 */
	protected void writeWeaponInfo(ByteBuffer buf, Item item, boolean isInventory)
	{
		this.writeWeaponInfo(buf, item, isInventory, false, false);
	}
	
	/**
	 *  For all weapon. Weapon is identified by weapon_type in xml
	 *  
	 * @param buf
	 * @param item
	 */
	protected void writeWeaponInfo(ByteBuffer buf, Item item, boolean isInventory, boolean isWeaponSwitch, boolean privateStore)
	{
		int itemSlotId = item.getEquipmentSlot();
		
		if(isWeaponSwitch)
			writeH(buf, 0x05); // next bytes count ??
		else
			writeH(buf, 0x4B); // next bytes count ??
		
		writeC(buf, 0x06);	
		writeD(buf, item.isEquipped() ? itemSlotId : 0);
		
		if(!isWeaponSwitch)
		{
			writeC(buf, 0x01);
			writeD(buf,	ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot()).get(0).getSlotIdMask());
			writeD(buf, 0x02);
			writeH(buf, 0x0B); //? some details separator
			writeC(buf, 0); //enchant (1-10)
			writeD(buf, item.getItemTemplate().getTemplateId());
			writeC(buf, 0);

			writeItemStones(buf, item);


			writeC(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);

			writeD(buf, 0);//unk 1.5.1.9
			writeC(buf, 0);//unk 1.5.1.9

			writeC(buf, 0x3E);
			writeC(buf, 0x0A);
			writeD(buf, item.getItemCount());
			writeD(buf, 0);
			writeD(buf, 0);
			writeD(buf, 0);
			if(!privateStore)
				writeH(buf, 0);
			writeC(buf, 0);
			writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
			if(isInventory)
				writeC(buf,  0);//item.isEquipped() ? 1 : 0
		}
	}

	/**
	 *  Writes manastones : 6C - statenum mask, 6H - value
	 * @param buf
	 * @param item
	 */
	private void writeItemStones(ByteBuffer buf, Item item)
	{
		int count = 0;
		List<ItemStone> itemStones = item.getItemStones();

		if(itemStones != null)
		{
			for(ItemStone itemStone : itemStones)
			{
				if(count == 6)
					break;

				StatModifier modifier = itemStone.getFirstModifier();
				if(modifier != null)
				{
					count++;
					writeC(buf, modifier.getStat().getItemStoneMask());
				}
			}
			writeB(buf, new byte[(6-count)]);
			count = 0;
			for(ItemStone itemStone : itemStones)
			{
				if(count == 6)
					break;

				StatModifier modifier = itemStone.getFirstModifier();
				if(modifier != null)
				{
					count++;
					writeH(buf, ((SimpleModifier)modifier).getValue());
				}
			}
			writeB(buf, new byte[(6-count)*2]);
		}
		else
		{
			writeB(buf, new byte[18]);
		}

		//for now max 6 stones - write some junk

	}

	/**
	 *  For all armor. Armor is identified by armor_type in xml
	 * @param buf
	 * @param item
	 */
	protected void writeArmorInfo(ByteBuffer buf, Item item, boolean isInventory, boolean privateStore)
	{
		int itemSlotId = item.getEquipmentSlot();
		writeH(buf, 0x4F);
		writeC(buf, 0x06);
		writeD(buf, item.isEquipped() ? itemSlotId : 0);
		writeC(buf, 0x02);
		writeD(buf,	ItemSlot.getSlotsFor(item.getItemTemplate().getItemSlot()).get(0).getSlotIdMask());
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0x0B); //? some details separator
		writeC(buf, 0); //enchant (1-10)
		writeD(buf, item.getItemTemplate().getTemplateId());

		writeC(buf, 0);

		writeItemStones(buf, item);

		writeC(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);

		writeD(buf, 0);//unk 1.5.1.9
		writeC(buf, 0);//unk 1.5.1.9

		writeC(buf, 0x3E);
		writeC(buf, 0x02);
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		if(!privateStore)
			writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
		if(isInventory)
			writeC(buf,  1);//item.isEquipped() ? 1 : 0
	}
}
