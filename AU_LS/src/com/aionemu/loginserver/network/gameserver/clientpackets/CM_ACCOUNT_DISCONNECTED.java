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

import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.AbstractGameClientPacket;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.service.AccountService;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * In this packet GameServer is informing LoginServer that some account is no longer on GameServer [ie was disconencted]
 *
 * @author -Nemesiss-
 *
 */
public class CM_ACCOUNT_DISCONNECTED extends AbstractGameClientPacket
{
	private final AccountService accountService;
	/**
	 * AccountId of account that was disconnected form GameServer.
	 */
	private int	accountId;

	/**
	 * Constructor.
	 *
	 * @param buf
	 * @param client
	 * @param accountService Service
	 */
	public CM_ACCOUNT_DISCONNECTED(ChannelBuffer buf, GameChannelHandler client, AccountService accountService)
	{
		super(buf, client, 0x03);
		this.accountService = accountService;
	}

	@Override
	protected void readImpl()
	{
		accountId = readD();
	}

	@Override
	protected void runImpl()
	{
		Account	account = gameChannelHandler.getGameServerInfo().removeAccountFromGameServer(accountId);

        /**
         * account can be null if a player logged out from gs
         * {@link CM_ACCOUNT_RECONNECT_KEY 
         */
		if(account != null)
		{
			accountService.onLogout(account);
		}
	}
}
