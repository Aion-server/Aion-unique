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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.spawn.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.utils.collections.IteratorIterator;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "spawns")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpawnsData implements Iterable<SpawnGroup>
{
	@XmlElement(name = "spawn")
	protected List<SpawnGroup> spawnGroups;
	
	//key is mapid
	@XmlTransient
	private Map<Integer, ArrayList<SpawnGroup>> spawnsByMapId = new HashMap<Integer, ArrayList<SpawnGroup>>();
	//key is npcid
	@XmlTransient
	private Map<Integer, ArrayList<SpawnGroup>> spawnsByNpcID = new HashMap<Integer, ArrayList<SpawnGroup>>();
	//key is mapid
	@XmlTransient
	private Map<Integer, ArrayList<SpawnGroup>> spawnsByMapIdNew = new HashMap<Integer, ArrayList<SpawnGroup>>();
	@XmlTransient
	private int counter = 0;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SpawnGroup spawnGroup : spawnGroups)
		{
			//set parent spawnGroup for each spawn object
			for(SpawnTemplate template : spawnGroup.getObjects())
			{
				template.setSpawnGroup(spawnGroup);
			}

			addNewSpawnGroup(spawnGroup, spawnGroup.getMapid(), spawnGroup.getNpcid(), false);

			counter += spawnGroup.getObjects().size();
		}
		spawnGroups = null;
	}

	/**
	 *  Will return one of possible spawns for this npcid
	 *  Used in quick location of objects
	 *  
	 * @param npcId
	 * @return SpawnTemplate
	 */
	public SpawnTemplate getFirstSpawnByNpcId(int npcId)
	{
		List<SpawnGroup> spawnGroups =  spawnsByNpcID.get(npcId);
		if(spawnGroups == null)
			return null;

		for(SpawnGroup spawnGroup : spawnGroups)
		{
			if(spawnGroup.getObjects() != null)
			{
				return spawnGroup.getObjects().get(0);
			}
		}
		return null;
	}

	@Override
	public Iterator<SpawnGroup> iterator()
	{	
		return new IteratorIterator<SpawnGroup>(spawnsByNpcID.values());
	}
	
	public List<SpawnGroup> getSpawnsForWorld(int worldId)
	{
		return spawnsByMapId.get(worldId);
	}
	
	public List<SpawnGroup> getNewSpawnsForWorld(int worldId)
	{
		return spawnsByMapIdNew.get(worldId);
	}

	/**
	 * @return counter
	 */
	public int size()
	{
		return counter;
	}

	/**
	 *  All new spawns have following structure : 1 SpawnGroup with 1 Object (pool=1)
	 *  Used only with admin command
	 *  
	 * @param spawnTemplate
	 * @param worldId
	 */
	public void addNewSpawnGroup(SpawnGroup spawnGroup, int worldId, int npcId, boolean isNew)
	{
		//put to map spawns
		ArrayList<SpawnGroup> mapSpawnGroups = spawnsByMapId.get(worldId);
		if(mapSpawnGroups == null)
		{
			mapSpawnGroups = new ArrayList<SpawnGroup>();
			spawnsByMapId.put(worldId, mapSpawnGroups);
		}
		mapSpawnGroups.add(spawnGroup);

		//put to npcid spawns
		ArrayList<SpawnGroup> npcIdSpawnGroups = spawnsByNpcID.get(npcId);
		if(npcIdSpawnGroups == null)
		{
			npcIdSpawnGroups = new ArrayList<SpawnGroup>();
			spawnsByNpcID.put(npcId, npcIdSpawnGroups);
		}
		npcIdSpawnGroups.add(spawnGroup);
		
		//put to new map spawns
		if(isNew)
		{
			//put to map spawns
			ArrayList<SpawnGroup> mapNewSpawnGroups = spawnsByMapIdNew.get(worldId);
			if(mapNewSpawnGroups == null)
			{
				mapNewSpawnGroups = new ArrayList<SpawnGroup>();
				spawnsByMapIdNew.put(worldId, mapNewSpawnGroups);
			}
			mapNewSpawnGroups.add(spawnGroup);
		}
	}

	public void clear()
	{
		spawnsByMapId.clear();
		spawnsByNpcID.clear();
	}

	/**
	 * @param spawn
	 */
	public void removeSpawn(SpawnTemplate spawn)
	{
		if(spawn.getSpawnGroup().size() > 1)
		{
			spawn.getSpawnGroup().getObjects().remove(spawn);
			return;
		}
		
		List<SpawnGroup> worldSpawns = spawnsByMapId.get(spawn.getWorldId());
		if(worldSpawns != null)
		{
			worldSpawns.remove(spawn.getSpawnGroup());
		}
		
		List<SpawnGroup> worldNewSpawns = spawnsByMapIdNew.get(spawn.getWorldId());
		if(worldNewSpawns != null)
		{
			worldNewSpawns.remove(spawn.getSpawnGroup());
		}
		
		List<SpawnGroup> spawnsByNpc = spawnsByNpcID.get(spawn.getSpawnGroup().getNpcid());
		if(spawnsByNpc != null)
		{
			spawnsByNpc.remove(spawn.getSpawnGroup());
		}
	}

	/**
	 * 
	 *  Don't use this method from core functionality
	 *  Used only while marshalling spawns to a file
	 *  
	 * @return the spawnGroups
	 */
	public List<SpawnGroup> getSpawnGroups()
	{
		if(spawnGroups == null)
			spawnGroups = new ArrayList<SpawnGroup>();
		return spawnGroups;
	}
	
}
