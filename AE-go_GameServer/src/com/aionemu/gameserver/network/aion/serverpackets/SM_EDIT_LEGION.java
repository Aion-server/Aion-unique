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

import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Simple
 * 
 */
public class SM_EDIT_LEGION extends AionServerPacket
{
	private int					type;
	private Legion				legion;
	private int					unixTime;
	private String				announcement;
	private static final int	legionarPermission1	= 0x40;

	public SM_EDIT_LEGION(int type)
	{
		this.type = type;
	}

	public SM_EDIT_LEGION(int type, int unixTime, String announcement)
	{
		this.type = type;
		this.announcement = announcement;
		this.unixTime = unixTime;
	}

	public SM_EDIT_LEGION(int type, Legion legion)
	{
		this.type = type;
		this.legion = legion;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
        writeC(buf, type);
		switch(type)
		{
			/** Change Legion Level **/
			case 0x00:
				writeC(buf, legion.getLegionLevel());
				break;
			/** Change Legion Rank **/
			case 0x01:
				writeC(buf, legion.getLegionRank());
				break;
			/** Change Legion Permissions **/
			case 0x02:
				writeC(buf, legion.getCenturionPermission1());
				writeC(buf, legion.getCenturionPermission2());
				writeC(buf, legionarPermission1);
				writeC(buf, legion.getLegionarPermission2());
				break;
			/** Change Legion Contributions **/
			case 0x03:
				writeC(buf, 0); // get Contributions
				break;
			/** Change Legion Announcement **/
			case 0x05:
				writeS(buf, announcement);
				writeD(buf, unixTime);
				break;
			/** Refresh Legion Announcement? **/
			case 0x08:
				break;
		}
	}
}
