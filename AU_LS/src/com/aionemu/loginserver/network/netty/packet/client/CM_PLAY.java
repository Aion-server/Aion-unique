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
package com.aionemu.loginserver.network.netty.packet.client;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.GameServer;
import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.netty.handler.ChannelHandler;
import com.aionemu.loginserver.network.netty.packet.AbstractClientPacket;
import com.aionemu.loginserver.network.netty.packet.server.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.netty.packet.server.SM_PLAY_FAIL;
import com.aionemu.loginserver.network.netty.packet.server.SM_PLAY_OK;

/**
 * @author -Nemesiss-
 */
public class CM_PLAY extends AbstractClientPacket
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
	 * Id of game server that this client is trying to play on.
	 */
	private byte	servId;

	/**
	 * Constructs new instance of <tt>SM_PLAY_FAIL</tt> packet.
	 * 
	 * @param buf       packet data
	 * @param client    client
	 */
	public CM_PLAY(ChannelBuffer buf, ChannelHandler client)
	{
		super(buf, client, 0x02);
	}

	@Override
	protected void readImpl()
	{
		accountId = readD();
		loginOk = readD();
		servId = (byte) readC();		
	}

	@Override
	protected void runImpl()
	{
		ChannelHandler channelHandler = getHandler();
		SessionKey key = channelHandler.getSessionKey();
		if (key.checkLogin(accountId, loginOk))
		{
			GameServer gsi = GameServerService.getGameServerInfo(servId);
			if (gsi == null || !gsi.isOnline())
				channelHandler.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.SERVER_DOWN));
			// else if(serv gm only)
			// con.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.GM_ONLY));
			else if(gsi.isFull())
				channelHandler.sendPacket(new SM_PLAY_FAIL(AionAuthResponse.SERVER_FULL));
			else
			{
				channelHandler.setJoinedGs();
				channelHandler.sendPacket(new SM_PLAY_OK(key));
			}
		}
		else
			channelHandler.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), true);
	}
}
