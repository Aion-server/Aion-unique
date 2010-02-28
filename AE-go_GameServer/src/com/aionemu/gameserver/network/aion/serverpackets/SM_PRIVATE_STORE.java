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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PrivateStore;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.InventoryPacket;

/**
 * @author Simple
 */
public class SM_PRIVATE_STORE extends InventoryPacket
{
	/** Private store Information **/
	private PrivateStore	store;

	public SM_PRIVATE_STORE(PrivateStore store)
	{
		this.store = store;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		if(store != null)
		{
			Player storePlayer = store.getOwner();
			LinkedHashMap<Integer, TradePSItem> soldItems = store.getSoldItems();
			
			writeD(buf, storePlayer.getObjectId());
			writeH(buf, soldItems.size());
			for(Integer itemObjId : soldItems.keySet())
			{
				Item item = storePlayer.getInventory().getItemByObjId(itemObjId);
				TradePSItem tradeItem = store.getTradeItemById(itemObjId);
				int price = tradeItem.getPrice();
				writeD(buf, itemObjId);
				writeD(buf, item.getItemTemplate().getTemplateId());
				writeH(buf, tradeItem.getCount());
				writeD(buf, price);

				ItemTemplate itemTemplate = item.getItemTemplate();

				if (itemTemplate.isWeapon())
				{
					writeWeaponInfo(buf, item, false, false, true);
				}
				else if (itemTemplate.isArmor())
				{
					writeArmorInfo(buf, item, false, true);
				}
				else
				{
					writeGeneralItemInfo(buf, item, false, true);
				}
			}
		}
	}
}