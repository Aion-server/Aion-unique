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
import java.util.ArrayList;

import com.aionemu.gameserver.model.legion.LegionMemberEx;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Simple
 * 
 */
public class SM_LEGION_MEMBERLIST extends AionServerPacket
{
	private static final int			OFFLINE	= 0x00;
	private static final int			ONLINE	= 0x01;
	private ArrayList<LegionMemberEx>	legionMembers;

	/**
	 * This constructor will handle legion member info when a List of members is given
	 * 
	 * @param ArrayList
	 *            <LegionMemberEx> legionMembers
	 */
	public SM_LEGION_MEMBERLIST(ArrayList<LegionMemberEx> legionMembers)
	{
		this.legionMembers = legionMembers;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, 0x01);
		writeH(buf, (65536 - legionMembers.size()));
		for(LegionMemberEx legionMember : legionMembers)
		{
			writeD(buf, legionMember.getObjectId());
			writeS(buf, legionMember.getName());
			writeC(buf, legionMember.getPlayerClass().getClassId());
			writeD(buf, legionMember.getLevel());
			writeC(buf, legionMember.getRank().getRankId());
			writeD(buf, legionMember.getWorldId());
			writeC(buf, legionMember.isOnline() ? ONLINE : OFFLINE);
			writeS(buf, legionMember.getSelfIntro());
			writeS(buf, legionMember.getNickname());
			int lastLogin = (int) (legionMember.getLastOnline().getTime() / 1000);
			writeD(buf, legionMember.isOnline() ? 0x00 : lastLogin);
		}
	}
}
