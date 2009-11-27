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
package com.aionemu.gameserver.model.templates.teleport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author orz
 * 
 */
@XmlRootElement(name="teleporter_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TeleporterTemplate
{
	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;
	
	@XmlAttribute(name = "name", required = true)
	private String name = "";

	@XmlAttribute(name = "teleportId", required = true)
	private int	 teleportId = 0;
	
	@XmlAttribute(name = "type", required = true)
	private TeleportType type;

	@XmlElement(name = "locations")
	private TeleLocIdData teleLocIdData;

	/**
	 * @return the npcId
	 */
	public int getNpcId()
	{
		return npcId;
	}

	/**
	 * @return the name of npc
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the teleportId
	 */
	public int getTeleportId()
	{
		return teleportId;
	}

	/**
	 * @return the type
	 */
	public TeleportType getType()
	{
		return type;
	}

	/**
	 * @return the teleLocIdData
	 */
	public TeleLocIdData getTeleLocIdData()
	{
		return teleLocIdData;
	}
}
