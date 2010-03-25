/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.model.gameobjects.state;

/**
 * @author ATracer, Sweetkr
 *
 */
public enum CreatureState
{
	NPC_IDLE(1<<6), // 64 (for npc)

	FLIGHT_TELEPORT(1<<1), // 2
	CHAIR(3<<1), // 6
	LOOTING(3<<2), // 12

	//confirmed in SM_EMOTION
	ACTIVE(1), // basic 1
	FLYING(1<<1), // 2
	RESTING(1<<2), // 4
	DEAD(3<<1), // 6
	//UNKNOWN8(1<<3), // 8
	PRIVATE_SHOP(5<<1), // 10
	//UNKNOWN16(1<<4), // 16
	WEAPON_EQUIPPED(1<<5), // 32
	WALKING(1<<6), // 64
	POWERSHARD(1<<7), // 128
	TREATMENT(1<<8), // 256
	GLIDING(1<<9); // 512
	/**
	 * Standing, path flying, 
	 * free flying, riding, 
	 * sitting, sitting on chair, 
	 * dead, fly dead, private shop, 
	 * looting, fly looting, default
	 */

	private int id;

	private CreatureState(int id)
	{
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}
}
