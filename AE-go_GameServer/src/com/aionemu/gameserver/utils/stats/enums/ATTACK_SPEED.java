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
public enum ATTACK_SPEED
{
	WARRIOR(1500),
	GLADIATOR(1500),
	TEMPLAR(1500),
	SCOUT(1500),
	ASSASSIN(1500),
	RANGER(1500),
	MAGE(1500),
	SORCERER(1500),
	SPIRIT_MASTER(1500),
	PRIEST(1500),
	CLERIC(1500),
	CHANTER(1500);
	
	private int value;
	
	private ATTACK_SPEED(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}