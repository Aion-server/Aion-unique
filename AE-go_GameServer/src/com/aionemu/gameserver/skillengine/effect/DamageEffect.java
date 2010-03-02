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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;


/**
 * @author ATracer
 *  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageEffect")
public abstract class DamageEffect
extends EffectTemplate
{

	@XmlAttribute(required = true)
	protected int value;

	@XmlAttribute
	protected int delta;
	
	@Override
	public void applyEffect(Effect effect)
	{
		effect.getEffected().getController().onAttack(effect.getEffector(),
			effect.getSkillId(), TYPE.REGULAR, effect.getReserved1());
	}
	
	public void calculate(Effect effect, DamageType damageType)
	{
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		valueWithDelta = applyActionModifiers(effect, valueWithDelta);
		switch(damageType)
		{
			case PHYSICAL:
				AttackUtil.calculatePhysicalSkillAttackResult(effect, valueWithDelta);
				break;
			case MAGICAL:
				AttackUtil.calculateMagicalSkillAttackResult(effect, valueWithDelta, getElement());
				break;
			default:
				AttackUtil.calculatePhysicalSkillAttackResult(effect, 0);
		}	
		
		if(effect.getAttackStatus() != AttackStatus.RESIST 
			&& effect.getAttackStatus() != AttackStatus.DODGE)
			effect.increaseSuccessEffect();
	}
	
}
