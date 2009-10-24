/*
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

/**
 * This packet is used to teleport player
 * 
 * @author Luno , orz
 * 
 */
public class SM_TELEPORT_LOC extends AionServerPacket
{

	private int	mapId;
	private float x , y , z;

	public SM_TELEPORT_LOC(int mapId, float x, float y, float z)
	{
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeC(buf, 3); //unk
		writeC(buf, 0x90);  //unk
		writeC(buf, 0x9E);  //unk
		writeD(buf, mapId); //mapid
		writeF(buf, x); //x
		writeF(buf, y); //y
		writeF(buf, z); //z
		writeC(buf, 0);  //headling
	}

}
