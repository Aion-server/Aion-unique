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
package com.aionemu.gameserver.controllers.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;

/**
 * @author ATracer
 *
 */
public class PlayerEffectController extends EffectController
{
	/**
	 * weapon mastery mask
	 */
	private int weaponEffects;
	
	/**
	 * armor mastery mask
	 */
	private int armorEffects;
	
	/**
	 * current food effect
	 */
	private Effect foodEffect;

	public PlayerEffectController(Creature owner)
	{
		super(owner);
	}
	
	@Override
	public void addEffect(Effect effect)
	{
		if(effect.isFood())
			addFoodEffect(effect);
		
		if(checkDuelCondition(effect))
			return;
		
		super.addEffect(effect);
	}
	
	@Override
	public void clearEffect(Effect effect)
	{
		if(effect.isFood())
			foodEffect = null;
	
		super.clearEffect(effect);
	}
	

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}

	/**
	 * Effect of DEBUFF should not be added if duel ended (friendly unit)
	 * @param effect
	 * @return
	 */
	private boolean checkDuelCondition(Effect effect)
	{
		Creature creature = effect.getEffector();
		if(creature instanceof Player)
		{
			if(getOwner().isFriend((Player) creature) && effect.getTargetSlot() == SkillTargetSlot.DEBUFF.ordinal())
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param effect
	 */
	private void addFoodEffect(Effect effect)
	{
		if(foodEffect != null)
			foodEffect.endEffect();
		foodEffect = effect;
	}

	/**
	 * Weapon mastery
	 */
	public void setWeaponMastery(int mask)
	{
		weaponEffects |= mask;
	}

	public void unsetWeaponMastery(int mask)
	{
		weaponEffects &= ~mask;
	}

	public int getWeaponMastery()
	{
		return weaponEffects;
	}
	
	public boolean isWeaponMasterySet(WeaponType weaponType)
	{
		int isState = this.weaponEffects & weaponType.getMask();
		return isState == weaponType.getMask();
	}
	
	/**
	 * Armor mastery
	 */
	public void setArmorMastery(int mask)
	{
		armorEffects |= mask;
	}

	public void unsetArmorMastery(int mask)
	{
		armorEffects &= ~mask;
	}

	public int getArmorMastery()
	{
		return armorEffects;
	}
	
	public boolean isArmorMasterySet(ArmorType armorType)
	{
		int isState = this.armorEffects & armorType.getMask();
		return isState == armorType.getMask();
	}
	
}
