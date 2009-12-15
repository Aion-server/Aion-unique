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
package com.aionemu.gameserver.model.gameobjects.stats.modifiers;

import com.aionemu.gameserver.model.gameobjects.stats.ItemEffect;
import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;

/**
 * @author xavier
 * 
 */
public abstract class StatModifier implements Comparable<StatModifier>
{
	private static int				MODIFIER_ID = 0;
	private StatEnum				stat;
	private StatModifierPriority	priority;
	private StatEffect				owner;
	private boolean					isBonus;
	private int						id;

	protected StatModifier(StatEnum stat, StatModifierPriority priority, boolean isBonus)
	{
		this.priority = priority;
		this.isBonus = isBonus;
		this.stat = stat;
		MODIFIER_ID = (MODIFIER_ID+1)%Integer.MAX_VALUE;
		this.id = MODIFIER_ID;
	}

	public StatEffect getOwner()
	{
		return owner;
	}

	public void setOwner(StatEffect owner)
	{
		this.owner = owner;
	}
	
	public boolean isBonus()
	{
		return isBonus;
	}

	public StatModifierPriority getPriority()
	{
		return priority;
	}
	
	public StatEnum getStat()
	{
		return stat;
	}

	private int getId()
	{
		return id;
	}
	
	@Override
	public int compareTo(StatModifier other)
	{
		int result = (other.getPriority().getValue() - this.priority.getValue());
		if (result==0) {
			result += id-other.getId();
			if (result==0) {
				if (owner!=null) {
					result += owner.compareTo(other.getOwner());
				}
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean result;
		result = (o!=null);
		result = (result)&&(o instanceof StatModifier);
		result = (result)&&(((StatModifier)o).getId()==id);
		result = (result)&&(((StatModifier)o).getOwner().equals(owner));
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("type:" + getClass().getSimpleName() + ",");
		sb.append("stat:" + stat + ",");
		if (owner!=null)
		{
			sb.append("effect:"+owner.getUniqueId()+",");
			if (owner instanceof ItemEffect)
			{
				sb.append("slot:"+((ItemEffect)owner).getEquippedSlot()+",");
			}
		}
		sb.append("id:" + id + ",");
		sb.append("priority:" + priority + ",");
		sb.append("bonus:" + isBonus);
		return sb.toString();
	}

	public abstract StatModifier clone();
	
	public abstract int apply(int stat);

	public void setStat(StatEnum stat)
	{
		this.stat = stat;
	}
}
