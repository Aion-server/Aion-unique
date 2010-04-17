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
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;

/**
 * @author ATracer
 *
 */
public class SummonGameStats extends CreatureGameStats<Summon>
{

	/**
	 * 
	 * @param owner
	 * @param statsTemplate
	 */
	public SummonGameStats(Summon owner, SummonStatsTemplate statsTemplate)
	{
		super(owner);
		initStat(StatEnum.MAXHP, statsTemplate.getMaxHp());
		initStat(StatEnum.MAXMP, statsTemplate.getMaxMp());
		initStat(StatEnum.MAIN_HAND_POWER, statsTemplate.getMainHandAttack());
		initStat(StatEnum.PHYSICAL_DEFENSE, statsTemplate.getPdefense());
		initStat(StatEnum.MAGICAL_RESIST, statsTemplate.getMresist());
		initStat(StatEnum.ATTACK_SPEED, 2000);
		initStat(StatEnum.SPEED, Math.round(statsTemplate.getRunSpeed() * 1000));
	}
}
