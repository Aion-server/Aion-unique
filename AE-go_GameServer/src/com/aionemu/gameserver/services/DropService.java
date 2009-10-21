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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.DropListDAO;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.drop.DropList;
import com.aionemu.gameserver.model.drop.DropTemplate;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 */
public class DropService
{
	private static final Logger log = Logger.getLogger(DropService.class);

	private DropList dropList;

	private Map<Integer, Set<DropItem>> currentDropMap = Collections.synchronizedMap(new HashMap<Integer, Set<DropItem>>());

	private ItemService itemService;

	private World world;

	@Inject
	public DropService(ItemService itemService, World world)
	{
		this.itemService = itemService;
		this.world = world;
		dropList = DAOManager.getDAO(DropListDAO.class).load();
		log.info(dropList.getSize() + " npc drops loaded");
	}

	/**
	 * After NPC dies - it can register arbitrary drop
	 * @param npc
	 */
	public void registerDrop(Npc npc)
	{
		int npcUniqueId = npc.getObjectId();
		int npcTemplateId = npc.getTemplate().getNpcId();

		Set<DropTemplate> templates = dropList.getDropsFor(npcTemplateId);
		if(templates != null)
		{
			Set<DropItem> droppedItems = new HashSet<DropItem>();

			for(DropTemplate dropTemplate : templates)
			{
				DropItem dropItem = new DropItem(dropTemplate, droppedItems.size());
				dropItem.calculateCount();

				if(dropItem.getCount() > 0)
				{
					droppedItems.add(dropItem);
				}		
			}

			currentDropMap.put(npcUniqueId, droppedItems);
		}		
	}

	/**
	 *  After NPC respawns - drop should be unregistered
	 *  //TODO more correct - on despawn
	 * @param npc
	 */
	public void unregisterDrop(Npc npc)
	{
		int npcUniqueId = npc.getObjectId();
		currentDropMap.remove(npcUniqueId);
	}

	/**
	 *  When player clicks on dead NPC to request drop list
	 *  
	 * @param player
	 * @param npcId
	 */
	public void requestDropList(Player player, int npcId)
	{
		Set<DropItem> dropItems = currentDropMap.get(npcId);

		if(dropItems == null)
		{
			dropItems = Collections.emptySet();
		}

		PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems));
		//PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, size > 0 ? size - 1 : size));
		PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
		PacketSendUtility.sendPacket(player, new SM_EMOTION(npcId, 35, 0));
	}

	public void requestDropItem(Player player, int npcId, int itemIndex)
	{
		Set<DropItem> dropItems = currentDropMap.get(npcId);
		
		//drop was unregistered
		if(dropItems == null)
		{
			return;
		}
		
		//TODO prevent stealing drop 
		
		DropItem requestedItem = null;

		synchronized(dropItems)
		{
			for(DropItem dropItem : dropItems)
			{
				if(dropItem.getIndex() == itemIndex)
				{
					requestedItem = dropItem;
					break;
				}
			}
		}
		
		Inventory inventory = player.getInventory();
		
		if(requestedItem != null)
		{
			int currentDropItemCount = requestedItem.getCount();
			int itemId = requestedItem.getDropTemplate().getItemId();
			
			if(itemId == ItemId.KINAH.value())
			{
				inventory.getKinahItem().increaseItemCount(currentDropItemCount);
				PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(inventory.getKinahItem()));
				dropItems.remove(requestedItem);
				resendDropList(player, npcId, dropItems);
				return;
			}
			
			Item newItem = itemService.newItem(itemId, currentDropItemCount);
			
			Item existingItem = inventory.getItemByItemId(itemId);
			
			//item already in cube
			if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
			{
				int oldItemCount = existingItem.getItemCount();
				Item addedItem = inventory.addToBag(newItem);
				if(addedItem != null)
				{
					currentDropItemCount -= addedItem.getItemCount() - oldItemCount;
					PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(addedItem));
				}
			}
			// new item and inventory is not full
			else if (!inventory.isFull())
			{
				Item addedItem = inventory.addToBag(newItem);
				if(addedItem != null)
				{
					currentDropItemCount -= addedItem.getItemCount();
					PacketSendUtility.sendPacket(player, new SM_INVENTORY_INFO(Collections.singletonList(addedItem)));
				}
			}
			
			//item fully pickuped
			if(currentDropItemCount == 0)
			{
				dropItems.remove(requestedItem);
			}
			
			//show updated droplist
			resendDropList(player, npcId, dropItems);
		}	
	}


	private void resendDropList(Player player, int npcId, Set<DropItem> dropItems)
	{
		if(dropItems.size() != 0)
		{
			PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems));
			PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 0));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 3));

			Creature creature = (Creature) world.findAionObject(npcId);
			if(creature != null)
			{
				PacketSendUtility.broadcastPacket(creature, new SM_DELETE(creature));
			}

			//TODO send 7B ??
			//7B 54 38 00 00 0D 00 00 00 00 00 00
			// or
			//7B 54 38 00 00 0E 00 00 00 00 00 00
		}
	}
}
