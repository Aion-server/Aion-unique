/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.effect;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.templates.SkillEffectTemplate;

/**
 * @author ATracer
 *	
 *	Probably temporary solution for prototyping
 */
public enum SkillEffectType
{
	
	DAMAGE("damage");

	private static final Logger log = Logger.getLogger(SkillEffectType.class);
	
	private String name;
	
	private SkillEffectType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public static AbstractEffect getEffectByName(SkillEffectTemplate effectTemplate)
	{
		String effectName = effectTemplate.getName();
		for(SkillEffectType effect : values())
		{
			if(effectName.equals(effect.name))
			{
				AbstractEffect abstractEffect = SkillEffectFactory.createSkillEffect(effect);
				abstractEffect.setSkillEffectTemplate(effectTemplate);
				return abstractEffect;
			}
		}		
		return null;
	}
}
