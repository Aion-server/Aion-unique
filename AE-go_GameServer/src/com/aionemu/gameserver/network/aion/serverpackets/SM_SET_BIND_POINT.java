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

/*
 *
 * @author sweetkr
 *
 */
public class SM_SET_BIND_POINT extends AionServerPacket
{
	private final int		mapId;
	private final float		x;
	private final float		y;
	private final float		z;

	public SM_SET_BIND_POINT(int mapId, float x, float y, float z)
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
		writeC(buf, 0x00);// unk
		writeC(buf, 0x01);// unk
		writeD(buf, mapId);// map id
		writeF(buf, x); // coordinate x
		writeF(buf, y); // coordinate y
		writeF(buf, z); // coordinate z
		writeD(buf, 0x00);// unk
	}
}
