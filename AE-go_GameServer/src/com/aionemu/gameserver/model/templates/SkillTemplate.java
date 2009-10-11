/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.templates;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "skill_template")
public class SkillTemplate
{
	@XmlAttribute(name = "skill_id", required = true)
	private int	skillId;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "type", required = true)
	private String skillType;
	
	@XmlAttribute(name = "handler", required = true)
	private String handlerType;
	
	@XmlAttribute(name = "level", required = true)
	private int level;
	
	@XmlAttribute(name = "duration", required = true)
	private int duration;
	
	@XmlAttribute(name = "target", required = true)
	private String target;
	
	@XmlAttribute(name = "cooldown", required = true)
	private String coolDown;
	
	//TODO min/max damage
	@XmlAttribute(name = "damage", required = false)
	private int damage;
	
	@XmlElement(name = "effects")
	private SkillEffectData skillEffectData;

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skillId;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the skillType
	 */
	public String getSkillType()
	{
		return skillType;
	}

	/**
	 * @return the handlerType
	 */
	public String getHandlerType()
	{
		return handlerType;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the duration
	 */
	public int getDuration()
	{
		return duration;
	}

	/**
	 * @return the target
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * @return the coolDown
	 */
	public String getCoolDown()
	{
		return coolDown;
	}

	/**
	 * @return the damage
	 */
	public int getDamage()
	{
		return damage;
	}

	/**
	 * @return the skillEffectData
	 */
	public SkillEffectData getSkillEffectData()
	{
		return skillEffectData;
	}
}
