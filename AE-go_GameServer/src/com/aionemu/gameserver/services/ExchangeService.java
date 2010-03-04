/*
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

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.trade.Exchange;
import com.aionemu.gameserver.model.trade.ExchangeItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_ADD_KINAH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class ExchangeService
{
	@Inject
	private ItemService itemService;

	private Map<Integer, Exchange> exchanges = new HashMap<Integer, Exchange>();

	/**
	 * @param objectId
	 * @param objectId2
	 */
	public void registerExchange(Player player1, Player player2)
	{
		if(!validateParticipants(player1, player2))
			return;
		
		player1.setTrading(true);
		player2.setTrading(true);
		
		exchanges.put(player1.getObjectId(), new Exchange(player1, player2));
		exchanges.put(player2.getObjectId(), new Exchange(player2, player1));

		PacketSendUtility.sendPacket(player2, new SM_EXCHANGE_REQUEST(player1.getName()));
		PacketSendUtility.sendPacket(player1, new SM_EXCHANGE_REQUEST(player2.getName()));
	}

	/**
	 * @param player1
	 * @param player2
	 */
	private boolean validateParticipants(Player player1, Player player2)
	{
		return RestrictionsManager.canTrade(player1) && RestrictionsManager.canTrade(player2);
	}

	public Player getCurrentParter(Player player)
	{
		Exchange exchange = exchanges.get(player.getObjectId());
		return exchange != null ? exchange.getTargetPlayer() : null;
	}
	/**
	 * 
	 * @param player
	 * @return Exchange
	 */
	public Exchange getCurrentExchange(Player player)
	{
		return exchanges.get(player.getObjectId());
	}

	/**
	 * 
	 * @param player
	 * @return Exchange
	 */
	public Exchange getCurrentParnterExchange(Player player)
	{
		Player partner = getCurrentParter(player);
		return partner != null ? getCurrentExchange(partner) : null;
	}

	/**
	 * @param activePlayer
	 * @param itemCount
	 */
	public void addKinah(Player activePlayer, int itemCount)
	{
		Exchange currentExchange = getCurrentExchange(activePlayer);
		if(currentExchange.isLocked())
			return;
		
		if(itemCount < 1)
			return;

		//count total amount in inventory
		int availableCount = activePlayer.getInventory().getKinahItem().getItemCount();
		//count amount that was already added to exchange
		availableCount -= currentExchange.getKinahCount();

		int countToAdd = availableCount > itemCount ? itemCount : availableCount;

		if(countToAdd > 0)
		{
			Player partner = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_KINAH(countToAdd, 0));
			PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_KINAH(countToAdd, 1));
			currentExchange.addKinah(countToAdd);
		}
	}

	/**
	 * @param activePlayer
	 * @param itemObjId
	 * @param itemCount
	 */
	public void addItem(Player activePlayer, int itemObjId, int itemCount)
	{
		Item item = activePlayer.getInventory().getItemByObjId(itemObjId);
		if(item == null)
			return;
		
		if(itemCount < 1)
			return;

		Player partner = getCurrentParter(activePlayer);
		Exchange currentExchange = getCurrentExchange(activePlayer);

		if(currentExchange.isLocked())
			return;

		if(currentExchange.isExchangeListFull())
			return;

		ExchangeItem exchangeItem = currentExchange.getItems().get(item.getObjectId());

		int actuallAddCount = 0;
		//item was not added previosly
		if(exchangeItem == null)
		{
			if(itemCount >= item.getItemCount())
			{
				exchangeItem =  new ExchangeItem(item);
				currentExchange.addItem(itemObjId, exchangeItem);
				actuallAddCount = item.getItemCount();
			}
			else
			{
				Item newItem = itemService.newItem(item.getItemTemplate().getTemplateId(), itemCount);
				exchangeItem = new ExchangeItem(itemObjId, itemCount, newItem, item);
				currentExchange.addItem(itemObjId, exchangeItem);
				actuallAddCount = itemCount;			
			}
		}
		//item was already added
		else
		{
			//if player add item count that is more than possible
			//happens with exploits
			if(item.getItemCount() == exchangeItem.getItemCount())
				return;

			int possibleToAdd = item.getItemCount() - exchangeItem.getItemCount();

			actuallAddCount = itemCount > possibleToAdd ? possibleToAdd : itemCount;	

			exchangeItem.addCount(actuallAddCount);

			//if added full count of initial item - just make update
			if(exchangeItem.getItemCount() == item.getItemCount())
			{
				exchangeItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
			}			
		}	

		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_ADD_ITEM(0, exchangeItem.getNewItem()));
		PacketSendUtility.sendPacket(partner, new SM_EXCHANGE_ADD_ITEM(1, exchangeItem.getNewItem()));
	}

	/**
	 * @param activePlayer
	 */
	public void lockExchange(Player activePlayer)
	{
		Exchange exchange = getCurrentExchange(activePlayer);
		if(exchange != null)
		{
			exchange.lock();
			Player currentParter = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(3));
		}
	}

	/**
	 * @param activePlayer
	 */
	public void cancelExchange(Player activePlayer)
	{
		Exchange exchange1 = getCurrentExchange(activePlayer);
		if(exchange1 != null)
			exchange1.cancel(itemService);

		Exchange exchange2 = getCurrentParnterExchange(activePlayer);
		if(exchange2 != null)
			exchange2.cancel(itemService);

		Player currentParter = getCurrentParter(activePlayer);;		
		cleanupExchanges(activePlayer, currentParter);	
		if(currentParter != null)
			PacketSendUtility.sendPacket(currentParter, new SM_EXCHANGE_CONFIRMATION(1));
	}

	/**
	 * @param activePlayer
	 */
	public void confirmExchange(Player activePlayer)
	{
		Exchange currentExchange = getCurrentExchange(activePlayer);
		currentExchange.confirm();

		Player currentPartner = getCurrentParter(activePlayer);	
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(2));

		if(getCurrentExchange(currentPartner).isConfirmed())
		{
			performTrade(activePlayer, currentPartner);
		}
	}

	/**
	 * @param activePlayer
	 * @param currentPartner
	 */
	private void performTrade(Player activePlayer, Player currentPartner)
	{
		//TODO message here
		if(!validateExchange(activePlayer, currentPartner))
			return;


		PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_CONFIRMATION(0));
		PacketSendUtility.sendPacket(currentPartner, new SM_EXCHANGE_CONFIRMATION(0));

		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);

		cleanupExchanges(activePlayer, currentPartner);

		removeItemsFromInventory(activePlayer, exchange1);
		removeItemsFromInventory(currentPartner, exchange2);

		putItemToInventory(currentPartner, exchange1);
		putItemToInventory(activePlayer, exchange2);	
	}

	/**
	 * 
	 * @param activePlayer
	 * @param currentPartner
	 */
	private void cleanupExchanges(Player activePlayer, Player currentPartner)
	{
		if(activePlayer != null)
		{
			exchanges.remove(activePlayer.getObjectId());
			activePlayer.setTrading(false);
		}
			
		if(currentPartner != null)
		{
			exchanges.remove(currentPartner.getObjectId());
			currentPartner.setTrading(false);
		}
	}

	/**
	 * @param player
	 * @param exchange
	 */
	private void removeItemsFromInventory(Player player, Exchange exchange)
	{
		Storage inventory = player.getInventory();

		for(ExchangeItem exchangeItem : exchange.getItems().values())
		{
			int itemObjId = exchangeItem.getItemObjId();
			Item itemInInventory = inventory.getItemByObjId(itemObjId);

			int itemCount = exchangeItem.getNewItem().getItemCount();

			switch(exchangeItem.getPersistentState())
			{
				case NEW:
					itemInInventory.decreaseItemCount(itemCount);
					PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(itemInInventory));
					break;
				case UPDATE_REQUIRED:
					inventory.removeFromBag(itemInInventory, false);
					PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemInInventory.getObjectId()));
					break;
			}	
		}
		player.getInventory().decreaseKinah(exchange.getKinahCount());
	}

	/**
	 * @param activePlayer
	 * @param currentPartner
	 * @return
	 */
	private boolean validateExchange(Player activePlayer, Player currentPartner)
	{
		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);

		return validateInventorySize(activePlayer, exchange2) 
		&& validateInventorySize(currentPartner, exchange1);
	}

	private boolean validateInventorySize(Player activePlayer, Exchange exchange)
	{
		int numberOfFreeSlots = activePlayer.getInventory().getNumberOfFreeSlots();
		return numberOfFreeSlots >=  exchange.getItems().size();			
	}

	/**
	 * 
	 * @param player
	 * @param exchange
	 */
	private void putItemToInventory(Player player, Exchange exchange)
	{
		for(ExchangeItem exchangeItem : exchange.getItems().values())
		{
			Item itemToPut = null;
			switch(exchangeItem.getPersistentState())
			{
				case NEW:
					itemToPut = exchangeItem.getNewItem();
					break;
				case UPDATE_REQUIRED:
					itemToPut = exchangeItem.getOriginalItem();
					break;
			}
			player.getInventory().putToBag(itemToPut);
			itemService.updateItem(player, itemToPut, true);
		}	
		player.getInventory().increaseKinah(exchange.getKinahCount());
	}
}
