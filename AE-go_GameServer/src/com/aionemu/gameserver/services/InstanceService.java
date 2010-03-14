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

import java.util.Iterator;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.PortalData;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.WorldMapTemplate;
import com.aionemu.gameserver.model.templates.portal.EntryPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class InstanceService
{
	private static Logger	log	= Logger.getLogger(InstanceService.class);
	@Inject
	private World			world;
	@Inject
	private SpawnEngine		spawnEngine;
	@Inject
	private PortalData		portalData;
	@Inject
	private WorldMapsData	worldMapsData;

	/**
	 * @param worldId
	 * @param destroyTime
	 * @return
	 */
	public synchronized WorldMapInstance getNextAvailableInstance(int worldId, int destroyTime)
	{
		WorldMap map = world.getWorldMap(worldId);

		if(!map.isInstanceType())
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);

		int nextInstanceId = map.getNextInstanceId();

		log.info("Creating new instance: " + worldId + " " + nextInstanceId);

		WorldMapInstance worldMapInstance = new WorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		spawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId());
		
		if(destroyTime == 0)
			destroyTime = 60 * 30;//TODO take from template
		
		setDestroyTime(worldMapInstance, destroyTime);
		return worldMapInstance;
	}

	/**
	 * Will create new instance if there are not free yet and spawn according to xml data
	 * 
	 * @param worldId
	 * @return WorldMapInstance
	 */
	public WorldMapInstance getNextAvailableInstance(int worldId)
	{
		return getNextAvailableInstance(worldId, 0);
	}

	/**
	 * 
	 * @param sec
	 */
	private void setDestroyTime(final WorldMapInstance instance, int sec)
	{
		Future<?> destroyTask = instance.getDestroyTask();
		if(destroyTask != null)
			destroyTask.cancel(true);

		destroyTask = ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				destroyInstance(instance);
			}
		}, sec * 1000);
		instance.setDestroyTask(destroyTask);
	}

	/**
	 * Instance will be destroyed All players moved to bind location All objects - deleted
	 */
	public void destroyInstance(WorldMapInstance instance)
	{
		int worldId = instance.getMapId();
		int instanceId = instance.getInstanceId();

		WorldMap map = world.getWorldMap(worldId);
		map.removeWorldMapInstance(instanceId);

		log.info("Destroying instance:" + worldId + " " + instanceId);
		
		PortalTemplate portalTemplate = portalData.getPortalTemplateByMap(worldId);
		
		Iterator<VisibleObject> it = instance.objectIterator();
		while(it.hasNext())
		{
			VisibleObject obj = it.next();
			if(obj instanceof Player)
				moveToEntryPoint((Player) obj, portalTemplate);
			else
				obj.getController().delete();
		}
	}
	
	/**
	 * 
	 * @param instance
	 * @param player
	 */
	public void registerPlayerWithInstance(WorldMapInstance instance, Player player)
	{
		instance.register(player.getObjectId());
	}
	
	/**
	 * 
	 * @param instance
	 * @param group
	 */
	public void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group)
	{
		instance.register(group.getGroupId());
	}
	
	/**
	 * 
	 * @param worldId
	 * @param objectId
	 * @return instance or null
	 */
	public WorldMapInstance getRegisteredInstance(int worldId, int objectId)
	{
		Iterator<WorldMapInstance> iterator = world.getWorldMap(worldId).iterator();
		while(iterator.hasNext())
		{
			WorldMapInstance instance = iterator.next();
			if(instance.isRegistered(objectId))
				return instance;
		}
		return null;
	}

	/**
	 * @param player
	 */
	public void onPlayerLogin(Player player)
	{
		int worldId = player.getWorldId();
		
		WorldMapTemplate worldTemplate = worldMapsData.getTemplate(worldId);
		if(worldTemplate.isInstance())
		{
			PortalTemplate portalTemplate = portalData.getPortalTemplateByMap(worldId);
			
			int lookupId = player.getObjectId();
			if(portalTemplate.isGroup() && player.getPlayerGroup() != null)
			{
				lookupId = player.getPlayerGroup().getGroupId();
			}
			
			WorldMapInstance registeredInstance = this.getRegisteredInstance(worldId, lookupId);
			if(registeredInstance != null)
			{
				world.setPosition(player, worldId, registeredInstance.getInstanceId(), player.getX(), player.getY(),
					player.getZ(), player.getHeading());
				return;
			}
			
			
			if(portalTemplate == null)
			{
				log.error("No portal template found for " + worldId);
				return;
			}
			
			moveToEntryPoint(player, portalTemplate);			
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param portalTemplate
	 */
	private void moveToEntryPoint(Player player, PortalTemplate portalTemplate)
	{
		EntryPoint entryPoint = portalTemplate.getEntryPoint();
		world.setPosition(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(),
			entryPoint.getZ(), player.getHeading());
	}

	/**
	 * @param worldId
	 * @param instanceId
	 * @return
	 */
	public boolean isInstanceExist(int worldId, int instanceId)
	{
		return world.getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}
}
