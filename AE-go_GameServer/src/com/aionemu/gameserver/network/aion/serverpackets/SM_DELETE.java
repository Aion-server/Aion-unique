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

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is informing client that some AionObject is no longer visible.
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_DELETE extends AionServerPacket
{
	/**
	 * Object that is no longer visible.
	 */
	private final int	objectId;

	/**
	 * Constructor.
	 * 
	 * @param object
	 */
	public SM_DELETE(AionObject object)
	{
		this.objectId = object.getObjectId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		int action = 0;
		if (action != 1){
			writeD(buf, objectId);
			writeC(buf, 15); // removal animation speed
		}
	}
}
