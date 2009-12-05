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
package com.aionemu.gameserver.model;

/**
 * @author Luno
 *
 */
public enum NpcType
{
	/** These are regular monsters */
	ATTACKABLE(0),
	/** These are monsters that are pre-aggressive */
	AGGRESSIVE(8),
	/** These are non attackable NPCs */
	NON_ATTACKABLE(38),
	/** Binding obelisks **/
	RESURRECT(38), //TODO check
	/** Mail boxes **/
	POSTBOX(38),
	/** Action item **/
	USEITEM(38);
	
	private int someClientSideId;
	
	private NpcType(int id)
	{
		this.someClientSideId = id;
	}
	
	public int getId()
	{
		return someClientSideId;
	}
}
