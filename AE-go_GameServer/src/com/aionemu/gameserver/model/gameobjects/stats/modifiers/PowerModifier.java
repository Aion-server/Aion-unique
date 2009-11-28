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

import com.aionemu.gameserver.model.gameobjects.AionObject;

/**
 * @author xavier
 *
 */
public class PowerModifier extends StatModifier
{
	private int value;
	
	public PowerModifier (AionObject owner, int minDamages, int maxDamages) {
		super(owner,StatModifierPriority.HIGH,StatModifierSign.PLUS,false);
		this.value = Math.round((minDamages+maxDamages)/2f);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier#apply(int)
	 */
	@Override
	public int apply(int stat)
	{
		if (isBonus()) {
			throw new IllegalArgumentException("cannot apply power modifier on bonus stat");
		}
		return stat+value;
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(",value:"+value);
		return sb.toString();
	}
}
