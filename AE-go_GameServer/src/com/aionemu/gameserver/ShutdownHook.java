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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.utils.gametime.GameTimeManager;

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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		log.info("Starting AE GS shutdown sequence");
		
		GameTimeManager.saveTime();
	}
}
