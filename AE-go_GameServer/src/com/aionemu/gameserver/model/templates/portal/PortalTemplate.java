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
package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Portal")
public class PortalTemplate
{
	@XmlAttribute(name = "npcid")
	protected int	npcId;
	@XmlAttribute(name = "instance")
	protected boolean	instance;
	@XmlAttribute(name = "minlevel")
	protected int	minLevel;
	@XmlAttribute(name = "maxlevel")
	protected int	maxLevel;
	@XmlAttribute(name = "group")
	protected boolean	group;
	@XmlAttribute(name = "race")
	protected Race race;
	@XmlElement(name = "entrypoint")
	protected EntryPoint	entryPoint;
	@XmlElement(name = "exitpoint")
	protected ExitPoint	exitPoint;
	/**
	 * @return the npcId
	 */
	public int getNpcId()
	{
		return npcId;
	}
	/**
	 * @return the instance
	 */
	public boolean isInstance()
	{
		return instance;
	}
	/**
	 * @return the minLevel
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	/**
	 * @return the group
	 */
	public boolean isGroup()
	{
		return group;
	}
	/**
	 * @return the race
	 */
	public Race getRace()
	{
		return race;
	}
	/**
	 * @return the entryPoint
	 */
	public EntryPoint getEntryPoint()
	{
		return entryPoint;
	}
	/**
	 * @return the exitPoint
	 */
	public ExitPoint getExitPoint()
	{
		return exitPoint;
	}
}
