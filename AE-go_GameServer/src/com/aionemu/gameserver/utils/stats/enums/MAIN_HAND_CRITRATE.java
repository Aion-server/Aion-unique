/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.utils.stats.enums;

/**
 * @author ATracer
 *
 */
public enum MAIN_HAND_CRITRATE
{
	WARRIOR(2),
	GLADIATOR(2),
	TEMPLAR(2),
	SCOUT(3),
	ASSASSIN(3),
	RANGER(3),
	MAGE(1),
	SORCERER(2),
	SPIRIT_MASTER(2),
	PRIEST(2),
	CLERIC(2),
	CHANTER(1);
	
	private int value;
	
	private MAIN_HAND_CRITRATE(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}