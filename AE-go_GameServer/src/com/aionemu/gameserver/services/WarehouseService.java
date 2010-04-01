/**
 * This file is part of aion-unique <aion-unique.org>.
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

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.WarehouseExpandData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.templates.WarehouseExpandTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Simple
 */
public class WarehouseService
{
	private static final Logger	log			= Logger.getLogger(WarehouseService.class);

	@Inject
	WarehouseExpandData			warehouseExpandData;

	private static final int	MIN_EXPAND	= 0;
	private static final int	MAX_EXPAND	= 10;

	/**
	 * Shows Question window and expands on positive response
	 * 
	 * @param player
	 * @param npc
	 */
	public void expandWarehouse(final Player player, Npc npc)
	{
		final WarehouseExpandTemplate expandTemplate = warehouseExpandData.getWarehouseExpandListTemplate(npc
			.getNpcId());

		if(expandTemplate == null)
		{
			log.error("Warehouse Expand Template could not be found for Npc ID: " + npc.getObjectId());
			return;
		}

		if(npcCanExpandLevel(expandTemplate, player.getWarehouseSize() + 1)
			&& validateNewSize(player.getWarehouseSize() + 1))

			if(validateNewSize(player.getWarehouseSize() + 1))
			{
				/**
				 * Check if our player can pay the warehouse expand price
				 */
				final int price = getPriceByLevel(expandTemplate, player.getWarehouseSize() + 1);
				if(player.getInventory().getKinahItem().getItemCount() < price)
				{
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300831));
					return;
				}

				RequestResponseHandler responseHandler = new RequestResponseHandler(npc){
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						expand(responder);
						player.getInventory().decreaseKinah(price);
					}

					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// nothing to do
					}
				};

				boolean result = player.getResponseRequester().putRequest(900686, responseHandler);
				if(result)
				{
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(900686, 0, String.valueOf(price)));
				}
			}
			else
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300430));
	}

	/**
	 * 
	 * @param player
	 */
	private void expand(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300433, "8")); // 8 Slots added
		player.setWarehouseSize(player.getWarehouseSize() + 1);
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(
			StorageType.REGULAR_WAREHOUSE.getId()).getStorageItems(), StorageType.REGULAR_WAREHOUSE.getId(), player
			.getWarehouseSize()));
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.REGULAR_WAREHOUSE.getId(), player
			.getWarehouseSize()));
	}

	/**
	 * Checks if new player cube is not max
	 * 
	 * @param level
	 * @return true or false
	 */
	private boolean validateNewSize(int level)
	{
		// check min and max level
		if(level < MIN_EXPAND || level > MAX_EXPAND)
			return false;
		return true;
	}

	/**
	 * Checks if npc can expand level
	 * 
	 * @param clist
	 * @param level
	 * @return true or false
	 */
	private boolean npcCanExpandLevel(WarehouseExpandTemplate clist, int level)
	{
		// check if level exists in template
		if(!clist.contains(level))
			return false;
		return true;
	}

	/**
	 * The guy who created cube template should blame himself :) One day I will rewrite them
	 * 
	 * @param template
	 * @param level
	 * @return
	 */
	private int getPriceByLevel(WarehouseExpandTemplate clist, int level)
	{
		return clist.get(level).getPrice();
	}
	
	
	/**
	 *  Sends correctly warehouse packets
	 *  
	 * @param player
	 */
	public void sendWarehouseInfo(Player player)
	{		
//		List<Item> items = player.getStorage(StorageType.REGULAR_WAREHOUSE.getId()).getStorageItems();
//		int whSize = player.getWarehouseSize();
//
//		int itemsSize = items.size();
//
//		if(itemsSize != 0)
//		{
//			int index = 0;
//			while(index + 20 < itemsSize)
//			{
//				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, index + 20),
//					StorageType.REGULAR_WAREHOUSE.getId(), whSize));
//				index += 20;
//			}
//			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(items.subList(index, itemsSize),
//				StorageType.REGULAR_WAREHOUSE.getId(), whSize));
//		}

		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(
			StorageType.REGULAR_WAREHOUSE.getId()).getStorageItems(),
			StorageType.REGULAR_WAREHOUSE.getId(), player.getWarehouseSize()));
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.REGULAR_WAREHOUSE
			.getId(), player.getWarehouseSize())); // strange
		// retail
		// way of sending
		// warehouse packets
		PacketSendUtility
			.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(
				StorageType.ACCOUNT_WAREHOUSE.getId()).getAllItems(),
				StorageType.ACCOUNT_WAREHOUSE.getId(), 0));
		PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, StorageType.ACCOUNT_WAREHOUSE
			.getId(), 0));
	}
}