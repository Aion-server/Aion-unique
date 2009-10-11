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
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 *
 */
public class DamageEffect extends AbstractEffect
{
	
	public DamageEffect()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public DamageEffect(SkillEffectTemplate skillEffectTemplate)
	{
		super(skillEffectTemplate);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.effect.AbstractEffect#influence(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public int influence(Player influencer, Creature target)
	{
		//TODO check whether calculations of damage differs between magical spells and physical ones
		int damage = StatFunctions.calculateMagicDamageToTarget(influencer, target, getSkillEffectTemplate());
        target.getLifeStats().reduceHp(damage);
        
        return damage;
	}
}
