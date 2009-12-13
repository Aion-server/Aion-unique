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
public class MeanModifier extends StatModifier
{
	private int value;
	
	public MeanModifier(StatEnum stat, int min, int max, boolean isBonus)
	{
		super(stat,StatModifierPriority.HIGH,isBonus);
		this.value = Math.round((min+max)/2.0f);
	}
	
	private MeanModifier(StatEnum stat, int value, boolean isBonus)
	{
		super(stat,StatModifierPriority.HIGH,isBonus);
		this.value = value;
	}
	
	@Override
	public int apply(int stat)
	{
		return value;
	}

	@Override
	public StatModifier clone()
	{
		MeanModifier copy = new MeanModifier (getStat(),value,isBonus());
		return copy;
	}

}
