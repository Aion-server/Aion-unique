/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
import java.util.TreeMap;

import com.aionemu.gameserver.model.legion.LegionHistory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class SM_LEGION_TABS extends AionServerPacket
{
	private int										page;
	private TreeMap<Timestamp, LegionHistory>	legionHistory;

	public SM_LEGION_TABS(TreeMap<Timestamp, LegionHistory> legionHistory)
	{
		this.legionHistory = legionHistory;
		this.page = 0;
	}

	public SM_LEGION_TABS(TreeMap<Timestamp, LegionHistory> legionHistory, int page)
	{
		this.legionHistory = legionHistory;
		this.page = page;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		if(legionHistory.size()+8 < (page*8))
			return;
		
		writeD(buf, 0x12); // Unk
		writeD(buf, page); // current page
		writeD(buf, legionHistory.size());
		
		int i = 0;
		for(Timestamp time : legionHistory.descendingKeySet())
		{
			LegionHistory history = legionHistory.get(time);
			writeD(buf, (int) (time.getTime() / 1000));
			writeC(buf, history.getLegionHistoryType());
			writeC(buf, 0);
			if(history.getName().length() > 0)
			{
				writeS(buf, history.getName());
				int size = 134 - (history.getName().length()*2+2);
				writeB(buf, new byte[size]);
			}
			else
				writeB(buf, new byte[134]);
			i++;
			if(i >= 8)
				break;
		}
		writeH(buf, 0);
	}
}