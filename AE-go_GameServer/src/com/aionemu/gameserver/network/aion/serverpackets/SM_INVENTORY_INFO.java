/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.ItemList;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending Inventory Info?
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * @finisher Avol ;d
 */
public class SM_INVENTORY_INFO extends AionServerPacket
{
	private int uniqueItemId;
	private int itemId;
	private int itemNameId;
	private int itemQuanty;
	private int entries;
	private int slot;
	
	private List<Item> items;
	private int size;
	/**
	 * Constructs new <tt>SM_INVENTORY_INFO </tt> packet
	 */
	public SM_INVENTORY_INFO(int uniqueItemId, int itemId, int itemQuanty, int entries, int slot)
	{
		this.uniqueItemId = uniqueItemId;
		this.itemId = itemId;
		this.itemQuanty = itemQuanty;
		this.entries = entries;
		this.slot = slot;
	}
	
	/**
	 * @param inventory
	 */
	public SM_INVENTORY_INFO(Inventory inventory)
	{
		this.items = inventory.getItems();
		this.size = items.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		// something wrong with cube part.
		writeC(buf, 1); // TRUE/FALSE (1/0) update cube size
		writeC(buf, 0); // cube size
		writeH(buf, 0); // padding?
		writeH(buf, size); // number of entries

		
		for(Item item : items)
		{
			writeGeneralInfo(buf, item);
			
			ItemTemplate itemTemplate = item.getItemTemplate();
			int itemSlot = itemTemplate.getItemSlot();
			
			if(itemSlot != 0)
			{
				writeGeneralItemInfo(buf, item);
			}
			else
			{
				writeEquipmentInfo(buf, item);
			}
		}
	}
	
	private void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getItemId());
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getItemId());
		writeH(buf, 0);
	}
	
	private void writeGeneralItemInfo(ByteBuffer buf, Item item)
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
		writeH(buf, item.getEquipmentSlot());
		writeC(buf, 0);
	}
	
	private void writeEquipmentInfo(ByteBuffer buf, Item item)
	{
		//TODO the structure is incorrect here
		writeC(buf, 0x01); //can be 0x02
		writeD(buf, convertSlot(item.getEquipmentSlot()));
		writeD(buf, 0);
		writeD(buf, 0);
		writeC(buf, 0x06); //eq data follows
		writeD(buf,	convertSlot(item.getEquipmentSlot()));
		writeC(buf, 0);
		writeC(buf, 0x3E);
		writeC(buf, 0x0A);
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 255); // FF FF equipment
		writeC(buf, item.isEquipped() ? 1 : 0);
	}
	
	//TODO find out dependency of slot number to slot in xml
	// calculate mask
	private int convertSlot(int slotNumber)
	{
		switch(slotNumber)
		{
			case 5 :
				return 1;// or 2 weapon
			case 6 :
				return 8192;//or 16384 power shard
			case 7 :
				return 256;// 512 rings
			case 9 :
				return 64;// 128 earrings
			default : 
				return 0;
		}
	}
}
