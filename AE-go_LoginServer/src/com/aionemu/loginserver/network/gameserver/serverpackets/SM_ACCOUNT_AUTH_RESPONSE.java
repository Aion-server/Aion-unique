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

import com.aionemu.loginserver.model.AccountTime;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

import java.nio.ByteBuffer;

/**
 * In this packet LoginServer is answering on GameServer request about valid authentication data and also sends account
 * name of user that is authenticating on GameServer.
 *
 * @author -Nemesiss-
 *
 */
public class SM_ACCOUNT_AUTH_RESPONSE extends GsServerPacket
{
	/**
	 * Account id
	 */
	private final int		accountId;

	/**
	 * True if account is authenticated.
	 */
	private final boolean	ok;

	/**
	 * account name
	 */
	private final String	accountName;
	
	/**
	 * Access level
	 */
	private final byte		accessLevel;
	
	/**
	 * Membership
	 */
	private final byte		membership;

	/**
	 * Constructor.
	 *
	 * @param accountId
	 * @param ok
	 * @param accountName
	 * @param accessLevel 
	 * @param membership 
	 */
	public SM_ACCOUNT_AUTH_RESPONSE(int accountId, boolean ok, String accountName, byte accessLevel, byte membership)
	{
		super(0x01);

		this.accountId   = accountId;
		this.ok          = ok;
		this.accountName = accountName;
		this.accessLevel = accessLevel;
		this.membership  = membership;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(GsConnection con, ByteBuffer buf)
	{
		writeC(buf, getOpcode());
		writeD(buf, accountId);
		writeC(buf, ok ? 1 : 0);

		if(ok)
		{
			writeS(buf, accountName);

			AccountTime	accountTime = con.getGameServerInfo().getAccountFromGameServer(accountId).getAccountTime();

			writeQ(buf, accountTime.getAccumulatedOnlineTime());
			writeQ(buf, accountTime.getAccumulatedRestTime());
			writeC(buf, accessLevel);
			writeC(buf, membership);
		}
	}
}        
