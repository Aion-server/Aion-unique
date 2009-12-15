/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.stats;

import java.util.Collection;
import java.util.UUID;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifierContainer;

/**
 * @author xavier
 * 
 */
public class StatEffect extends StatModifierContainer implements Comparable<StatEffect>
{
	private int	id;
	private static int EFFECT_ID = 0;
	
	public StatEffect()
	{
		super();
		EFFECT_ID = (EFFECT_ID+1)%Integer.MAX_VALUE;
		this.id = UUID.randomUUID().hashCode();
	}

	public int getUniqueId()
	{
		return id;
	}

	protected void setUniqueId(int id)
	{
		this.id = id;
	}

	@Override
	public void add(StatModifier modifier)
	{
		super.add(modifier);
		modifier.setOwner(this);
	}

	@Override
	public void addAll(Collection<? extends StatModifier> modifiers)
	{
		for(StatModifier modifier : modifiers)
		{
			add(modifier);
		}
	}

	@Override
	public String toString()
	{
		String str = "effect#" + id + ":" + super.toString();
		return str;
	}

	public ItemEffect getEffectForSlot(ItemSlot slot)
	{
		ItemEffect statEffect = new ItemEffect(slot);
		for (StatModifier modifier : getModifiers())
		{
			StatEnum statToModify = modifier.getStat().getMainOrSubHandStat(slot);
			StatModifier newModifier = modifier.clone();
			newModifier.setStat(statToModify);
			statEffect.add(newModifier);
		}
		return statEffect;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean result = true;
		result = result&&(o instanceof StatEffect);
		result = result&&(((StatEffect)o).getUniqueId()==id);
		return result;
	}
	
	@Override
	public int compareTo(StatEffect o)
	{
		int result;
		if (o==null) {
			result = id;
		} else {
			result = id-o.getUniqueId();
		}
		return result;
	}
}
