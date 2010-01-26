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

import java.util.concurrent.Future;

import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class Effect
{
	private int effectorId;
	private int skillId;
	private String stack;
	private int skillLevel;
	private int skillStackLvl;
	private int duration;
	private int endTime;
	private boolean isPassive;

	private EffectController controller;
	private Effects effects;
	private Creature effected;
	private Creature effector;
	private Future<?> task = null;
	private Future<?> periodicTask = null;

	public Effect(Creature effector, SkillTemplate skillTemplate, int skillLevel, int duration)
	{
		this.effector = effector;
		this.effectorId = effector.getObjectId();
		this.skillId = skillTemplate.getSkillId();
		this.stack = skillTemplate.getStack();
		this.skillLevel = skillLevel;
		this.skillStackLvl = skillTemplate.getLvl();
		this.duration = duration;
		this.effects = skillTemplate.getEffects();
		this.isPassive = skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}
	
	/**
	 * @return the effectorId
	 */
	public int getEffectorId()
	{
		return effectorId;
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skillId;
	}

	/**
	 * @return the stack
	 */
	public String getStack()
	{
		return stack;
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
	 * @return the duration
	 */
	public int getDuration()
	{
		return duration;
	}

	/**
	 * @return the effected
	 */
	public Creature getEffected()
	{
		return effected;
	}

	/**
	 * @return the effector
	 */
	public Creature getEffector()
	{
		return effector;
	}

	/**
	 * @return the isPassive
	 */
	public boolean isPassive()
	{
		return isPassive;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Future<?> task)
	{
		this.task = task;
	}

	/**
	 * @return the periodicTask
	 */
	public Future<?> getPeriodicTask()
	{
		return periodicTask;
	}

	/**
	 * @param periodicTask the periodicTask to set
	 */
	public void setPeriodicTask(Future<?> periodicTask)
	{
		this.periodicTask = periodicTask;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(EffectController controller)
	{
		this.controller = controller;
		this.effected = controller.getOwner();
	}

	public void startEffect()
	{	
		for(EffectTemplate template : effects.getEffects())
		{
			template.startEffect(this);
		}
		
		if(duration == 0)
			return;
		
		endTime = (int) System.currentTimeMillis() + duration;

		task = ThreadPoolManager.getInstance().scheduleEffect((new Runnable()
		{
			@Override
			public void run()
			{				
				endEffect();
			}

		}), duration);
	}


	public void endEffect()
	{
		for(EffectTemplate template : effects.getEffects())
		{
			template.endEffect(this);
		}
		controller.removeEffect(this);
		stopTasks();
	}
	
	public void stopTasks()
	{
		if(task != null)
		{
			task.cancel(false);
			task = null;
		}
		
		if(periodicTask != null)
		{
			periodicTask.cancel(false);
			periodicTask = null;
		}
	}

	public int getElapsedTime()
	{
		int elapsedTime = endTime - (int)System.currentTimeMillis();
		return elapsedTime > 0 ? elapsedTime : 0;
	}
}
