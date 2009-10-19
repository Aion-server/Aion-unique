/**
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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.Race;
/**
 * I have no idea wtf is this
 * 
 * @author -orz-
 * 
 */
public class SM_PLAY_MOVIE extends AionServerPacket
{
	//Don't be fooled with empty class :D
	//This packet is just sending opcode, without any content
	private int	unk = 0;
	private Player player;
		// 1.5.x sending 2 bytes
	public SM_PLAY_MOVIE(int unk)
	{
		this.unk = unk;
	}
	
	public SM_PLAY_MOVIE(Player player)
	{
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		

		writeC(buf, 0x01);
		writeD(buf, 0x00);
		writeH(buf, 0x00);
		if (unk != 0)
			writeC(buf, unk);
		else if (player.getCommonData().getRace() == Race.ELYOS)
			writeC(buf, 1); 
		else if (player.getCommonData().getRace() == Race.ASMODIANS)
			writeC(buf, 2); 
		writeD(buf, 0x00);
		writeC(buf, 0x00);
	}	
}
