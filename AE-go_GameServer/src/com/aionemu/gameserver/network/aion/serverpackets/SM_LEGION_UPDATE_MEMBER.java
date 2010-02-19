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
 * @author Simple
 * 
 */
public class SM_LEGION_UPDATE_MEMBER extends AionServerPacket
{
	private static final int	OFFLINE	= 0x00;
	private static final int	ONLINE	= 0x01;
	private Player				player;
	private int					msgId;
	private String				text;

	public SM_LEGION_UPDATE_MEMBER(Player player, int msgId, String text)
	{
		this.player = player;
		this.msgId = msgId;
		this.text = text;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());
		writeC(buf, player.getLegionMember().getRank().getRankId());
		writeC(buf, player.getCommonData().getPlayerClass().getClassId());
		writeC(buf, player.getLevel());
		writeD(buf, player.getPosition().getMapId());
		writeC(buf, player.isOnline() ? ONLINE : OFFLINE);
		int lastLogin = (int) (player.getCommonData().getLastOnline().getTime() / 1000);
		writeD(buf, player.isOnline() ? 0x00 : lastLogin);
		writeD(buf, msgId);
		writeS(buf, text);
	}
}

// MAP ID: 90 9E 8E 06
// ONLINE: 00
// 00 00 00 00
// D8 74 7C 4B 00 00 4B 00 00

// MAP ID: 90 9E 8E 06
// ONLINE: 01
// 00 00 00 00
// 00 00 00 00

// MAP ID: 90 9E 8E 06
// ONLINE: 01
// 00 00 00 00
// 00 00 00 00
// 00 00

// ONLINE: 01
// UNK: 00 00 00 00
// MEMBER ID: 31 D7 13 00
// MEMBER NAME: 40 00 60 00 60 00 00 00
// but why is it longer?