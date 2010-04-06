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
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;
/**
 *
 * @author kosyachok
 */
public class SM_WAREHOUSE_INFO extends InventoryPacket
{
	private int warehouseType;
	private List<Item> itemList;
	private boolean firstPacket;
	private int expandLvl;

	public SM_WAREHOUSE_INFO(List<Item> items, int warehouseType, int expandLvl, boolean firstPacket)
	{
		this.warehouseType = warehouseType;
		this.expandLvl = expandLvl;
		this.firstPacket = firstPacket;
		if(items == null)
			this.itemList = Collections.emptyList();
		else
			this.itemList = items;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, warehouseType);
		writeC(buf, firstPacket ? 1 : 0);
		writeC(buf, expandLvl); //warehouse expand (0 - 9)
		writeH(buf, 0);
		writeH(buf, itemList.size());
		for(Item item : itemList)
		{
			writeGeneralInfo(buf, item);

			ItemTemplate itemTemplate = item.getItemTemplate();

			if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
				writeKinah(buf, item, false);

			else if (itemTemplate.isWeapon())
				writeWeaponInfo(buf, item, false);

			else if (itemTemplate.isArmor())
				writeArmorInfo(buf, item, false, false, false);

			else
				writeGeneralItemInfo(buf, item, false, false);
		}
	}

	@Override
	protected void writeGeneralInfo(ByteBuffer buf, Item item)
	{
		writeD(buf, item.getObjectId());
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(buf, itemTemplate.getTemplateId());
		writeC(buf, 0); //some item info (4 - weapon, 7 - armor, 8 - rings, 17 - bottles)
		writeH(buf, 0x24);
		writeD(buf, itemTemplate.getNameId());
		writeH(buf, 0);
	}

	@Override
	protected void writeKinah(ByteBuffer buf, Item item, boolean isInventory)
	{
		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeH(buf, item.getItemMask());
		writeD(buf, item.getItemCount());
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0xFF); // FF FF equipment
		writeC(buf, 0xFF);
	}
}
