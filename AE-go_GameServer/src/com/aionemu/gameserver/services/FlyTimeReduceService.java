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
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author sphinx
 *
 */
public class FlyTimeReduceService
{
	private static final int DEFAULT_DELAY = 1000;
	private static FlyTimeReduceService instance = new FlyTimeReduceService();
	
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
				if(lifeStats.getOwner().isInState(CreatureState.FLYING) && lifeStats.getCurrentFp() == 0)
				{															
					PacketSendUtility.broadcastPacket(lifeStats.getOwner(), new SM_EMOTION(lifeStats.getOwner(), 9, 0, 0), true);
					lifeStats.getOwner().unsetState(CreatureState.FLYING);
					lifeStats.getOwner().getController().endFly();
					
					//this is probably needed to change back fly speed into speed.
					PacketSendUtility.sendPacket(lifeStats.getOwner(), new SM_STATS_INFO(lifeStats.getOwner()));
					PacketSendUtility.broadcastPacket(lifeStats.getOwner(), new SM_EMOTION(lifeStats.getOwner(), 30, 0, 0), true);
					
					lifeStats.triggerFpRestore();
				}
				
				// this is in state flying + gliding
				else if(lifeStats.getOwner().isInState(CreatureState.GLIDING))
				{
					//do nothing
				}
				
				else
				{					
					lifeStats.reduceFp(1);			
				}
			}
			
		}), 2000, DEFAULT_DELAY);

	}
	
	public static FlyTimeReduceService getInstance()
	{
		return instance;
	}
}
