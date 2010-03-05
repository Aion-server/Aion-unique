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
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponType;
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

		return (int) Math.floor(baseXP * xpPercentage * player.getRates().getXpRate() / 100);
	}

	public static long calculateGroupExperienceReward(Player player, Creature target)
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();

		//TODO take baseXP from target object (additional attribute in stats template is needed)
		int baseXP = targetLevel * 90; //promotion the group

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);

		return (int) Math.floor(baseXP * xpPercentage * RateConfig.GROUPXP_RATE / 100);
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
		NpcRank npcRank = ((Npc) target).getObjectTemplate().getRank();

		//TODO: fix to see monster Rank level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		//look at: http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009-a.html
		int baseDP = targetLevel * calculateRankMultipler(npcRank);

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		return (int) Math.floor(baseDP * xpPercentage * player.getRates().getXpRate() / 100);

	}
	
	/**
	 * 
	 * @param player
	 * @param target
	 * @return int
	 */
	public static int calculateSoloAPReward(Player player, Creature target) 
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();								
		int percentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);
		return (int) Math.floor(10 * percentage * player.getRates().getApRate() / 100);
	}

	public static int calculateGroupDPReward(Player player, Creature target)
	{
		int playerLevel = player.getCommonData().getLevel();
		int targetLevel = target.getLevel();
		NpcRank npcRank = ((Npc) target).getObjectTemplate().getRank();								

		//TODO: fix to see monster Rank level, NORMAL lvl 1, 2 | ELITE lvl 1, 2 etc..
		int baseDP = targetLevel * calculateRankMultipler(npcRank);

		int xpPercentage =  XPRewardEnum.xpRewardFrom(targetLevel - playerLevel);

		return (int) Math.floor(baseDP * xpPercentage * RateConfig.GROUPXP_RATE / 100);
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

		int Damage = 0;

		if (attacker instanceof Player)
		{
			int totalMin = ags.getCurrentStat(StatEnum.MIN_DAMAGES);
			int totalMax = ags.getCurrentStat(StatEnum.MAX_DAMAGES);
			int average = Math.round((totalMin + totalMax)/2);
			int mainHandAttack = ags.getBaseStat(StatEnum.MAIN_HAND_POWER);

			Equipment equipment = ((Player)attacker).getEquipment();

			WeaponType weaponType = equipment.getMainHandWeaponType();

			if(weaponType != null)
			{
				if(average < 1)
				{
					average = 1;
					log.warn("Weapon stat MIN_MAX_DAMAGE resulted average zero in main-hand calculation");
					log.warn("Weapon ID: " + String.valueOf(equipment.getMainHandWeapon().getItemTemplate().getTemplateId()));
					log.warn("MIN_DAMAGE = " + String.valueOf(totalMin));
					log.warn("MAX_DAMAGE = " + String.valueOf(totalMax));
				}
				
				//TODO move to controller
				if(weaponType == WeaponType.BOW)
					equipment.useArrow();

				int min = Math.round((((mainHandAttack * 100)/ average) * totalMin)/100);
				int max = Math.round((((mainHandAttack * 100)/ average) * totalMax)/100);

				if(skillDamages != 0)
				{
					int base = Rnd.get(min,max);
					Damage = base + ags.getStatBonus(StatEnum.MAIN_HAND_POWER) + Math.round(skillDamages * (ags.getCurrentStat(StatEnum.POWER)/100f));
				}
				else
				{
					int base = Rnd.get(min,max);
					Damage = Math.round(base + (ags.getBaseStat(StatEnum.MAIN_HAND_POWER)/ 2 + ags.getBaseStat(StatEnum.OFF_HAND_POWER)/ 2)/10f)
					+ ags.getStatBonus(StatEnum.MAIN_HAND_POWER);
				}

			}
			else   //if hand attack
			{
				int base = Rnd.get(16,20);
				Damage = Math.round(base + (ags.getBaseStat(StatEnum.MAIN_HAND_POWER)/ 2 + ags.getBaseStat(StatEnum.OFF_HAND_POWER)/ 2)/10f)
				+ ags.getStatBonus(StatEnum.MAIN_HAND_POWER);
			}

			//adjusting baseDamages according to attacker and target level
			//
			Damage = adjustDamages(attacker, target, Damage);

			if(attacker.isInState(CreatureState.POWERSHARD))
			{
				Item mainHandPowerShard = equipment.getMainHandPowerShard();
				if(mainHandPowerShard != null)
				{
					Damage += mainHandPowerShard.getItemTemplate().getWeaponBoost();

					equipment.usePowerShard(mainHandPowerShard, 1);
				}
			}
		}
		else
		{
			NpcRank npcRank = ((Npc) attacker).getObjectTemplate().getRank();
			int multipler = calculateRankMultipler(npcRank);
			Damage += ags.getCurrentStat(StatEnum.MAIN_HAND_POWER);
			Damage = (Damage * multipler) + ((Damage*attacker.getLevel())/10);
		}

		Damage -= Math.round(tgs.getCurrentStat(StatEnum.PHYSICAL_DEFENSE) * 0.10f);

		if (Damage<=0)
			Damage=1;

		log.debug("|=> Damages calculation result: damages("+Damage+")");

		return Damage;
	}

	public static int calculateOffHandPhysicDamageToTarget(Creature attacker, Creature target)
	{
		CreatureGameStats<?> ags = attacker.getGameStats();
		CreatureGameStats<?> tgs = target.getGameStats();

		int totalMin = ags.getCurrentStat(StatEnum.MIN_DAMAGES);
		int totalMax = ags.getCurrentStat(StatEnum.MAX_DAMAGES);
		int average = Math.round((totalMin + totalMax)/2);
		int offHandAttack = ags.getBaseStat(StatEnum.OFF_HAND_POWER);

		Equipment equipment = ((Player)attacker).getEquipment();
		
		if(average < 1)
		{
			average = 1;
			log.warn("Weapon stat MIN_MAX_DAMAGE resulted average zero in off-hand calculation");
			log.warn("Weapon ID: " + String.valueOf(equipment.getOffHandWeapon().getItemTemplate().getTemplateId()));
			log.warn("MIN_DAMAGE = " + String.valueOf(totalMin));
			log.warn("MAX_DAMAGE = " + String.valueOf(totalMax));
		}

		int Damage = 0;
		int min = Math.round((((offHandAttack * 100)/ average) * totalMin)/100);
		int max = Math.round((((offHandAttack * 100)/ average) * totalMax)/100);

		int base = Rnd.get(min,max);
		Damage = Math.round(base + (ags.getBaseStat(StatEnum.MAIN_HAND_POWER)/ 2 + ags.getBaseStat(StatEnum.OFF_HAND_POWER)/ 2)/10)
		+ ags.getStatBonus(StatEnum.OFF_HAND_POWER);

		Damage = adjustDamages(attacker, target, Damage);

		if(attacker.isInState(CreatureState.POWERSHARD))
		{
			Item offHandPowerShard = equipment.getOffHandPowerShard();
			if(offHandPowerShard != null)
			{
				Damage += offHandPowerShard.getItemTemplate().getWeaponBoost();
				equipment.usePowerShard(offHandPowerShard, 1);
			}
		}

		Damage -= Math.round(tgs.getCurrentStat(StatEnum.PHYSICAL_DEFENSE) * 0.10f);
		
		for(float i = 0.5f; i <= 1; i+=0.25f)
		{
			if(Rnd.get(0, 100) < 25)
			{
				Damage *= i;
				break;
			}
		}

		if (Damage<=0)
			Damage=1;

		return Damage;
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

		int damages = Math.round(baseDamages * ((sgs.getCurrentStat(StatEnum.KNOWLEDGE) / 100f) + (sgs.getCurrentStat(StatEnum.BOOST_MAGICAL_SKILL) / 1000f)));
		
		//adjusting baseDamages according to attacker and target level
		//
		damages = adjustDamages(speller, target, damages);

		// element resist: fire, wind, water, eath
		//
		// 10 elemental resist ~ 1% reduce of magical baseDamages
		//
		damages = Math.round(damages * (1 - tgs.getMagicalDefenseFor(element) / 1000f));

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

	public static int calculateRankMultipler(NpcRank npcRank)
	{
		//FIXME: to correct formula, have any reference?
		int multipler;
		switch(npcRank) 
		{
			case JUNK: 
				multipler = 1;
				break;
			case NORMAL: 
				multipler = 2;
				break;
			case ELITE:
				multipler = 3;
				break;
			case HERO: 
				multipler = 4;
				break;
			case LEGENDARY: 
				multipler = 5;
				break;
			default: 
				multipler = 1;
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

	/**
	 *  Calculates DODGE chance
	 *  
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static int calculatePhysicalDodgeRate(Creature attacker, Creature attacked)
	{
		//check always dodge
		if(attacked.getObserveController().checkAttackStatus(AttackStatus.DODGE))
			return 100;
		
		int accuracy;

		if(attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null)
			accuracy = Math.round((attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY) + attacker
				.getGameStats().getCurrentStat(StatEnum.OFF_HAND_ACCURACY)) / 2);
		else
			accuracy = attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY);

		int dodgeRate = (attacked.getGameStats().getCurrentStat(StatEnum.EVASION) - accuracy) / 10;
		// maximal dodge rate
		if(dodgeRate > 30)
			dodgeRate = 30;

		if(dodgeRate <= 0)
			return 1;

		return dodgeRate;
	}
	
	/**
	 *  Calculates PARRY chance
	 *  
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static int calculatePhysicalParryRate(Creature attacker, Creature attacked)
	{
		//check always parry
		if(attacked.getObserveController().checkAttackStatus(AttackStatus.PARRY))
			return 100;
		
		int accuracy;
		
		if(attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null)
			accuracy = Math.round((attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY) + attacker
				.getGameStats().getCurrentStat(StatEnum.OFF_HAND_ACCURACY)) / 2);
		else
			accuracy = attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY);

		int parryRate = (attacked.getGameStats().getCurrentStat(StatEnum.PARRY) - accuracy) / 10;
		// maximal parry rate
		if(parryRate > 40)
			parryRate = 40;

		if(parryRate <= 0)
			return 1;

		return parryRate;
	}
	
	/**
	 *  Calculates BLOCK chance
	 *  
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static int calculatePhysicalBlockRate(Creature attacker, Creature attacked)
	{
		//check always block
		if(attacked.getObserveController().checkAttackStatus(AttackStatus.BLOCK))
			return 100;
		
		int accuracy;

		if(attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null)
			accuracy = Math.round((attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY) + attacker
				.getGameStats().getCurrentStat(StatEnum.OFF_HAND_ACCURACY)) / 2);
		else
			accuracy = attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_ACCURACY);

		int blockRate = (attacked.getGameStats().getCurrentStat(StatEnum.BLOCK) - accuracy) / 10;
		// maximal block rate
		if(blockRate > 50)
			blockRate = 50;

		if(blockRate <= 0)
			return 1;

		return blockRate;
	}
	
	/**
	 *  Calculates CRITICAL chance
	 *  
	 * @param attacker
	 * @return double
	 */
	public static double calculatePhysicalCriticalRate(Creature attacker)
	{
		int critical;

		if(attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null)
			critical = Math.round((attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_CRITICAL) + attacker
				.getGameStats().getCurrentStat(StatEnum.OFF_HAND_CRITICAL)) / 2);
		else
			critical = attacker.getGameStats().getCurrentStat(StatEnum.MAIN_HAND_CRITICAL);

		double criticalRate;

		if(critical <= 440)
			criticalRate = critical * 0.1f;
		else if(critical <= 600)
			criticalRate = (440 * 0.1f) + ((critical - 440) * 0.05f);
		else
			criticalRate = (440 * 0.1f) + (160 * 0.05f) + ((critical - 600) * 0.02f);
		// minimal critical rate
		if(criticalRate < 1)
			criticalRate = 1;

		return criticalRate;
	}
	
	/**
	 *  Calculates RESIST chance
	 *  
	 * @param attacker
	 * @param attacked
	 * @return int
	 */
	public static int calculateMagicalResistRate(Creature attacker, Creature attacked)
	{		
		if(attacked.getObserveController().checkAttackStatus(AttackStatus.RESIST))
			return 100;
		
		int resistRate = Math.round((attacked.getGameStats().getCurrentStat(StatEnum.MAGICAL_RESIST) - attacker
			.getGameStats().getCurrentStat(StatEnum.MAGICAL_ACCURACY)) / 10);

		return resistRate;
	}

}
