/**
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
package com.aionemu.gameserver.world;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.templates.WorldMapTemplate;

/**
 * This object is representing one in-game map and can have instances.
 * 
 * @author -Nemesiss-
 * 
 */
public class WorldMap
{
	private static Logger log = Logger.getLogger(WorldMap.class);
	
	private WorldMapTemplate				worldMapTemplate;

	private int nextInstanceId = 1;
	/**
	 * List of instances.
	 */
	private Map<Integer, WorldMapInstance>	instances	= new HashMap<Integer, WorldMapInstance>();

	/** World to which belongs this WorldMap */
	private World world;
	
	public WorldMap(WorldMapTemplate worldMapTemplate, World world)
	{
		this.world = world;
		this.worldMapTemplate = worldMapTemplate;
		if(worldMapTemplate.getTwinCount() != 0)
			for(int i = 1; i <= worldMapTemplate.getTwinCount(); i++)
				addInstance(nextInstanceId, new WorldMapInstance(this, nextInstanceId));
		else
			addInstance(nextInstanceId, new WorldMapInstance(this, nextInstanceId));
	}

	/**
	 * Returns map name
	 * 
	 * @return map name
	 */
	public String getName()
	{
		return worldMapTemplate.getName();
	}

	/**
	 * Returns map id
	 * 
	 * @return map id
	 */
	public int getMapId()
	{
		return worldMapTemplate.getMapId();
	}
	
	/**
	 * 
	 * @return int
	 */
	public int getInstanceCount()
	{
		int twinCount = worldMapTemplate.getTwinCount();
		return twinCount > 0 ? twinCount : 1;
	}
	
	/**
	 *  Will create new instance if there are not free yet and spawn according to xml data
	 *  //TODO limit
	 *  //TODO dispose unused instances (lifecycle)
	 * @return WorldMapInstance
	 */
	public synchronized WorldMapInstance getNextFreeInstance()
	{	
		if (!worldMapTemplate.isInstance())
		{
			for(WorldMapInstance instance : instances.values())
			{
				if(!instance.isInUse())
					return instance;
			}
		}
		log.info("Creating new instance: " + worldMapTemplate.getMapId() + " " + nextInstanceId );
		if (worldMapTemplate.isInstance())
			addInstance(nextInstanceId, new WorldMapScriptInstance(this, nextInstanceId));
		else
			addInstance(nextInstanceId, new WorldMapInstance(this, nextInstanceId));
		world.getSpawnEngine().spawnInstance(worldMapTemplate.getMapId(), nextInstanceId-1);
		
		return instances.get(nextInstanceId-1);
	}

	/**
	 * Return a WorldMapInstance - depends on map configuration one map may have twins instances to balance player. This
	 * method will return WorldMapInstance by server chose.
	 * 
	 * @return WorldMapInstance.
	 */
	public WorldMapInstance getWorldMapInstance()
	{
		/**
		 * twin map - balance players
		 */
		if(worldMapTemplate.getTwinCount() != 0)
		{
			/**
			 * Balance players into instances.
			 */
			for(int i = 1; i <= worldMapTemplate.getTwinCount(); i++) // FIXME: DEAD CODE!
			{
				WorldMapInstance inst = getWorldMapInstance(i);
				// TODO! user count etc..
				return inst;
			}
			// TODO! whats now?
			return getWorldMapInstance(worldMapTemplate.getTwinCount());
		}
		else
			return getWorldMapInstance(1);
	}
	
	/**
	 *  This method return WorldMapInstance by specified instanceId
	 *  
	 * @param instanceId
	 * @return WorldMapInstance
	 */
	public WorldMapInstance getWorldMapInstanceById(int instanceId)
	{
		if(worldMapTemplate.getTwinCount() !=0)
		{
			if(instanceId > worldMapTemplate.getTwinCount())
			{
				throw new IllegalArgumentException("WorldMapInstance " + worldMapTemplate.getMapId() + " has lower instances count than " + instanceId);
			}		
		}
		return getWorldMapInstance(instanceId);
	}

	/**
	 * Returns WorldMapInstance by instanceId.
	 * 
	 * @param instanceId
	 * @return WorldMapInstance/
	 */
	private WorldMapInstance getWorldMapInstance(int instanceId)
	{
		return instances.get(instanceId);
	}

	/**
	 * Remove WorldMapInstance by instanceId.
	 * 
	 * @param instanceId
	 * @return WorldMapInstance/
	 */
	public WorldMapInstance removeWorldMapInstance(int instanceId)
	{
		WorldMapInstance instance = instances.get(instanceId);
		if (instance != null)
		{
			instance.destroyInstance();
			return instances.remove(instanceId);
		}
		return null;
	}

	public int getWorldMapScriptInstanceIdByPlyerObjId(int objId)
	{
		for (WorldMapInstance instance : instances.values())
		{
			if (instance.isInInstance(objId))
				return instance.getInstanceId();
		}
		return -1;
	}

	private void addInstance(int instanceId, WorldMapInstance instance)
	{
		instances.put(instanceId, instance);
		nextInstanceId++;
	}
	/**
	 * Returns the World containing this WorldMap.
	 */
	public World getWorld()
	{
		return world;
	}
}
