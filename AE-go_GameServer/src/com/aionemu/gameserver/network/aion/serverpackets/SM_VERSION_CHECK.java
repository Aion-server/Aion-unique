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
		if((Config.SERVER_MODE ==0)&&(Config.SERVER_COUNTRY_CODE ==1))
		{
            writeH(buf, 256);// unk
            writeD(buf, 0);// unk
            writeD(buf, 0);// unk
            writeD(buf, 0);
            writeD(buf, 90819);
            writeD(buf, 1254913038);
            writeC(buf, 0);
            writeC(buf, 1);  //
            writeC(buf, 0);
            writeC(buf, 0x80);
            writeC(buf, 0x36);
            writeC(buf, 0xFE);
            writeC(buf, 0xcd);
            writeH(buf, 24138);
            writeC(buf, 1);//
            writeC(buf, 1);
            writeC(buf, 0);
            writeC(buf, 0);//0
            writeD(buf, (int) (System.currentTimeMillis() / 1000));// ServerTime in seconds

            writeC(buf, 1);
            writeC(buf, 40);
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
		// Server Version Check for 1.5.0.6 NA/EU
		writeC(buf, 0x00);
		writeC(buf, Config.GAMESERVER_ID); // Server id
		writeD(buf, 0x0001631F);
		writeD(buf, 0x000162C3);
		writeD(buf, 0x00);
		writeD(buf, 0x000162C3);
		writeD(buf, 0x4AB3CB5C);
		writeC(buf, 0x00);
		writeC(buf, Config.SERVER_COUNTRY_CODE); // Server country code (cc)
		writeC(buf, 0x00);
		writeC(buf, Config.SERVER_MODE); // Server mode : 0x00 = one race / 0x01 = free race / 0x22 = Character Reservation

		writeD(buf, (int) (System.currentTimeMillis() / 1000));// ServerTime in seconds

		writeD(buf, 0x0001015E);
		writeD(buf, 0x9C7FCE00);
		writeC(buf, 0xB0);
		writeH(buf, 0x2801);
		}
	}
}