/*
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
package com.aionemu.gameserver.model.account;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * This class is holding information about player, that is displayed on char selection screen, such as: player
 * commondata, player's appereance and creation/deletion time.
 * 
 * @see PlayerCommonData
 * @see PlayerAppearance
 * @author Luno
 * 
 */
public class PlayerAccountData
{
	private PlayerCommonData	playerCommonData;
	private PlayerAppearance	appereance;
	private Inventory			inventory;
	private Timestamp			creationDate;
	private Timestamp			deletionDate;

	public PlayerAccountData(PlayerCommonData playerCommonData, PlayerAppearance appereance, Inventory inventory)
	{
		this.playerCommonData = playerCommonData;
		this.appereance = appereance;
		this.inventory = inventory;
	}

	public Timestamp getCreationDate()
	{
		return creationDate;
	}

	/**
	 * Sets deletion date.
	 * 
	 * @param deletionDate
	 */
	public void setDeletionDate(Timestamp deletionDate)
	{
		this.deletionDate = deletionDate;
	}

	/**
	 * Get deletion date.
	 * 
	 * @return Timestamp date when char should be deleted.
	 */
	public Timestamp getDeletionDate()
	{
		return deletionDate;
	}

	/**
	 * Get time in seconds when this player will be deleted ( 0 if player was not set to be deleted )
	 * 
	 * @return deletion time in seconds
	 */
	public int getDeletionTimeInSeconds()
	{
		return deletionDate == null ? 0 : (int) (deletionDate.getTime() / 1000);
	}

	public PlayerCommonData getPlayerCommonData()
	{
		return playerCommonData;
	}

	public PlayerAppearance getAppereance()
	{
		return appereance;
	}

	/**
	 * @return the inventory
	 */
	protected Inventory getInventory()
	{
		return inventory;
	}

	/**
	 * @param timestamp
	 */
	public void setCreationDate(Timestamp creationDate)
	{
		this.creationDate = creationDate;
	}
}
