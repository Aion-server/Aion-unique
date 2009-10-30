/*
 * This file is part of aion-emu <aion-emu.com>.
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
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Injector;

/**
 * This task is run, when server is shutting down.
 * We should do here all data saving etc. 
 * 
 * @author Luno
 *
 */
public class ShutdownHook implements Runnable
{
	private static final Logger log = Logger.getLogger(ShutdownHook.class);
	private World world;
	private PlayerService playerService;
	private LoginServer loginServer;
	
	public ShutdownHook (Injector injector) {
		world = injector.getInstance(World.class);
		playerService = injector.getInstance(PlayerService.class);
		loginServer = injector.getInstance(LoginServer.class);
	}
	
	private boolean broadcastShutdownMessage (int duration, int interval) {
		for (int i=duration; i>=interval; i-=interval) {
			Iterator<Player> onlinePlayers = world.getPlayersIterator();
			if (!onlinePlayers.hasNext()) {
				return false;
			}
			while (onlinePlayers.hasNext()) {
				Player onlinePlayer = onlinePlayers.next();
				onlinePlayer.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.SERVER_SHUTDOWN(i));
			}
			if (i>interval) {
				try {
					Thread.sleep(interval*1000);
				} catch (InterruptedException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		Iterator<Player> onlinePlayers;
		
		log.info("Starting AE GS shutdown sequence");
		loginServer.gameServerDisconnected();
		
		broadcastShutdownMessage(Config.SERVER_SHUTDOWN_DELAY,10);
		broadcastShutdownMessage(9,1);
		
		onlinePlayers = world.getPlayersIterator();
		while (onlinePlayers.hasNext()) {
			playerService.storePlayer(onlinePlayers.next());
		}
		
		GameTimeManager.saveTime();
	}
}
