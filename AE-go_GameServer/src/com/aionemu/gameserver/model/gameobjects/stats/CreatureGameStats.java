/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.stats;

import java.util.List;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.Enhancable;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.id.ItemStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.id.StatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.StatChangeListener;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ItemSlot;

/**
 * @author xavier
 * 
 */
public class CreatureGameStats<T extends Creature>
{
	protected static final Logger							log					= Logger
																					.getLogger(CreatureGameStats.class);

	private static final int								ATTACK_MAX_COUNTER	= Integer.MAX_VALUE;

	protected FastMap<StatEnum, Stat>						stats;
	protected FastMap<StatEffectId, TreeSet<StatModifier>>	statsModifiers;

	private int												attackCounter		= 0;
	protected T												owner				= null;
	protected final ReentrantReadWriteLock					lock				= new ReentrantReadWriteLock();

	/**
	 * 
	 * @param owner
	 */
	protected CreatureGameStats(T owner)
	{
		this.owner = owner;
		this.stats = new FastMap<StatEnum,Stat> ();
		this.statsModifiers = new FastMap<StatEffectId, TreeSet<StatModifier>>();
	}
	
	/**
	 * @return the atcount
	 */
	public int getAttackCounter()
	{
		return attackCounter;
	}

	/**
	 * @param atcount
	 *            the atcount to set
	 */
	protected void setAttackCounter(int attackCounter)
	{
		if(attackCounter <= 0)
		{
			this.attackCounter = 1;
		}
		else
		{
			this.attackCounter = attackCounter;
		}
	}
	
	public void increaseAttackCounter()
	{
		if(attackCounter == ATTACK_MAX_COUNTER)
		{
			this.attackCounter = 1;
		}
		else
		{
			this.attackCounter++;
		}
	}
	
	/**
	 * 
	 * @param stat
	 * @param value
	 */
	public void setStat(StatEnum stat, int value)
	{
		lock.writeLock().lock();
		try
		{
			setStat(stat,value,false);
		}
		finally
		{
			lock.writeLock().unlock();
		}		
	}

	/**
	 * @param stat
	 * @return
	 */
	public int getBaseStat(StatEnum stat)
	{
		int value = 0;
		lock.readLock().lock();
		try
		{
			if(stats.containsKey(stat))
			{
				value = stats.get(stat).getBase();
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		return value;
	}
	
	/**
	 * 
	 * @param stat
	 * @return
	 */
	public int getStatBonus(StatEnum stat)
	{
		int value = 0;
		lock.readLock().lock();
		try
		{
			if (stats.containsKey(stat))
			{
				value = stats.get(stat).getBonus();
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		return value;
	}
	
	/**
	 * 
	 * @param stat
	 * @return
	 */
	public int getCurrentStat(StatEnum stat)
	{
		int value = 0;

		lock.readLock().lock();
		try
		{
			if (stats.containsKey(stat))
			{
				value = stats.get(stat).getCurrent();
			}
		}
		finally
		{
			lock.readLock().unlock();
		}

		return value;
	}

	/**
	 * 
	 * @param id
	 * @param modifiers
	 */
	public void addModifiers(StatEffectId id, TreeSet<StatModifier> modifiers)
	{
		if (modifiers==null)
		{
			return;
		}

		if (statsModifiers.containsKey(id))
		{
			throw new IllegalArgumentException("Effect "+id+" already active");
		}

		statsModifiers.put(id, modifiers);
		recomputeStats();
	}
	
	/**
	 * Recomputation of all stats
	 * Additional logic is in StatChangeListener callbacks
	 */
	@Enhancable(callback = StatChangeListener.class)
	protected void recomputeStats()
	{
		//need check this lock, may be remove
		lock.writeLock().lock();
		try
		{
			resetStats();
			FastMap<StatEnum, StatModifiers> orderedModifiers = new FastMap<StatEnum, StatModifiers>();

			for(Entry<StatEffectId, TreeSet<StatModifier>> modifiers : statsModifiers.entrySet())
			{
				StatEffectId eid = modifiers.getKey();
				int slots;

				for(StatModifier modifier : modifiers.getValue())
				{
					slots = ItemSlot.NONE.getSlotIdMask();
					if(eid instanceof ItemStatEffectId)
					{
						slots = ((ItemStatEffectId) eid).getSlot();
					}

					if(modifier.getStat().isMainOrSubHandStat() && owner instanceof Player)
					{
						if(slots != ItemSlot.MAIN_HAND.getSlotIdMask() && slots != ItemSlot.SUB_HAND.getSlotIdMask())
						{
							if(((Player) owner).getEquipment().getOffHandWeaponType() != null)
								slots = ItemSlot.MAIN_OR_SUB.getSlotIdMask();
							else
							{
								slots = ItemSlot.MAIN_HAND.getSlotIdMask();
								setStat(StatEnum.OFF_HAND_ACCURACY, 0, false);
							}
						}
						else if(slots == ItemSlot.MAIN_HAND.getSlotIdMask())
							setStat(StatEnum.MAIN_HAND_POWER, 0);
					}

					List<ItemSlot> oSlots = ItemSlot.getSlotsFor(slots);
					for(ItemSlot slot : oSlots)
					{
						StatEnum statToModify = modifier.getStat().getMainOrSubHandStat(slot);
						if(!orderedModifiers.containsKey(statToModify))
						{
							orderedModifiers.put(statToModify, new StatModifiers());
						}
						orderedModifiers.get(statToModify).add(modifier);
					}
				}
			}

			for(Entry<StatEnum, StatModifiers> entry : orderedModifiers.entrySet())
			{
				applyModifiers(entry.getKey(), entry.getValue());
			}
			
			setStat(StatEnum.ATTACK_SPEED, Math.round(getBaseStat(StatEnum.MAIN_HAND_ATTACK_SPEED)
													+ getBaseStat(StatEnum.OFF_HAND_ATTACK_SPEED) * 0.25f), false);
			
			setStat(StatEnum.ATTACK_SPEED, getStatBonus(StatEnum.MAIN_HAND_ATTACK_SPEED) 
										 + getStatBonus(StatEnum.OFF_HAND_ATTACK_SPEED), true);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * 
	 * @param id
	 */
	public void endEffect(StatEffectId id)
	{
		statsModifiers.remove(id);
		recomputeStats();
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	public int getMagicalDefenseFor(SkillElement element)
	{
		switch(element)
		{
			case EARTH:
				return getCurrentStat(StatEnum.EARTH_RESISTANCE);
			case FIRE:
				return getCurrentStat(StatEnum.FIRE_RESISTANCE);
			case WATER:
				return getCurrentStat(StatEnum.WATER_RESISTANCE);
			case WIND:
				return getCurrentStat(StatEnum.WIND_RESISTANCE);
			default:
				return 0;
		}
	}
	
	/**
	 *  Reset all stats
	 *  No need to syncronized becaused guarded by recompute() write lock
	 */
	protected void resetStats()
	{
		for(Stat stat : stats.values())
		{
			stat.reset();
		}
	}
	
	/**
	 * 
	 * @param stat
	 * @param modifiers
	 */
	protected void applyModifiers(StatEnum stat, StatModifiers modifiers)
	{
		if(modifiers == null)
			return;

		if(!stats.containsKey(stat))
		{
			initStat(stat, 0);
		}

		Stat oStat = stats.get(stat);
		for(StatModifierPriority priority : StatModifierPriority.values())
		{
			for(StatModifier modifier : modifiers.getModifiers(priority))
			{
				int newValue = modifier.apply(oStat.getBase(), oStat.getCurrent());
				oStat.increase(newValue, modifier.isBonus());
			}
		}
	}

	
	/**
	 * 
	 * @param stat
	 * @param value
	 */
	protected void initStat(StatEnum stat, int value)
	{
		if(!stats.containsKey(stat))
		{
			stats.put(stat, new Stat(stat, value));
		}
		else
		{
			stats.get(stat).reset();
			stats.get(stat).set(value, false);
		}
	}
	
	/**
	 * 
	 * @param stat
	 * @param value
	 * @param bonus
	 */
	protected void setStat(StatEnum stat, int value, boolean bonus)
	{
		if(!stats.containsKey(stat))
		{
			stats.put(stat, new Stat(stat, 0));
		}
		stats.get(stat).set(value, bonus);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("owner:" + owner.getObjectId());
		for(Stat stat : stats.values())
		{
			sb.append(stat);
		}
		sb.append('}');
		return sb.toString();
	}
}
