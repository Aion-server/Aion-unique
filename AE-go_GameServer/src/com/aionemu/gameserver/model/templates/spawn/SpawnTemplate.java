/*
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
package com.aionemu.gameserver.model.templates.spawn;

import java.util.BitSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Luno
 * 
 * modified by ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "object")
public class SpawnTemplate
{
	/**
	 * XML attributes
	 * Order should be reversed to XML attributes order
	 */
	@XmlTransient
	private SpawnGroup			spawnGroup;
	@XmlAttribute(name = "rw")
	private int			randomWalk;
	@XmlAttribute(name = "w")
	private int			walkerId;
	@XmlAttribute(name = "h")
	private byte heading;
	@XmlAttribute(name = "z")
	private float z;
	@XmlAttribute(name = "y")
	private float y;
	@XmlAttribute(name = "x")
	private float x;
	@XmlAttribute(name = "staticid")
	private int staticid;
	
	@XmlTransient
	private BitSet spawnState = new BitSet();
	@XmlTransient
	private BitSet noRespawn = new BitSet();
	@XmlTransient
	private BitSet restingState = new BitSet();
	
	/**
	 * Constructor used by unmarshaller
	 */
	public SpawnTemplate()
	{
		
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param walkerId
	 * @param randomWalk
	 */
	public SpawnTemplate(float x, float y, float z, byte heading, int walkerId, int randomWalk)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.walkerId = walkerId;
		this.randomWalk = randomWalk;
	}
	
	public int getWorldId()
	{
		return spawnGroup.getMapid();
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getZ()
	{
		return z;
	}

	public byte getHeading()
	{
		return heading;
	}
	
	public int getWalkerId()
	{
		return walkerId;
	}
	
	public boolean hasRandomWalk()
	{
		return randomWalk > 0;
	}

	/**
	 * @return the spawnGroup
	 */
	public SpawnGroup getSpawnGroup()
	{
		return spawnGroup;
	}

	/**
	 * @param spawnGroup the spawnGroup to set
	 */
	public void setSpawnGroup(SpawnGroup spawnGroup)
	{
		this.spawnGroup = spawnGroup;
	}

	/**
	 * @return the isResting
	 */
	public boolean isResting(int instance)
	{
		return restingState.get(instance);
	}

	/**
	 * @param isResting the isResting to set
	 */
	public void setResting(boolean isResting, int instance)
	{
		restingState.set(instance, isResting);
	}

	/**
	 * @return the isSpawned
	 */
	public boolean isSpawned(int instance)
	{
		return spawnState.get(instance);
	}

	/**
	 * @param isSpawned the isSpawned to set
	 */
	public void setSpawned(boolean isSpawned, int instance)
	{
		spawnState.set(instance, isSpawned);

	}

	/**
	 * @param instance
	 * @return true or false
	 */
	public boolean isNoRespawn(int instance)
	{		
		return noRespawn.get(instance);
	}

	/**
	 * @param noRespawn the respawn to set
	 */
	public void setNoRespawn(boolean noRespawn, int instance)
	{
		this.noRespawn.set(instance, noRespawn);
	}

	/**
	 * @return the staticid
	 */
	public int getStaticid()
	{
		return staticid;
	}
}
