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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class DebugService
{
	private static final Logger	log							= Logger.getLogger(DebugService.class);

	@Inject
	private World				world;

	private static final int	ANALYZE_PLAYERS_INTERVAL	= 30 * 60 * 1000;

	public DebugService()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				analyzeWorldPlayers();
			}

		}, ANALYZE_PLAYERS_INTERVAL, ANALYZE_PLAYERS_INTERVAL);
	}

	private void analyzeWorldPlayers()
	{
		log.info("Starting analysis of world players at " + System.currentTimeMillis());

		Iterator<Player> playersIterator = world.getPlayersIterator();
		while(playersIterator.hasNext())
		{
			Player player = playersIterator.next();

			/**
			 * Check connection
			 */
			AionConnection connection = player.getClientConnection();
			if(connection == null)
			{
				log.warn(String.format("[DEBUG SERVICE] Player without connection: "
					+ "detected: ObjId %d, Name %s, Spawned %s", player.getObjectId(), player.getName(), player
					.isSpawned()));
				continue;
			}

			/**
			 * Check CM_PING packet
			 */
			long lastPingTimeMS = connection.getLastPingTimeMS();
			long pingInterval = System.currentTimeMillis() - lastPingTimeMS;
			if(lastPingTimeMS > 0 && pingInterval > 300000)
			{
				log.warn(String.format("[DEBUG SERVICE] Player with large ping interval: "
					+ "ObjId %d, Name %s, Spawned %s, PingMS %d", player.getObjectId(), player.getName(), player
					.isSpawned(), pingInterval));
			}
		}
	}

}
