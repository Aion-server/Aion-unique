/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Random;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket
{
	private int	targetObjectId;
	private int	count ;
	private int	itemid ;
	 
	
	public SM_LOOT_ITEMLIST(int targetObjectId, int itemid ,int count)
	{
		this.targetObjectId = targetObjectId;
		this.count = count;
		this.itemid = itemid;
		
	}

	/**
	 * {@inheritDoc} dc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		/*
		writeD(buf, targetObjectId);
		writeC(buf, 0x01); 
		writeC(buf, 0x00);
		writeC(buf, 0xc4);
		writeC(buf, 0x68);
		writeC(buf, 0xf7);
		
		writeC(buf, 0x05);
		writeC(buf, 0x01);
		writeD(buf, 0x00);
		writeC(buf, 0x00);
		*/
		
		writeD(buf, targetObjectId);
		
		writeH(buf, count);
		for(int i = 0; i < count; i++)
		{
		//writeD(buf, 169400039+ ran); //itemid
		writeD(buf, itemid);
		writeH(buf, 1); //count
		writeH(buf, 0);
		writeC(buf, 0);
		writeC(buf, 0);
		}

	}	
}
