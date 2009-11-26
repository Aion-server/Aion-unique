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
package com.aionemu.gameserver.model.gameobjects.stats.modifiers;

import com.aionemu.gameserver.model.gameobjects.AionObject;

/**
 * @author xavier
 * 
 */
public abstract class StatModifier implements Comparable<StatModifier>
{
	private static int				MODIFIER_ID	= 1;
	private StatModifierPriority	priority;
	private StatModifierSign		sign;
	private boolean					isBonus;
	private AionObject				owner;
	private int						id;

	protected StatModifier(AionObject owner, StatModifierPriority priority, StatModifierSign sign, boolean isBonus)
	{
		this.priority = priority;
		this.isBonus = isBonus;
		this.sign = sign;
		this.owner = owner;
		this.id = MODIFIER_ID++;
	}

	protected StatModifier(AionObject owner)
	{
		this(owner, StatModifierPriority.LOW, StatModifierSign.PLUS, false);
	}

	public AionObject getOwner()
	{
		return owner;
	}

	public boolean isBonus()
	{
		return isBonus;
	}

	protected int getSign()
	{
		return sign.get();
	}

	protected int getId()
	{
		return id;
	}

	public StatModifierPriority getPriority()
	{
		return priority;
	}

	@Override
	public int compareTo(StatModifier other)
	{
		int result = (other.getPriority().getValue() - this.priority.getValue());
		if(result == 0)
		{
			result = this.getId()-other.getId();
		}
		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean result;
		result = (o!=null);
		result = (result)&&(o instanceof StatModifier);
		result = (result)&&(((StatModifier)o).getId()==this.getId());
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("type:" + this.getClass().getSimpleName() + ",");
		sb.append("id:" + id + ",");
		sb.append("priority:" + priority + ",");
		sb.append("bonus:" + isBonus + ",");
		sb.append("sign:" + sign);
		return sb.toString();
	}

	public abstract int apply(int stat);
}
