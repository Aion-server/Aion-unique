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
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Effect;
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
	 * @param attacker
	 * @param attacked
	 * @return List<AttackResult>
	 */
	public static List<AttackResult> calculateAttackResult(Creature attacker, Creature attacked)
	{
		int damage = StatFunctions.calculateBaseDamageToTarget(attacker, attacked);
		AttackStatus status = calculatePhysicalStatus(attacker, attacked);

		CreatureGameStats<?> gameStats = attacker.getGameStats();

		if(attacker instanceof Player && ((Player)attacker).getEquipment().getOffHandWeaponType() != null)
		{
			AttackStatus offHandStatus;

			switch(status)
			{
				case BLOCK:
					offHandStatus = AttackStatus.OFFHAND_BLOCK;
					break;
				case DODGE:
					offHandStatus = AttackStatus.OFFHAND_DODGE;
					break;
				case CRITICAL:
					offHandStatus = AttackStatus.OFFHAND_CRITICAL;
					break;
				case PARRY:
					offHandStatus = AttackStatus.OFFHAND_PARRY;
					break;
				default:
					offHandStatus = AttackStatus.OFFHAND_NORMALHIT;
					break;
			}

			int offHandDamage = StatFunctions.calculateOffHandPhysicDamageToTarget(attacker, attacked);

			int mainHandHits = Rnd.get(1,gameStats.getCurrentStat(StatEnum.MAIN_HAND_HITS));
			int offHandHits =  Rnd.get(1,gameStats.getCurrentStat(StatEnum.OFF_HAND_HITS));

			List<AttackResult> attackList = new ArrayList<AttackResult>();
			attackList.addAll(splitPhysicalDamage(attacker, attacked, mainHandHits, damage, status));
			attackList.addAll(splitPhysicalDamage(attacker, attacked, offHandHits, offHandDamage, offHandStatus));
			attacked.getObserveController().checShieldStatus(attackList);
			
			return attackList;
		}

		int hitCount = Rnd.get(1,gameStats.getCurrentStat(StatEnum.MAIN_HAND_HITS));
		List<AttackResult> attackList = splitPhysicalDamage(attacker, attacked, hitCount, damage, status);
		attacked.getObserveController().checShieldStatus(attackList);
		return attackList;
	}


	public static List<AttackResult> splitPhysicalDamage(Creature attacker, Creature attacked, int hitCount, int damage, AttackStatus status)
	{
		List<AttackResult> attackList = new ArrayList<AttackResult>();

		for (int i=0; i < hitCount; i++) 
		{
			int damages = damage;

			if (i!=0)
			{
				damages = Math.round(damage * 0.1f);
			}

			WeaponType weaponType;

			//TODO this is very basic calcs, for initial testing only
			switch(status)
			{
				case BLOCK:
				case OFFHAND_BLOCK:
					int shieldDamageReduce = ((Player)attacked).getGameStats().getCurrentStat(StatEnum.DAMAGE_REDUCE);
					damages -= Math.round((damages * shieldDamageReduce) / 100);
					break;

				case DODGE:
				case OFFHAND_DODGE:
					damages = 0;
					break;

				case CRITICAL:
					weaponType = ((Player)attacker).getEquipment().getMainHandWeaponType();
					damages = calculateWeaponCritical(damages, weaponType);
					break;

				case OFFHAND_CRITICAL:
					weaponType = ((Player)attacker).getEquipment().getOffHandWeaponType();
					damages = calculateWeaponCritical(damages, weaponType);
					break;

				case PARRY:
				case OFFHAND_PARRY:
					damages *= 0.5;
					break;

				default:
					break;
			}
			attackList.add(new AttackResult(damages, status));
		}
		return attackList;
	}

	/**
	 * [Critical]
	 *	Spear : x1.5
	 *	Sword : x2.5
	 *	Dagger : x2.3
	 *	Mace : x2.0
	 *	Greatsword : x1.5
	 *	Orb : x2.0
	 *	Spellbook : x2.0
	 *	Bow : x1.4
	 *	Staff : x1.5
	 *
	 * @param damages
	 * @param weaponType
	 * @return
	 */
	private static int calculateWeaponCritical(int damages, WeaponType weaponType)
	{
		switch(weaponType)
		{
			case DAGGER_1H:
				damages = Math.round(damages * 2.3f);
				break;
			case SWORD_1H:
				damages = Math.round(damages * 2.2f);
				break;
			case MACE_1H:
				damages *= 2;
				break;
			case SWORD_2H:
			case POLEARM_2H:
				damages = Math.round(damages * 1.8f);
				break;
			case STAFF_2H:
			case BOW:
				damages = Math.round(damages * 1.8f);
				break;
			default:
				damages = Math.round(damages * 1.5f);
				break;
		}
		return damages;
	}
	
	/**
	 * 
	 * @param effect
	 * @param skillDamage
	 */
	public static void calculatePhysicalSkillAttackResult(Effect effect, int skillDamage)
	{
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		int damage = StatFunctions.calculatePhysicDamageToTarget(effector, effected, skillDamage);

		AttackStatus status = calculatePhysicalStatus(effector, effected);
		switch(status)
		{
			case BLOCK:
				int shieldDamageReduce = ((Player)effected).getGameStats().getCurrentStat(StatEnum.DAMAGE_REDUCE);
				damage -= Math.round((damage * shieldDamageReduce) / 100);
				break;
			case DODGE:
				damage = 0;
				break;
			case CRITICAL:
				damage *= 2;
				break;
			case PARRY:
				damage *= 0.5;
				break;
			default:
				break;
		}
		
		calculateEffectResult(effect, effected, damage, status);
	}

	/**
	 * 
	 * @param effect
	 * @param effected
	 * @param damage
	 * @param status
	 */
	private static void calculateEffectResult(Effect effect, Creature effected, int damage, AttackStatus status)
	{
		AttackResult attackResult = new AttackResult(damage, status);
		effected.getObserveController().checShieldStatus(Collections.singletonList(attackResult));
		effect.setReserved1(attackResult.getDamage());
		effect.setAttackStatus(attackResult.getAttackStatus());
		effect.setShieldDefense(attackResult.getShieldType());
	}

	/**
	 * 
	 * @param effect
	 * @param skillDamage
	 * @param element
	 */
	public static void calculateMagicalSkillAttackResult(Effect effect, int skillDamage, SkillElement element)
	{
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		
		int damage = StatFunctions.calculateMagicDamageToTarget(effector, effected, skillDamage, element);  //TODO SkillElement

		AttackStatus status = calculateMagicalStatus(effector, effected);
		switch(status)
		{
			case RESIST:
				damage = 0;
				break;
			default:
				break;
		}
		
		calculateEffectResult(effect, effected, damage, status);
	}

	/**
	 * Manage attack status rate
	 * @source http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009-a.html
	 * @return AttackStatus
	 */
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked)
	{		
		if( Rnd.get( 0, 100 ) < StatFunctions.calculatePhysicalDodgeRate(attacker, attacked) )
			return AttackStatus.DODGE;

		if( attacked instanceof Player && ((Player)attacked).getEquipment().getMainHandWeaponType() != null                  // PARRY can only be done with weapon, also weapon can have humanoid mobs,
			&& Rnd.get( 0, 100 ) < StatFunctions.calculatePhysicalParryRate(attacker, attacked) ) // but for now there isnt implementation of monster category
			return AttackStatus.PARRY;

		if( attacked instanceof Player && ((Player) attacked).getEquipment().isShieldEquipped()
			&& Rnd.get( 0, 100 ) < StatFunctions.calculatePhysicalBlockRate(attacker, attacked) )
			return AttackStatus.BLOCK;


		if( attacker instanceof Player && ((Player)attacker).getEquipment().getMainHandWeaponType() != null           // CRITICAL can only be done with weapon, weapon can have humanoid mobs also,
			&& Rnd.get( 0, 100 ) < StatFunctions.calculatePhysicalCriticalRate(attacker) ) // but for now there isnt implementation of monster category
			return AttackStatus.CRITICAL;

		return AttackStatus.NORMALHIT;
	}


	public static AttackStatus calculateMagicalStatus(Creature attacker, Creature attacked)
	{
		if(Rnd.get( 0, 100 ) < StatFunctions.calculateMagicalResistRate(attacker, attacked))
			return AttackStatus.RESIST;

		return AttackStatus.NORMALHIT;
	}
}