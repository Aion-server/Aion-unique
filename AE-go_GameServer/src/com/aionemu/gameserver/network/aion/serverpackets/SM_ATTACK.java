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
 * @author -Nemesiss-
 * 
 */
public class SM_ATTACK extends AionServerPacket
{
	private int attackerobjectid;
	private int	targetObjectId;
	private int	attackno;
	private int	time;
	private int	type;
	private int damage;
	
	public SM_ATTACK(int attackerobjectid ,int targetObjectId, int attackno, int time, int type, int damage)
	{
		this.attackerobjectid = attackerobjectid;
		this.targetObjectId = targetObjectId;
		this.attackno = attackno;// empty
		this.time = time ;// empty
		this.type = type;// empty
		this.damage = damage;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		//attacker
		writeD(buf, attackerobjectid);
		writeC(buf, attackno); // unknown
		writeH(buf, time); // unknown
		writeC(buf, type); // unknown
		//defender
		writeD(buf, targetObjectId);
		writeC(buf, attackno + 1);
		writeH(buf, 84); // unknown
		writeC(buf, 0); // unknown
		
		writeC(buf, 1);
		writeD(buf, damage); // damage
		writeH(buf, 10); // unknown
		
		writeC(buf, 0);
	}	
}
