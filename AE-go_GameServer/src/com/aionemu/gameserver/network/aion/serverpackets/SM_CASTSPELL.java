/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_CASTSPELL extends AionServerPacket
{

	private int attackerobjectid;
	private int	targetObjectId;
	private int	spellid;
	private int	level;
	private int	unk; //can cast?? 
	private int duration;
	
	public SM_CASTSPELL(int attackerobjectid ,int spellid,int level,int unk, int targetObjectId, int duration)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.spellid = spellid ;// empty
		this.level = level ;
		this.unk = unk ;
		this.duration = duration;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
	
		writeD(buf, attackerobjectid);
		writeH(buf, spellid); 
		writeC(buf, level);
		writeC(buf, unk);
		writeD(buf, targetObjectId); 
		writeH(buf, duration); // CAST TIME
		writeC(buf, 0x00);//writeC(0);

	}	
}
