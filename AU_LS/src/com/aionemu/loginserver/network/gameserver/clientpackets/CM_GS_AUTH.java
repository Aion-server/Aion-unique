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
package com.aionemu.loginserver.network.gameserver.clientpackets;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.commons.network.IPRange;
import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.gameserver.AbstractGameClientPacket;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_GS_AUTH_RESPONSE;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler.State;

/**
 * This is authentication packet that gs will send to login server for registration.
 * 
 * @author -Nemesiss-
 */
public class CM_GS_AUTH extends AbstractGameClientPacket
{
	private GameServerService gameServerService;
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
	 * @param gameChannelHandler
	 */
	public CM_GS_AUTH(ChannelBuffer buf, GameChannelHandler gameChannelHandler, GameServerService gameServerService)
	{
		super(buf, gameChannelHandler, 0x00);
		this.gameServerService = gameServerService;
	}

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

	@Override
	protected void runImpl()
	{
		GsAuthResponse resp = gameServerService.registerGameServer(gameChannelHandler, gameServerId, defaultAddress, ipRanges, port,
			maxPlayers, password);

		switch (resp)
		{
			case AUTHED:
				gameChannelHandler.setState(State.AUTHED);
				gameChannelHandler.sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;

			default:
				gameChannelHandler.close(new SM_GS_AUTH_RESPONSE(resp), true);
		}
	}
}
