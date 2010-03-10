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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Sweetkr
 *
 */
public class SM_CUBE_UPDATE extends AionServerPacket
{
	private Player player;
	private int unk;

	/**
	 * Constructs new <tt>SM_CUBE_UPDATE</tt> packet
	 * 
	 * @param player
	 */
	public SM_CUBE_UPDATE(Player player, int unk)
	{
		this.player = player;
		this.unk = unk;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, unk); // unk
		writeC(buf, 0); // unk
		switch(unk)
		{
			case 0:
				writeD(buf, player.getInventory().size());
				writeC(buf, player.getCubeSize()); // cube size from npc (so max 5 for now)
				writeC(buf, 0); // cube size from quest (so max 2 for now)
				writeC(buf, 0); // unk
				break;
			default:
				break;
		}
	}
}
