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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Luno
 * 
 */
@XmlRootElement(name="map")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapTemplate
{
	/**
	 * Map name.
	 */
	@XmlAttribute(name = "name")
	private String	name	= "";

	/**
	 * Map Id.
	 */
	@XmlAttribute(name = "id", required = true)
	private int		mapId;

	/**
	 * Number of twin instances [players will be balanced so every one could exp easy]
	 */
	@XmlAttribute(name = "twin_count")
	private int		twinCount;

	/**
	 * Max user at twin instance.
	 */
	@XmlAttribute(name = "max_user")
	private int		maxUser;

	/**
	 * True if this map is a prison.
	 */
	@XmlAttribute(name = "prison")
	private boolean	prison	= false;

	/**
	 * True if this map is a instance.
	 */
	@XmlAttribute(name = "instance")
	private boolean	instance	= false;

	public String getName()
	{
		return name;
	}

	public int getMapId()
	{
		return mapId;
	}

	public int getTwinCount()
	{
		return twinCount;
	}

	public int getMaxUser()
	{
		return maxUser;
	}

	public boolean isPrison()
	{
		return prison;
	}

	/**
	 * @return the instance
	 */
	public boolean isInstance()
	{
		return instance;
	}
}
