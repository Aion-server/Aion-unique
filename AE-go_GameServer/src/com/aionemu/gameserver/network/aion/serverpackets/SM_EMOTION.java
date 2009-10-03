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

package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Emotion packet
 * 
 * @author SoulKeeper
 */
public class SM_EMOTION extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_EMOTION.class);

	/**
	 * Object id of emotion sender
	 */
	private int	senderObjectId;
	

	/**
	 * Some unknown variable
	 */
	private int	unknown;
	

	/**
	 * ID of emotion
	 */
	private int	emotionId;

	/**
	 * Constructs new server packet with specified opcode
	 * 
	 * @param senderObjectId
	 *            who sended emotion
	 * @param unknown
	 *            Dunno what it is, can be 0x10 or 0x11
	 * @param emotionId
	 *            emotion to play
	 */
	public SM_EMOTION(int senderObjectId, int unknown, int emotionId)
	{
		this.senderObjectId = senderObjectId;
		this.emotionId = emotionId;
		this.unknown = unknown;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, senderObjectId);
		writeC(buf, unknown);
		
		if (unknown == 13 ){
			//emote die
			writeD(buf, 0x07); // unknown
			writeC(buf, 0xC0); // unknown
			writeC(buf, 0x40); // unknown
			writeD(buf, emotionId);
		}
		else if (unknown == 35 )
		{
			//emote startloop
			writeD(buf, 44); // unknown
			writeC(buf, 0xc0); // unknown
			writeC(buf, 0x40); // unknown
		}
		else if (unknown == 30 )
		{
			//emote startloop
			writeD(buf, 33); // unknown
			writeC(buf, 0xe0); // unknown
			writeC(buf, 0x40); // unknown
			writeH(buf, 2142); // unknown
			writeH(buf, 2142); // unknown
			
		}
		else if (unknown == 19 )
		{
			//emote startloop
			writeD(buf, 1); // unknown
			writeC(buf, 0xe0); // unknown
			writeC(buf, 0x40); // unknown
			
		}
		else if (unknown == 36 )
		{
			//emote endloop
			writeD(buf, 33); // unknown
			writeC(buf, 0xc0); // unknown
			writeC(buf, 0x40); // unknown
		}
		else
		{
		
		writeC(buf, 0x01); // unknown
		writeC(buf, 0x00); // unknown
		writeH(buf, 0x00); // unknown
		writeC(buf, 0xC0); // unknown
		writeC(buf, 0x40); // unknown
		}
		if(unknown == 0x10)
		{
			writeD(buf, 0x00); // unknown
			writeH(buf, emotionId);
			writeC(buf, 0x01); // unknown
		}

	}
}
