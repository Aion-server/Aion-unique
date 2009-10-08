/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.locations;

import com.aionemu.gameserver.world.WorldMapType;

public class Loc
{
	private float _x;
	private float _y;
	private float _z;
	private byte _h; // ??
	private WorldMapType _worldType;

	public Loc()
	{
		_x = 0;
		_y = 0;
		_z = 0;
		_h = -1;
	}

	public Loc(float x, float y, float z)
	{
		_x = x;
		_y = y;
		_z = z;
		_h = -1;
	}

	public Loc(float x, float y, float z, byte h)
	{
		_x = x;
		_y = y;
		_z = z;
		_h = h;
	}

	public Loc(WorldMapType map, float x, float y, float z)
	{
		_x = x;
		_y = y;
		_z = z;
		_h = -1;
		_worldType = map;
	}

	public Loc(WorldMapType map, float x, float y, float z, byte h)
	{
		_x = x;
		_y = y;
		_z = z;
		_h = h;
		_worldType = map;
	}

	public float getX()
	{
		return _x;
	}

	public float getY()
	{
		return _y;
	}

	public float getZ()
	{
		return _z;
	}

	public byte getH()
	{
		return _h;
	}

	public void setX(float x)
	{
		_x = x;
	}

	public void setY(float y)
	{
		_y = y;
	}

	public void setZ(float z)
	{
		_z = z;
	}

	public void setH(byte h)
	{
		_h = h;
	}

	public WorldMapType getWorldType()
	{
		return _worldType;
	}

	public void setWorldType(WorldMapType type)
	{
		_worldType = type;
	}
}
