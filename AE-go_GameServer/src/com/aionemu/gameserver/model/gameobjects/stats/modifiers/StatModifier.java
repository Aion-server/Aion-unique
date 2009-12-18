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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.gameobjects.stats.StatModifierPriority;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Modifier")
public abstract class StatModifier implements Comparable<StatModifier>
{
	@XmlAttribute
	private StatEnum name;
	
	@XmlAttribute
	private boolean bonus;
	
	protected static int MODIFIER_ID = 0;
	
	protected int id;
	
	public StatModifier()
	{
		nextId();
	}
	
	protected void setStat(StatEnum stat)
	{
		this.name = stat;
	}
	
	protected void setBonus(boolean bonus)
	{
		this.bonus = bonus;
	}
	
	protected void nextId()
	{
		MODIFIER_ID = (MODIFIER_ID + 1) % Integer.MAX_VALUE;
		id = MODIFIER_ID;
	}
	
	public StatEnum getStat ()
	{
		return name;
	}
	
	public boolean isBonus ()
	{
		return bonus;
	}
	
	@Override
	public int compareTo(StatModifier o)
	{
		int result = getPriority().getValue() - o.getPriority().getValue();
		if (result==0)
		{
			result = id - o.id;
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean result = (o!=null);
		result = (result)&&(o instanceof StatModifier);
		result = (result)&&(((StatModifier)o).id==id);
		return result;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()+",");
		sb.append("i:"+id+",");
		sb.append("s:"+name+",");
		sb.append("b:"+bonus);
		return sb.toString();
	}
	
	public abstract int apply (int value);
	
	public abstract StatModifierPriority getPriority();
}
