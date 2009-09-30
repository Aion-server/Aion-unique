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
import com.aionemu.gameserver.network.aion.Version;
import java.util.Random;


public class SM_UPDATE_ITEM extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_UPDATE_ITEM.class);

	public SM_UPDATE_ITEM()
	{
		
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
//[22:46] <ATracer> 62 03 00 00 24 00 DB 69 15 00 00 00 16
//[22:46] <ATracer> 00 00 3E 0A D6 00 00 00 00 00 00 00 00 00 00 00
//[22:46] <ATracer> 00 00 00 00 00 00 00

		writeD(buf, 866);

		writeD(buf, 36);
		writeD(buf, 0);
		writeD(buf, 0xDB);
		writeD(buf, 0x69);
		writeD(buf, 0x15);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0x16);
		writeD(buf, 0);
		writeD(buf, 0);

		writeD(buf, 0x3E);
		writeD(buf, 0x0A);
		writeD(buf, 0xD6);

		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);
		writeD(buf, 0);

	}
}