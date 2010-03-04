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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.items.NpcEquippedGear;
import com.aionemu.gameserver.model.templates.stats.NpcRank;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;

/**
 * @author Luno
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "npc_template")
public class NpcTemplate extends VisibleObjectTemplate
{
	private int					npcId;
	@XmlAttribute(name = "level", required = true)
	private byte				level;
	@XmlAttribute(name = "name_id", required = true)
	private int					nameId;
	@XmlAttribute(name = "title_id")
	private int					titleId;
	@XmlAttribute(name = "name")
	private String				name;
	@XmlAttribute(name = "height")
	private float				height			= 1;
	@SuppressWarnings("unused")
	@XmlAttribute(name = "talking_distance")
	private int					talkingDistance	= 2;
	@XmlAttribute(name = "npc_type", required = true)
	private NpcType				npcType;
	@XmlElement(name = "stats")
	private NpcStatsTemplate	statsTemplate;
	@XmlElement(name = "equipment")
	private NpcEquippedGear		equipment;
	@SuppressWarnings("unused")
	@XmlElement(name = "ammo_speed")
	private int					ammoSpeed		= 0;
	@XmlAttribute(name = "rank")
	private NpcRank				rank;
	@XmlAttribute(name = "srange")
	private int					aggrorange;
	@XmlAttribute(name = "tribe")
	private String				tribe;
	
	@Override
	public int getTemplateId()
	{
		return npcId;
	}

	public int getNameId()
	{
		return nameId;
	}

	public int getTitleId()
	{
		return titleId;
	}

	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @return float
	 */
	public float getHeight()
	{
		return height;
	}

	public NpcType getNpcType()
	{
		return npcType;
	}

	public NpcEquippedGear getEquipment()
	{
		return equipment;
	}

	public byte getLevel()
	{
		return level;
	}

	/**
	 * @return the statsTemplate
	 */
	public NpcStatsTemplate getStatsTemplate()
	{
		return statsTemplate;
	}

	/**
	 * @param statsTemplate the statsTemplate to set
	 */
	public void setStatsTemplate(NpcStatsTemplate statsTemplate)
	{
		this.statsTemplate = statsTemplate;
	}
	
	/**
	 * @return the tribe
	 */
	public String getTribe()
	{
		return tribe;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Npc Template id: " + npcId + " name: " + name;
	}

	
	@SuppressWarnings("unused")
	@XmlID
	@XmlAttribute(name = "npc_id", required = true)
	private void setXmlUid(String uid)
	{
		/*
		 * This method is used only by JAXB unmarshaller.
		 * I couldn't set annotations at field, because
		 * ID must be a string. 
		 */
		npcId = Integer.parseInt(uid);
	}

	/**
	 * @return the rank
	 */
	public NpcRank getRank()
	{
		return rank;
	}
	
	public int getAggroRange()
	{
		return aggrorange;
	}

}