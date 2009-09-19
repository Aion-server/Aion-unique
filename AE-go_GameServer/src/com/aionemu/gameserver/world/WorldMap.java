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

import com.aionemu.gameserver.model.templates.WorldMapTemplate;

/**
 * This object is representing one in-game map and can have instances.
 * 
 * @author -Nemesiss-
 * 
 */
public class WorldMap
{
	private WorldMapTemplate				worldMapTemplate;

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
			for(int i = 0; i < worldMapTemplate.getTwinCount(); i++)
				instances.put(i, new WorldMapInstance(this));
		else
			instances.put(0, new WorldMapInstance(this));
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
			for(int i = 0; i < worldMapTemplate.getTwinCount(); i++)
			{
				WorldMapInstance inst = getWorldMapInstance(i);
				// TODO! user count etc..
				return inst;
			}
			// TODO! whats now?
			return getWorldMapInstance(worldMapTemplate.getTwinCount());
		}
		else
			return getWorldMapInstance(0);
	}

	/**
	 * Returns WorldMapInstance by instanceId.
	 * 
	 * @param instanceId
	 * @return WorldMapInstance/
	 */
	public WorldMapInstance getWorldMapInstance(int instanceId)
	{
		return instances.get(instanceId);
	}

	/**
	 * Returns the World containing this WorldMap.
	 */
	public World getWorld()
	{
		return world;
	}
}
