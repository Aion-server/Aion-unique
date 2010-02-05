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
package com.aionemu.gameserver.network.aion.serverpackets;


import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.BlockList;
import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Packet responsible for telling a player his block list
 * @author Ben
 *
 */
public class SM_BLOCK_LIST extends AionServerPacket
{
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		BlockList list = con.getActivePlayer().getBlockList();
		writeH(buf,list.getSize());
		writeC(buf, 0); //Unk
		for (BlockedPlayer player : list)
		{
			writeS(buf, player.getName());
			writeS(buf,	player.getReason());
		}
	}
}
