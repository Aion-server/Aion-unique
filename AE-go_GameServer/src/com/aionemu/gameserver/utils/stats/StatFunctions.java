/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.utils.stats;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillEffectTemplate;

/**
 * @author ATracer
 * @author alexa026
 */
public class StatFunctions
{
	private static Logger log = Logger.getLogger(StatFunctions.class);

	/**
	 * 
	 * @param player
	 * @param target
	 * @return XP reward from target
	 */
	public static long calculateSoloExperienceReward(Player player, Creature target)
	{
		
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		
		//TODO take baseXP from target object (additional attribute in stats template is needed)
		int baseXP = targetLevel * 80;
		
		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		
		return (int) Math.floor(baseXP * xpPercentage * Config.EXP_RATE / 100);
	}
	
	/**
	 * 
	 * @param player
	 * @param target
	 * @return Damage made to target (-hp value)
	 */
	public static int calculateBaseDamageToTarget(Player player, Creature target)
	{
		int pAttack = ClassStats.getPowerFor(player.getPlayerClass());
		int targetPDef = ((Npc) target).getTemplate().getStatsTemplate().getMaxHp();
		
		return pAttack - targetPDef / 10;
	}
	
	/**
	 * @param player
	 * @param target
	 * @param skillEffectTemplate
	 * @return HP damage to target
	 */
	public static int calculateMagicDamageToTarget(Player player, Creature target, SkillEffectTemplate skillEffectTemplate)
	{
		//TODO this is a dummmy cacluations
		return Integer.parseInt(skillEffectTemplate.getValue()) * 3;
	}
	
	public static int calculateNpcBaseDamageToPlayer(Npc npc, Player player)
	{
		//TODO this is a dummy calcs
		return npc.getLevel() * 5 + 20;
	}
	
}
