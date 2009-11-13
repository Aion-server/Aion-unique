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
package com.aionemu.loginserver.network.aion.clientpackets;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.aion.AbstractClientPacket;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.aion.serverpackets.SM_SERVER_LIST;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;

/**
 * @author -Nemesiss-
 */
public class CM_SERVER_LIST extends AbstractClientPacket
{
	private GameServerService gameServerService;
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private int	accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private int	loginOk;

	/**
	 * Constructs new instance of <tt>CM_SERVER_LIST </tt> packet.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_SERVER_LIST(ChannelBuffer buf, ClientChannelHandler client, GameServerService gameServerService)
	{
		super(buf, client, 0x05);
		this.gameServerService = gameServerService;
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		accountId = readD();
		loginOk = readD();
		readD();// unk
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		if (clientChannelHandler.getSessionKey().checkLogin(accountId, loginOk))
		{
			if (gameServerService.getGameServers().size() == 0)
				clientChannelHandler.close(new SM_LOGIN_FAIL(AionAuthResponse.NO_GS_REGISTERED), true);
			else
				clientChannelHandler.sendPacket(new SM_SERVER_LIST(gameServerService));
		}
		else
		{
			/**
			 * Session key is not ok - inform client that smth went wrong - dc client
			 */
			clientChannelHandler.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), true);
		}
	}
}
