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
package com.aionemu.loginserver.network.netty.packet.l2c.client;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.packet.l2c.AbstractClientPacket;
import com.aionemu.loginserver.network.netty.packet.l2c.server.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.netty.packet.l2c.server.SM_SERVER_LIST;

/**
 * @author -Nemesiss-
 */
public class CM_SERVER_LIST extends AbstractClientPacket
{
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
	public CM_SERVER_LIST(ChannelBuffer buf, ClientChannelHandler client)
	{
		super(buf, client, 0x05);
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
		ClientChannelHandler channelHandler = getHandler();
		if (channelHandler.getSessionKey().checkLogin(accountId, loginOk))
		{
			if (GameServerService.getGameServers().size() == 0)
				channelHandler.close(new SM_LOGIN_FAIL(AionAuthResponse.NO_GS_REGISTERED), true);
			else
				channelHandler.sendPacket(new SM_SERVER_LIST());
		}
		else
		{
			/**
			 * Session key is not ok - inform client that smth went wrong - dc client
			 */
			channelHandler.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), true);
		}
	}
}
