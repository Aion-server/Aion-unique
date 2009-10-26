/**
 * This file is part of aion-unique <aion-unique.com>.
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
import java.util.Random;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Avol
 */

public class SM_EXCHANGE_ADD_ITEM extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_EXCHANGE_ADD_ITEM.class);

	private int itemObjId;
	private int itemCount;
	private int action;
	private int itemId;
	private int itemNameId;

	public SM_EXCHANGE_ADD_ITEM(int itemObjId, int itemCount, int action, int itemId, int itemNameId)
	{
		this.itemObjId = itemObjId;
		this.itemCount = itemCount;	
		this.action = action;
		this.itemId = itemId;
		this.itemNameId = itemNameId;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		writeC(buf, action); // 0 -self 1-other
		writeD(buf, itemId); // itemId
		writeD(buf, itemObjId); // itemObjId

		writeH(buf, 36);

		writeD(buf, itemNameId); // itemNameId

		writeH(buf, 0); 

		writeH(buf, 0x16); //length of details
		writeC(buf, 0);
		writeC(buf, 0x3E); //or can be 0x1E
		writeC(buf, 0x63); // ?
		writeD(buf, itemCount);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeH(buf, 0);
		writeC(buf, 0);
		writeH(buf, 1); // not equipable items
		writeC(buf, 0);
	}
}