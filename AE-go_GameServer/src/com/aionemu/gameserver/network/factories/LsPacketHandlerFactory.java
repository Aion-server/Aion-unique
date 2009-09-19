/*
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
package com.aionemu.gameserver.network.factories;

import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.network.loginserver.LsPacketHandler;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_ACOUNT_AUTH_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_GS_AUTH_RESPONSE;
import com.aionemu.gameserver.network.loginserver.clientpackets.CM_REQUEST_KICK_ACCOUNT;
import com.google.inject.Injector;

/**
 * @author Luno
 * 
 */
public class LsPacketHandlerFactory
{
	private Injector		injector;
	private LsPacketHandler	handler	= new LsPacketHandler();

	/**
	 * @param loginServer
	 */
	public LsPacketHandlerFactory(Injector injector)
	{
		this.injector = injector;

		addPacket(new CM_ACCOUNT_RECONNECT_KEY(0x03), State.AUTHED);
		addPacket(new CM_ACOUNT_AUTH_RESPONSE(0x01), State.AUTHED);
		addPacket(new CM_GS_AUTH_RESPONSE(0x00), State.CONNECTED);
		addPacket(new CM_REQUEST_KICK_ACCOUNT(0x02), State.AUTHED);

	}

	private void addPacket(LsClientPacket prototype, State... states)
	{
		injector.injectMembers(prototype);
		handler.addPacketPrototype(prototype, states);
	}

	public LsPacketHandler getPacketHandler()
	{
		return handler;
	}
}