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

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class PlayerUpdateService implements Runnable
{
	private static final Logger	log	= Logger.getLogger(PlayerUpdateService.class);
	
	private static final int DELAY = 10 * 60 * 1000;
	
	private World world;
	
	@Inject
	public PlayerUpdateService(World world)
	{
		this.world = world;
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, DELAY, DELAY);
	}
	
	@Override
	public void run()
	{
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
			
			//DAOManager.getDAO(InventoryDAO.class).store(player);
			playersUpdated++;
		}
		long workTime = System.currentTimeMillis() - startTime;
		log.info("Players update complete after: " + workTime + " ms, players: " + playersUpdated);
	}
}
