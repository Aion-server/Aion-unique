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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

import java.nio.ByteBuffer;

/**
 * In this packet LoginServer is sending response for CM_ACCOUNT_RECONNECT_KEY with account name and reconnectionKey.
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_ACCOUNT_RECONNECT_KEY extends GsServerPacket
{
	/**
	 * accountId of account that will be reconnecting.
	 */
	private final int	accountId;
	/**
	 * ReconnectKey that will be used for authentication.
	 */
	private final int	reconnectKey;

	/**
	 * Constructor.
	 * 
	 * @param accountId
	 * @param reconnectKey
	 */
	public SM_ACCOUNT_RECONNECT_KEY(int accountId, int reconnectKey)
	{
		super(0x03);

		this.accountId = accountId;
		this.reconnectKey = reconnectKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(GsConnection con, ByteBuffer buf)
	{
		writeC(buf, getOpcode());
		writeD(buf, accountId);
		writeD(buf, reconnectKey);
	}
}
