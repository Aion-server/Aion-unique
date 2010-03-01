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

package com.aionemu.gameserver.network.loginserver.clientpackets;

import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.google.inject.Inject;

/**
 * In this packet LoginServer is answering on GameServer request about valid authentication data and also sends account
 * name of user that is authenticating on GameServer.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_ACOUNT_AUTH_RESPONSE extends LsClientPacket
{
	/**
	 * accountId
	 */
	private int			accountId;

	/**
	 * result - true = authed
	 */
	private boolean		result;

	/**
	 * accountName [if response is ok]
	 */
	private String		accountName;
	private AccountTime	accountTime;
	/**
	 * access level - regular/gm/admin
	 */
	private byte		accessLevel;
	/**
	 * Membership - regular/premium
	 */
	private byte		membership;
	@Inject
	private LoginServer	loginServer;
	
	/**
	 * Constructs new instance of <tt>CM_ACOUNT_AUTH_RESPONSE </tt> packet.
	 * @param opcode
	 */
	public CM_ACOUNT_AUTH_RESPONSE(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		accountId = readD();
		result = readC() == 1;

		if(result)
		{
			accountName = readS();
			accountTime = new AccountTime();

			accountTime.setAccumulatedOnlineTime(readQ());
			accountTime.setAccumulatedRestTime(readQ());
			
			accessLevel = (byte) readC();
			membership = (byte) readC();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		loginServer.accountAuthenticationResponse(accountId, accountName, result, accountTime, accessLevel, membership);
	}
}
