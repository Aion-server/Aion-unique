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

import com.aionemu.gameserver.model.ItemSlot;

/**
 * @author xavier
 * @author ATracer
 *
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum
{
	MAXDP("maxdp"),
	MAXHP("maxhp"),
	MAXMP("maxmp"),
	AGILITY("agility",true),
	BLOCK("block"),
	EVASION("dodge"),
	CONCENTRATION("concentration"),
	WILL("will",true),
	HEALTH("health",true),
	ACCURACY("accuracy",true),
	KNOWLEDGE("knowledge",true),
	PARRY("parry"),
	POWER("strength",true),
	SPEED("speed",true),
	
	HIT_COUNT("hitcount",true),
	ATTACK_RANGE("attackrange",true),
	ATTACK_SPEED("attackdelay",-1,true),
	PHYSICAL_ATTACK("phyattack"),
	PHYSICAL_ACCURACY("hitaccuracy"),
	PHYSICAL_CRITICAL("critical"),
	PHYSICAL_DEFENSE("physicaldefend"),
	MAIN_HAND_ACCURACY("mainhandaccuracy"),
	MAIN_HAND_CRITICAL("mainhandcritical"),
	MAIN_HAND_POWER("mainhandpower"),
	OFF_HAND_ACCURACY("offhandaccuracy"),
	OFF_HAND_CRITICAL("offhandcritical"),
	OFF_HAND_POWER("offhandpower"),
	
	MAGICAL_ATTACK("magicalattack"),
	MAGICAL_ACCURACY("magicalhitaccuracy"),
	MAGICAL_CRITICAL("magicalcritical"),
	MAGICAL_RESIST("magicalresist"),
	MAX_DAMAGES("maxdamages"),
	MIN_DAMAGES("mindamages"),
	IS_MAGICAL_ATTACK("ismagicalattack",true),
	
	EARTH_RESISTANCE("elementaldefendearth"),
	FIRE_RESISTANCE("elementaldefendfire"),
	WIND_RESISTANCE("elementaldefendair"),
	WATER_RESISTANCE("elementaldefendwater"),
	
	BOOST_MAGICAL_SKILL("magicalskillboost"),
	BOOST_CASTING_TIME("boostcastingtime",-1),
	BOOST_HATE("boosthate"),
	
	FLY_TIME("maxfp"),
	FLY_SPEED("flyspeed"),
	
	PVP_ATTACK_RATIO("pvpattackratio"),
	PVP_DEFEND_RATIO("pvpdefendratio"),
	
	DAMAGE_REDUCE("damagereduce"),
	
	BLEED_RESISTANCE("arbleed"),
	BLIND_RESISTANCE("arblind"),
	CHARM_RESISTANCE("archarm"),
	CONFUSE_RESISTANCE("arconfuse"),
	CURSE_RESISTANCE("arcurse"),
	DISEASE_RESISTANCE("ardisease"),
	FEAR_RESISTANCE("arfear"),
	OPENAREIAL_RESISTANCE("aropenareial"),
	PARALYZE_RESISTANCE("arparalyze"),
	PERIFICATION_RESISTANCE("arperification"),
	POISON_RESISTANCE("arpoison"),
	ROOT_RESISTANCE("arroot"),
	SILENCE_RESISTANCE("arsilence"),
	SLEEP_RESISTANCE("arsleep"),
	SLOW_RESISTANCE("arslow"),
	SNARE_RESISTANCE("arsnare"),
	SPIN_RESISTANCE("arspin"),
	STAGGER_RESISTANCE("arstagger"),
	STUMBLE_RESISTANCE("arstumble"),
	STUN_RESISTANCE("arstun"),
	
	REGEN_MP("mpregen"),
	REGEN_HP("hpregen");
	
	private String name;
	private boolean replace;
	private int sign;
	
	private StatEnum (String name) {
		this(name,1,false);
	}
	
	private StatEnum (String name, boolean replace) {
		this(name,1,replace);
	}
	
	private StatEnum (String name, int sign) {
		this(name,sign,false);
	}
	
	private StatEnum (String name, int sign, boolean replace) {
		this.name = name;
		this.replace = replace;
		this.sign = sign;
	}
	
	public String getName () {
		return name;
	}
	
	public int getSign () {
		return sign;
	}
	
	public static StatEnum find(String name)
	{
		for(StatEnum sEnum : values())
		{
			if(sEnum.getName().equals(name))
			{
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for: " + name);
	}
	
	public StatEnum getMainOrSubHandStat (ItemSlot slot) {
		if (slot==null)
			return this;
		switch (this) {
			case PHYSICAL_ATTACK:
				switch (slot) {
					case SUB_HAND:
						return OFF_HAND_POWER;
					default:
						return MAIN_HAND_POWER;
				}
			case PHYSICAL_ACCURACY:
				switch (slot) {
					case SUB_HAND:
						return OFF_HAND_ACCURACY;
					default:
						return MAIN_HAND_ACCURACY;
				}
			case PHYSICAL_CRITICAL:
				switch (slot) {
					case SUB_HAND:
						return OFF_HAND_CRITICAL;
					default:
						return MAIN_HAND_CRITICAL;
				}
			default:
				return this;
		}
	}
	
	public boolean isReplace () {
		return replace;
	}
}
