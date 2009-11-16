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

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author xavier
 * 
 */
public class CreatureGameStats<T extends Creature>
{
	protected static final Logger						log					= Logger.getLogger(CreatureGameStats.class);

	private static final int							ATTACK_MAX_COUNTER	= Integer.MAX_VALUE;

	private FastMap<StatEnum, Integer>					baseStats;
	private FastMap<StatEnum, Integer>					bonusStats;

	private FastMap<Integer, FastList<StatModifier>>	effects;
	private static Integer								nextEffectId		= 1;

	private int											attackCounter		= 0;
	private boolean										initialized			= false;
	private T											owner				= null;

	protected CreatureGameStats(T owner)
	{
		this.owner = owner;
		this.baseStats = new FastMap<StatEnum, Integer>();
		this.bonusStats = new FastMap<StatEnum, Integer>();
		this.effects = new FastMap<Integer, FastList<StatModifier>>();
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

	private int getNextEffectId()
	{
		int effectId = -1;
		while(effectId < 0)
		{
			synchronized(nextEffectId)
			{
				nextEffectId = (nextEffectId % Integer.MAX_VALUE) + 1;
				effectId = nextEffectId;
			}
			synchronized(effects)
			{
				if(effects.size() == Integer.MAX_VALUE)
				{
					effectId = 0;
				}
				if(effects.containsKey(effectId))
				{
					effectId = -1;
				}
			}
		}
		return effectId;
	}

	public int getEffectId() throws IllegalAccessException
	{
		int effectId = getNextEffectId();

		if(effectId == 0)
		{
			throw new IllegalAccessException("Cannot get a new Effect id");
		}

		FastList<StatModifier> modifiers = new FastList<StatModifier>();
		synchronized(effects)
		{
			effects.put(effectId, modifiers);
		}
		return effectId;
	}

	public void addEffectOnStat(int effectId, StatEnum stat, String value)
	{
		addEffectOnStat(effectId, stat, value, false);
	}

// TODO update applyModifiersOnStat to get awaited behaviour
//	private int applyOldModifiersOnNewStat(int effectId, StatEnum stat, int base)
//	{
//		int newValue = base;
//		synchronized(effects)
//		{
//			for(Entry<Integer, FastList<StatModifier>> effect : effects.entrySet())
//			{
//				if(effect.getKey() != effectId)
//				{
//					FastList<StatModifier> modifiers = effect.getValue();
//					synchronized(modifiers)
//					{
//						for(StatModifier modifier : modifiers)
//						{
//							if(modifier.getModifiedStat() == stat)
//							{
//								modifier.setOldValue(newValue);
//								if(stat.isReplace()&&!modifier.isBonus())
//								{
//									newValue = modifier.getModifier();
//								}
//								else
//								{
//									newValue = newValue + modifier.getModifier();
//								}
//							}
//						}
//					}
//
//				}
//			}
//
//		}
//		return newValue;
//	}

//	private int applyOldModifiersOnOldStat(StatEnum stat, int base)
//	{
//		int newValue = base;
//		synchronized(effects)
//		{
//			for(Entry<Integer, FastList<StatModifier>> effect : effects.entrySet())
//			{
//				FastList<StatModifier> modifiers = effect.getValue();
//				synchronized(modifiers)
//				{
//					for(StatModifier modifier : modifiers)
//					{
//						if((modifier.getModifiedStat() == stat) && (!modifier.isEnded()))
//						{
//							modifier.setOldValue(newValue);
//							if(stat.isReplace()&&!modifier.isBonus())
//							{
//								newValue = modifier.getModifier();
//							}
//							else
//							{
//								newValue = newValue + modifier.getModifier();
//							}
//						}
//					}
//				}
//
//			}
//		}
//
//		return newValue;
//	}

	public void addEffectOnStat(int effectId, StatEnum stat, String value, boolean bonus)
	{
		log.debug("Adding modifier for effect#" + effectId + ": stat:" + stat.getName() + ",value:" + value + ",bonus:"
			+ bonus);
		FastList<StatModifier> modifiers = null;
		synchronized(effects)
		{
			modifiers = effects.get(effectId);
		}
		if(modifiers == null)
		{
			throw new IllegalArgumentException("Invalid effect id " + effectId);
		}

		StatModifier modifier = new StatModifier(stat, value, getBaseStat(stat), bonus);

		if(bonus)
		{
			synchronized(bonusStats)
			{
				if (stat.isReplace()) {
					// TODO do the right thing to remove bonus modifier to a replacable stat
				} else {
					bonusStats.put(stat, getStatBonus(stat) + modifier.getModifier());
				}
			}
		}
		else
		{
			synchronized(baseStats)
			{
				if(stat.isReplace())
				{
					// TODO correct behaviour to set the new stats, then apply all active modifiers to it
					// baseStats.put(stat, applyOldModifiersOnNewStat(effectId, stat, modifier.getModifier()));
					baseStats.put(stat, modifier.getModifier());
				}
				else
				{
					baseStats.put(stat, getBaseStat(stat) + modifier.getModifier());
				}
			}
		}

		synchronized(modifiers)
		{
			modifiers.add(modifier);
		}

	}

	public void endEffect(int effectId)
	{
		synchronized(effects)
		{
			FastList<StatModifier> modifiers = effects.get(effectId);
			if(modifiers != null)
			{
				for(StatModifier modifier : modifiers)
				{
					log.debug("Removing modifier for effect#" + effectId + ": stat:"
						+ modifier.getModifiedStat().getName() + ",value:" + modifier.getModifier() + ",bonus:"
						+ modifier.isBonus());
					if(modifier.isBonus())
					{
						synchronized(bonusStats)
						{
							if (modifier.getModifiedStat().isReplace()) {
								// TODO do the right thing to remove bonus modifier to a replacable stat
							} else {
								bonusStats.put(modifier.getModifiedStat(), modifier.endModifier(bonusStats.get(modifier
									.getModifiedStat())));
							}
						}
					}
					else
					{
						synchronized(baseStats)
						{
							if(modifier.getModifiedStat().isReplace())
							{
								// TODO update normal behaviour: apply all actives modifiers on old stat
								// baseStats.put(modifier.getModifiedStat(), applyOldModifiersOnOldStat(modifier
								//  	.getModifiedStat(), modifier.endModifier(modifier.getModifier())));
								baseStats.put(modifier.getModifiedStat(), modifier.endModifier(modifier.getModifier()));
							}
							else
							{
								baseStats.put(modifier.getModifiedStat(), modifier.endModifier(baseStats.get(modifier
									.getModifiedStat())));
							}
						}
					}
				}
				effects.remove(effectId);
			}
			else
			{
				throw new IllegalArgumentException("Invalid effect id " + effectId);
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
