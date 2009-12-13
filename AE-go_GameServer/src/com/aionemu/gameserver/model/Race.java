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

package com.aionemu.gameserver.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Basic enum with races.<br>
 * I believe that NPCs will have their own rances, so it's quite comfortable to have it in the same place
 * 
 * @author SoulKeeper
 */

@XmlEnum
public enum Race
{
	/**
	 * Playable race
	 */
	ELYOS(0),

	/**
	 * Playable races
	 */
	ASMODIANS(1);

	/**
	 * id of race
	 */
	private int	raceId;

	/**
	 * Constructor.
	 * 
	 * @param raceId
	 *            id of the message
	 */
	private Race(int raceId)
	{
		this.raceId = raceId;
	}

	/**
	 * Get id of this race.
	 * 
	 * @return race id
	 */
	public int getRaceId()
	{
		return raceId;
	}
}
