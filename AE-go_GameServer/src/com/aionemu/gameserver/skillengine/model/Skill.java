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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.controllers.movement.StartMovingListener;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.Property;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class Skill
{
	private List<Creature> effectedList;
	
	private Creature firstTarget;
	
	private Player effector;
	
	private int skillLevel;
	
	private int skillStackLvl;
	
	private StartMovingListener conditionChangeListener;
	
	private SkillTemplate skillTemplate;

	private boolean	firstTargetRangeCheck = true;
	
	public enum SkillType
	{
		CAST,
		ITEM,
		PASSIVE
	}
	
	/**
	 *  Each skill is a separate object upon invocation
	 *  Skill level will be populated from player SkillList
	 *  
	 * @param skillTemplate
	 * @param effector
	 * @param world
	 */
	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget)
	{
		this(skillTemplate, effector,
			effector.getSkillList().getSkillLevel(skillTemplate.getSkillId()), firstTarget);
	}
	
	/**
	 * 
	 * @param skillTemplate
	 * @param effector
	 * @param world
	 */
	public Skill(SkillTemplate skillTemplate, Player effector, int skillLvl, Creature firstTarget)
	{
		this.effectedList = new ArrayList<Creature>();
		this.conditionChangeListener = new StartMovingListener();
		this.firstTarget = firstTarget;
		this.skillLevel = skillLvl;
		this.skillStackLvl = skillTemplate.getLvl();
		this.skillTemplate = skillTemplate;
		this.effector = effector;
	}

	/**
	 *  Skill entry point
	 */
	public void useSkill()
	{
		if(!skillTemplate.isActive() 
			&& skillTemplate.isPassive()
			&& skillTemplate.isProvoked()
			&& skillTemplate.isToggle())
			return;
		
		if(!setProperties(skillTemplate.getInitproperties()))
			return;
		
		if(!preCastCheck())
			return;
		
		if(!setProperties(skillTemplate.getSetproperties()))
			return;
		
		Iterator<Creature> effectedIter = effectedList.iterator();
		while(effectedIter.hasNext())
		{
			Creature effected = effectedIter.next();
			if (!RestrictionsManager.canAffectBySkill(getEffector(), effected))
				effectedIter.remove();
		}
		
		if(effectedList.size() == 0)
			return;
		
		//start casting
		effector.setCasting(this);

		//temporary hook till i find permanent solution
		if(skillTemplate.isActive() || skillTemplate.isToggle())
		{
			startCast();
		}

		effector.getObserveController().attach(conditionChangeListener);
		
		if(skillTemplate.getDuration() > 0)
		{
			schedule(skillTemplate.getDuration());
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
		int targetObjId = firstTarget !=  null ? firstTarget.getObjectId() : 0;
		final int unk = 0;
		PacketSendUtility.broadcastPacket(effector, 
			new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel,
				unk, targetObjId, skillTemplate.getDuration()), true);
	}
	
	/**
	 *  Apply effects and perform actions specified in skill template
	 */
	private void endCast()
	{
		if(!effector.isCasting())
			return;

		//stop casting must be before preUsageCheck()
		effector.setCasting(null);
		
		if(!preUsageCheck())
			return;

		/**
		 * Create effects and precalculate result
		 */
		int spellStatus = 0;
		
		List<Effect> effects = new ArrayList<Effect>();		 
		if(skillTemplate.getEffects() != null)
		{
			int duration = skillTemplate.getEffects().getEffectsDuration();

			for(Creature effected : effectedList)
			{
				Effect effect = new Effect(effector, effected, skillTemplate,	skillLevel, duration);
				effect.initialize();
				spellStatus = effect.getSpellStatus().getId();
				effects.add(effect);
			}
		}
		
		/**
		 * If castspell - send SM_CASTSPELL_END packet
		 */
		if(skillTemplate.isActive() || skillTemplate.isToggle())
		{
			PacketSendUtility.broadcastPacket(effector,
				new SM_CASTSPELL_END(effector, skillTemplate.getSkillId(), skillLevel,
					firstTarget, effects, skillTemplate.getCooldown(), spellStatus), true);
		}
		
		/**
		 * Perform necessary actions (use mp,dp items etc)
		 */
		Actions skillActions = skillTemplate.getActions();
		if(skillActions != null)
		{
			for(Action action : skillActions.getActions())
			{	
				action.act(this);
			}
		}
		
		/**
		 * Apply effects to effected objects
		 */
		for(Effect effect : effects)
		{
			effect.applyEffect();
		}
	}
	
	/**
	 *  Schedule actions/effects of skill (channeled skills)
	 */
	private void schedule(int delay)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			public void run() 
			{
				endCast();
			}   
		}, delay);
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
				if(!condition.verify(this))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean setProperties(Properties properties)
	{
		if(properties != null)
		{
			for(Property property : properties.getProperties())
			{
				if(!property.set(this))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return the effectedList
	 */
	public List<Creature> getEffectedList()
	{
		return effectedList;
	}

	/**
	 * @return the effector
	 */
	public Player getEffector()
	{
		return effector;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel()
	{
		return skillLevel;
	}

	/**
	 * @return the skillStackLvl
	 */
	public int getSkillStackLvl()
	{
		return skillStackLvl;
	}

	/**
	 * @return the conditionChangeListener
	 */
	public StartMovingListener getConditionChangeListener()
	{
		return conditionChangeListener;
	}

	/**
	 * @return the skillTemplate
	 */
	public SkillTemplate getSkillTemplate()
	{
		return skillTemplate;
	}

	/**
	 * @return the firstTarget
	 */
	public Creature getFirstTarget()
	{
		return firstTarget;
	}

	/**
	 * @param firstTarget the firstTarget to set
	 */
	public void setFirstTarget(Creature firstTarget)
	{
		this.firstTarget = firstTarget;
	}

	/**
	 * @return true or false
	 */
	public boolean isPassive()
	{
		return skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}

	/**
	 * @return the firstTargetRangeCheck
	 */
	public boolean isFirstTargetRangeCheck()
	{
		return firstTargetRangeCheck;
	}

	/**
	 * @param firstTargetRangeCheck the firstTargetRangeCheck to set
	 */
	public void setFirstTargetRangeCheck(boolean firstTargetRangeCheck)
	{
		this.firstTargetRangeCheck = firstTargetRangeCheck;
	}
}
