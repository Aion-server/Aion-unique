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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 */
public class SM_ITEM_USAGE_ANIMATION extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_ITEM_USAGE_ANIMATION.class);

	private int playerObjId;
	private int itemObjId;
	private int itemId;

	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId)
	{
		this.playerObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, playerObjId); // player obj id
		writeD(buf, playerObjId); // player obj id 2x - other player? maybe item can be used on other player.
		
		writeD(buf, itemObjId); // itemObjId
		writeD(buf, itemId); // item id
		
		writeD(buf, 0); // unk
		writeC(buf, 1); // unk
		writeC(buf, 1); // unk
		writeC(buf, 0); // unk
		writeC(buf, 1); // unk
		writeH(buf, 0); // unk
		writeC(buf, 0); // unk
	}
}