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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 *
 */
public class DamageOverTimeEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int checktime;	
	@XmlAttribute(required = true)
	protected int value;
	@XmlAttribute
	protected int delta;

	@Override
	public void endEffect(Effect effect)
	{
		Creature effected = effect.getEffected();
		effected.getEffectController().unsetAbnormal(EffectId.DAMAGE_OT.getEffectId());
	}

	@Override
	public void onPeriodicAction(Effect effect)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void startEffect(Effect effect)
	{

		final int valueWithDelta = value + delta * effect.getSkillLevel();
		
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		final int skillId = effect.getSkillId();
		effected.getEffectController().setAbnormal(EffectId.DAMAGE_OT.getEffectId());

		Future<?> task = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(new Runnable(){

			@Override
			public void run()
			{
				int damage = StatFunctions.calculateMagicDamageToTarget(effector, effected, valueWithDelta, SkillElement.NONE);
				effected.getController().onAttack(effector, skillId, damage);
				
				//TODO SM_ATTACK_STATUS
			}
		}, checktime, checktime);
		
		effect.setTask(task);
	}

}
