/**
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
package com.aionemu.gameserver.model.gameobjects;

/**
 * This is the base class for all "in-game" objects, that player can interact with, such as: npcs, monsters, players,
 * items.<br>
 * <br>
 * Each AionObject is uniquely identified by objectId.
 * 
 * @author -Nemesiss-
 * @author SoulKeeper
 * 
 */
public abstract class AionObject
{
	/**
	 * Unique id, for all game objects such as: items, players, monsters.
	 */
	private int	objectId;

	public AionObject(int objId)
	{
		this.objectId = objId;
	}

	/**
	 * Returns unique ObjectId of AionObject
	 * 
	 * @return Int ObjectId
	 */
	public int getObjectId()
	{
		return objectId;
	}

	/**
	 * Returns name of the object.<br>
	 * Unique for players, common for NPCs, items, etc
	 * 
	 * @return name of the object
	 */
	public abstract String getName();
}
