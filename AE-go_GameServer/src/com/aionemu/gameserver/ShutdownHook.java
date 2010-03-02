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

import com.aionemu.commons.utils.ExitCode;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Injector;

/**
 * @author lord_rex
 * 
 */
public class ShutdownHook extends Thread
{
	private static final Logger		log	= Logger.getLogger(ShutdownHook.class);

	private static World			world;
	private static PlayerService	playerService;
	private static LoginServer		loginServer;

	public ShutdownHook(Injector injector)
	{
		world = injector.getInstance(World.class);
		playerService = injector.getInstance(PlayerService.class);
		loginServer = injector.getInstance(LoginServer.class);
	}

	public static ShutdownHook getInstance(Injector injector)
	{
		return new ShutdownHook(injector);
	}

	@Override
	public void run()
	{
		if(Config.SHUTDOWN_HOOK_MODE == 1)
			shutdownHook(Config.SHUTDOWN_HOOK_DELAY, Config.SHUTDOWN_ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
		else if(Config.SHUTDOWN_HOOK_MODE == 2)
			shutdownHook(Config.SHUTDOWN_HOOK_DELAY, Config.SHUTDOWN_ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
	}

	public static enum ShutdownMode
	{
		NONE("terminating"),
		SHUTDOWN("shutting down"),
		RESTART("restarting");

		private final String	text;

		private ShutdownMode(String text)
		{
			this.text = text;
		}

		public String getText()
		{
			return text;
		}
	}

	private static void sendShutdownMessage(int seconds)
	{
		Iterator<Player> onlinePlayers = world.getPlayersIterator();
		if(!onlinePlayers.hasNext())
			return;
		while(onlinePlayers.hasNext())
			onlinePlayers.next().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.SERVER_SHUTDOWN(seconds));
	}

	private static void sendShutdownStatus(boolean status)
	{
		Iterator<Player> onlinePlayers = world.getPlayersIterator();
		if(!onlinePlayers.hasNext())
			return;
		while(onlinePlayers.hasNext())
			onlinePlayers.next().getController().setInShutdownProgress(status);
	}

	private static void shutdownHook(int duration, int interval, ShutdownMode mode)
	{
		for(int i = duration; i >= interval; i -= interval)
		{
			log.info("System is closing in " + i + " seconds.");
			sendShutdownMessage(i);
			sendShutdownStatus(true);
			try
			{
				if(!world.getPlayersIterator().hasNext())
				{
					log.info("Counter is stopped because there are no players in the server.");
					break;
				}

				if(i > interval)
				{
					Thread.sleep(interval * 1000);
				}
				else
				{
					Thread.sleep(1000);
				}
			}
			catch(InterruptedException e)
			{
				return;
			}
		}

		// Disconnect login server from game.
		loginServer.gameServerDisconnected();

		// Disconnect all players.
		Iterator<Player> onlinePlayers;
		onlinePlayers = world.getPlayersIterator();
		while(onlinePlayers.hasNext())
		{
			Player activePlayer = onlinePlayers.next();
			playerService.playerLoggedOut(activePlayer);
		}
		log.info("All players are disconnected...");

		// Save game time.
		GameTimeManager.saveTime();

		// Do system exit.
		if(mode == ShutdownMode.RESTART)
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		else
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);

		log.info("Runtime is closing now...");
	}

	public static void doShutdown(int delay, int announceInterval, ShutdownMode mode)
	{
		shutdownHook(delay, announceInterval, mode);
	}
}
