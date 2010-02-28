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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * 
 * @author ATracer
 * 
 */
public class SM_INVENTORY_UPDATE extends InventoryPacket
{
	private List<Item> items;
	private int size;

	public SM_INVENTORY_UPDATE(List<Item> items)
	{
		this.items = items;
		this.size = items.size();
	}

	/**
	 * {@inheritDoc} dc
	 */

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		writeH(buf, 25); // padding?
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
				writeGeneralItemInfo(buf, item, item.isQuest(), false);
				writeC(buf, 0);
			}
		}
	}	
}