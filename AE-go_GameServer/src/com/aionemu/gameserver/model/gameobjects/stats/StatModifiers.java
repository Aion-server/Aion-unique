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
package com.aionemu.gameserver.model.gameobjects.stats;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 *
 */
public class StatModifiers
{
	private FastMap<StatModifierPriority,FastList<StatModifier>> modifiers;
	
	public StatModifiers()
	{
		modifiers = new FastMap<StatModifierPriority,FastList<StatModifier>>();
	}
	
	public boolean add(StatModifier modifier)
	{
		if (!modifiers.containsKey(modifier.getPriority()))
		{
			modifiers.put(modifier.getPriority(), new FastList<StatModifier>());
		}
		return modifiers.get(modifier.getPriority()).add(modifier);
	}
	
	public FastList<StatModifier> getModifiers(StatModifierPriority priority)
	{
		if (!modifiers.containsKey(priority))
		{
			modifiers.put(priority, new FastList<StatModifier>());
		}
		return modifiers.get(priority);
	}
}
