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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;

/**
 * @author -Nemesiss-
 * 
 */
public class SM_VERSION_CHECK extends AionServerPacket
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		if(Version.Chiness)
		{
			writeH(buf, 0x0600);// unk
			writeD(buf, 0x1618A);// unk
			writeD(buf, 0x1618A);// unk
			writeD(buf, 0x00);// unk
			writeD(buf, 0x1618A);// unk
			writeD(buf, 0x4A11DC04);// unk
			writeD(buf, 0x30000500);// unk
		}
		else
		{
			writeH(buf, 0x0C00);// unk
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x00);// unk
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x4A4CEC02);// unk
			writeD(buf, 0x01000100);// unk
		}

		writeD(buf, (int) (System.currentTimeMillis() / 1000));// ServerTime in seconds

		if(Version.Chiness)
		{
			writeD(buf, 0x3A000001);// unk
			writeD(buf, 0x18372D7);// unk
			writeC(buf, 0x28);// unk
		}
	}
}
