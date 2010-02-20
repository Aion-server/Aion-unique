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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponMasteryEffect")
public class WeaponMasteryEffect extends BufEffect
{
	@XmlAttribute(name = "weapon")
	private WeaponType weaponType;

	/**
	 * @return the weaponType
	 */
	public WeaponType getWeaponType()
	{
		return weaponType;
	}

	@Override
	public void calculate(Effect effect)
	{
		//right now only players are affected
		Player player = (Player)  effect.getEffector();
		//check best mastery skill
		Integer skillId = player.getSkillList().getWeaponMasterySkill(weaponType);
		if(skillId != null && skillId != effect.getSkillId())
			return;
		//check weather already skill applied and weapon isEquipeed
		boolean weaponMasterySet = player.getEffectController().isWeaponMasterySet(weaponType);
		boolean isWeaponEquiped = player.getEquipment().isWeaponEquipped(weaponType);
		if(!weaponMasterySet && isWeaponEquiped)
			effect.increaseSuccessEffect();
	}

	@Override
	public void endEffect(Effect effect)
	{
		super.endEffect(effect);
		Player player = (Player)  effect.getEffector();
		player.getEffectController().unsetWeaponMastery(weaponType.getMask());
	}

	@Override
	public void startEffect(Effect effect)
	{
		super.startEffect(effect);
		Player player = (Player)  effect.getEffector();
		player.getEffectController().setWeaponMastery(weaponType.getMask());
	}
	
	
}
