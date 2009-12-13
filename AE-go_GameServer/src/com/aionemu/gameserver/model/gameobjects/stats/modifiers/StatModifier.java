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
	private int						effectId;
	private boolean					isBonus;
	private int						id;

	protected StatModifier(StatEnum stat, StatModifierPriority priority, boolean isBonus)
	{
		this.priority = priority;
		this.isBonus = isBonus;
		this.stat = stat;
		this.effectId = -1;
		MODIFIER_ID = (MODIFIER_ID+1)%Integer.MAX_VALUE;
		this.id = MODIFIER_ID;
	}

	public int getEffectId()
	{
		return effectId;
	}

	public void setEffectId(int effectId)
	{
		if ((this.effectId!=-1)&&(effectId!=this.effectId))
		{
			throw new IllegalStateException("effect id already set to "+this.effectId+" but you tried to set it to "+effectId);
		}
		this.effectId = effectId;
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
		if(result == 0)
		{
			result = id-other.getId();
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
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("type:" + getClass().getSimpleName() + ",");
		sb.append("stat:" + stat + ",");
		sb.append("effect:"+effectId+",");
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
