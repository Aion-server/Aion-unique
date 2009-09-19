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
package com.aionemu.gameserver.network.aion.clientpackets;


import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * Received when a player sets his status in the social search window
 * @author Ben
 *
 */
public class CM_SEARCH_STATUS extends AionClientPacket
{
	
	int statusCode;
	public CM_SEARCH_STATUS(int opcode)
	{
		super(opcode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readImpl()
	{
		readC(); // unk
		this.statusCode = readC();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void runImpl()
	{
		getConnection().getActivePlayer().setLookingForGroup(statusCode == 2);
	}
}
