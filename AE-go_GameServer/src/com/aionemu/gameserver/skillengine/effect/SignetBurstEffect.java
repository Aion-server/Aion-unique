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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignetBurstEffect")
public class SignetBurstEffect extends DamageEffect
{
	@XmlAttribute
    protected int signetlvl;
	@XmlAttribute
	protected String signet;

	@Override
	public void calculate(Effect effect)
	{
		Creature effected = effect.getEffected();
		Effect signetEffect = effected.getEffectController().getAnormalEffect(signet);
		if(signetEffect == null)
			return;
		
		int level = signetEffect.getSkillLevel();
		int valueWithDelta = value + delta * effect.getSkillLevel();
		int finalDamage = valueWithDelta * level / 5;
		
		effect.setReserved1(finalDamage);		
		effect.increaseSuccessEffect();
		
		signetEffect.endEffect();
	}

}
