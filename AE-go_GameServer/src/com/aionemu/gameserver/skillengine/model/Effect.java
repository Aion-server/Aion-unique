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

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.movement.AttackCalcObserver;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_ACTIVATION;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.Effects;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 *
 */
public class Effect
{
	private SkillTemplate skillTemplate;
	private int skillLevel;
	private int duration;
	private int endTime;

	private Creature effected;
	private Creature effector;
	private Future<?> checkTask = null;
	private Future<?> task = null;
	private Future<?> periodicTask = null;
	
	/**
	 * Used for damage/heal values
	 */
	private int reserved1;
	/**
	 * Used for shield total hit damage;
	 */
	private int reserved2;
	/**
	 * Used for shield hit damage
	 */
	private int reserved3;
	
	/**
	 * Spell Status
	 * 
	 * 1 : stumble
	 * 2 : knockback
	 * 4 : open aerial
	 * 8 : close aerial
	 * 16 : spin
	 * 32 : block
	 * 64 : parry
	 * 128 : dodge
	 * 256 : resist
	 */
	private SpellStatus spellStatus = SpellStatus.NONE;
	
	private AttackStatus attackStatus = AttackStatus.NORMALHIT;
	private int shieldDefense;
	
	private boolean addedToController;
	private int successEffect;
	private AttackCalcObserver attackStatusObserver;
	private AttackCalcObserver attackShieldObserver;
	
	private boolean launchSubEffect = true;
	
	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration)
	{
		this.effector = effector;
		this.effected = effected;
		this.skillTemplate = skillTemplate;
		this.skillLevel = skillLevel;
		this.duration = duration;
	}
	
	/**
	 * @return the effectorId
	 */
	public int getEffectorId()
	{
		return effector.getObjectId();
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId()
	{
		return skillTemplate.getSkillId();
	}

	/**
	 * @return the stack
	 */
	public String getStack()
	{
		return skillTemplate.getStack();
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
		return skillTemplate.getLvl();
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
		return skillTemplate.isPassive();
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
	 * @return the reserved1
	 */
	public int getReserved1()
	{
		return reserved1;
	}

	/**
	 * @param reserved1 the reserved1 to set
	 */
	public void setReserved1(int reserved1)
	{
		this.reserved1 = reserved1;
	}

	/**
	 * @return the reserved2
	 */
	public int getReserved2()
	{
		return reserved2;
	}

	/**
	 * @param reserved2 the reserved2 to set
	 */
	public void setReserved2(int reserved2)
	{
		this.reserved2 = reserved2;
	}

	/**
	 * @return the reserved3
	 */
	public int getReserved3()
	{
		return reserved3;
	}

	/**
	 * @param reserved3 the reserved3 to set
	 */
	public void setReserved3(int reserved3)
	{
		this.reserved3 = reserved3;
	}

	/**
	 * @return the attackStatus
	 */
	public AttackStatus getAttackStatus()
	{
		return attackStatus;
	}

	/**
	 * @param attackStatus the attackStatus to set
	 */
	public void setAttackStatus(AttackStatus attackStatus)
	{
		this.attackStatus = attackStatus;
	}

	/**
	 * @return the successEffect
	 */
	public int getSuccessEffect()
	{
		return successEffect;
	}
	
	public void increaseSuccessEffect()
	{
		successEffect++;
	}
	
	public List<EffectTemplate> getEffectTemplates()
	{
		return skillTemplate.getEffects().getEffects();
	}
	
	public boolean isFood()
	{
		Effects effects = skillTemplate.getEffects();
		return effects != null && effects.isFood();
	}
	

	public boolean isToggle()
	{
		return skillTemplate.getActivationAttribute() == ActivationAttribute.TOGGLE;
	}
	
	public int getTargetSlot()
	{
		return skillTemplate.getTargetSlot().ordinal();
	}

	/**
	 * @return the attackCalcObserver
	 */
	public AttackCalcObserver getAttackStatusObserver()
	{
		return attackStatusObserver;
	}

	/**
	 * @param attackStatusObserver the attackCalcObserver to set
	 */
	public void setAttackStatusObserver(AttackCalcObserver attackStatusObserver)
	{
		this.attackStatusObserver = attackStatusObserver;
	}

	/**
	 * @return the attackShieldObserver
	 */
	public AttackCalcObserver getAttackShieldObserver()
	{
		return attackShieldObserver;
	}

	/**
	 * @param attackShieldObserver the attackShieldObserver to set
	 */
	public void setAttackShieldObserver(AttackCalcObserver attackShieldObserver)
	{
		this.attackShieldObserver = attackShieldObserver;
	}

	/**
	 * @return the launchSubEffect
	 */
	public boolean isLaunchSubEffect()
	{
		return launchSubEffect;
	}

	/**
	 * @param launchSubEffect the launchSubEffect to set
	 */
	public void setLaunchSubEffect(boolean launchSubEffect)
	{
		this.launchSubEffect = launchSubEffect;
	}

	/**
	 * @return the shieldDefense
	 */
	public int getShieldDefense()
	{
		return shieldDefense;
	}

	/**
	 * @param shieldDefense the shieldDefense to set
	 */
	public void setShieldDefense(int shieldDefense)
	{
		this.shieldDefense = shieldDefense;
	}

	/**
	 * @return the spellStatus
	 */
	public SpellStatus getSpellStatus()
	{
		return spellStatus;
	}

	/**
	 * @param spellStatus the spellStatus to set
	 */
	public void setSpellStatus(SpellStatus spellStatus)
	{
		this.spellStatus = spellStatus;
	}

	/**
	 * 
	 * @param effectId
	 * @return true or false
	 */
	public boolean containsEffectId(int effectId)
	{
		for(EffectTemplate template : getEffectTemplates())
		{
			if(template.getEffectid() == effectId)
				return true;
		}
		return false;
	}
	
	/**
	 * Correct lifecycle of Effect
	 *  - INITIALIZE
	 *  - APPLY
	 *  - START
	 *  - END
	 */
	
	
	/**
	 * Do initialization with proper calculations
	 */
	public void initialize()
	{
		if(skillTemplate.getEffects() == null)
			return;
		
		int effectCounter = 0;
		for(EffectTemplate template : getEffectTemplates())
		{
			if(effectCounter != successEffect)
				break;
			
			template.calculate(this);
			effectCounter++;
		}
	}
	
	/**
	 * Apply all effect templates
	 */
	public void applyEffect()
	{
		if(skillTemplate.getEffects() == null)
			return;
		
		int effectCounter = 0;
		for(EffectTemplate template : getEffectTemplates())
		{
			if(effectCounter == successEffect)
				break;
			
			template.applyEffect(this);
			template.startSubEffect(this);
			effectCounter++;
		}
	}
	
	/**
	 * Start effect which includes:
	 * - start effect defined in template
	 * - start subeffect if possible
	 * - activate toogle skill if needed
	 * - schedule end of effect
	 */
	public void startEffect()
	{	
		for(EffectTemplate template : getEffectTemplates())
		{
			template.startEffect(this);
		}
		
		if(isToggle() && effector instanceof Player)
		{
			activateToggleSkill();			
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
	
	/**
	 * Will activate toggle skill and start checking task
	 */
	private void activateToggleSkill()
	{
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), true));
	}
	
	/**
	 * Will deactivate toggle skill and stop checking task
	 */
	private void deactivateToggleSkill()
	{
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), false));
	}
	
	/**
	 * End effect and all effect actions
	 */
	public void endEffect()
	{
		for(EffectTemplate template : getEffectTemplates())
		{
			template.endEffect(this);
		}
		
		if(isToggle() && effector instanceof Player)
		{
			deactivateToggleSkill();
		}
		
		effected.getEffectController().clearEffect(this);
		stopTasks();
	}

	public void stopTasks()
	{
		if(task != null)
		{
			task.cancel(false);
			task = null;
		}
		
		if(checkTask != null)
		{
			checkTask.cancel(false);
			checkTask = null;
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

	/**
	 * Try to add this effect to effected controller
	 */
	public void addToEffectedController()
	{
		if(!addedToController)
		{
			effected.getEffectController().addEffect(this);
			addedToController = true;
		}
	}
}
