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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * World map instance object.
 *
 * @author -Nemesiss-
 *
 */
public class WorldMapInstance
{
	/**
	 * Size of region
	 */
	public static final int						regionSize			= 500;
	/**
	 * Max world size - actually it must be some value bigger than world size. Used only for id generation.
	 */
	private static final int					maxWorldSize		= 10000;
	/**
	 * WorldMap witch is parent of this instance.
	 */
	private final WorldMap						parent;
	/**
	 * Map of active regions.
	 */
	private final Map<Integer, MapRegion>		regions				= new HashMap<Integer, MapRegion>();

	/**
	 * All objects spawned in this world map instance
	 */
	private final Map<Integer, VisibleObject>	worldMapObjects		= new ConcurrentHashMap<Integer, VisibleObject>();

	/**
	 * All players spawned in this world map instance
	 */
	private final Map<Integer, Player>			worldMapPlayers		= new ConcurrentHashMap<Integer, Player>();

	private final Set<Integer>					registeredObjects	= Collections
																		.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());

	/**
	 * Id of this instance (channel)
	 */
	private int									instanceId;
	/**
	 * Destroy task of this instance
	 */
	private Future<?>							destroyTask;
	/**
	 * Constructor.
	 *
	 * @param parent
	 */
	public WorldMapInstance(WorldMap parent, int instanceId)
	{
		this.parent = parent;
		this.instanceId = instanceId;
	}

	/**
	 * Return World map id.
	 *
	 * @return world map id
	 */
	public int getMapId()
	{
		return getParent().getMapId();
	}

	/**
	 * Returns WorldMap witch is parent of this instance
	 *
	 * @return parent
	 */
	public WorldMap getParent()
	{
		return parent;
	}

	/**
	 * Returns MapRegion that contains coordinates of VisibleObject. If the region doesn't exist, it's created.
	 *
	 * @param object
	 *
	 * @return a MapRegion
	 */
	MapRegion getRegion(VisibleObject object)
	{
		return getRegion(object.getX(), object.getY());
	}

	/**
	 * Returns MapRegion that contains given x,y coordinates. If the region doesn't exist, it's created.
	 *
	 * @param x
	 * @param y
	 * @return a MapRegion
	 */
	MapRegion getRegion(float x, float y)
	{
		int regionId = getRegionId(x, y);
		MapRegion region = regions.get(regionId);
		if(region == null)
		{
			synchronized(this)
			{
				region = regions.get(regionId);
				if(region == null)
				{
					region = createMapRegion(regionId);
				}
			}
		}
		return region;
	}

	/**
	 * Calculate region id from cords.
	 *
	 * @param x
	 * @param y
	 * @return region id.
	 */
	private int getRegionId(float x, float y)
	{
		return ((int) x) / regionSize * maxWorldSize + ((int) y) / regionSize;
	}

	/**
	 * Create new MapRegion and add link to neighbours.
	 *
	 * @param regionId
	 * @return newly created map region
	 */
	private MapRegion createMapRegion(int regionId)
	{
		MapRegion r = new MapRegion(regionId, this);
		regions.put(regionId, r);

		int rx = regionId / maxWorldSize;
		int ry = regionId % maxWorldSize;

		for(int x = rx - 1; x <= rx + 1; x++)
		{
			for(int y = ry - 1; y <= ry + 1; y++)
			{
				if(x == rx && y == ry)
					continue;
				int neighbourId = x * maxWorldSize + y;

				MapRegion neighbour = regions.get(neighbourId);
				if(neighbour != null)
				{
					r.addNeighbourRegion(neighbour);
					neighbour.addNeighbourRegion(r);
				}
			}
		}
		return r;
	}

	/**
	 * Returs {@link World} instance to which belongs this WorldMapInstance
	 * @return World
	 */
	public World getWorld()
	{
		return getParent().getWorld();
	}
	
	/**
	 * 
	 * @param object
	 */
	public void addObject(VisibleObject object)
	{
		if(worldMapObjects.put(object.getObjectId(), object) != null)
			throw new DuplicateAionObjectException();

		if(object instanceof Player)
			worldMapPlayers.put(object.getObjectId(), (Player) object);
	}
	
	/**
	 * 
	 * @param object
	 */
	public void removeObject(AionObject object)
	{
		worldMapObjects.remove(object.getObjectId());
		if(object instanceof Player)
			worldMapPlayers.remove(object.getObjectId());
	}

	/**
	 * @return the instanceIndex
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 *  Check player is in instance
	 *  
	 * @param objId
	 * @return
	 */
	public boolean isInInstance(int objId)
	{
		return worldMapPlayers.containsKey(objId);
	}

	/**
	 * @return the destroyTask
	 */
	public Future<?> getDestroyTask()
	{
		return destroyTask;
	}

	/**
	 * @param destroyTask the destroyTask to set
	 */
	public void setDestroyTask(Future<?> destroyTask)
	{
		this.destroyTask = destroyTask;
	}
	
	/**
	 * @return
	 */
	public Iterator<VisibleObject> objectIterator()
	{
		return worldMapObjects.values().iterator();
	}
	
	/**
	 * @return
	 */
	public Iterator<Player> playerIterator()
	{
		return worldMapPlayers.values().iterator();
	}
	
	/**
	 * @param objectId
	 */
	public void register(int objectId)
	{
		registeredObjects.add(objectId);
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean isRegistered(int objectId)
	{
		return registeredObjects.contains(objectId);
	}
}
