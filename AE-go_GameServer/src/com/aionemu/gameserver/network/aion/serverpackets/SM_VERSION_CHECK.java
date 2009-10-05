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

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

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
		write_1_5(con, buf);
	}

	private void write_1_5(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 0x2800);// unk
		writeD(buf, 0x000162C8);// unk
		writeD(buf, 0x000162C3);// unk
		writeD(buf, 0x00);// unk
		writeD(buf, 0x000162C3);// unk
		writeD(buf, 0x4AAc2E70);// unk
		//writeD(buf, 0x80000200);// unk
		writeC(buf, 0x00);//unk
		writeC(buf, Config.SERVER_COUNTRY_CODE); // Server country code (cc)
		writeC(buf, 0x00);//unk
		writeC(buf, Config.SERVER_MODE); // Server mode : 0x00 = one race / 0x01 = free race / 0x22 = Character Reservation
		writeD(buf, (int) (System.currentTimeMillis() / 1000));
		writeD(buf, 0x0001015E);
		writeC(buf, 0);
		writeD(buf, 0xB09C7FCE);
		writeH(buf, 0x2801);

	}
}
