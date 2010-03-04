/*
 * This file is part of aion-unique <aionu-unique.com>.
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
package com.aionemu.gameserver.dao;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;

/**
 * @author ATracer
 */
public abstract class InventoryDAO implements IDFactoryAwareDAO
{
	/**
	 * @param player
	 * @param StorageType
	 * @return Storage
	 */
	public abstract Storage loadStorage(Player player, StorageType storageType);

	/**
	 * @param player
	 * @return Equipment
	 */
	public abstract Equipment loadEquipment(Player player);

	/**
	 * @param inventory
	 */
	public abstract boolean store(Player player);

	/**
	 * @param item
	 */
	public abstract boolean store(Item item, int playerId);

	/**
	 * @param playerId
	 */
	public abstract boolean deletePlayerItems(int playerId);

	@Override
	public String getClassName()
	{
		return InventoryDAO.class.getName();
	}

}
