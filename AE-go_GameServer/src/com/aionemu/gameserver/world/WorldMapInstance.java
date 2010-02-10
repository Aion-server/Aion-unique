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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

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
	public static final int				regionSize		= 500;
	/**
	 * Max world size - actually it must be some value bigger than world size. Used only for id generation.
	 */
	private static final int				maxWorldSize	= 10000;
	/**
	 * WorldMap witch is parent of this instance.
	 */
	private final WorldMap					parent;
	/**
	 * List of active regions.
	 */
	private final Map<Integer, MapRegion>	regions			= new HashMap<Integer, MapRegion>();
	
	/**
	 * Current player count in this instance
	 */
	private int 							currentPlayerCount;
	
	private int								instanceId;
	/**
	 * Constructor.
	 *
	 * @param parent
	 */
	WorldMapInstance(WorldMap parent, int instanceId)
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
	 * @return the currentPlayerCount
	 */
	public int getCurrentPlayerCount()
	{
		return currentPlayerCount;
	}
	
	public void onEnter(VisibleObject object)
	{
		if (object instanceof Player)
			currentPlayerCount++;
	}
	
	public void onLeave(VisibleObject object)
	{
		if (object instanceof Player)
			currentPlayerCount--;
	}
	
	public boolean isInUse()
	{
		return currentPlayerCount > 0;
	}

	/**
	 * @return the instanceIndex
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	public boolean isInInstance(int objId)
	{
		return false;
	}

	public void setDestroyTime(int sec)
	{	
	}

	public void destroyInstance()
	{
	}

	public void addPlayer(int objId)
	{
	}

	public void removePlayer(int objId)
	{
	}
}
