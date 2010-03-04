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
package com.aionemu.gameserver.model.templates.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;


/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spawn")
public class SpawnGroup
{
	/**
	 * XML attributes
	 * Order should be reversed to XML attributes order
	 */
	@XmlAttribute(name = "time")
	private SpawnTime spawnTime;
	@XmlAttribute(name = "anchor")
	private String anchor;
	@XmlAttribute(name = "handler")
	private SpawnHandlerType handler;
	@XmlAttribute(name = "interval")
	private int interval;
	@XmlAttribute(name = "pool")
	private int pool;
	@XmlAttribute(name = "npcid")
	private int npcid;
	@XmlAttribute(name = "map")
	private int mapid;
	
	@XmlElement(name = "object")
	private List<SpawnTemplate> objects;
	
	
	/**
	 * Real-time properties
	 */
	@XmlTransient
	private Map<Integer, Integer> lastSpawnedTemplate = new HashMap<Integer, Integer>();
	
	/**
	 * Constructor used by unmarshaller
	 */
	public SpawnGroup()
	{
	}

	/**
	 *  Constructor used to create new spawns not defined in xml
	 *  
	 * @param mapid
	 * @param npcid
	 * @param interval
	 * @param pool
	 */
	public SpawnGroup(int mapid, int npcid, int interval, int pool)
	{
		super();
		this.mapid = mapid;
		this.npcid = npcid;
		this.interval = interval;
		this.pool = pool;
	}

	/**
	 * @return the mapid
	 */
	public int getMapid()
	{
		return mapid;
	}

	

	/**
	 * @return the npcid
	 */
	public int getNpcid()
	{
		return npcid;
	}

	/**
	 * @return the interval
	 */
	public int getInterval()
	{
		return interval;
	}

	/**
	 * @return the pool
	 */
	public int getPool()
	{
		return pool;
	}

	/**
	 * @return the objects
	 */
	public List<SpawnTemplate> getObjects()
	{
		if(this.objects == null)
			this.objects = new ArrayList<SpawnTemplate>();
		
		return this.objects;
	}

	/**
	 * @return the handler
	 */
	public SpawnHandlerType getHandler()
	{
		return handler;
	}

	/**
	 * @return the anchor
	 */
	public String getAnchor()
	{
		return anchor;
	}

	/**
	 * @return the dayTime
	 */
	public SpawnTime getSpawnTime()
	{
		return spawnTime;
	}
	
	/**
	 *  Returns next template to spawn
	 *  
	 * @param instance
	 * @return SpawnTemplate
	 */
	public SpawnTemplate getNextAvailableTemplate(int instance)
	{
		for(int i = 0; i < getObjects().size(); i++)
		{
			Integer lastSpawnCounter = lastSpawnedTemplate.get(instance);
			int nextSpawnCounter = lastSpawnCounter == null ? 0 : lastSpawnCounter + 1;
			
			if(nextSpawnCounter >= objects.size())
				nextSpawnCounter = 0;
			
			 SpawnTemplate nextSpawn = objects.get(nextSpawnCounter);
			 if(nextSpawn.isSpawned(instance))
				 continue;
			 
			 lastSpawnedTemplate.put(instance, nextSpawnCounter);
			 return nextSpawn;
		}
		return null;
	}
	
	public int size()
	{
		return getObjects().size();
	}

	/**
	 * 
	 */
	public SpawnTemplate getNextRandomTemplate()
	{
		return objects.get(Rnd.get(0, size() - 1));
	}

	/**
	 *  Last spawn counter will be reseted during instance respawn
	 * 
	 * @param instanceIndex
	 */
	public void resetLastSpawnCounter(int instanceIndex)
	{
		if(lastSpawnedTemplate.containsKey(instanceIndex))
			lastSpawnedTemplate.remove(instanceIndex);
	}
	
}
