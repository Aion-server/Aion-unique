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

import java.util.concurrent.Future;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawn.SpawnTime;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class RespawnService
{
	private static final int DECAY_DEFAULT_DELAY = 20000;
	
	@Inject
	private InstanceService instanceService;

	/**
	 * 
	 * @param npc
	 * @return Future<?>
	 */
	public Future<?> scheduleDecayTask(final Npc npc)
	{
		return ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				npc.getController().onDespawn(false);
			}
		}, DECAY_DEFAULT_DELAY);
	}
	/**
	 * 
	 * @param visibleObject
	 */
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
				int worldId = visibleObject.getSpawn().getWorldId();
				boolean instanceExists = instanceService.isInstanceExist(worldId, instanceId);
				
				if(visibleObject.getSpawn().isNoRespawn(instanceId) || !instanceExists)
				{
					visibleObject.getController().delete();				
				}
				else
				{
					visibleObject.getSpawn().getSpawnGroup().exchangeSpawn(visibleObject);		
					world.setPosition(visibleObject, worldId, visibleObject.getSpawn().getX(), visibleObject.getSpawn().getY(), visibleObject.getSpawn().getZ(), visibleObject.getSpawn().getHeading());
					//call onRespawn before actual spawning
					visibleObject.getController().onRespawn();
					world.spawn(visibleObject);		
				}				
			}
			
		}, interval * 1000);
	}
}
