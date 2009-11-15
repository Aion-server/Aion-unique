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

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.model.ReconnectingAccount;
import com.aionemu.loginserver.network.gameserver.AbstractGameClientPacket;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;

/**
 * This packet is sended by GameServer when player is requesting fast reconnect to login server. LoginServer in response
 * will send reconectKey.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_ACCOUNT_RECONNECT_KEY extends AbstractGameClientPacket
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(CM_ACCOUNT_RECONNECT_KEY.class);

	private final AccountController accountController;

	/**
	 * accoundId of account that will be reconnecting.
	 */
	private int			accountId;

	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 * @param accpAccountController
	 */
	public CM_ACCOUNT_RECONNECT_KEY(ChannelBuffer buf, GameChannelHandler client, AccountController accpAccountController)
	{
		super(buf, client, 0x02);
		this.accountController = accpAccountController;
	}

	@Override
	protected void readImpl()
	{
		accountId = readD();
	}

	@Override
	protected void runImpl()
	{
		int reconnectKey = Rnd.nextInt();
		Account acc = gameChannelHandler.getGameServerInfo().removeAccountFromGameServer(accountId);
		if (acc == null){
			log.error("This shouldn't happen!");
			// TODO: Throw exception here
		}
		else{
			accountController.addReconnectingAccount(new ReconnectingAccount(acc, reconnectKey));
		}
		gameChannelHandler.sendPacket(new SM_ACCOUNT_RECONNECT_KEY(accountId, reconnectKey));
	}
}
