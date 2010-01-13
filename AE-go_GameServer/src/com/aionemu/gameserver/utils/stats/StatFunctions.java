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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.templates.stats.NpcRank;

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

	public static long calculateGroupExperienceReward(Player player, Creature target)
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();

		//TODO take baseXP from target object (additional attribute in stats template is needed)
		int baseXP = targetLevel * 90; //promotion the group

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);

		return (int) Math.floor(baseXP * xpPercentage * Config.GROUPXP_RATE / 100);
	}

	/**
	 * ref: http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009-a.html
	 * 
	 * @param player
	 * @param target
	 * @return DP reward from target
	 */

	public static int calculateSoloDPReward(Player player, Creature target) 
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		NpcRank npcRank = ((Npc) target).getTemplate().getRank();								

		//TODO: fix to see monster Rank level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		//look at: http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009-a.html
		int baseDP = targetLevel * calculateRankMultipler(npcRank);

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		return (int) Math.floor(baseDP * xpPercentage * Config.XP_RATE / 100);

	}

	public static int calculateGroupDPReward(Player player, Creature target)
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		NpcRank npcRank = ((Npc) target).getTemplate().getRank();								

		//TODO: fix to see monster Rank level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		int baseDP = targetLevel * calculateRankMultipler(npcRank);

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);

		return (int) Math.floor(baseDP * xpPercentage * Config.GROUPXP_RATE / 100);
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

		//adjusting baseDamages according to attacker and target level
		//
		baseDamages = adjustDamages(attacker, target, baseDamages);

		if (attacker instanceof Player)
		{
			int min = ags.getCurrentStat(StatEnum.MIN_DAMAGES);
			int max = ags.getCurrentStat(StatEnum.MAX_DAMAGES);
			int base = Rnd.get(min,max);
			float multiplier = Math.round((ags.getBaseStat(StatEnum.MAIN_HAND_POWER)+ags.getBaseStat(StatEnum.OFF_HAND_POWER))/100f);
			baseDamages += Math.round(base * multiplier) + ags.getStatBonus(StatEnum.MAIN_HAND_POWER) + ags.getStatBonus(StatEnum.OFF_HAND_POWER);
		}
		else
		{
			NpcRank npcRank = ((Npc) attacker).getTemplate().getRank();
			int multipler = calculateRankMultipler(npcRank);
			baseDamages += ags.getCurrentStat(StatEnum.MAIN_HAND_POWER);
			baseDamages = (baseDamages * multipler) + ((baseDamages*attacker.getLevel())/10);			
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

		// not yet used for now
		//int magicalResistance = tgs.getCurrentStat(StatEnum.MAGICAL_RESIST);

		//adjusting baseDamages according to attacker and target level
		//
		baseDamages = adjustDamages(speller, target, baseDamages);

		// element resist: fire, wind, water, eath
		//
		// 10 elemental resist ~ 1% reduce of magical baseDamages
		//
		int elementaryDefenseBase = tgs.getMagicalDefenseFor(element);
		int elementaryDefense = Math.round( (elementaryDefenseBase / 1000f) * baseDamages);
		baseDamages -= elementaryDefense;

		// then, add magical attack bonus..

		// TODO: fix this to the correct Magical Attack formula
		int magicAttBase = sgs.getCurrentStat(StatEnum.MAGICAL_ATTACK);
		baseDamages += magicAttBase;

		// magicBoost formula
		// i think after researching in several forums, this is correct formula for magicBoost
		//
		int magicBoostBase = sgs.getCurrentStat(StatEnum.BOOST_MAGICAL_SKILL);
		int magicBoost = Math.round(baseDamages * ((magicBoostBase / 12f) / 100f));

		int damages = baseDamages + magicBoost;

		// IMPORTANT NOTES
		//
		// magicalResistance supposed to be counted to EVADE magic, not to reduce damage, only the elementaryDefense it's counted to reduce magic attack
		//
		//     so example if 200 magic resist vs 100 magic accuracy, 200 - 100 = 100/10 = 0.10 or 10% chance of EVADE
		//
		// damages -= Math.round((elementaryDefense+magicalResistance)*0.60f);


		if (damages<=0) {
			damages=1;
		}
		log.debug("|=> Magic damages calculation result: damages("+damages+")");
		return damages;
	}

	public static int calculateRankMultipler(NpcRank NpcRank)
	{
		//FIXME: to correct formula, have any reference?
		int multipler = 1;
		switch(NpcRank) {
			case JUNK: multipler = 2;
			break;
			case NORMAL: multipler = 3;
			break;
			case ELITE: multipler = 4;
			break;
			case HERO: multipler = 5;
			break;
			case LEGENDARY: multipler = 6;
			break;
			default: multipler = 2;
		}

		return multipler;
	}

	/**
	 * adjust baseDamages according to their level || is PVP?
	 *
	 * @ref:
	 *
	 * @param attacker lvl
	 * @param target lvl
	 * @param baseDamages
	 *
	 **/
	public static int adjustDamages(Creature attacker, Creature target, int Damages) {

		int attackerLevel = attacker.getLevel();
		int targetLevel = target.getLevel();
		int baseDamages = Damages;

		//fix this for better monster target condition please
		if ( (attacker instanceof Player) && !(target instanceof Player)) {

			if(targetLevel > attackerLevel) {

				float multipler = 0.0f;
				int differ = (targetLevel - attackerLevel);

				if( differ <= 2 ) {
					return baseDamages;
				}
				else if( differ > 2 && differ < 10 ) {
					multipler = (differ - 2f) / 10f;
					baseDamages -= Math.round((baseDamages * multipler));
				}

				else {
					baseDamages -= Math.round((baseDamages * 0.80f));
				}

				return baseDamages;
			}
		} //end of damage to monster

		//PVP damages is capped of 60% of the actual baseDamage
		else if( (attacker instanceof Player) && (target instanceof Player) ) {
			baseDamages = Math.round(baseDamages * 0.60f);
			return baseDamages;
		}

		return baseDamages;

	}

	public static int calculatePhysicalDodgeRate( Creature attacker, Creature attacked )
	{
		int dodgeRate = ( attacked.getGameStats().getCurrentStat(StatEnum.BLOCK) - attacker.getGameStats().getCurrentStat(StatEnum.ACCURACY) ) / 10;
		//maximal dodge rate
		if( dodgeRate > 50) {
			dodgeRate = 50;
		}
		return dodgeRate;
	}

	public static int calculatePhysicalParryRate( Creature attacker, Creature attacked )
	{
		int parryRate = ( attacked.getGameStats().getCurrentStat(StatEnum.PARRY) - attacker.getGameStats().getCurrentStat(StatEnum.ACCURACY) ) / 10;
		//maximal parry rate
		if( parryRate > 40) {
			parryRate = 40;
		}
		return parryRate;
	}

	public static int calculatePhysicalBlockRate( Creature attacker, Creature attacked )
	{
		int blockRate = ( attacked.getGameStats().getCurrentStat(StatEnum.BLOCK) - attacker.getGameStats().getCurrentStat(StatEnum.ACCURACY) ) / 10;
		//maximal block rate
		if( blockRate > 30 ) {
			blockRate = 30;
		}
		return blockRate;
	}

	public static double calculatePhysicalCriticalRate( Creature attacker )
	{
		double criticalRate = 75d * Math.sin( ( ( 900 - attacker.getGameStats().getCurrentStat(StatEnum.PHYSICAL_CRITICAL) ) / 1800 ) * Math.PI );
		//minimal critical rate
		if(criticalRate < 0.1d) {
			criticalRate = 0.1d;
		}
		return criticalRate * 100d;
	}

	public static int calculateMagicalResistRate( Creature attacker, Creature attacked )
	{
		int resistRate = Math.round((attacked.getGameStats().getCurrentStat(StatEnum.MAGICAL_RESIST) - attacker.getGameStats().getCurrentStat(StatEnum.MAGICAL_ACCURACY)) / 10);

		return resistRate;
	}

}
