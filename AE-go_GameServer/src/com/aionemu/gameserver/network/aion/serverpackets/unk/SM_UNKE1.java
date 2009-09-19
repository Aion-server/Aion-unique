/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets.unk;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_UNKE1 extends AionServerPacket
{
	private final static byte[]	data	= new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x90, (byte) 0x7F, (byte) 0x84,
		(byte) 0x0C, (byte) 0x55, (byte) 0x89, (byte) 0x97, (byte) 0x44, (byte) 0x14, (byte) 0xA0, (byte) 0x82,
		(byte) 0x44, (byte) 0xA0, (byte) 0xB4, (byte) 0x0C, (byte) 0x43 };

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeB(buf, data);
	}
}
