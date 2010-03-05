/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.dao;

import java.sql.Timestamp;
import java.util.List;

import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.world.World;

/**
 * Class that is responsible for storing/loading player data
 * 
 * @author SoulKeeper, Saelya
 */

public abstract class PlayerDAO implements IDFactoryAwareDAO
{

	/**
	 * Returns true if name is used, false in other case
	 * 
	 * @param name
	 *            name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isNameUsed(String name);

	/**
	 * Stores player to db
	 * @param player
	 */
	public abstract void storePlayer(Player player);

	/**
	 * This method is used to store only newly created characters
	 * 
	 * @param pcd
	 *            player to save in database
	 * @return true if every things went ok.
	 */
	public abstract boolean saveNewPlayer(PlayerCommonData pcd, int accountId, String accountName);

	public abstract PlayerCommonData loadPlayerCommonData(int playerObjId, World world, PlayerInitialData playerInitialData);

	/**
	 * Removes player and all related data (Done by CASCADE DELETION)
	 * 
	 * @param playerId
	 *            player to delete
	 */
	public abstract void deletePlayer(int playerId);

	public abstract void updateDeletionTime(int objectId, Timestamp deletionDate);

	public abstract void storeCreationTime(int objectId, Timestamp creationDate);

	/**
	 * Loads creation and deletion time from database, for particular player and sets these values in given
	 * <tt>PlayerAccountData</tt> object.
	 * 
	 * @param acData
	 */
	public abstract void setCreationDeletionTime(PlayerAccountData acData);

	/**
	 * Returns a list of objectId of players that are on the account with given accountId
	 * 
	 * @param accountId
	 * @return List<Integer>
	 */
	public abstract List<Integer> getPlayerOidsOnAccount(int accountId);

	/**
	 * Stores the last online time
	 * @param objectId Object ID of player to store
	 * @param lastOnline Last online time of player to store
	 */
	public abstract void storeLastOnlineTime(final int objectId, final Timestamp lastOnline);
	
	/**
	 * Store online or offline player status
	 * 
	 * @param player
	 * @param online
	 */
	public abstract void onlinePlayer(final Player player, final boolean online);
	
	/**
	 * Set all players offline status
	 * 
	 * @param online
	 */
	public abstract void setPlayersOffline(final boolean online);
	
	/**
	 * Identifier name for all PlayerDAO classes
	 * 
	 * @return PlayerDAO.class.getName()
	 */
	@Override
	public final String getClassName()
	{
		return PlayerDAO.class.getName();
	}
}
