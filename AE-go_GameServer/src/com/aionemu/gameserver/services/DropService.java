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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.DropListDAO;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.drop.DropList;
import com.aionemu.gameserver.model.drop.DropTemplate;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 */
public class DropService
{
	private static final Logger						log					= Logger.getLogger(DropService.class);

	private DropList								dropList;

	private Map<Integer, Set<DropItem>>				currentDropMap		= Collections
																			.synchronizedMap(new HashMap<Integer, Set<DropItem>>());
	private Map<Integer, FastMap<Integer, Boolean>>	dropRegistrationMap	= new ConcurrentHashMap<Integer, FastMap<Integer, Boolean>>();
	private ItemService								itemService;
	private GroupService							groupService;

	private World									world;

	@Inject
	public DropService(ItemService itemService, World world, GroupService groupService)
	{
		this.itemService = itemService;
		this.world = world;
		this.groupService = groupService;
		dropList = DAOManager.getDAO(DropListDAO.class).load();
		log.info(dropList.getSize() + " npc drops loaded");
	}

	/**
	 * @return the dropList
	 */
	public DropList getDropList()
	{
		return dropList;
	}

	/**
	 * After NPC dies - it can register arbitrary drop
	 * 
	 * @param npc
	 */
	public void registerDrop(Npc npc, Player player)
	{
		int npcUniqueId = npc.getObjectId();
		int npcTemplateId = npc.getObjectTemplate().getTemplateId();

		Set<DropTemplate> templates = dropList.getDropsFor(npcTemplateId);
		if(templates != null)
		{
			Set<DropItem> droppedItems = new HashSet<DropItem>();

			int index = 1;
			for(DropTemplate dropTemplate : templates)
			{
				DropItem dropItem = new DropItem(dropTemplate);
				int questId = dropItem.getDropTemplate().getQuest();
				if(questId != 0)
				{
					if(player == null)
						continue;
					QuestState qs = player.getQuestStateList().getQuestState(questId);
					if(qs == null || qs.getStatus() != QuestStatus.START)
						continue;
				}
				dropItem.calculateCount(player.getRates().getDropRate());

				if(dropItem.getCount() > 0)
				{
					dropItem.setIndex(index++);
					droppedItems.add(dropItem);
				}
			}

			currentDropMap.put(npcUniqueId, droppedItems);

			// TODO player should not be null
			if(player != null)
			{
				if(player.isInGroup())
				{
					dropRegistrationMap.put(npcUniqueId, groupService.getMembersToRegistrateByRules(player, player
						.getPlayerGroup()));
				}
				else
				{
					FastMap<Integer, Boolean> singlePlayer = new FastMap<Integer, Boolean>();
					singlePlayer.put(player.getObjectId(), false);
					dropRegistrationMap.put(npcUniqueId, singlePlayer);
				}
			}

		}
	}

	/**
	 * After NPC respawns - drop should be unregistered //TODO more correct - on despawn
	 * 
	 * @param npc
	 */
	public void unregisterDrop(Npc npc)
	{
		int npcUniqueId = npc.getObjectId();
		currentDropMap.remove(npcUniqueId);
		if(dropRegistrationMap.containsKey(npcUniqueId))
		{
			dropRegistrationMap.remove(npcUniqueId);
		}
	}

	/**
	 * When player clicks on dead NPC to request drop list
	 * 
	 * @param player
	 * @param npcId
	 */
	public void requestDropList(Player player, int npcId)
	{
		if(player == null || !dropRegistrationMap.containsKey(npcId))
			return;

		if(!dropRegistrationMap.get(npcId).containsKey(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_NO_RIGHT());
			return;
		}

		if(dropRegistrationMap.get(npcId).containsValue(true))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LOOT_FAIL_ONLOOTING());
			return;
		}
		
		dropRegistrationMap.get(npcId).put(player.getObjectId(), true);
		
		Set<DropItem> dropItems = currentDropMap.get(npcId);

		if(dropItems == null)
		{
			dropItems = Collections.emptySet();
		}

		PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems));
		// PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, size > 0 ? size - 1 : size));
		PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 35, 0, npcId), true);

		// if dropitems is empty, resend droplist for close loot
		if (dropItems.size() == 0)
			resendDropList(player, npcId, dropItems);
	}

	public void requestDropItem(Player player, int npcId, int itemIndex)
	{
		Set<DropItem> dropItems = currentDropMap.get(npcId);

		// drop was unregistered
		if(dropItems == null)
		{
			return;
		}

		// TODO prevent possible exploits

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

		if(requestedItem != null)
		{
			int currentDropItemCount = requestedItem.getCount();
			int itemId = requestedItem.getDropTemplate().getItemId();
			int questId = requestedItem.getDropTemplate().getQuest();

			currentDropItemCount = itemService.addItem(player, itemId, currentDropItemCount, questId > 0);

			if(currentDropItemCount == 0)
			{
				dropItems.remove(requestedItem);
			}
			else
			{
				// If player didnt got all item stack
				requestedItem.setCount(currentDropItemCount);
			}

			// show updated droplist
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
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 36, 0, npcId), true);
			Npc npc = (Npc) world.findAionObject(npcId);
			if(npc != null)
			{
				npc.getController().onDespawn(true);
			}
		}
	}
}