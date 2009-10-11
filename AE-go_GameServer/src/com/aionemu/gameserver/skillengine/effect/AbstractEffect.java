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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillEffectTemplate;

/**
 * @author ATracer
 *
 */
public abstract class AbstractEffect
{
	protected SkillEffectTemplate skillEffectTemplate;
	
	/**
	 * If using this contructor - setting effectTemplate is required later
	 */
	public AbstractEffect()
	{
		
	}
	
	/**
	 * @param skillEffectTemplate
	 */
	public AbstractEffect(SkillEffectTemplate skillEffectTemplate)
	{
		this.skillEffectTemplate = skillEffectTemplate;
	}
	
	/**
	 * @return the skillEffectTemplate
	 */
	protected SkillEffectTemplate getSkillEffectTemplate()
	{
		return skillEffectTemplate;
	}
	/**
	 * @param skillEffectTemplate the skillEffectTemplate to set
	 */
	protected void setSkillEffectTemplate(SkillEffectTemplate skillEffectTemplate)
	{
		this.skillEffectTemplate = skillEffectTemplate;
	}
	/**
	 * @param target
	 * 
	 * Implemented as Visitor pattern
	 * 
	 * TODO: Possible change for Creature-Creature interaction
	 * TODO: generalize return type
	 */
	public abstract int influence(Player influencer, Creature target);
}
