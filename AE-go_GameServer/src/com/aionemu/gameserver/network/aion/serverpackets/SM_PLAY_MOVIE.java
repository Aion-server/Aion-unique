/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
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
	private int	type = 1;
	private int	movieId = 0;
		// 1.5.x sending 2 bytes
	public SM_PLAY_MOVIE(int type, int movieId)
	{
		this.type = type;
		this.movieId = movieId;
	}
	
	public SM_PLAY_MOVIE(Player player)
	{
		if (player.getCommonData().getRace() == Race.ELYOS)
			this.movieId = 1;
		else
			this.movieId = 2;
	}
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		

		writeC(buf, type);
		writeD(buf, 0x00);
		writeH(buf, 0x00);
		writeC(buf, movieId);
		writeD(buf, 0x00);
		writeC(buf, 0x00);
	}	
}
