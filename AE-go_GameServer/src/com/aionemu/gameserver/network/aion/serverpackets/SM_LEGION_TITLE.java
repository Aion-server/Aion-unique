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
public class SM_LEGION_TITLE extends AionServerPacket
{
	private Player	player;

	public SM_LEGION_TITLE(Player player)
	{
		this.player = player;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());
		writeS(buf, player.getName());
		writeH(buf, 0x0100); // unk
		writeC(buf, player.getCommonData().getPlayerClass().getClassId());
		writeC(buf, player.getLevel());
		writeD(buf, player.getPosition().getMapId());
		writeD(buf, 0x00); // unk
		writeH(buf, 0x00); // unk
	}
}

/*
 * Aion-Unique 8F 55 2C DC 8D 00 00 53 00 69 00 6D 00 70 00 6C .U,....S.i.m.p.l 00 65 00 00 00 00 01 00 01 90 9E 8E 06
 * 00 00 00 .e.............. 00 00 00 ... ...
 */

/*
 * Retail 8F 55 2C 62 8D 00 00 53 00 63 00 68 00 69 00 7A .U,b...S.c.h.i.z 00 6F 00 70 00 68 00 72 00 65 00 6E 00 69 00
 * 61 .o.p.h.r.e.n.i.a 00 00 00 00 01 04 28 90 9E 8E 06 00 00 00 00 00 ......(......... 00 .
 */