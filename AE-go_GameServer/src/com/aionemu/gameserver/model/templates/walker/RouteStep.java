/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
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
package com.aionemu.gameserver.model.templates.walker;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author KKnD
 *
 */
@XmlRootElement(name = "routestep")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteStep
{
	@XmlAttribute(name = "step", required = true)
	private int step;
	
	@XmlAttribute(name = "loc_x", required = true)
	private float locX;

	@XmlAttribute(name = "loc_y", required = true)
	private float locY;
	
	@XmlAttribute(name = "loc_z", required = true)
	private float locZ;
	
	@XmlAttribute(name = "rest_time", required = true)
	private int time;

	public float getX()
	{
		return locX;
	}

	public float getY()
	{
		return locY;
	}
	
	public float getZ()
	{
		return locZ;
	}
	
	public int getRouteStep()
	{
		return step;
	}
	
	public int getRestTime()
	{
		return time;
	}
}
