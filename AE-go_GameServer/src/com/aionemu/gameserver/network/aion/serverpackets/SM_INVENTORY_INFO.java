/*
 * This file is part of aion-unique <aionunique.com>.
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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.sun.tools.javac.jvm.Items;

/**
 * In this packet Server is sending Inventory Info?
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * @finisher Avol ;d
 */
public class SM_INVENTORY_INFO extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_INVENTORY_INFO.class);
	
	public static final int EMPTY = 0;
	public static final int FULL = 1;
	
	private List<Item> items;
	private int size;

	public int packetType = FULL;
	
	/**
	 * @param items
	 */
	public SM_INVENTORY_INFO(List<Item> items)
	{
		this.items = items;
		this.size = items.size();
	}
	
	/**
	 * @param isEmpty
	 */
	public SM_INVENTORY_INFO()
	{
		this.packetType = EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		if(this.packetType == EMPTY)
		{
			writeD(buf, 0);
			writeH(buf, 0);
			return;
		}
		
		// something wrong with cube part.
		writeC(buf, 1); // TRUE/FALSE (1/0) update cube size
		writeC(buf, 0); // cube size
		writeH(buf, 0); // padding?
		writeH(buf, size); // number of entries

		for(Item item : items)
		{
			writeGeneralInfo(buf, item);
			
			ItemTemplate itemTemplate = item.getItemTemplate();
			int itemSlot = item.getEquipmentSlot();
			
			if(itemTemplate.getItemId() == ItemId.KINAH.value())
			{
				writeKinah(buf, item);
			}
			//TODO write based on item template type - armor/weapon/misc
			else if(item.isEquipped())
			{
				writeEquipmentInfo(buf, item);
			}
			else
			{
				writeGeneralItemInfo(buf, item);
			}
			
		}
	}
	
	private void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getItemId());
		writeH(buf, 0x24);
		writeD(buf, Integer.parseInt(itemTemplate.getDescription()));
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
		writeH(buf, 255); // FF FF equipment
		writeC(buf, 0);
	}
	
	private void writeKinah(ByteBuffer buf, Item item)
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
	
	private void writeEquipmentInfo(ByteBuffer buf, Item item)
	{
		int itemSlotId = item.getEquipmentSlot();
		writeH(buf, 0x4A);
		writeC(buf, 0x06);		
		writeD(buf, itemSlotId);
		writeC(buf, 0x02); //can be 0x01 for weapons
		writeD(buf,	itemSlotId);
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
		writeH(buf, 255); // FF FF equipment
		writeC(buf,  1);//item.isEquipped() ? 1 : 0
	}
}
