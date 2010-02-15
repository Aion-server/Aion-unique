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
package com.aionemu.gameserver.utils.gametime;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.GameTimeDAO;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Manages ingame time
 * 
 * @author Ben
 * 
 */
public class GameTimeManager
{
	private static final Logger log = Logger.getLogger(GameTimeManager.class);
	private static GameTime			instance;
	private static GameTimeUpdater	updater;
	private static boolean			clockStarted	= false;

	static
	{
		GameTimeDAO dao = DAOManager.getDAO(GameTimeDAO.class);
		instance = new GameTime(dao.load());
	}

	/**
	 * Gets the current GameTime
	 * 
	 * @return GameTime
	 */
	public static GameTime getGameTime()
	{
		return instance;
	}

	/**
	 * Starts the counter that increases the clock every tick
	 * 
	 * @throws IllegalStateException
	 *             If called twice
	 */
	public static void startClock()
	{
		if(clockStarted)
		{
			throw new IllegalStateException("Clock is already started");
		}

		updater = new GameTimeUpdater(getGameTime());
		ThreadPoolManager.getInstance().scheduleAtFixedRate(updater, 0, 5000);
		clockStarted = true;
	}

	/**
	 * Saves the current time to the database
	 * 
	 * @return Success
	 */
	public static boolean saveTime()
	{
		log.info("Game time saved...");
		return DAOManager.getDAO(GameTimeDAO.class).store(getGameTime().getTime());
	}
}
