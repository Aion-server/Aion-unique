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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.trade.TradeItem;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class TradeService
{
	@Inject
	private ItemService itemService;

	/**
	 * 
	 * @param player
	 * @param tradeList
	 * @return
	 */
	public boolean performBuyFromShop(Player player, TradeList tradeList)
	{
		Inventory inventory  = player.getInventory();
		Item kinahItem = inventory.getKinahItem();

		int freeSlots = inventory.getLimit() - inventory.getUnquippedItems().size() + 1;
		//1. check kinah
		int kinahCount = kinahItem.getItemCount();
		int tradeListPrice = tradeList.calculateBuyListPrice() * 2;

		if(kinahCount < tradeListPrice)
			return false; //ban :)

		//2. check free slots, need to check retail behaviour
		if(freeSlots < tradeList.size())
			return false; //TODO message

		List<Item> addedItems = new ArrayList<Item>();
		for(TradeItem tradeItem : tradeList.getTradeItems())
		{
			Item item = itemService.newItem(tradeItem.getItemId(), tradeItem.getCount());

			if(item != null)
			{
				Item resultItem = inventory.addToBag(item);
				if(resultItem != null)
				{
					if(resultItem.getObjectId() != item.getObjectId())
						itemService.releaseItemId(item);
					addedItems.add(resultItem);
				}
				else
				{
					itemService.releaseItemId(item);
				}

			}
		}
		kinahItem.decreaseItemCount(tradeListPrice);
		PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(kinahItem));
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(addedItems));
		//TODO message
		return true;
	}

	/**
	 * 
	 * @param player
	 * @param tradeList
	 * @return
	 */
	public boolean performSellToShop(Player player, TradeList tradeList)
	{
		Inventory inventory = player.getInventory();

		int kinahReward = 0;		
		for(TradeItem tradeItem : tradeList.getTradeItems())
		{
			Item item = inventory.getItemByObjId(tradeItem.getItemId());
			// 1) don't allow to sell fake items;
			if(item == null)
				return false;

			if(item.getItemCount() - tradeItem.getCount() == 0)
			{
				inventory.removeFromBag(item);    // need to be here to avoid exploit by sending packet with many items with same unique ids
				kinahReward += item.getItemTemplate().getPrice() * item.getItemCount();
				//TODO check retail packet here
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId()));
			}
			else if(item.getItemCount() - tradeItem.getCount() > 0)
			{
				if(item.decreaseItemCount(tradeItem.getCount()))
				{
					//TODO check retail packet here
					kinahReward += item.getItemTemplate().getPrice() * tradeItem.getCount();
					PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(item));
				}
				else return false;
			}
			else return false;
		}

		Item kinahItem = inventory.getKinahItem();
		kinahItem.increaseItemCount(kinahReward / 2);
		PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(kinahItem));

		return true;
	}
}
