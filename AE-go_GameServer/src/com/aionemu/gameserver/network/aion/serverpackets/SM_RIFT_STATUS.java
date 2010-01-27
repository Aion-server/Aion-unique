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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 *
 */
public class SM_RIFT_STATUS extends AionServerPacket
{
	private int usedEntries;
	private int maxEntries;
	private int maxLevel;
	private int targetObjectId;
	
	public SM_RIFT_STATUS(int targetObjId, int usedEntries, int maxEntries, int maxLevel)
	{
		this.targetObjectId = targetObjId;
		this.usedEntries = usedEntries;
		this.maxEntries = maxEntries;
		this.maxLevel = maxLevel;
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, targetObjectId);
		writeD(buf, usedEntries);
		writeD(buf, maxEntries);
		writeD(buf, 6793); //unk
		writeD(buf, 25); // min level
		writeD(buf, maxLevel);	
	}
}
