/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

/**
 * @author ATracer
 *
 */
public class PlayerGameStats extends CreatureGameStats
{
	private int itemId; //TODO remove
	
	private int itemNameId; //TODO remove

	public PlayerGameStats()
	{
		super();
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return the itemNameId
	 */
	public int getItemNameId()
	{
		return itemNameId;
	}

	/**
	 * @param itemNameId the itemNameId to set
	 */
	public void setItemNameId(int itemNameId)
	{
		this.itemNameId = itemNameId;
	}
}
