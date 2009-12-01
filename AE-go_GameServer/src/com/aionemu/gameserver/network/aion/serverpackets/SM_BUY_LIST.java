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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerStore;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * @author Xav
 *
 */
public class SM_BUY_LIST extends InventoryPacket
{
	private int playerObjId;
	private PlayerStore store;
	/**
	 * 
	 */
	public SM_BUY_LIST(Player seller)
	{
		this.playerObjId = seller.getObjectId();
		this.store = seller.getStore();
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf,playerObjId);
		
		writeH(buf,store.getSoldItems().size());
		for (Entry<Item, Integer> itemToSell : store.getSoldItems().entrySet())
		{
			Item item = itemToSell.getKey();
			writeD(buf,item.getObjectId());
			writeD(buf,item.getItemTemplate().getItemId());
			writeH(buf,item.getItemCount());
			writeD(buf,itemToSell.getValue());
			
			ItemTemplate itemTemplate = item.getItemTemplate();
			
			if(itemTemplate.getItemId() == ItemId.KINAH.value())
			{
				writeKinah(buf, item);
			}
			else if (itemTemplate.isWeapon())
			{
				writeWeaponInfo(buf, item);
			}
			else if (itemTemplate.isArmor())
			{
				writeArmorInfo(buf,item);
			}
			else
			{
				writeGeneralItemInfo(buf, item);
			}
		}
	}
}
