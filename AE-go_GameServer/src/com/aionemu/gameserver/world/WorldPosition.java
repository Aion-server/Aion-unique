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

/**
 * Position of object in the world.
 * 
 * @author -Nemesiss-
 * 
 */
public class WorldPosition
{
	/**
	 * Map Region.
	 */
	private MapRegion	mapRegion;
	/**
	 * World position x
	 */
	private float		x;
	/**
	 * World position y
	 */
	private float		y;
	/**
	 * World position z
	 */
	private float		z;

	/**
	 * Value from 0 to 120 (120==0 actually)
	 */
	private byte		heading;
	/**
	 * indicating if object is spawned or not.
	 */
	private boolean		isSpawned	= false;

	/**
	 * Return World map id.
	 * 
	 * @return world map id
	 */
	public int getMapId()
	{
		return mapRegion.getMapId();
	}

	/**
	 * Return World position x
	 * 
	 * @return x
	 */
	public float getX()
	{
		return x;
	}

	/**
	 * Return World position y
	 * 
	 * @return y
	 */
	public float getY()
	{
		return y;
	}

	/**
	 * Return World position z
	 * 
	 * @return z
	 */
	public float getZ()
	{
		return z;
	}

	/**
	 * Return map region
	 * 
	 * @return Map region
	 */
	public MapRegion getMapRegion()
	{
		return isSpawned ? mapRegion : null;
	}
	
	public int getInstanceId()
	{
		return mapRegion.getParent().getInstanceId();
	}
	
	public int getInstanceCount()
	{
		return mapRegion.getParent().getParent().getInstanceCount();
	}

	/**
	 * Return heading.
	 * 
	 * @return heading
	 */
	public byte getHeading()
	{
		return heading;
	}

	/**
	 * Returns the {@link World} instance in which this position is located. :D
	 * @return World
	 */
	public World getWorld()
	{
		return mapRegion.getWorld();
	}
	/**
	 * Check if object is spawned.
	 * 
	 * @return true if object is spawned.
	 */
	public boolean isSpawned()
	{
		return isSpawned;
	}

	/**
	 * Set isSpawned to given value.
	 * 
	 * @param val
	 */
	void setIsSpawned(boolean val)
	{
		isSpawned = val;
	}

	/**
	 * Set map region
	 * 
	 * @param r
	 *            - map region
	 */
	void setMapRegion(MapRegion r)
	{
		mapRegion = r;
	}

	/**
	 * Set world position.
	 * 
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 *            Value from 0 to 120 (120==0 actually)
	 */
	void setXYZH(float newX, float newY, float newZ, byte newHeading)
	{
		x = newX;
		y = newY;
		z = newZ;
		heading = newHeading;
	}

	@Override
	public String toString()
	{
		return "WorldPosition [heading=" + heading + ", isSpawned=" + isSpawned + ", mapRegion=" + mapRegion + ", x="
			+ x + ", y=" + y + ", z=" + z + "]";
	}
	
}
