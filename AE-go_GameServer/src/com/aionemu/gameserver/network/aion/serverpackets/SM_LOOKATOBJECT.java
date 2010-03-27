/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * 
 */
public class SM_LOOKATOBJECT extends AionServerPacket
{
	private VisibleObject	visibleObject;
	private int		targetObjectId;
	private int		heading;

	public SM_LOOKATOBJECT(VisibleObject visibleObject)
	{
		this.visibleObject = visibleObject;
		if(visibleObject.getTarget() != null)
		{
			this.targetObjectId = visibleObject.getTarget().getObjectId();
			this.heading = Math.abs(128 - visibleObject.getTarget().getHeading());
		}
		else
		{
			this.targetObjectId = 0;
			this.heading = visibleObject.getHeading();
		}
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, visibleObject.getObjectId());
		writeD(buf, targetObjectId);
		writeC(buf, heading);
	}
}
