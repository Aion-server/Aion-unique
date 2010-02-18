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
public class SM_LEGION_ADD_MEMBER extends AionServerPacket
{	
	private Player	player;

	public SM_LEGION_ADD_MEMBER(Player player)
	{
		this.player = player;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());
		writeS(buf, player.getName());
		writeC(buf, player.getLegionMember().getRank());
		writeC(buf, 0x00);// is New Member? rank?
		writeC(buf, player.getCommonData().getPlayerClass().getClassId());
		writeC(buf, player.getLevel());
		writeD(buf, player.getPosition().getMapId());
		writeD(buf, 0x00);
		writeH(buf, 0x00);
	}
}