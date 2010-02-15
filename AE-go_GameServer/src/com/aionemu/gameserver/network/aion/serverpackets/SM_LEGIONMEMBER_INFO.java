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
import com.aionemu.gameserver.model.legion.OfflineLegionMember;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Simple
 * 
 */
public class SM_LEGIONMEMBER_INFO extends AionServerPacket
{
	private Player	player = null;
	private OfflineLegionMember offlineLegionMember = null;
	private static final int OFFLINE = 0x00;
	private static final int ONLINE = 0x01;

	public SM_LEGIONMEMBER_INFO(Player player)
	{
		this.player = player;
	}

	public SM_LEGIONMEMBER_INFO(OfflineLegionMember offlineLegionMember)
	{
		this.offlineLegionMember = offlineLegionMember;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		if(player != null)
		{
			writeC(buf, 0x01); // Pledge Emblem related?
			writeH(buf, 0xFFFF); // Pledge Emblem id?
			writeD(buf, player.getObjectId());
			writeS(buf, player.getName());
			writeC(buf, player.getCommonData().getPlayerClass().getClassId());
			writeD(buf, player.getLevel());
			writeC(buf, player.getLegionMember().getRank());
			writeD(buf, player.getPosition().getMapId());
			writeC(buf, player.isOnline() ? ONLINE : OFFLINE);
			writeS(buf, player.getLegionMember().getSelfIntro());
			writeS(buf, player.getLegionMember().getNickname());
			writeD(buf, (int) player.getCommonData().getLastOnline().getTime());
			writeH(buf, 0x00); // empty?
		}
		else
		{
			writeC(buf, 0x01); // Pledge Emblem related?
			writeH(buf, 0xFFFF); // Pledge Emblem id?
			writeD(buf, offlineLegionMember.getPlayerObjId());
			writeS(buf, offlineLegionMember.getName());
			writeC(buf, offlineLegionMember.getPlayerClass().getClassId());
			writeD(buf, offlineLegionMember.getLevel());
			writeC(buf, offlineLegionMember.getRank());
			writeD(buf, offlineLegionMember.getWorldId());
			writeC(buf, OFFLINE);
			writeS(buf, offlineLegionMember.getSelfIntro());
			writeS(buf, offlineLegionMember.getNickname());
			writeD(buf, (int) offlineLegionMember.getLastOnline().getTime());
			writeH(buf, 0x00); // empty?			
		}
	}
}
