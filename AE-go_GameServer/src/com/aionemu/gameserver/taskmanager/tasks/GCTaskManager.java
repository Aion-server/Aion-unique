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
package com.aionemu.gameserver.taskmanager.tasks;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author lord_rex
 * 
 */
public class GCTaskManager implements Runnable
{
	private static final Logger	log	= Logger.getLogger(GCTaskManager.class);
	private final int			PERIOD;

	public GCTaskManager(int period)
	{
		PERIOD = period;
		log.info(getClass().getSimpleName() + ": Initialized.");
	}

	@Override
	public void run()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){
			@Override
			public void run()
			{
				System.gc();
				System.runFinalization();
				log.info("GC Called...");
			}
		}, PERIOD, PERIOD);
	}
}
