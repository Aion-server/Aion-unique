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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author avol
 * 
 */
@XmlRootElement(name="bind_points")
@XmlAccessorType(XmlAccessType.NONE)

public class BindPointTemplate
{
	@XmlAttribute(name = "name", required = true)
	private String		name;

	@XmlAttribute(name = "npcid")
	private int		npcId;

	@XmlAttribute(name = "bindid")
	private int		bindId;
	
	@XmlAttribute(name = "mapid")
	private int	 	mapId = 0;

	@XmlAttribute(name = "posX")
	private float	 	x = 0;

	@XmlAttribute(name = "posY")
	private float	 	y = 0;

	@XmlAttribute(name = "posZ")
	private float	 	z = 0;

	@XmlAttribute(name = "price")
	private int	 	price = 0;

	public String getName()
	{
		return name;
	}

	public int getNpcId()
	{
		return npcId;
	}

	public int getBindId()
	{
		return bindId;
	}

	public int getZoneId()
	{
		return mapId;
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

	public int getPrice()
	{
		return price;
	}
}
