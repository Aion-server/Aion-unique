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
public class PercentModifier extends StatModifier
{
	private float value;
	/**
	 * @param modifiedStat
	 * @param isBonus
	 */
	public PercentModifier(int ownerId, boolean isBonus, String percent, int sign)
	{
		super(ownerId,StatModifierPriority.LOW, StatModifierSign.get(sign), isBonus);
		
		if (percent.contains("%")) {
			this.value = Integer.parseInt(percent.substring(0, percent.indexOf("%")))/100f;
		} else {
			throw new IllegalArgumentException("Percentage value "+percent+" is not valid");
		}
	}
	
	public PercentModifier(int ownerId, boolean isBonus, String percent) 
	{
		this(ownerId,isBonus,percent,1);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier#apply()
	 */
	@Override
	public int apply(int stat)
	{
		if (isBonus()) {
			return Math.round(getSign()*value*stat);
		} else {
			return Math.round(stat * (1 + getSign()*value));
		}
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(",value:"+value);
		return sb.toString();
	}
}
