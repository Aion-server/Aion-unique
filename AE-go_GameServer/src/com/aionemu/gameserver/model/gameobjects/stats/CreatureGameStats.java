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
	protected static final Logger						log					= Logger.getLogger(CreatureGameStats.class);

	private static final int							ATTACK_MAX_COUNTER	= Integer.MAX_VALUE;

	private FastMap<StatEnum,Stat>									stats;
	private FastMap<StatEffectId, TreeSet<StatModifier>>			statsModifiers;

	private int											attackCounter		= 0;
	private int											moveCounter			= 0;
	private T											owner				= null;

	protected CreatureGameStats(T owner)
	{
		this.owner = owner;
		this.stats = new FastMap<StatEnum,Stat> ();
		this.statsModifiers = new FastMap<StatEffectId, TreeSet<StatModifier>>();
	}

	protected void initStats(int maxHp, int maxMp, int power, int health, int agility, int accuracy, int knowledge,
		int will, int mainHandAttack, int mainHandCritRate, int attackSpeed, int attackRange, int runSpeed, int flySpeed)
	{
		stats.clear();
		initStat(StatEnum.MAXHP, maxHp);
		initStat(StatEnum.MAXMP, maxMp);
		initStat(StatEnum.POWER, power);
		initStat(StatEnum.ACCURACY, accuracy);
		initStat(StatEnum.HEALTH, health);
		initStat(StatEnum.AGILITY, agility);
		initStat(StatEnum.KNOWLEDGE, knowledge);
		initStat(StatEnum.WILL, will);
		initStat(StatEnum.MAIN_HAND_POWER, Math.round(18 * (power / 100)));
		initStat(StatEnum.MAIN_HAND_CRITICAL, mainHandCritRate);
		initStat(StatEnum.OFF_HAND_POWER, 0);
		initStat(StatEnum.OFF_HAND_CRITICAL, 0);
		initStat(StatEnum.ATTACK_SPEED, attackSpeed);
		initStat(StatEnum.ATTACK_RANGE, attackRange);
		initStat(StatEnum.PHYSICAL_DEFENSE, 0);
		initStat(StatEnum.PARRY, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.EVASION, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.BLOCK, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.DAMAGE_REDUCE, 0);
		initStat(StatEnum.MAIN_HAND_ACCURACY, Math.round((accuracy * 2 - 10) + 8 * (int)owner.getLevel()));
		initStat(StatEnum.OFF_HAND_ACCURACY, Math.round((accuracy * 2 - 10) + 8 * (int)owner.getLevel()));
		initStat(StatEnum.MAGICAL_RESIST, 0);
		initStat(StatEnum.WIND_RESISTANCE, 0);
		initStat(StatEnum.FIRE_RESISTANCE, 0);
		initStat(StatEnum.WATER_RESISTANCE, 0);
		initStat(StatEnum.EARTH_RESISTANCE, 0);
		initStat(StatEnum.MAGICAL_ACCURACY, Math.round(14.26f * (int)owner.getLevel()));
		initStat(StatEnum.BOOST_MAGICAL_SKILL, 0);
		initStat(StatEnum.SPEED, runSpeed);
		initStat(StatEnum.FLY_SPEED, flySpeed);
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

	public void initStat(StatEnum stat, int value)
	{
		if (!stats.containsKey(stat))
		{
			stats.put(stat,new Stat(stat,value));
		}
		else
		{
			stats.get(stat).reset();
			stats.get(stat).set(value,false);
		}
	}

	public void setStat(StatEnum stat, int value, boolean bonus)
	{
		if (!stats.containsKey(stat))
		{
			stats.put(stat,new Stat(stat,0));
		}
		stats.get(stat).set(value,bonus);
	}

	public void setStat(StatEnum stat, int value)
	{
		setStat(stat,value,false);
	}

	/**
	 * @return the atcount
	 */
	public int getAttackCounter()
	{
		return attackCounter;
	}

	/**
	 * @return the moveCounter
	 */
	public int getMoveCounter()
	{
		return moveCounter;
	}

	/**
	 * @param atcount
	 *            the atcount to set
	 */
	public void setAttackCounter(int attackCounter)
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

	public void increaseMoveCounter()
	{
		this.moveCounter++;
	}

	public int getBaseStat(StatEnum stat)
	{
		int value = 0;
		synchronized(stats)
		{
			if(stats.containsKey(stat))
			{
				value = stats.get(stat).getBase();
			}
		}
		return value;
	}

	public int getStatBonus(StatEnum stat)
	{
		int value = 0;
		synchronized(stats)
		{
			if (stats.containsKey(stat))
			{
				value = stats.get(stat).getBonus();
			}
		}
		return value;
	}

	public int getCurrentStat(StatEnum stat)
	{
		int value = 0;

		synchronized(stats)
		{
			if (stats.containsKey(stat))
			{
				value = stats.get(stat).getCurrent();
			}
		}

		return value;
	}

	protected void resetStats()
	{
		synchronized(stats)
		{
			for (Stat stat : stats.values())
			{
				stat.reset();
			}
		}
	}

	protected void applyModifiers(StatEnum stat, StatModifiers modifiers)
	{
		if(modifiers == null)
		{
			return;
		}

		if (!stats.containsKey(stat))
		{
			initStat(stat, 0);
		}
		Stat oStat = stats.get(stat);

		for(StatModifierPriority priority : StatModifierPriority.values())
		{
			for(StatModifier modifier : modifiers.getModifiers(priority))
			{
				int newValue = 0;
				if(modifier.isBonus())
					newValue = modifier.apply(oStat.getCurrent());
				else
					newValue = modifier.apply(oStat.getBase());
				
				if((this instanceof PlayerGameStats) && (log.isDebugEnabled()))
				{
					log.debug("Applying modifier " + modifier + " on stat " + oStat	+ "), result:" + newValue);
				}
				oStat.increase(newValue, modifier.isBonus());
			}
		}
	}

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

		if ((this instanceof PlayerGameStats)&&(log.isDebugEnabled()))
		{
			log.debug("Adding effect "+id);
			for (StatModifier modifier : modifiers)
			{
				log.debug("Adding modifier "+modifier);
			}
		}

		statsModifiers.put(id, modifiers);

		recomputeStats();
	}
	
	@Enhancable(callback = StatChangeListener.class)
	protected void recomputeStats()
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
	}

	public void endEffect(StatEffectId id)
	{
		statsModifiers.remove(id);
		recomputeStats();
	}

	/**
	 * @return the owner
	 */
	public Creature getOwner()
	{
		return owner;
	}

	/**
	 * @param Creature
	 *            the owner
	 */
	public void setOwner(T owner)
	{
		this.owner = owner;
	}

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
}
