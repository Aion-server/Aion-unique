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
	NPC_IDLE(65),//for npc
	//confirmed in SM_EMOTION
	STANDING(1),
	FLY_TELEPORT(2),
	FLYING(3),
	RESTING(5),
	DEAD(7),
	PRIVATE_SHOP(11),
	WALKING(65),
	POWERSHARD(129);

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
