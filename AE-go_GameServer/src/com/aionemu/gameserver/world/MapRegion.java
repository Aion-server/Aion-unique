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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * Just some part of map.
 * 
 * @author -Nemesiss-
 * 
 */
public class MapRegion
{
	/**
	 * Region id of this map region [NOT WORLD ID!]
	 */
	private final int					regionId;
	/**
	 * WorldMapInstance witch is parent of this map region.
	 */
	private final WorldMapInstance		parent;
	/**
	 * Surrounding regions + self.
	 */
	private final List<MapRegion>		neighbours	= new ArrayList<MapRegion>(9);
	/**
	 * Objects on this map region.
	 */
	private final FastMap<Integer, VisibleObject> 	objects	= new FastMap<Integer, VisibleObject>().setShared(true);

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param parent
	 */
	MapRegion(int id, WorldMapInstance parent)
	{
		this.regionId = id;
		this.parent = parent;
		this.neighbours.add(this);
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

	/** Return an instance of {@link World}, which keeps map, to which belongs this region */
	public World getWorld()
	{
		return getParent().getWorld();
	}
	/**
	 * Returns region id of this map region. [NOT WORLD ID!]
	 * 
	 * @return region id.
	 */
	public int getRegionId()
	{
		return regionId;
	}

	/**
	 * Returns WorldMapInstance witch is parent of this instance
	 * 
	 * @return parent
	 */
	public WorldMapInstance getParent()
	{
		return parent;
	}

	/**
	 * Returns iterator over neighbours regions
	 * 
	 * @return neighbours iterator
	 */
	public Iterator<MapRegion> getNeighboursIterator()
	{
		return neighbours.iterator();
	}

	/**
	 * Returns iterator over AionObjects on this region
	 * 
	 * @return objects iterator
	 */
	public Collection<VisibleObject> getObjects()
	{
		return objects.values();
	}

	/**
	 * Add neighbour region to this region neighbours list.
	 * 
	 * @param neighbour
	 */
	void addNeighbourRegion(MapRegion neighbour)
	{
		neighbours.add(neighbour);
	}

	/**
	 * Add AionObject to this region objects list.
	 * 
	 * @param object
	 */
	void add(VisibleObject object)
	{
		objects.put(object.getObjectId(), object);
		parent.onEnter(object);
	}

	/**
	 * Remove AionObject from region objects list.
	 * 
	 * @param object
	 */
	void remove(VisibleObject object)
	{
		objects.remove(object.getObjectId());
		parent.onLeave(object);
	}
}
