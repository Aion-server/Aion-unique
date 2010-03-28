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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.ActionObserver.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.ProvokeTarget;
import com.aionemu.gameserver.skillengine.model.ProvokeType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvokerEffect")
public class ProvokerEffect extends EffectTemplate
{
	@XmlAttribute
	protected int			prob2;
	@XmlAttribute
	protected int			prob1;
	@XmlAttribute(name = "provoke_target")
	protected ProvokeTarget	provokeTarget;
	@XmlAttribute(name = "provoke_type")
	protected ProvokeType	provokeType;
	@XmlAttribute(name = "skill_id")
	protected int			skillId;

	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		effect.increaseSuccessEffect();
	}

	@Override
	public void startEffect(Effect effect)
	{
		ActionObserver observer = null;
		final Creature effector = effect.getEffector();
		switch(provokeType)
		{
			case ATTACK:
				observer = new ActionObserver(ObserverType.ATTACK){

					@Override
					public void attack(Creature creature)
					{
						if(Rnd.get(0, 100) <= prob2)
						{
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}

				};
				break;
			case ATTACKED:
				observer = new ActionObserver(ObserverType.ATTACKED){

					@Override
					public void attacked(Creature creature)
					{
						if(Rnd.get(0, 100) <= prob2)
						{
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
				break;
		}

		if(observer == null)
			return;

		effect.setActionObserver(observer);
		effect.getEffected().getObserveController().addObserver(observer);
	}

	/**
	 * 
	 * @param effector
	 * @param target
	 */
	private void createProvokedEffect(final Creature effector, Creature target)
	{
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		Effect e = new Effect(effector, target, template, template.getLvl(), template.getEffectsDuration());
		e.initialize();
		e.applyEffect();
	}

	/**
	 * 
	 * @param provokeTarget
	 * @param effector
	 * @param target
	 * @return
	 */
	private Creature getProvokeTarget(ProvokeTarget provokeTarget, Creature effector, Creature target)
	{
		switch(provokeTarget)
		{
			case ME:
				return effector;
			case OPPONENT:
				return target;
		}
		throw new IllegalArgumentException("Provoker target is invalid " + provokeTarget);
	}

	@Override
	public void endEffect(Effect effect)
	{
		ActionObserver observer = effect.getActionObserver();
		if(observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
