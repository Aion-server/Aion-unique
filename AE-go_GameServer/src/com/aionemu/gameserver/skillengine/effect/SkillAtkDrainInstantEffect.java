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

import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillAtkDrainInstantEffect")
public class SkillAtkDrainInstantEffect extends DamageEffect
{
	@XmlAttribute
	protected int		percent;
	@XmlAttribute(name = "heal_type")
	protected HealType	healType;

	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect);
		int value = effect.getReserved1() * percent / 100;
		switch(healType)
		{
			case HP:
				effect.getEffector().getLifeStats().increaseHp(value);
				break;
			case MP:
				effect.getEffector().getLifeStats().increaseMp(value);
				break;
		}
	}

	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, DamageType.PHYSICAL);
	}
}
