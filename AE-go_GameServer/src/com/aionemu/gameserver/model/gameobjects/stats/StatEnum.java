/*
 * This file is part of aion-emu <aion-emu.com>.
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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.items.ItemSlot;

/**
 * @author xavier
 * @author ATracer
 *
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum
{
//	None = 0,
//	FireResistance = 15,
//	HP = 18,
//	MP = 20,
//	FlightTime = 23,
//	Attack = 25,
//	PhysicalDefense = 26,
//	MagicalAttack = 27,
//	MagicalRes = 28,
//	AttackSpeed = 29, //%
//	Accuracy = 30,
//	Evasion = 31,
//	Parry = 32,
//	Block = 33,
//	PhysicalCrit = 34,
//	Speed = 36, // %
//	FlightSpeed = 37, // %
//	MagicalCrit = 40,
//	Concentration = 41,
//	MagicPower = 104,
//	MagicalAccuracy = 105,
//	Knowledge = 106,
//	Agility = 107,
//	Hate = 109
	MAXDP(0, "maxdp"),
	MAXHP(18, "maxhp"),
	MAXMP(20, "maxmp"),

	AGILITY(107, "agility",true),
	BLOCK(33, "block"),
	EVASION(31, "dodge"),
	CONCENTRATION(41, "concentration"),
	WILL(0, "will",true),
	HEALTH(0, "health",true),
	ACCURACY(0, "accuracy",true),
	KNOWLEDGE(106, "knowledge",true),
	PARRY(32, "parry"),
	POWER(0, "strength",true),
	SPEED(36, "speed",true),
	HIT_COUNT(0, "hitcount",true),

	ATTACK_RANGE(0, "attackrange",true),
	ATTACK_SPEED(29, "attackdelay",-1,true),
	PHYSICAL_ATTACK(25, "phyattack"),
	PHYSICAL_ACCURACY(30, "hitaccuracy"),
	PHYSICAL_CRITICAL(34, "critical"),
	PHYSICAL_DEFENSE(26, "physicaldefend"),
	MAIN_HAND_HITS(0, "mainhandhits"),
	MAIN_HAND_ACCURACY(0, "mainhandaccuracy"),
	MAIN_HAND_CRITICAL(0, "mainhandcritical"),
	MAIN_HAND_POWER(0, "mainhandpower"),
	OFF_HAND_HITS(0, "offhandhits"),
	OFF_HAND_ACCURACY(0, "offhandaccuracy"),
	OFF_HAND_CRITICAL(0, "offhandcritical"),
	OFF_HAND_POWER(0, "offhandpower"),

	MAGICAL_ATTACK(27, "magicalattack"),
	MAGICAL_ACCURACY(105, "magicalhitaccuracy"),
	MAGICAL_CRITICAL(40, "magicalcritical"),
	MAGICAL_RESIST(28, "magicalresist"),
	MAX_DAMAGES(0, "maxdamages"),
	MIN_DAMAGES(0, "mindamages"),
	IS_MAGICAL_ATTACK(0, "ismagicalattack",true),

	EARTH_RESISTANCE(0, "elementaldefendearth"),
	FIRE_RESISTANCE(15, "elementaldefendfire"),
	WIND_RESISTANCE(0, "elementaldefendair"),
	WATER_RESISTANCE(0, "elementaldefendwater"),

	BOOST_MAGICAL_SKILL(104, "magicalskillboost"),
	BOOST_CASTING_TIME(0, "boostcastingtime",-1),
	BOOST_HATE(109, "boosthate"),

	FLY_TIME(23, "maxfp"),
	FLY_SPEED(37, "flyspeed"),

	PVP_ATTACK_RATIO(0, "pvpattackratio"),
	PVP_DEFEND_RATIO(0, "pvpdefendratio"),

	DAMAGE_REDUCE(0, "damagereduce"),

	BLEED_RESISTANCE(0, "arbleed"),
	BLIND_RESISTANCE(0, "arblind"),
	CHARM_RESISTANCE(0, "archarm"),
	CONFUSE_RESISTANCE(0, "arconfuse"),
	CURSE_RESISTANCE(0, "arcurse"),
	DISEASE_RESISTANCE(0, "ardisease"),
	FEAR_RESISTANCE(0, "arfear"),
	OPENAREIAL_RESISTANCE(0, "aropenareial"),
	PARALYZE_RESISTANCE(0, "arparalyze"),
	PERIFICATION_RESISTANCE(0, "arperification"),
	POISON_RESISTANCE(0, "arpoison"),
	ROOT_RESISTANCE(0, "arroot"),
	SILENCE_RESISTANCE(0, "arsilence"),
	SLEEP_RESISTANCE(0, "arsleep"),
	SLOW_RESISTANCE(0, "arslow"),
	SNARE_RESISTANCE(0, "arsnare"),
	SPIN_RESISTANCE(0, "arspin"),
	STAGGER_RESISTANCE(0, "arstagger"),
	STUMBLE_RESISTANCE(0, "arstumble"),
	STUN_RESISTANCE(0, "arstun"),

	REGEN_MP(0, "mpregen"),
	REGEN_HP(0, "hpregen");

	private String name;
	private boolean replace;
	private int sign;
	
	private int itemStoneMask;

	private StatEnum (int stoneMask, String name) 
	{
		this(stoneMask, name,1,false);
	}

	private StatEnum (int stoneMask, String name, boolean replace) 
	{
		this(stoneMask,name,1,replace);
	}

	private StatEnum (int stoneMask, String name, int sign) 
	{
		this(stoneMask, name,sign,false);
	}

	private StatEnum (int stoneMask, String name, int sign, boolean replace) 
	{
		this.itemStoneMask = stoneMask;
		this.name = name;
		this.replace = replace;
		this.sign = sign;
	}

	public String getName () 
	{
		return name;
	}

	public int getSign () 
	{
		return sign;
	}

	/**
	 * @return the itemStoneMask
	 */
	public int getItemStoneMask()
	{
		return itemStoneMask;
	}

	public static StatEnum find(String name)
	{
		for(StatEnum sEnum : values())
		{
			if(sEnum.getName().toLowerCase().equals(name.toLowerCase()))
			{
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for: " + name);
	}
	
	/**
	 *  Used to find specific StatEnum by its item stone mask
	 *   
	 * @param mask
	 * @return StatEnum
	 */
	public static StatEnum findByItemStoneMask(int mask)
	{
		for(StatEnum sEnum : values())
		{
			if(sEnum.getItemStoneMask() == mask)
			{
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for stone mask: " + mask);
	}

	public StatEnum getMainOrSubHandStat (ItemSlot slot) 
	{
		if(slot == null)
			return this;
		switch(this)
		{
			case PHYSICAL_ATTACK:
			case POWER:
				switch(slot)
				{
					case SUB_HAND:
						return OFF_HAND_POWER;
					case MAIN_HAND:
						return MAIN_HAND_POWER;
				}
			case PHYSICAL_ACCURACY:
				switch(slot)
				{
					case SUB_HAND:
						return OFF_HAND_ACCURACY;
					case MAIN_HAND:
						return MAIN_HAND_ACCURACY;
				}
			case PHYSICAL_CRITICAL:
				switch(slot)
				{
					case SUB_HAND:
						return OFF_HAND_CRITICAL;
					case MAIN_HAND:
						return MAIN_HAND_CRITICAL;
				}
			case HIT_COUNT:
				switch(slot)
				{
					case SUB_HAND:
						return OFF_HAND_HITS;
					case MAIN_HAND:
						return MAIN_HAND_HITS;
				}
			default:
				return this;
		}
	}

	public boolean isMainOrSubHandStat()
	{
		switch(this)
		{
			case PHYSICAL_ATTACK:
			case POWER:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
				return true;

			default:
				return false;
		}
	}

	public boolean isReplace () {
		return replace;
	}
}
