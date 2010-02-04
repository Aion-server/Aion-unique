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
package com.aionemu.gameserver.world.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 *
 */
public class ZoneManager
{
	private Map<ZoneName, ZoneInstance> zoneMap = new HashMap<ZoneName, ZoneInstance>();
	private Map<Integer, List<ZoneInstance>> zoneByMapIdMap = new HashMap<Integer, List<ZoneInstance>>();

	private static final ZoneManager instance = new ZoneManager();
	
	/**
	 *  Initializes zone instances using zone templates from xml
	 *  Adds neighbors to each zone instance using lookup by ZoneName
	 */
	public void initializeZones()
	{
		Iterator<ZoneTemplate> iterator = DataManager.ZONE_DATA.iterator();
		while(iterator.hasNext())
		{
			ZoneTemplate template = iterator.next();
			ZoneInstance instance = new ZoneInstance(template);
			zoneMap.put(template.getName(), instance);

			List<ZoneInstance> zoneListForMap = zoneByMapIdMap.get(template.getMapid());
			if(zoneListForMap == null)
			{
				zoneListForMap = new ArrayList<ZoneInstance>();
				zoneByMapIdMap.put(template.getMapid(), zoneListForMap);
			}
			zoneListForMap.add(instance);
		}

		for(ZoneInstance zoneInstance : zoneMap.values())
		{
			ZoneTemplate template = zoneInstance.getTemplate();
			List<ZoneInstance> neighbors = new ArrayList<ZoneInstance>();
			for(ZoneName zone : template.getLink())
			{
				neighbors.add(zoneMap.get(zone));
			}
			zoneInstance.setNeighbors(neighbors);
		}
	}

	/**
	 *  Will check current zone of player and call corresponding controller methods
	 *  
	 * @param player
	 */
	public void checkZone(Player player)
	{
		ZoneInstance currentInstance = player.getZoneInstance();
		if(currentInstance == null)
		{
			return;
		}

		List<ZoneInstance> neighbors = currentInstance.getNeighbors();
		for(ZoneInstance zone : neighbors)
		{
			if(checkPointInZone(zone, player.getPosition()))
			{
				player.setZoneInstance(zone);
				player.getController().onEnterZone(zone);
				player.getController().onLeaveZone(currentInstance);
			}
		}
	}

	/**
	 * @param player
	 */
	public void findZoneInCurrentMap(Player player)
	{
		MapRegion mapRegion = player.getActiveRegion();
		if(mapRegion == null)
			return;
		
		List<ZoneInstance> zones = zoneByMapIdMap.get(mapRegion.getMapId());
		if(zones == null)
			return;
		
		for(ZoneInstance zone : zones)
		{
			if(checkPointInZone(zone, player.getPosition()))
			{
				player.setZoneInstance(zone);
				player.getController().onEnterZone(zone);
			}
		}
	}
	
	/**
	 *  Checks whether player is inside specific zone
	 *  
	 * @param player
	 * @param zoneName
	 * @return true if player is inside specified zone
	 */
	public boolean isInsideZone(Player player, ZoneName zoneName)
	{
		ZoneInstance zoneInstance = zoneMap.get(zoneName);
		if(zoneInstance == null)
			return false;
		
		return checkPointInZone(zoneInstance, player.getPosition());
	}

	/**
	 *  Main algorithm that analyzes point-in-polygon
	 *  
	 * @param zone
	 * @param position
	 * @return
	 */
	private boolean checkPointInZone(ZoneInstance zone, WorldPosition position)
	{
		int corners = zone.getCorners();
		float[] xCoords = zone.getxCoordinates();
		float[] yCoords = zone.getyCoordinates();

		float x = position.getX();
		float y = position.getY();

		int i, j = corners-1;
		boolean  inside = false;

		for (i=0; i<corners; i++) 
		{
			if (yCoords[i] < y && yCoords[j] >= y || yCoords[j] < y && yCoords[i] >= y) 
			{
				if (xCoords[i]+(y-yCoords[i])/(yCoords[j]-yCoords[i])*(xCoords[j]-xCoords[i])<x) 
				{
					inside = !inside; 
				}
			}
			j=i; 
		}

		return inside;
	}
	
	public static ZoneManager getInstance()
	{
		return instance;
	}
}
