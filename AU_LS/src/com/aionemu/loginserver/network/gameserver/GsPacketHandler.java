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

import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.service.AccountTimeService;
import com.aionemu.loginserver.network.gameserver.GsConnection.State;
import com.aionemu.loginserver.network.gameserver.clientpackets.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @author -Nemesiss-
 */
@Component
public class GsPacketHandler
{
	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(GsPacketHandler.class);

	private final GameServerService gameServerService;

	private final AccountTimeService accountTimeService;

	@Autowired
	public GsPacketHandler(GameServerService gameServerService, AccountTimeService accountTimeService){
		this.gameServerService = gameServerService;
		this.accountTimeService = accountTimeService;
	}

	/**
	 * Reads one packet from given ByteBuffer
	 * 
	 * @param data
	 * @param client
	 * @return GsClientPacket object from binary data
	 */
	public GsClientPacket handle(ByteBuffer data, GsConnection client)
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
						msg = new CM_GS_AUTH(data, client, gameServerService);
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
						msg = new CM_ACCOUNT_DISCONNECTED(data, client, accountTimeService);
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
	private void unknownPacket(State state, int id)
	{
		log.warn(String.format("Unknown packet received from Game Server: 0x%02X state=%s", id, state.toString()));
	}
}
