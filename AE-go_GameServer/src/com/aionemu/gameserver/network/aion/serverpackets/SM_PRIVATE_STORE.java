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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PrivateStore;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.trade.TradeItem;
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
			
			writeD(buf, storePlayer.getObjectId());
			writeH(buf, store.getSoldItems().size());
			for(TradeItem tradeItem : store.getSoldItems().keySet())
			{
				Item item = storePlayer.getInventory().getItemByObjId(tradeItem.getItemId());
				int price = store.getSoldItems().get(tradeItem);
				writeD(buf, item.getObjectId());
				writeD(buf, item.getItemTemplate().getItemId());
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