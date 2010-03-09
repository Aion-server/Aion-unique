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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class PlayerUpdateService
{
	private static final Logger	log	= Logger.getLogger(PlayerUpdateService.class);

	private World				world;
	private LegionService		legionService;
	
	@Inject
	public PlayerUpdateService(World world, LegionService legionService)
	{
		this.world = world;
		this.legionService = legionService;
		int DELAY_GENERAL = PeriodicSaveConfig.PLAYER_GENERAL * 1000;
		int DELAY_ITEM = PeriodicSaveConfig.PLAYER_ITEMS * 1000;
		int DELAY_LEGION_ITEM = PeriodicSaveConfig.LEGION_ITEMS * 1000;
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(), DELAY_GENERAL, DELAY_GENERAL);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(), DELAY_ITEM, DELAY_ITEM);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new LegionWhUpdateTask(), DELAY_LEGION_ITEM, DELAY_LEGION_ITEM);
	}

	private class GeneralUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			log.info("Player update task started");
			long startTime = System.currentTimeMillis();
			Iterator<Player> playersIterator = world.getPlayersIterator();
			int playersUpdated = 0;
			while(playersIterator.hasNext())
			{
				Player player = playersIterator.next();
				try
				{
					DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
					DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
					DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
					DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
				}
				catch(Exception ex)
				{
					log.error("Exception during periodic saving of player " + ex.getMessage());
				}

				playersUpdated++;
			}
			long workTime = System.currentTimeMillis() - startTime;
			log.info("Player update: " + workTime + " ms, players: " + playersUpdated);
		}
	}

	private class ItemUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			log.info("Player item update task started");
			long startTime = System.currentTimeMillis();
			Iterator<Player> playersIterator = world.getPlayersIterator();
			int playersUpdated = 0;
			while(playersIterator.hasNext())
			{
				Player player = playersIterator.next();
				try
				{
					DAOManager.getDAO(InventoryDAO.class).store(player);
					DAOManager.getDAO(ItemStoneListDAO.class).save(player);
				}
				catch(Exception ex)
				{
					log.error("Exception during periodic saving of player items " + ex.getMessage());
				}

				playersUpdated++;
			}
			long workTime = System.currentTimeMillis() - startTime;
			log.info("Player item update: " + workTime + " ms, players: " + playersUpdated);
		}
	}
	
	private class LegionWhUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			log.info("Legion WH update task started");
			long startTime = System.currentTimeMillis();
			Iterator<Legion> legionsIterator = legionService.getCachedLegionIterator();
			int legionWhUpdated = 0;
			while(legionsIterator.hasNext())
			{
				Legion legion = legionsIterator.next();
				List<Item> allItems = legion.getLegionWarehouse().getAllItems();
				try
				{
					for(Item item : allItems)
					{
						DAOManager.getDAO(InventoryDAO.class).store(item, legion.getLegionId());
					}
					
					for(Item item : allItems)
					{
						List<ItemStone> itemStones = item.getItemStones();
						if(itemStones != null)
						{
							DAOManager.getDAO(ItemStoneListDAO.class).store(itemStones);	
						}
						
					}
				}
				catch(Exception ex)
				{
					log.error("Exception during periodic saving of legion WH " + ex.getMessage());
				}

				legionWhUpdated++;
			}
			long workTime = System.currentTimeMillis() - startTime;
			log.info("Legion WH update: " + workTime + " ms, legions: " + legionWhUpdated);
		}
	}

}
