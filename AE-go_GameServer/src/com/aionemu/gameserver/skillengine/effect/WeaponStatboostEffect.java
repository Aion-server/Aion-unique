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
package com.aionemu.gameserver.skillengine.effect;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.ActionObserver.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.id.SkillEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponStatboostEffect")
public class WeaponStatboostEffect extends BufEffect
{
	@XmlAttribute(name = "weapon")
	private WeaponType weaponType;
	
	@Override
	public void startEffect(final Effect effect)
	{
		final Player effected  = (Player) effect.getEffected();
	
		final SkillEffectId skillEffectId = getSkillEffectId(effect);
		final TreeSet<StatModifier> stats = getModifiers(effect);
		
		if(effected.getEquipment().isWeaponEquipped(weaponType))
			effected.getGameStats().addModifiers(skillEffectId, stats);
		
		/**
		 * Since weapon stat boost is only for bows in templates - checking only
		 * one weapon is enough for final result.
		 */
		ActionObserver aObserver = new ActionObserver(ObserverType.EQUIP){

			@Override
			public void equip(Item item, Player owner)
			{
				if(item.getItemTemplate().getWeaponType() == weaponType)
					effected.getGameStats().addModifiers(skillEffectId, stats);
			}

			@Override
			public void unequip(Item item, Player owner)
			{
				if(item.getItemTemplate().getWeaponType() == weaponType)
					effected.getGameStats().endEffect(skillEffectId);
			}
			
		};
		
		effected.getObserveController().addEquipObserver(aObserver);
		effect.setActionObserver(aObserver, position);
	}
	
	@Override
	public void endEffect(Effect effect)
	{
		ActionObserver observer = effect.getActionObserver(position);
		if(observer != null)
			effect.getEffected().getObserveController().removeEquipObserver(observer);
		
		final SkillEffectId skillEffectId = getSkillEffectId(effect);
		
		if(effect.getEffected().getGameStats().effectAlreadyAdded(skillEffectId))
			effect.getEffected().getGameStats().endEffect(skillEffectId);
		
	}
	
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect)
	{
		effect.increaseSuccessEffect();
	}

}
