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
package com.aionemu.loginserver.network.gameserver;

import com.aionemu.commons.network.packet.BaseClientPacket;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * Base class for every GameServer -> LS Client Packet
 * 
 * @author -Nemesiss-
 */
public abstract class GsClientPacket extends BaseClientPacket<GsConnection>
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(GsClientPacket.class);

	/**
	 * Creates new packet instance. 
	 * 
	 * @param buf packet data
	 * @param client client
	 * @param opcode packet id
	 */
	protected GsClientPacket(ByteBuffer buf, GsConnection client, int opcode)
	{
		super(buf, opcode);
		setConnection(client);
	}

	/**
	 * run runImpl catching and logging Throwable.
	 */
	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Throwable e)
		{
			log.warn("error handling gs (" + getConnection().getIP() + ") message " + this, e);
		}
	}

	/**
	 * Send new GsServerPacket to connection that is owner of this packet. This method is equivalent to:
	 * getConnection().sendPacket(msg);
	 * 
	 * @param msg
	 */
	protected void sendPacket(GsServerPacket msg)
	{
		getConnection().sendPacket(msg);
	}
}
