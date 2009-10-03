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

import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is displaying movement of players etc.
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_MOVE extends AionServerPacket
{
	/**
	 * Object that is moving.
	 */
	private final Creature							movingCreature;
	private final float								x, y, z, x2, y2, z2;
	private final byte								heading;
	private final MovementType	moveType;

	/**
	 * Constructs new <tt>SM_MOVE</tt> packet
	 * 
	 * @param movingObject
	 *            object that is moving.
	 * @param x
	 * @param y
	 * @param z
	 * @param x2
	 * @param y2
	 * @param z2
	 * @param unk1
	 * @param unk2
	 * 
	 */
	public SM_MOVE(Creature movingCreature, float x, float y, float z, float x2, float y2, float z2, byte heading,
		MovementType moveType)
	{
		this.movingCreature = movingCreature;
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.heading = heading;
		this.moveType = moveType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, movingCreature.getObjectId());
		writeF(buf, x);
		writeF(buf, y);
		writeF(buf, z);
		writeC(buf, heading);
		writeC(buf, moveType.getMovementTypeId());

		if(moveType != MovementType.MOVEMENT_STOP)
		{
			writeF(buf, x2);
			writeF(buf, y2);
			writeF(buf, z2);
		}
	}
}
