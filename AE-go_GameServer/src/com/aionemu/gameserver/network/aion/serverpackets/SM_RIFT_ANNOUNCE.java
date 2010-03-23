/*
 * This file is part of aion-unique <aion-unique.org>.
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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Sweetkr
 *
 */
public class SM_RIFT_ANNOUNCE extends AionServerPacket
{
	private Race race;

	/**
	 * Constructs new <tt>SM_RIFT_ANNOUNCE</tt> packet
	 * 
	 * @param player
	 */
	public SM_RIFT_ANNOUNCE(Race race)
	{
		this.race = race;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		switch(race) //destination
		{
			//master rift announcements
			case ASMODIANS:
				writeD(buf, 1);
				writeD(buf, 0);
				break;
			case ELYOS:
				writeD(buf, 1);
				writeD(buf, 0);
				break;
		}
		
		// ELYSEA:
		// 1 0 -> to asmodae
		// 0 1 -> to elysea
		// ASMODAE
		// 1 0 -> to elysea
		// 0 1 -> to asmodae
	}
}
