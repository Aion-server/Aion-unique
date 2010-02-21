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
import java.sql.Timestamp;
import java.util.Map;

import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Simple
 * 
 */
public class SM_LEGION_INFO extends AionServerPacket
{
	/** Legion information **/
	private Legion	legion;

	/**
	 * This constructor will handle legion info
	 * @param legion
	 */
	public SM_LEGION_INFO(Legion legion)
	{
		this.legion = legion;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeS(buf, legion.getLegionName());
		writeC(buf, legion.getLegionLevel());
		writeD(buf, legion.getLegionRank());
		writeC(buf, legion.getCenturionPermission1());
		writeC(buf, legion.getCenturionPermission2());
		writeC(buf, legion.getLegionarPermission1());
		writeC(buf, legion.getLegionarPermission2());
		writeD(buf, legion.getContributionPoints());
		writeD(buf, 0x00); // unk
		writeD(buf, 0x00); // unk
		writeD(buf, 0x00); // unk

		/** Get Announcements List From DB By Legion **/
		Map<Timestamp, String> announcementList = legion.getAnnouncementList();
		for(Timestamp unixTime : announcementList.keySet())
		{
			writeS(buf, announcementList.get(unixTime));
			writeD(buf, (int)(unixTime.getTime()/1000));
		}
		writeH(buf, 0x00);
	}
}
