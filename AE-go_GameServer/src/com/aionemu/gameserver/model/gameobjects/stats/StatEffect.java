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

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 * 
 */
public class StatEffect
{
	private static final Logger		log	= Logger.getLogger(StatEffect.class);

	private TreeSet<StatModifier>	modifiers;
	int								baseValue;

	public StatEffect()
	{
		this.modifiers = new TreeSet<StatModifier>();
	}

	public void add(StatModifier modifier)
	{
		if(!modifiers.add(modifier))
		{
			log.error("Cannot add modifier " + modifier + " !");
		}
	}

	public void addAll(Collection<? extends StatModifier> modifiers)
	{
		if(!this.modifiers.addAll(modifiers))
		{
			log.error("Cannot add " + modifiers.size() + " modifiers !");
		}
	}

	public TreeSet<StatModifier> getModifiers()
	{
		return modifiers;
	}

	public void endEffects(int ownerId)
	{
		ArrayList<StatModifier> toRemove = new ArrayList<StatModifier>();
		for(StatModifier modifier : modifiers)
		{
			if(modifier.getOwnerId() == ownerId)
			{
				toRemove.add(modifier);
			}
		}

		if(toRemove.size() > 0)
		{
			if(!modifiers.removeAll(toRemove))
			{
				log.error("Cannot remove " + toRemove.size() + "modifiers from " + ownerId);
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{count:" + modifiers.size());
		int index = 0;
		for(StatModifier modifier : modifiers)
		{
			sb.append(',');
			sb.append("[" + index + "]:");
			sb.append(modifier.toString());
			index++;
		}
		sb.append("}");
		return sb.toString();
	}
}
