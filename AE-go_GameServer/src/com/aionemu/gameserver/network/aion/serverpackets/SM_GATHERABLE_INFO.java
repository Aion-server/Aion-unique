/*
 * This file is part of aion-emu <aion-unique.com>.
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

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 *
 */
public class SM_GATHERABLE_INFO  extends AionServerPacket
{
	private Gatherable gatherable;

	public SM_GATHERABLE_INFO(Gatherable gatherable)
	{
		super();
		this.gatherable = gatherable;
	}
	
	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeF(buf, gatherable.getX());
		writeF(buf, gatherable.getY());
		writeF(buf, gatherable.getZ());
		writeD(buf, gatherable.getObjectId());
		writeD(buf, 0); //unk
		writeD(buf,gatherable.getTemplate().getTemplateId());
		writeH(buf, 1); //unk
		writeC(buf, 0);
		writeD(buf, gatherable.getTemplate().getNameId());
		writeH(buf, 0);
		writeH(buf, 0);
		writeH(buf, 0);
		writeC(buf, 100); //unk
	}
}
