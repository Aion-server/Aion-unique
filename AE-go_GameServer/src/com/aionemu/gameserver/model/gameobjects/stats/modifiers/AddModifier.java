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
import com.aionemu.gameserver.model.gameobjects.stats.StatModifierPriority;

/**
 * @author xavier
 * 
 */
public class AddModifier extends SimpleModifier
{
	@Override
	public int apply(int stat)
	{
		if(isBonus())
		{
			return Math.round(value);
		}
		else
		{
			return Math.round(stat + value);
		}
	}

	@Override
	public StatModifierPriority getPriority()
	{
		return StatModifierPriority.LOW;
	}
	
	public static AddModifier newInstance (StatEnum stat, int value, boolean isBonus)
	{
		AddModifier m = new AddModifier();
		m.setStat(stat);
		m.setValue(value);
		m.setBonus(isBonus);
		m.nextId();
		return m;
	}
}
