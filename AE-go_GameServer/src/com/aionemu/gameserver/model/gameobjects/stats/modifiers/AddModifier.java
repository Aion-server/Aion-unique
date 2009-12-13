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
public class AddModifier extends StatModifier
{
	private int value;
	/**
	 * @param modifiedStat
	 * @param isBonus
	 */
	public AddModifier(StatEnum stat, int value, boolean isBonus)
	{
		super(stat,StatModifierPriority.MEDIUM, isBonus);
		this.value = value;
	}
	
	@Override
	public int apply(int stat)
	{
		if (isBonus()) {
			return Math.round(value);
		} else {
			return Math.round(stat + value);
		}
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(",value:"+value);
		return sb.toString();
	}

	@Override
	public StatModifier clone()
	{
		AddModifier copy = new AddModifier (getStat(),value,isBonus());
		return copy;
	}
}
