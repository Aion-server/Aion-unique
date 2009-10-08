/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class LifeStatsRestoreService
{
	private static final Logger log = Logger.getLogger(LifeStatsRestoreService.class);

	private static final int DEFAULT_DELAY = 1500;
	
	private static final int HP_RESTORE_TICK = 5;
	
	private static final int MP_RESTORE_TICK = 5;

	private static LifeStatsRestoreService instance = new LifeStatsRestoreService();
	
	/**
	 * HP and MP restoring task
	 * @param creature
	 * @return
	 */
	public Future<?> scheduleRestoreTask(final CreatureLifeStats<? extends Creature> lifeStats)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
		{
			@Override
			public void run()
			{				
				
				if(lifeStats.getCurrentHp() == lifeStats.getMaxHp() || lifeStats.isAlreadyDead())
				{
					lifeStats.cancelRestoreTask();
				}
				else
				{
					lifeStats.increaseHp(HP_RESTORE_TICK);
				}
				
				//TODO restore MP also
			}
			
		}), 1700, DEFAULT_DELAY);

	}
	
	public static LifeStatsRestoreService getInstance()
	{
		return instance;
	}
}
