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
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;

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
		
		return (int) Math.floor(baseXP * xpPercentage * Config.XP_RATE / 100);
	}
	
	/**
	 * 
	 * @param player
	 * @param target
	 * @return Damage made to target (-hp value)
	 */
	public static int calculateBaseDamageToTarget(Creature attacker, Creature target)
	{
		return calculatePhysicDamageToTarget(attacker, target, 0);
	}
	
	/**
	 * 
	 * @param player
	 * @param target
	 * @param skillDamages
	 * @return Damage made to target (-hp value)
	 */
	public static int calculatePhysicDamageToTarget(Creature attacker, Creature target, int skillDamages)
	{
		CreatureGameStats<?> ags = attacker.getGameStats();
		CreatureGameStats<?> tgs = target.getGameStats();
		
		log.debug("Calculating base damages (skill base damages: "+skillDamages+") ...");
		log.debug("| Attacker: "+ags);
		log.debug("| Target  : "+tgs);
		
		int baseDamages = skillDamages;
		if (baseDamages==0)
		{
			baseDamages = ags.getCurrentStat(StatEnum.MAIN_HAND_POWER)*2;
		}
		int pDef = tgs.getCurrentStat(StatEnum.PHYSICAL_DEFENSE);
		int damages = baseDamages; // + Math.round(baseDamages*0.60f);
		damages -= Math.round(pDef * 0.10f);
		if (damages<=0) {
			damages=1;
		}
		log.debug("|=> Damages calculation result: damages("+damages+")");
		return damages;
	}
	
	
	/**
	 * @param player
	 * @param target
	 * @param skillEffectTemplate
	 * @return HP damage to target
	 */
	public static int calculateMagicDamageToTarget(Creature speller, Creature target, int baseDamages, SkillElement element)
	{
		CreatureGameStats<?> sgs = speller.getGameStats();
		CreatureGameStats<?> tgs = target.getGameStats();
		log.debug("Calculating magic damages between "+speller.getObjectId()+" and "+target.getObjectId()+" (skill base damages: "+baseDamages+")...");
		log.debug("| Speller : "+sgs);
		log.debug("| Target  : "+tgs);
		int elementaryDefense = tgs.getMagicalDefenseFor(element);
		int magicalResistance = tgs.getCurrentStat(StatEnum.MAGICAL_RESIST);
		int magicBoost = sgs.getCurrentStat(StatEnum.MAGICAL_ATTACK);
		int damages = baseDamages+Math.round(magicBoost*0.60f);
		damages -= Math.round((elementaryDefense+magicalResistance)*0.60f);
		if (damages<=0) {
			damages=1;
		}
		log.debug("|=> Magic damages calculation result: damages("+damages+")");
		return damages;
	}
}
