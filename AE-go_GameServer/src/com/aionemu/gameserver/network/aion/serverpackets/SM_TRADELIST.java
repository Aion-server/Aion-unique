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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.TradeListTemplate;
import com.aionemu.gameserver.model.templates.TradeListTemplate.TradeTab;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * modified by ATracer
 */
public class SM_TRADELIST extends AionServerPacket
{
	
	private int	targetObjectId;
	private TradeListTemplate tlist;

	public SM_TRADELIST(Npc npc, TradeListTemplate tlist)
	{
		this.targetObjectId = npc.getObjectId();
		this.tlist = tlist;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		if ((tlist != null)&&(tlist.getNpcId()!=0)&&(tlist.getCount()!=0))
		{
			writeD(buf, targetObjectId);
			writeC(buf, tlist.isAbyss() ? 2 : 1); //abyss or normal
            writeC(buf, 200);
			writeH(buf, 0);//unk
			writeC(buf, 0); // unknown
			writeH(buf, tlist.getCount()); // unknown
			for(TradeTab tradeTabl : tlist.getTradeTablist())
			{
				writeD(buf, tradeTabl.getId());
			}
		}
	}	
}
