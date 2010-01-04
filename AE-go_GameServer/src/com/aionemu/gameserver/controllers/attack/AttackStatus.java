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
	PARRY(2),
	BLOCK(4),
	RESIST(6),
	BUF(8),// ??
	NORMALHIT(10),
	CRITICAL(12);
	
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
