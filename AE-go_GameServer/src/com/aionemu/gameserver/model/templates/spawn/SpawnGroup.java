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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spawn")
public class SpawnGroup
{
	/**
	 * XML attribues
	 */
	@XmlAttribute(name = "map")
	private int mapid;
	@XmlAttribute(name = "npcid")
	private int npcid;
	@XmlAttribute(name = "interval")
	private int interval;
	@XmlAttribute(name = "pool")
	private int pool;
	@XmlElement(name = "object")
	private List<SpawnTemplate> objects;
	
	/**
	 * Real-time properties
	 */
	private int lastSpawnedTemplate = -1;
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

	public SpawnTemplate getNextAvailableTemplate()
	{
		for(int i = 0; i < getObjects().size(); i++)
		{
			int nextSpawnCounter = lastSpawnedTemplate + 1;
			if(nextSpawnCounter >= objects.size())
				nextSpawnCounter = 0;
			
			 SpawnTemplate nextSpawn = objects.get(nextSpawnCounter);
			 if(nextSpawn.isSpawned())
				 continue;
			 
			 lastSpawnedTemplate = nextSpawnCounter;
			 return nextSpawn;
		}
		return null;
	}
	
	public int size()
	{
		return getObjects().size();
	}
	
}
