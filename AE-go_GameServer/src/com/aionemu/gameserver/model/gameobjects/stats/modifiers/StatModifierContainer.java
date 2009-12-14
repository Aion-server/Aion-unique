/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats.modifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;

/**
 * @author xavier
 *
 */
public class StatModifierContainer
{
	private TreeSet<StatModifier> modifiers;
	
	public StatModifierContainer ()
	{
		this.modifiers = new TreeSet<StatModifier>();
	}
	
	public void add(StatModifier modifier)
	{
		modifiers.add(modifier);
	}

	public void addAll(Collection<? extends StatModifier> modifiers)
	{
		this.modifiers.addAll(modifiers);
	}
	
	public Collection<StatModifier> getModifiers()
	{
		return modifiers;
	}
	
	public void removeModifiersOfEffect(StatEffect effect)
	{
		ArrayList<StatModifier> modifiersToRemove = new ArrayList<StatModifier>();
		
		for(StatModifier modifier : modifiers)
		{
			if (modifier.getOwner().equals(effect))
			{
				modifiersToRemove.add(modifier);
			}
		}
		modifiers.removeAll(modifiersToRemove);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{count:" + modifiers.size() +",\n");
		int index = 0;
		for(StatModifier modifier : modifiers)
		{
			sb.append("[" + index + "]:");
			sb.append(modifier.toString()+"\n");
			index++;
		}
		sb.append("}");
		return sb.toString();
	}
}
