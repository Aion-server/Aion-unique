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
package com.aionemu.gameserver.model.templates.modifier;

import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.SubModifier;

/**
 * @author xavier
 *
 */
public class SubModifierTemplate extends SimpleModifierTemplate
{

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.modifier.ModifierTemplate#getModifier()
	 */
	@Override
	public StatModifier getModifier()
	{
		return new SubModifier (getStat(),getValue(),isBonus());
	}

}
