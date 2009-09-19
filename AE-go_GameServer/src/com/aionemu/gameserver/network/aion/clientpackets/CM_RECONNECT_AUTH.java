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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.google.inject.Inject;

/**
 * In this packets aion client is asking for fast reconnection to LoginServer.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_RECONNECT_AUTH extends AionClientPacket
{
	@Inject
	private LoginServer	loginServer;

	/**
	 * Constructs new instance of <tt>CM_RECONNECT_AUTH </tt> packet
	 * @param opcode
	 */
	public CM_RECONNECT_AUTH(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		AionConnection client = getConnection();
		// TODO! check if may reconnect
		loginServer.requestAuthReconnection(client);
	}
}
