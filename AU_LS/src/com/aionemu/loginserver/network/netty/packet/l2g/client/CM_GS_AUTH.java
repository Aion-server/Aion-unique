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
package com.aionemu.loginserver.network.netty.packet.l2g.client;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.commons.network.IPRange;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler.State;
import com.aionemu.loginserver.network.netty.packet.l2g.AbstractGameClientPacket;
import com.aionemu.loginserver.network.netty.packet.l2g.server.SM_GS_AUTH_RESPONSE;

/**
 * This is authentication packet that gs will send to login server for registration.
 * 
 * @author -Nemesiss-
 */
public class CM_GS_AUTH extends AbstractGameClientPacket
{
	/**
	 * Password for authentication
	 */
	private String		password;

	/**
	 * Id of GameServer
	 */
	private byte			gameServerId;

	/**
	 * Maximum number of players that this Gameserver can accept.
	 */
	private int			maxPlayers;

	/**
	 * Port of this Gameserver.
	 */
	private int			port;

	/**
	 * Default address for server
	 */
	private byte[]		defaultAddress;

	/**
	 * List of IPRanges for this gameServer
	 */
	private List<IPRange>	ipRanges;

	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_GS_AUTH(ChannelBuffer buf, GameChannelHandler client)
	{
		super(buf, client, 0x00);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		gameServerId = (byte) readC();

		defaultAddress = readB(readC());
		int size = readD();
		ipRanges = new ArrayList<IPRange>(size);
		for (int i = 0; i < size; i++)
		{
			ipRanges.add(new IPRange(readB(readC()), readB(readC()), readB(readC())));
		}

		port = readH();
		maxPlayers = readD();
		password = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		GameChannelHandler handler = getChannelHandler();

		GsAuthResponse resp = GameServerTable.registerGameServer(handler, gameServerId, defaultAddress, ipRanges, port,
			maxPlayers, password);

		switch (resp)
		{
			case AUTHED:
				handler.setState(State.AUTHED);
				handler.sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;

			default:
				handler.close(new SM_GS_AUTH_RESPONSE(resp), true);
		}
	}
}
