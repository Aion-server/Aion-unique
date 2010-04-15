/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.Summon;

/**
 * @author ATracer
 *
 */
public class SummonGameStats extends CreatureGameStats<Summon>
{

	public SummonGameStats(Summon owner)
	{
		super(owner);
		initStat(StatEnum.MAXHP, 1000);
		initStat(StatEnum.MAXMP, 1000);
		initStat(StatEnum.MAIN_HAND_POWER, 82);
		initStat(StatEnum.PHYSICAL_DEFENSE, 285);
		initStat(StatEnum.MAGICAL_RESIST, 252);
		initStat(StatEnum.ATTACK_SPEED, 2000);
		initStat(StatEnum.SPEED, 6000);
	}
}
