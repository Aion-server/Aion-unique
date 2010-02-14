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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Injector;

/**
 * @author lord_rex
 * 
 */
public class ShutdownHook extends Thread
{
	private static final Logger		log		= Logger.getLogger(ShutdownHook.class);

	private static ShutdownMode		mode	= ShutdownMode.NONE;
	private static ShutdownHook		hookInstance;

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
		if(hookInstance == null)
			hookInstance = new ShutdownHook(injector);

		return hookInstance;
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

		private String getText()
		{
			return text;
		}
	}

	@Override
	public void run()
	{
		if(this == hookInstance)
			shutdownHook(mode);
	}

	private static void sendShutdownMessage(int seconds)
	{
		Iterator<Player> onlinePlayers = world.getPlayersIterator();
		if(!onlinePlayers.hasNext())
			return;
		while(onlinePlayers.hasNext())
			onlinePlayers.next().getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.SERVER_SHUTDOWN(seconds));
	}

	private static void sendShutdownStatus()
	{
		Iterator<Player> onlinePlayers = world.getPlayersIterator();
		if(!onlinePlayers.hasNext())
			return;
		while(onlinePlayers.hasNext())
			onlinePlayers.next().getController().setInShutdownProgress(true);
	}

	private static void shutdownHook(ShutdownMode mode)
	{
		try
		{
			loginServer.gameServerDisconnected();
			log.info("LoginServer: Disconnected from Login Server...");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		Iterator<Player> onlinePlayers = world.getPlayersIterator();
		while(onlinePlayers.hasNext())
		{
			playerService.playerLoggedOut(onlinePlayers.next());
			log.info("PlayerService: All players are disconnected...");
		}

		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		try
		{
			GameTimeManager.saveTime();
			log.info("GameTimeManager: Game time saved...");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		log.info("Server is " + mode.getText() + " NOW!");

		if(mode == ShutdownMode.RESTART)
			Runtime.getRuntime().halt(2);
		else
			Runtime.getRuntime().halt(0);
	}

	public static void doShutdown(String initiator, int seconds, final ShutdownMode mode)
	{
		log.warn(initiator + " issued shutdown command: " + mode.getText() + " in " + seconds + " seconds!");
		sendShutdownMessage(seconds);
		sendShutdownStatus();
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				ShutdownHook.mode = mode;
				shutdownHook(mode);
			}
		}, seconds * 1000);
	}
}
