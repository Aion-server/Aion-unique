/*
 * This file is part of aion-emu <aion-unique.org>.
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
package com.aionemu.gameserver.controllers.attack;

/**
 * @author ATracer
 *
 */
public enum AttackStatus
{
	DODGE(0),
	OFFHAND_DODGE(1),
	PARRY(2),
	OFFHAND_PARRY(3),
	BLOCK(4),
	OFFHAND_BLOCK(5),
	RESIST(6),
	OFFHAND_RESIST(7),
	BUF(8),// ??
	OFFHAND_BUF(9),
	NORMALHIT(10),
	OFFHAND_NORMALHIT(11),
	CRITICAL_DODGE(-64),
	CRITICAL_PARRY(-62),
	CRITICAL_BLOCK(-60),
	CRITICAL_RESIST(-58),
	CRITICAL(-54),
	OFFHAND_CRITICAL_DODGE(-47),
	OFFHAND_CRITICAL_PARRY(-45),
	OFFHAND_CRITICAL_BLOCK(-43),
	OFFHAND_CRITICAL_RESIST(-41),
	OFFHAND_CRITICAL(-37);

	private int _type;

	private AttackStatus(int type)
	{
		this._type = type;
	}

	public int getId()
	{
		return _type;
	}
}
