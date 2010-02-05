/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.model.geometry;

import java.awt.Point;
import java.io.Serializable;

/**
 * This class represents 3D point.<br>
 * It's valid for serializing and cloning.
 * 
 * @author SoulKeeper
 */
@SuppressWarnings("serial")
public class Point3D implements Cloneable, Serializable
{

	/**
	 * X coord of the point
	 */
	private int	x;

	/**
	 * Y coord of the point
	 */
	private int	y;

	/**
	 * Z coord of the point
	 */
	private int	z;

	/**
	 * Creates new point with coords 0, 0, 0
	 */
	public Point3D()
	{
	}

	/**
	 * Creates new 3D point from 2D point and z coord
	 * 
	 * @param point
	 *            2D point
	 * @param z
	 *            z coord
	 */
	public Point3D(Point point, int z)
	{
		this(point.x, point.y, z);
	}

	/**
	 * Clones another 3D point
	 * 
	 * @param point
	 *            3d point to clone
	 */
	public Point3D(Point3D point)
	{
		this(point.getX(), point.getY(), point.getZ());
	}

	/**
	 * Creates new 3d point with given coords
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public Point3D(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns x coord
	 * 
	 * @return x coord
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Sets x coord of this point
	 * 
	 * @param x
	 *            x coord
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Returns y coord of this point
	 * 
	 * @return y coord
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Sets y coord of this point
	 * 
	 * @param y
	 *            y coord
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Returns z coord of this point
	 * 
	 * @return z coord
	 */
	public int getZ()
	{
		return z;
	}

	/**
	 * Sets z coord of this point
	 * 
	 * @param z
	 *            z coord
	 */
	public void setZ(int z)
	{
		this.z = z;
	}

	/**
	 * Checks if this point is equal to another point
	 * 
	 * @param o
	 *            point to compare with
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof Point3D))
			return false;

		Point3D point3D = (Point3D) o;

		return x == point3D.x && y == point3D.y && z == point3D.z;
	}

	/**
	 * Returns point's hashcode.<br>
	 * 
	 * <pre>
	 * int result = x;
	 * result = 31 * result + y;
	 * result = 31 * result + z;
	 * return result;
	 * </pre>
	 * 
	 * @return hashcode
	 */
	@Override
	public int hashCode()
	{
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}

	/**
	 * Clones this point
	 * 
	 * @return copy of this point
	 * @throws CloneNotSupportedException
	 *             never thrown
	 */
	@Override
	public Point3D clone() throws CloneNotSupportedException
	{
		return new Point3D(this);
	}

	/**
	 * Formatted string representation of this point
	 * 
	 * @return returns formatted string that represents this point
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Point3D");
		sb.append("{x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", z=").append(z);
		sb.append('}');
		return sb.toString();
	}
}
