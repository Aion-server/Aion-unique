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
 * @author ATracer
 *
 */
public class SkillEffectId extends StatEffectId
{
	private int effectId;
	private int effectOrder;

	private SkillEffectId(int skillId, int effectId, int effectOrder)
	{
		super(skillId,StatEffectType.SKILL_EFFECT);
		this.effectId = effectId;
		this.effectOrder = effectOrder;
	}

	public static SkillEffectId getInstance(int skillId, int effectId, int effectOrder)
	{
		return new SkillEffectId(skillId, effectId, effectOrder);
	}

	@Override
	public boolean equals(Object o)
	{
		boolean result = super.equals(o);
		result = (result)&&(o!=null);
		result = (result)&&(o instanceof SkillEffectId);
		result = (result)&&(((SkillEffectId)o).effectId==effectId);
		result = (result)&&(((SkillEffectId)o).effectOrder==effectOrder);
		return result;
	}

	@Override
	public int compareTo(StatEffectId o)
	{
		int result = super.compareTo(o);
		if (result==0)
		{
			if (o instanceof SkillEffectId)
			{
				result = effectId - ((SkillEffectId)o).effectId;
				if(result == 0)
					result = effectOrder - ((SkillEffectId)o).effectOrder;
			}
		}
		return result;
	}

	@Override
	public String toString()
	{
		final String str = super.toString()+",effectId:"+effectId+",effectOrder:"+effectOrder;
		return str;
	}

	/**
	 * @return the effectId
	 */
	public int getEffectId()
	{
		return effectId;
	}

	/**
	 * @return the effectOrder
	 */
	public int getEffectOrder()
	{
		return effectOrder;
	}

	
}
