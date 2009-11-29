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

import java.util.Map.Entry;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 * 
 */
public class CreatureGameStats<T extends Creature>
{
	protected static final Logger						log					= Logger.getLogger(CreatureGameStats.class);

	private static final int							ATTACK_MAX_COUNTER	= Integer.MAX_VALUE;

	private FastMap<StatEnum, Integer>					defaultStats;
	private FastMap<StatEnum, Integer>					baseStats;
	private FastMap<StatEnum, Integer>					bonusStats;
	private FastMap<StatEnum, StatEffect>				effects;

	private int											attackCounter		= 0;
	private boolean										initialized			= false;
	private T											owner				= null;

	protected CreatureGameStats(T owner)
	{
		this.owner = owner;
		this.defaultStats = new FastMap<StatEnum, Integer>();
		this.baseStats = new FastMap<StatEnum, Integer>();
		this.bonusStats = new FastMap<StatEnum, Integer>();
		this.effects = new FastMap<StatEnum, StatEffect>();
	}

	protected void initStats(int maxHp, int maxMp, int power, int health, int agility, int accuracy, int knowledge,
		int will, int mainHandAttack, int mainHandCritRate, int attackSpeed, int attackRange)
	{
		baseStats.put(StatEnum.MAXHP, maxHp);
		baseStats.put(StatEnum.MAXMP, maxMp);
		baseStats.put(StatEnum.POWER, power);
		baseStats.put(StatEnum.ACCURACY, accuracy);
		baseStats.put(StatEnum.HEALTH, health);
		baseStats.put(StatEnum.AGILITY, agility);
		baseStats.put(StatEnum.KNOWLEDGE, knowledge);
		baseStats.put(StatEnum.WILL, will);
		baseStats.put(StatEnum.MAIN_HAND_POWER, mainHandAttack);
		baseStats.put(StatEnum.MAIN_HAND_CRITICAL, mainHandCritRate);
		baseStats.put(StatEnum.OFF_HAND_POWER, 0);
		baseStats.put(StatEnum.OFF_HAND_CRITICAL, 0);
		baseStats.put(StatEnum.ATTACK_SPEED, attackSpeed);
		baseStats.put(StatEnum.ATTACK_RANGE, attackRange);
		baseStats.put(StatEnum.PHYSICAL_DEFENSE, Math.round(health / 3.1f));
		baseStats.put(StatEnum.PARRY, Math.round(agility / 3.1f));
		baseStats.put(StatEnum.EVASION, Math.round(agility / 3.1f));
		baseStats.put(StatEnum.BLOCK, Math.round(agility / 3.1f));
		baseStats.put(StatEnum.MAIN_HAND_ACCURACY, Math.round(accuracy * 1.25f));
		baseStats.put(StatEnum.OFF_HAND_ACCURACY, 0);
		baseStats.put(StatEnum.MAGICAL_RESIST, Math.round(knowledge / 3.1f));
		baseStats.put(StatEnum.MAGICAL_ACCURACY, Math.round(will * 0.75f));
		this.initialized = true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("owner:" + owner.getObjectId());
		for(Entry<StatEnum, Integer> stat : baseStats.entrySet())
		{
			sb.append(';');
			sb.append(stat.getKey().getName());
			sb.append(':');
			sb.append("(" + getBaseStat(stat.getKey()) + "+" + getStatBonus(stat.getKey()) + ")");
		}
		sb.append('}');
		return sb.toString();
	}

	public void setStat(StatEnum stat, int value)
	{
		setStat(stat, value, false);
	}

	public void setStat(StatEnum stat, int value, boolean bonus)
	{
		if(bonus)
		{
			synchronized(bonusStats)
			{
				bonusStats.put(stat, value);
			}
		}
		else
		{
			synchronized(baseStats)
			{
				baseStats.put(stat, value);
			}
		}
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
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

	public int getBaseStat(StatEnum stat)
	{
		int value = 0;
		synchronized(baseStats)
		{
			if(baseStats.containsKey(stat))
			{
				value = baseStats.get(stat);
			}
		}
		return value;
	}

	public int getStatBonus(StatEnum stat)
	{
		int value = 0;
		synchronized(bonusStats)
		{
			if(bonusStats.containsKey(stat))
			{
				value = bonusStats.get(stat);
			}
		}
		return value;
	}

	public int getCurrentStat(StatEnum stat)
	{
		int baseStat = getBaseStat(stat);
		boolean contains = false;
		synchronized(bonusStats)
		{
			contains = bonusStats.containsKey(stat);
		}
		if(contains)
		{
			return (baseStat + getStatBonus(stat));
		}
		return baseStat;
	}

	public void processEffects(StatEnum stat)
	{
		StatEffect effect = effects.get(stat);
		
		setStat(stat, defaultStats.get(stat));
		setStat(stat, 0, true);
		
		for (StatModifier modifier : effect.getModifiers()) {
			int newValue = modifier.apply(getCurrentStat(stat));
			StringBuilder sb = new StringBuilder();
			sb.append("Applying modifier "+modifier+" on "+stat+": old:");
			sb.append(getBaseStat(stat)+"+"+getStatBonus(stat)+",new:");
			sb.append(getBaseStat(stat)+(modifier.isBonus()?0:newValue));
			sb.append("+");
			sb.append((modifier.isBonus()?getStatBonus(stat)+newValue:0));
			log.debug(sb.toString());
			if (modifier.isBonus()) {
				bonusStats.put(stat, newValue+getStatBonus(stat));
			} else {
				baseStats.put(stat, newValue);
			}
		}
	}
	
	public void addModifierOnStat(StatEnum stat, StatModifier modifier) {
		synchronized(effects) {
			if (effects.containsKey(stat)) {
				effects.get(stat).add(modifier);
			} else {
				StatEffect effect = new StatEffect ();
				defaultStats.put(stat, getCurrentStat(stat));
				effect.add(modifier);
				effects.put(stat,effect);
			}
		}
		
		processEffects(stat);
	}
	
	public void addEffectOnStat(StatEnum stat, StatEffect effect)
	{	
		synchronized(effects) {
			if (effects.containsKey(stat)) {
				effects.get(stat).addAll(effect.getModifiers());
			} else {
				defaultStats.put(stat, getCurrentStat(stat));
				effects.put(stat,effect);
			}
		}
		
		processEffects(stat);
	}
	
	public void endEffect(int ownerId) {
		synchronized (effects) {
			for(Entry<StatEnum,StatEffect> entry : effects.entrySet()) {
				entry.getValue().endEffects(ownerId);
				processEffects(entry.getKey());
			}
		}
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
