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

public class RateModifier extends SimpleModifier
{
	@Override
	public int apply(int stat)
	{
		if(isBonus())
		{
			return Math.round(value * stat / 100f);
		}
		else
		{
			return Math.round(stat * (1 + value / 100f));
		}
	}

	@Override
	public StatModifierPriority getPriority()
	{
		return StatModifierPriority.MEDIUM;
	}
	
	public static RateModifier newInstance (StatEnum stat, int value, boolean isBonus)
	{
		RateModifier m = new RateModifier();
		m.setStat(stat);
		m.setValue(value);
		m.setBonus(isBonus);
		m.nextId();
		return m;
	}
}
