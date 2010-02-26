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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Godstone")
public class GodstoneInfo
{
	@XmlAttribute
	private int skillid;
	@XmlAttribute
	private int skilllvl;
	@XmlAttribute
	private int probability;
	@XmlAttribute
	private int probabilityleft;
	/**
	 * @return the skillid
	 */
	public int getSkillid()
	{
		return skillid;
	}
	/**
	 * @return the skilllvl
	 */
	public int getSkilllvl()
	{
		return skilllvl;
	}
	/**
	 * @return the probability
	 */
	public int getProbability()
	{
		return probability;
	}
	/**
	 * @return the probabilityleft
	 */
	public int getProbabilityleft()
	{
		return probabilityleft;
	}
}
