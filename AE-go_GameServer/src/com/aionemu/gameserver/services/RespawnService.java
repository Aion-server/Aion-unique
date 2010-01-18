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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class RespawnService
{
	private static final Logger log = Logger.getLogger(RespawnService.class);

	private static RespawnService instance = new RespawnService();
	
	public void scheduleRespawnTask(final VisibleObject visibleObject)
	{
		final World world = visibleObject.getActiveRegion().getWorld();
		final int interval = visibleObject.getSpawn().getSpawnGroup().getInterval();
		
		//TODO separate thread executor for decay/spawns
		// or schedule separate decay runnable service with queue 
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				exchangeSpawnTemplate(visibleObject);		
				world.setPosition(visibleObject, visibleObject.getSpawn().getWorldId(), visibleObject.getSpawn().getX(), visibleObject.getSpawn().getY(), visibleObject.getSpawn().getZ(), visibleObject.getSpawn().getHeading());
				//call onRespawn before actual spawning
				visibleObject.getController().onRespawn();
				world.spawn(visibleObject);			
			}

			private synchronized void exchangeSpawnTemplate(final VisibleObject visibleObject)
			{
				SpawnTemplate nextSpawn = visibleObject.getSpawn().getSpawnGroup().getNextAvailableTemplate();
				if(nextSpawn != null)
				{
					nextSpawn.setSpawned(true);
					visibleObject.getSpawn().setSpawned(false);
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
