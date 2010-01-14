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
package com.aionemu.gameserver.model.gameobjects.stats.id;

import com.aionemu.gameserver.model.gameobjects.stats.StatEffectType;

/**
 * @author xavier
 *
 */
public class StatEffectId implements Comparable<StatEffectId>
{
	private int id;
	private StatEffectType type;
	
	protected StatEffectId(int id, StatEffectType type)
	{
		this.id = id;
		this.type = type;
	}
	
	public static StatEffectId getInstance(int id, StatEffectType type)
	{
		return new StatEffectId(id,type);
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean result = true;
		result = (result)&&(o!=null);
		result = (result)&&(o instanceof StatEffectId);
		result = (result)&&(((StatEffectId)o).id==id);
		result = (result)&&(((StatEffectId)o).type.getValue()==type.getValue());
		return result;
	}
	
	@Override
	public int compareTo(StatEffectId o)
	{
		int result = 0;
		if (o==null)
		{
			result = id;
		}
		else
		{
			result = type.getValue() - o.type.getValue();
			if (result==0)
			{
				result = id - o.id;
			}
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		final String str = "id:"+id+",type:"+type;
		return str;
	}
}
