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
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.ExitCode;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
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
		if(ShutdownConfig.SHUTDOWN_HOOK_MODE == 1)
			shutdownHook(ShutdownConfig.SHUTDOWN_HOOK_DELAY, ShutdownConfig.SHUTDOWN_ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
		else if(ShutdownConfig.SHUTDOWN_HOOK_MODE == 2)
			shutdownHook(ShutdownConfig.SHUTDOWN_HOOK_DELAY, ShutdownConfig.SHUTDOWN_ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
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
		try
		{
			Iterator<Player> onlinePlayers = world.getPlayersIterator();
			if(!onlinePlayers.hasNext())
				return;
			while(onlinePlayers.hasNext())
			{
				Player player = onlinePlayers.next();
				if(player != null && player.getClientConnection() != null)
					player.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.SERVER_SHUTDOWN(seconds));
			}
		}
		catch(NoSuchElementException e)
		{

		}
	}

	private static void sendShutdownStatus(boolean status)
	{
		try
		{
			Iterator<Player> onlinePlayers = world.getPlayersIterator();
			if(!onlinePlayers.hasNext())
				return;
			while(onlinePlayers.hasNext())
			{
				Player player = onlinePlayers.next();
				if(player != null && player.getClientConnection() != null)
					player.getController().setInShutdownProgress(status);
			}
		}
		catch(NoSuchElementException e)
		{

		}
	}

	private static void shutdownHook(int duration, int interval, ShutdownMode mode)
	{
		for(int i = duration; i >= interval; i -= interval)
		{
			try
			{
				if(world.getPlayersIterator().hasNext())
				{

					log.info("Runtime is " + mode.getText() + " in " + i + " seconds.");
					sendShutdownMessage(i);
					sendShutdownStatus(true);
				}
				else
				{
					log.info("Runtime is " + mode.getText() + " now ...");
					break; // fast exit.
				}

				if(i > interval)
				{
					sleep(interval * 1000);
				}
				else
				{
					sleep(1000);
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
			try
			{
				playerService.playerLoggedOut(activePlayer);
			}
			catch(Exception e)
			{
				log.error("Error while saving player " + e.getMessage());
			}

		}
		log.info("All players are disconnected...");

		// Save game time.
		GameTimeManager.saveTime();

		// Do system exit.
		if(mode == ShutdownMode.RESTART)
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		else
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);

		log.info("Runtime is " + mode.getText() + " now...");
	}

	public static void doShutdown(int delay, int announceInterval, ShutdownMode mode)
	{
		shutdownHook(delay, announceInterval, mode);
	}
}
