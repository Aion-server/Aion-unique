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

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.loginserver.network.gameserver.GsConnection.State;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_AUTH;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_DISCONNECTED;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_LIST;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.loginserver.network.gameserver.clientpackets.CM_GS_AUTH;

/**
 * @author -Nemesiss-
 */
public class GsPacketHandler
{
	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(GsPacketHandler.class);

	/**
	 * Reads one packet from given ByteBuffer
	 * 
	 * @param data
	 * @param client
	 * @return GsClientPacket object from binary data
	 */
	public static GsClientPacket handle(ByteBuffer data, GsConnection client)
	{
		GsClientPacket msg = null;
		State state = client.getState();
		int id = data.get() & 0xff;

		switch (state)
		{
			case CONNECTED:
			{
				switch (id)
				{
					case 0x00:
						msg = new CM_GS_AUTH(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
			case AUTHED:
			{
				switch (id)
				{
					case 0x01:
						msg = new CM_ACCOUNT_AUTH(data, client);
						break;
					case 0x02:
						msg = new CM_ACCOUNT_RECONNECT_KEY(data, client);
						break;
					case 0x03:
						msg = new CM_ACCOUNT_DISCONNECTED(data, client);
						break;
					case 0x04:
						msg = new CM_ACCOUNT_LIST(data, client);
						break;
					default:
						unknownPacket(state, id);
				}
				break;
			}
		}
		return msg;
	}

	/**
	 * Logs unknown packet.
	 * 
	 * @param state
	 * @param id
	 */
	private static void unknownPacket(State state, int id)
	{
		log.warn(String.format("Unknown packet recived from Game Server: 0x%02X state=%s", id, state.toString()));
	}
}
