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
package com.aionemu.gameserver.controllers.attack;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 * 
 * Probably this is a temporary class for attack calculation
 * cause i need it during refactoring
 */
public class AttackUtil
{

	/**
	 *  TODO: 1) diff between physical and magical attacks - diff status
	 *  
	 *  
	 * @param attacker
	 * @param attacked
	 * @return
	 */
	public static List<AttackResult> calculateAttackResult(Creature attacker, Creature attacked)
	{
		int damage = StatFunctions.calculateBaseDamageToTarget(attacker, attacked);
		CreatureGameStats<?> gameStats = attacker.getGameStats();

		int hitCount = Rnd.get(1,gameStats.getCurrentStat(StatEnum.MAIN_HAND_HITS) + gameStats.getCurrentStat(StatEnum.OFF_HAND_HITS));

		List<AttackResult> attackList = new ArrayList<AttackResult>();
		
		for (int i=0; (i<hitCount); i++) 
		{
			int damages;
			if (i==0)
			{
				damages = Math.round(damage*0.75f);
			}
			else
			{
				damages = Math.round(damage/(hitCount-1));
			}
			damage -= damages;

			AttackStatus status = calculateStatus();
			//TODO this is very basic calcs, for initial testing only
			switch(status)
			{
				case BLOCK:
				case DODGE:
				case RESIST:
					damages = 0;
					break;
				case CRITICAL:
					damages *= 2;
					break;
				case PARRY:
					damages *= 0.5;
					break;
			}
			attackList.add(new AttackResult(damages, status));
		}
		return attackList;
	}

	/**
	 *  DUMMY for simplicity now
	 *  
	 * @return
	 */
	public static AttackStatus calculateStatus()
	{
		if(Rnd.get(0, 10) > 8)
		{
			return AttackStatus.values()[Rnd.get(0, 6)];
		}
		else
		{
			return AttackStatus.NORMALHIT;
		}
	}
}


