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

import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionMember;

/**
 * This class is holding information about player, that is displayed on char selection screen, such as: player
 * commondata, player's appearance and creation/deletion time.
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
	private Storage				inventory;
	private Equipment			equipment;
	private Timestamp			creationDate;
	private Timestamp			deletionDate;
	private LegionMember		legionMember;

	public PlayerAccountData(PlayerCommonData playerCommonData, PlayerAppearance appereance, Storage inventory,
		Equipment equipment, LegionMember legionMember)
	{
		this.playerCommonData = playerCommonData;
		this.appereance = appereance;
		this.inventory = inventory;
		this.equipment = equipment;
		this.legionMember = legionMember;
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
	public Storage getInventory()
	{
		return inventory;
	}

	public Equipment getEquipment()
	{
		return equipment;
	}

	/**
	 * @param timestamp
	 */
	public void setCreationDate(Timestamp creationDate)
	{
		this.creationDate = creationDate;
	}

	/**
	 * @return the legionMember
	 */
	public Legion getLegion()
	{
		return legionMember.getLegion();
	}
	
	/**
	 * Returns true if player is a legion member
	 * @return true or false
	 */
	public boolean isLegionMember()
	{
		return legionMember != null;
	}
}
