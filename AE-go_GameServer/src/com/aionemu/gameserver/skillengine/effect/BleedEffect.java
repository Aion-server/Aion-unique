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

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BleedEffect")
public class BleedEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int checktime;	
	@XmlAttribute
	protected int value;
	@XmlAttribute
	protected int delta;

	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		// TODO Auto-generated method stub
		//TODO calc probability
		effect.increaseSuccessEffect();
	}

	@Override
	public void endEffect(Effect effect)
	{
		Creature effected = effect.getEffected();
		effected.getEffectController().unsetAbnormal(EffectId.BLEED.getEffectId());
	}

	@Override
	public void onPeriodicAction(Effect effect)
	{
		Creature effected = effect.getEffected();
		Creature effector = effect.getEffector();
		int valueWithDelta = value + delta * effect.getSkillLevel();
		int damage = StatFunctions.calculateMagicDamageToTarget(effector, effected, valueWithDelta, getElement());
		effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, damage);			
	}

	@Override
	public void startEffect(final Effect effect)
	{
		final Creature effected = effect.getEffected();

		effected.getEffectController().setAbnormal(EffectId.BLEED.getEffectId());
		
		Future<?> task = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				onPeriodicAction(effect);
			}
		}, checktime, checktime);
		effect.setPeriodicTask(task);	
	}

}
