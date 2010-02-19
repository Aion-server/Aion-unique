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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Condition;
import com.aionemu.gameserver.skillengine.condition.ConditionChangeListener;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.Property;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class Skill
{
	private List<Creature> effectedList;
	
	private Creature firstTarget;
	
	private Player effector;
	
	private World world;
	
	private int skillLevel;
	
	private int skillStackLvl;
	
	private ConditionChangeListener conditionChangeListener;
	
	private SkillTemplate skillTemplate;
	
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
	public Skill(SkillTemplate skillTemplate, Player effector, World world)
	{
		this(skillTemplate, effector, world, 
			effector.getSkillList().getSkillLevel(skillTemplate.getSkillId()));
	}
	
	/**
	 * 
	 * @param skillTemplate
	 * @param effector
	 * @param world
	 */
	public Skill(SkillTemplate skillTemplate, Player effector, World world, int skillLvl)
	{
		this.effectedList = new ArrayList<Creature>();
		this.conditionChangeListener = new ConditionChangeListener();
		this.firstTarget = effector.getTarget() instanceof Creature ? (Creature) effector.getTarget() : null;
		this.skillLevel = skillLvl;
		this.skillStackLvl = skillTemplate.getLvl();
		this.skillTemplate = skillTemplate;
		this.effector = effector;
		this.world = world;
	}

	/**
	 *  Skill entry point
	 */
	public void useSkill(SkillType skillType)
	{
		//TODO OOP
		ActivationAttribute activation = skillTemplate.getActivationAttribute();
		if(activation != ActivationAttribute.ACTIVE && activation != ActivationAttribute.PASSIVE)
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
			if (!RestrictionsManager.canUseSkill(getEffector(), effected))
				effectedIter.remove();
		}
		
		if(effectedList.size() == 0)
			return;
		
		//temporary hook till i find permanent solution
		if(skillType == SkillType.CAST)
		{
			startCast();
		}

		effector.getController().attach(conditionChangeListener);
		
		if(skillTemplate.getDuration() > 0)
		{
			schedule(skillType, skillTemplate.getDuration());
		}
		else
		{
			endCast(skillType);
		}
	}
	
	/**
	 *  Start casting of skill
	 */
	private void startCast()
	{
		int targetObjId = firstTarget !=  null ? firstTarget.getObjectId() : 0;
		final int unk = 0;
		//start casting
		effector.setCasting(this);
		PacketSendUtility.broadcastPacket(effector, 
			new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel,
				unk, targetObjId, skillTemplate.getDuration()), true);
	}
	
	/**
	 *  Apply effects and perform actions specified in skill template
	 */
	private void endCast(SkillType skillType)
	{
		//stop casting must be before preUsageCheck()
		effector.setCasting(null);
		
		if(!preUsageCheck())
			return;
		
		int duration = 0;
		
		/**
		 * Create effects and precalculate result
		 */
		List<Effect> effects = new ArrayList<Effect>();		 
		if(skillTemplate.getEffects() != null)
		{
			List<EffectTemplate> effectTemplates = skillTemplate.getEffects().getEffects();
			for(EffectTemplate template : effectTemplates)
			{
				duration = duration > template.getDuration() ? duration : template.getDuration();
			}

			for(Creature effected : effectedList)
			{
				Effect effect = new Effect(effector, effected, skillTemplate,	skillLevel, duration);
				for(EffectTemplate template : effectTemplates)
				{
					template.calculate(effect);
				}
				effects.add(effect);
			}
		}
		
		/**
		 * If castspell - send SM_CASTSPELL_END packet
		 */
		if(skillType == SkillType.CAST)
		{
			PacketSendUtility.broadcastPacket(effector,
				new SM_CASTSPELL_END(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel,
					firstTarget.getObjectId(), effects, skillTemplate.getCooldown()), true);
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
			for(EffectTemplate template : skillTemplate.getEffects().getEffects())
			{
				template.applyEffect(effect);
			}
		}
	}
	
	/**
	 *  Schedule actions/effects of skill (channeled skills)
	 */
	private void schedule(final SkillType skillType, int delay)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable() 
		{
			public void run() 
			{
				endCast(skillType);
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
	 * @return the world
	 */
	public World getWorld()
	{
		return world;
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
	public ConditionChangeListener getConditionChangeListener()
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
	 * @return
	 */
	public boolean isPassive()
	{
		return skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}
}
