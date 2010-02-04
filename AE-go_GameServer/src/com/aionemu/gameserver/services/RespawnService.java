/*
 * This file is part of aion-unique <aion-unique.org.
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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnTime;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class RespawnService
{
	private static RespawnService instance = new RespawnService();
	
	public void scheduleRespawnTask(final VisibleObject visibleObject)
	{
		final World world = visibleObject.getPosition().getWorld();
		final int interval = visibleObject.getSpawn().getSpawnGroup().getInterval();		
	
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				SpawnTime spawnTime = visibleObject.getSpawn().getSpawnGroup().getSpawnTime();
				if(spawnTime != null)
				{
					DayTime dayTime = GameTimeManager.getGameTime().getDayTime();
					if(!spawnTime.isAllowedDuring(dayTime))
						return;
				}
				
				int instanceId = visibleObject.getInstanceId();
				if(visibleObject.getSpawn().isRespawn(instanceId))
				{
					exchangeSpawnTemplate(visibleObject);		
					world.setPosition(visibleObject, visibleObject.getSpawn().getWorldId(), visibleObject.getSpawn().getX(), visibleObject.getSpawn().getY(), visibleObject.getSpawn().getZ(), visibleObject.getSpawn().getHeading());
					//call onRespawn before actual spawning
					visibleObject.getController().onRespawn();
					world.spawn(visibleObject);		
				}
				else
				{
					visibleObject.getController().delete();
				}				
			}

			private synchronized void exchangeSpawnTemplate(final VisibleObject visibleObject)
			{
				int instanceId = visibleObject.getInstanceId();			
				SpawnTemplate nextSpawn = visibleObject.getSpawn().getSpawnGroup().getNextAvailableTemplate(instanceId);	
				if(nextSpawn != null)
				{
					nextSpawn.setSpawned(true, instanceId);
					visibleObject.getSpawn().setSpawned(false, instanceId);
					visibleObject.setSpawn(nextSpawn);
				}	
			}
			
		}, interval * 1000);
	}
	
	public static RespawnService getInstance()
	{
		return instance;
	}
}
