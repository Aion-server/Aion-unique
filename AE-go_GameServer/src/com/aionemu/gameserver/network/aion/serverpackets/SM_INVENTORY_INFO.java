/*
 * This file is part of aion-unique <aion-unique.com>.
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
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * In this packet Server is sending Inventory Info
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * @finisher Avol ;d
 * 
 * modified by ATracer
 */
public class SM_INVENTORY_INFO extends InventoryPacket
{	
	public static final int EMPTY = 0;
	public static final int FULL = 1;
	public int CUBE = 0;

	private List<Item> items;
	private int size;

	public int packetType = FULL;

	/**
	 * @param items
	 */
	public SM_INVENTORY_INFO(List<Item> items, int cubesize)
	{
		//this should prevent client crashes but need to discover when item is null
		items.removeAll(Collections.singletonList(null));
		this.items = items;
		this.size = items.size();
		this.CUBE = cubesize;
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
		writeC(buf, CUBE); // cube size
		writeH(buf, 0); // padding?
		writeH(buf, size); // number of entries

		for(Item item : items)
		{
			writeGeneralInfo(buf, item);

			ItemTemplate itemTemplate = item.getItemTemplate();

			if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
			{
				writeKinah(buf, item, true);
			}
			else if (itemTemplate.isWeapon())
			{
				writeWeaponInfo(buf, item, true);
			}
			else if (itemTemplate.isArmor())
			{
				writeArmorInfo(buf,item, true, false);
			}
			else
			{
				writeGeneralItemInfo(buf, item, false, false);
				writeC(buf, 0);
			}
		}
	}
}
