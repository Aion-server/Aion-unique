/**
 * This file is part of aion-emu <www.aion-unique.com>.
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Collections;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EXCHANGE_CONFIRMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author -Avol-
 * 
 */

public class CM_EXCHANGE_OK extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_EXCHANGE_OK.class);
	@Inject	
	private ItemService 		itemService;
	private int			action;	
	@Inject	
	private World			world;

	public CM_EXCHANGE_OK(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{	
		final Player activePlayer = getConnection().getActivePlayer();
		int targetPlayerId = activePlayer.getExchangeList().getExchangePartner();

		final Player targetPlayer = world.findPlayer(targetPlayerId);

		PacketSendUtility.sendPacket(targetPlayer, new SM_EXCHANGE_CONFIRMATION(2));

		/*
		 * Set confirmed trade to activePlayer.
		 */

		activePlayer.getExchangeList().setConfirm(true);

		/*
		 * Check if other player confirmed trade.
		 */

		//TODO run exchange operation under one transaction
		//TODO add more checks for items send along with client packet
		//TODO fail whole operation if at least one action was unsuccessful
		//TODO move logic to some Exchange service class

		if (targetPlayer.getExchangeList().getConfirm() == true)
		{
			PacketSendUtility.sendPacket(targetPlayer, new SM_EXCHANGE_CONFIRMATION(0));
			PacketSendUtility.sendPacket(activePlayer, new SM_EXCHANGE_CONFIRMATION(0));

			/*
			 * Store, delete items to active Player
			 */

			int lenght = targetPlayer.getExchangeList().getExchangeItemListLenght();
			while (lenght>0) 
			{
				lenght--;
				int itemObjId = targetPlayer.getExchangeList().getExchangeItemList(lenght);
				int itemCount = targetPlayer.getExchangeList().getExchangeItemCountList(lenght);


				/*
				 * remove traded items.
				 */
				Inventory bag = targetPlayer.getInventory();
				Item resultItem = bag.getItemByObjId(itemObjId);
				int itemId = resultItem.getItemTemplate().getItemId();

				if (resultItem != null) 
				{
					bag.removeFromBag(resultItem);
					PacketSendUtility.sendPacket(targetPlayer, new SM_DELETE_ITEM(itemObjId));
				}

				/*
				 * Add traded items.
				 */
				Inventory activeInventory = activePlayer.getInventory();

				Item newItem = itemService.newItem(itemId, itemCount);
				if(newItem == null)
					continue;

				Item addedItem = activeInventory.addToBag(newItem);

				if(addedItem != null)
				{
					if(addedItem.getObjectId() != newItem.getObjectId())
						itemService.releaseItemId(newItem);

					PacketSendUtility.sendPacket(activePlayer, new SM_INVENTORY_INFO(Collections.singletonList(addedItem), activePlayer.getCubeSize()));
				}	
				else
				{
					itemService.releaseItemId(newItem);
				}
			}

			/*
			 * Store, delete items to target Player
			 */

			lenght = activePlayer.getExchangeList().getExchangeItemListLenght();


			while (lenght>0) 
			{
				lenght--;
				int itemObjId = activePlayer.getExchangeList().getExchangeItemList(lenght);
				int itemCount = activePlayer.getExchangeList().getExchangeItemCountList(lenght);

				/*
				 * remove traded items.
				 */

				Inventory bag = activePlayer.getInventory();
				Item resultItem = bag.getItemByObjId(itemObjId);
				int itemId = resultItem.getItemTemplate().getItemId();

				if (resultItem != null) 
				{
					bag.removeFromBag(resultItem);
					PacketSendUtility.sendPacket(activePlayer, new SM_DELETE_ITEM(itemObjId));
				}

				/*
				 * Add traded items.
				 */

				Inventory targetInventory = targetPlayer.getInventory();

				Item newItem = itemService.newItem(itemId, itemCount);
				Item addedItem = targetInventory.addToBag(newItem);

				if(addedItem != null)
				{
					if(addedItem.getObjectId() != newItem.getObjectId())
						itemService.releaseItemId(newItem);
					
					PacketSendUtility.sendPacket(targetPlayer, new SM_INVENTORY_INFO(Collections.singletonList(addedItem), targetPlayer.getCubeSize()));
				}	
				else
				{
					itemService.releaseItemId(newItem);
				}
			} 

			/*
			 * set kinah activePlayer
			 */

			Inventory currentKinahActive = activePlayer.getInventory();
			int kinahCount = currentKinahActive.getKinahItem().getItemCount() - activePlayer.getExchangeList().getExchangeKinah() + targetPlayer.getExchangeList().getExchangeKinah();

			int newKinah = kinahCount - currentKinahActive.getKinahItem().getItemCount();
			currentKinahActive.increaseKinah(newKinah);

			/*
			 * set kinah targetPlayer
			 */

			Inventory currentKinahTarget = targetPlayer.getInventory();
			kinahCount = currentKinahTarget.getKinahItem().getItemCount() - targetPlayer.getExchangeList().getExchangeKinah() + activePlayer.getExchangeList().getExchangeKinah();

			newKinah = kinahCount - currentKinahTarget.getKinahItem().getItemCount();
			currentKinahTarget.increaseKinah(newKinah);
		}
	}
}