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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.ConditionChangeListener;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.Effect;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class Skill
{
	private Env env;
	private SkillTemplate skillTemplate;
	
	/**
	 *  Each skill is a separate object upon invocation
	 *  
	 * @param env
	 */
	public Skill(Env env)
	{
		super();
		this.env = env;
		this.skillTemplate = env.getSkillTemplate();
	}
	
	/**
	 *  Skill entry point
	 */
	public void useSkill()
	{
		if(!preCastCheck())
			return;
		
		startCast();
		env.getEffector().getController().attach(env.getConditionChangeListener());
		
		if(skillTemplate.getDuration() > 0)
		{
			schedule();
		}
		else
		{
			endCast();
		}
	}
	
	/**
	 *  Start casting of skill
	 */
	private void startCast()
	{
		Player effector = (Player) env.getEffector();
		int targetObjId = env.getEffected() !=  null ? env.getEffected().getObjectId() : 0;
		final int unk = 0;
		PacketSendUtility.broadcastPacket(effector, 
			new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillTemplate.getLevel(),
				unk, targetObjId, skillTemplate.getDuration()), true);
	}
	
	/**
	 *  Apply effects and perform actions specified in skill template
	 */
	private void endCast()
	{
		if(!preUsageCheck())
			return;
		
		Effects skillEffects = skillTemplate.getEffects();
		if(skillEffects != null)
		{
			for(Effect effect : skillEffects.getEffects())
			{
				effect.apply(env);
			}
		}
		
		Actions skillActions = skillTemplate.getActions();
		if(skillActions != null)
		{
			for(Action action : skillActions.getActions())
			{
				
				action.act(env);
			}
		}
	}
	
	/**
	 *  Schedule actions/effects of skill (channeled skills)
	 */
	private void schedule()
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			public void run() 
			{
				endCast();
			}   
		}, skillTemplate.getDuration());
	}
	
	/**
	 *  Check all conditions before starting cast
	 */
	private boolean preCastCheck()
	{
		Conditions skillConditions = skillTemplate.getStartconditions();
		return checkConditions(skillConditions);
	}
	
	/**
	 *  Check all conditions before using skill
	 */
	private boolean preUsageCheck()
	{
		Conditions skillConditions = skillTemplate.getUseconditions();
		return checkConditions(skillConditions);
	}
	
	private boolean checkConditions(Conditions conditions)
	{
		if(conditions != null)
		{
			for(Condition condition : conditions.getConditions())
			{
				if(!condition.verify(env))
				{
					return false;
				}
			}
		}
		return true;
	}
}
