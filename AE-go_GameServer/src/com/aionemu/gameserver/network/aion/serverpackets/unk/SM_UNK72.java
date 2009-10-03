/*
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
package com.aionemu.gameserver.network.aion.serverpackets.unk;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author orz
 *
 */
public class SM_UNK72 extends AionServerPacket
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 9);
		
		writeH(buf, 0);
		writeC(buf, 1);
		writeH(buf, 2200);
		writeC(buf, 1);
		writeH(buf, 2300);
		writeC(buf, 1);
		writeH(buf, 1130);
		writeC(buf, 1);
		writeH(buf, 1007);
		writeC(buf, 1);
		writeH(buf, 1006);
		writeC(buf, 1);
		writeH(buf, 2008);
		writeC(buf, 1);
		writeH(buf, 2009);
		writeC(buf, 1);
		writeH(buf, 1300);
		writeC(buf, 1);
		
		writeC(buf, 0);
	}

}
