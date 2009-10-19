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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class DropService
{
	private static final Logger log = Logger.getLogger(DropService.class);
	
	private DropList dropList;
	
	private Map<Integer, Set<DropItem>> currentDropMap = Collections.synchronizedMap(new HashMap<Integer, Set<DropItem>>());
	
	public DropService()
	{
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
	 *  When player clicks on dead NPC to request drop
	 *  
	 * @param player
	 * @param npcId
	 */
	public void requestDrop(Player player, int npcId)
	{
		Set<DropItem> dropItems = currentDropMap.get(npcId);
		if(dropItems != null)
		{
			int size = dropItems.size();
			PacketSendUtility.sendPacket(player, new SM_LOOT_ITEMLIST(npcId, dropItems));
			//PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, size > 0 ? size - 1 : size));
			PacketSendUtility.sendPacket(player, new SM_LOOT_STATUS(npcId, 2));
			PacketSendUtility.sendPacket(player, new SM_EMOTION(npcId, 35, 0));
		}	
	}
}
