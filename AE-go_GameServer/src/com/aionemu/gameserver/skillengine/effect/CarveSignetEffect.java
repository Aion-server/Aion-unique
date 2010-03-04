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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarveSignetEffect")
public class CarveSignetEffect extends DamageEffect
{
	@XmlAttribute(required = true)
    protected int signetlvl;
    @XmlAttribute(required = true)
    protected int signetid;
    @XmlAttribute(required = true)
    protected String signet;
	    
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect);
		
		Creature effected = effect.getEffected();
		Effect placedSignet = effected.getEffectController().getAnormalEffect(signet);
		int nextSignetlvl = 1;
		if(placedSignet != null)
		{
			nextSignetlvl = placedSignet.getSkillId() - this.signetid + 2;
			if(nextSignetlvl > signetlvl || nextSignetlvl > 5)
				return;
			placedSignet.endEffect();
		}

		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(signetid + nextSignetlvl - 1);
		int effectsDuration = template.getEffectsDuration();
		Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, nextSignetlvl, effectsDuration);
		newEffect.initialize();
		newEffect.applyEffect();
	}

	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, DamageType.PHYSICAL);
	}
}
