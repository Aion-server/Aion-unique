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

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author -Nemesiss-
 * CC fix modified by Novo
 */

public class SM_VERSION_CHECK extends AionServerPacket
{
	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		switch(GSConfig.SERVER_COUNTRY_CODE)
		{
			case 1:
				//only here check of Server Mode (OneRace, MultiRace)
				writeCountryCode_1(con, buf);
				break;
			case 2:
				// cc:2 (ENU,DEU,FRA)
				writeCountryCode_2(con, buf);
				break;
			case 7:
				// cc:7 (RUS)
				writeCoutnryCode_7(con, buf);
				break;
		}
	} 

	private void writeCountryCode_1(AionConnection con, ByteBuffer buf)
	{
		if(GSConfig.SERVER_MODE ==0)
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
			writeC(buf, 0x00);
			writeC(buf, NetworkConfig.GAMESERVER_ID);
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x00);
			writeD(buf, 0x15FFA);// unk
			writeD(buf, 0x4A4CEC02);// unk
			writeC(buf, 0x00);
			writeC(buf, GSConfig.SERVER_COUNTRY_CODE); // Server country code (cc)
			writeC(buf, 0x00);
			writeC(buf, GSConfig.SERVER_MODE); // Server mode : 0x00 = one race / 0x01 = free race / 0x22 = Character Reservation
			writeD(buf, (int) (System.currentTimeMillis() / 1000));// ServerTime in seconds
			writeD(buf, 0x0001015E);
			writeD(buf, 0x9C7FCE00);
			writeC(buf, 0xB0);
			writeH(buf, 0x2801);			
		}
/*			writeH(buf, 0x0C00);// unk
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
		} // of if(Config.SERVER_MODE ==0)*/
	}

	private void writeCountryCode_2(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 0x2800);// unk
		writeD(buf, 0x000162C8);// unk
		writeD(buf, 0x000162C3);// unk

		writeD(buf, 0x00);// unk
		writeD(buf, 0x000162C3);// unk
		writeD(buf, 0x4AAc2E70);// unk
		//writeD(buf, 0x80000200);// unk
		writeC(buf, 0x00);//unk
		writeC(buf, 2);// country code;
		writeC(buf, 0x00);//unk
		writeC(buf, 0x80);//server mode? unk?
		writeD(buf, (int) (System.currentTimeMillis() / 1000));
		writeD(buf, 0x0001015E);
		writeC(buf, 0);
		writeD(buf, 0xB09C7FCE);
		writeH(buf, 0x2801);   
	}


	private void writeCoutnryCode_7(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 0x2800);// unk
		writeD(buf, 0x000162C8);// unk
		writeD(buf, 0x000162C3);// unk
		writeD(buf, 0x00);// unk
		writeD(buf, 0x000162C3);// unk
		writeD(buf, 0x4AAc2E70);// unk
		//writeD(buf, 0x80000200);// unk
		writeC(buf, 0x00);//unk
		writeC(buf, 7);// country code;
		writeC(buf, 0x00);//unk
		writeC(buf, 0x80);//server mode? unk?
		writeD(buf, (int) (System.currentTimeMillis() / 1000));
		writeD(buf, 0x0001015E);
		writeC(buf, 0);
		writeD(buf, 0xB09C7FCE);
		writeH(buf, 0x2801);   
	} 
}
