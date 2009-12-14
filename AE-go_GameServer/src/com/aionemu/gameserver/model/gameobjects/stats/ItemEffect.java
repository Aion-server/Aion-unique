/*
 * This file is part of aion-unique <aion-unique.com>.
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

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 *
 */
public class ItemEffect extends StatEffect
{
	private ItemSlot equippedSlot;
	
	public ItemEffect(ItemSlot equippedSlot)
	{
		super();
		this.equippedSlot = equippedSlot;
	}
	
	public ItemSlot getEquippedSlot()
	{
		return equippedSlot;
	}
	
	@Override
	public StatEffect getEffectForSlot(ItemSlot slot)
	{
		StatEffect statEffect = new ItemEffect(slot);
		statEffect.setUniqueId(getUniqueId());
		for (StatModifier modifier : getModifiers())
		{
			StatEnum statToModify = modifier.getStat().getMainOrSubHandStat(slot);
			if (statToModify!=modifier.getStat())
			{
				StatModifier newModifier = modifier.clone();
				newModifier.setStat(statToModify);
				statEffect.add(newModifier);
			}
			else
			{
				statEffect.add(modifier);
			}
		}
				
		return statEffect;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean result = super.equals(o);
		if (result)
		{
			result = (((ItemEffect)o).getEquippedSlot().equals(equippedSlot));
		}
		return result;
	}

}
