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
package com.aionemu.gameserver.skillengine.effect;

/**
 * @author ATracer
 *
 */
public enum EffectId
{
	BUFF(0),
	ROOT(1),
	SLEEP(2),
	HIDE(3),
	HEAL_OT(4),
	DAMAGE_OT(5);
	
	private int effectId;
	
	private EffectId(int effectId)
	{
		this.effectId = effectId;
	}
	
	public int getEffectId()
	{
		return effectId;
	}
}
