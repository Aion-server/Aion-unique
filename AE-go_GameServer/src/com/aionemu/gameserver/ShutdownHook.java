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
import com.aionemu.gameserver.questEngine.handlers.QuestHandlersManager;
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
		ShutdownManager.getInstance().run();
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

	private static void shutdownHook(int duration, int interval)
	{
		for(int i = duration; i >= interval; i -= interval)
		{
			sendShutdownMessage(i);
			try
			{
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

		ShutdownManager.getInstance().isHook();

		try
		{
			loginServer.gameServerDisconnected();
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
			QuestHandlersManager.shutdown();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		try
		{
			GameTimeManager.saveTime();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		try
		{
			ThreadPoolManager.getInstance().shutdown();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}

		log.info("Runtime is closing now...");

		if(mode == ShutdownMode.RESTART)
			Runtime.getRuntime().halt(2);
		else
			Runtime.getRuntime().halt(0);
	}

	public static final class ShutdownManager extends Thread
	{
		private static int				counter	= Integer.MAX_VALUE;
		private static ShutdownManager	hookInstance;
		private static ShutdownManager	counterInstance;

		public static ShutdownManager getInstance()
		{
			if(hookInstance == null)
				hookInstance = new ShutdownManager();

			return hookInstance;
		}

		private ShutdownManager()
		{

		}

		@Override
		public void run()
		{
			if(isCounter())
				countdown();
			else if(isHook())
				shutdownHook(Config.SHUTDOWN_HOOK_DELAY, 1);
		}

		public boolean isHook()
		{
			return hookInstance != null;
		}

		public boolean isCounter()
		{
			return counterInstance != null;
		}

		private void countdown()
		{
			while(counter > 0 && this == counterInstance)
			{
				if(counter <= 30 || counter <= 600 && counter % 60 == 0 || counter <= 3600 && counter % 300 == 0)
					sendShutdownMessage(counter);

				counter--;

				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			isCounter();

			if(this != counterInstance)
				return;

			log.warn("Shutdown countdown is over: " + mode.getText() + " NOW!");

			if(mode == ShutdownMode.RESTART)
				System.exit(2);
			else
				System.exit(0);
		}

		public static void doShutdown(String initiator, int seconds, ShutdownMode mode)
		{
			log.warn(initiator + " issued shutdown command: " + mode.getText() + " in " + seconds + " seconds!");

			sendShutdownStatus(true);

			counter = seconds;
			ShutdownHook.mode = mode;

			counterInstance = new ShutdownManager();
			counterInstance.start();
		}

		public static void stopShutdown(String initiator)
		{
			log.warn(initiator + " issued shutdown abort: " + mode.getText() + " has been stopped!");

			sendShutdownStatus(false);

			counter = Integer.MAX_VALUE;
			mode = ShutdownMode.NONE;

			counterInstance = null;
		}
	}
}
