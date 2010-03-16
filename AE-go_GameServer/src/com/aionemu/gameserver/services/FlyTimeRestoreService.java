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

import java.util.concurrent.Future;

import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author sphinx
 *
 */
public class FlyTimeRestoreService
{
	private static final int DEFAULT_DELAY = 2000;
	private static FlyTimeRestoreService instance = new FlyTimeRestoreService();
	
	/**
	 * HP and MP restoring task
	 * @param creature
	 * @return Future<?>
	 */
	public Future<?> scheduleRestoreTask(final PlayerLifeStats lifeStats)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate((new Runnable()
		{
			@Override
			public void run()
			{							
				if(lifeStats.isFlyTimeFullyRestored() 
					|| lifeStats.getOwner().isInState(CreatureState.FLYING))
				{
					lifeStats.cancelFpRestore();
				}
				else
				{
					lifeStats.restoreFp();					
				}
			}
			
		}), 2000, DEFAULT_DELAY);

	}
	
	public static FlyTimeRestoreService getInstance()
	{
		return instance;
	}
}
