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
package com.aionemu.gameserver.model.legion;


/**
 * @author Simple
 * 
 */
public class LegionHistory
{
	private LegionHistoryType	legionHistoryType;
	private String				name	= "";

	// more but dunno what they mean yet

	public LegionHistory(LegionHistoryType legionHistoryType, String name)
	{
		this.legionHistoryType = legionHistoryType;
		this.name = name;
	}

	/**
	 * @return the legionHistoryType
	 */
	public int getLegionHistoryType()
	{
		return legionHistoryType.getHistoryId();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
}
