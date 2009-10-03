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
import java.util.Random;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;


public class SM_WEATHER extends AionServerPacket
{
	private static final Logger	log	= Logger.getLogger(SM_WEATHER.class);

	public SM_WEATHER()
	{
		
	}


	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		Random generator = new Random();
		int randomIndex = generator.nextInt(8);
		//log.info(String.format("Weather is %s", randomIndex));
		writeC(buf, randomIndex);
	}
}