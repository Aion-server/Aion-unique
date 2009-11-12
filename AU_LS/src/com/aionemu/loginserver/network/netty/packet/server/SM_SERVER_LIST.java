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
package com.aionemu.loginserver.network.netty.packet.server;

import com.aionemu.loginserver.GameServer;
import com.aionemu.loginserver.GameServerService;
import com.aionemu.loginserver.network.netty.handler.ChannelHandler;
import com.aionemu.loginserver.network.netty.packet.AbstractServerPacket;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Collection;

/**
 * @author -Nemesiss-
 */
public class SM_SERVER_LIST extends AbstractServerPacket
{
	/**
	 * Logger for this class.
	 */
	protected static Logger	log	= Logger.getLogger(SM_SERVER_LIST.class);

	private final GameServerService gameServerService;

	/**
	 * Constructs new instance of <tt>SM_SERVER_LIST</tt> packet.
	 * @param gaeGameServerService GameServer service
	 */
	public SM_SERVER_LIST(GameServerService gaeGameServerService)
	{
		super(0x04);
		this.gameServerService = gaeGameServerService;
	}

	@Override
	protected void writeImpl(ChannelHandler channelHandler, ChannelBuffer buf)
	{
		Collection<GameServer> servers = gameServerService.getGameServers();

		writeC(buf, getOpCode());
		writeC(buf, servers.size());// servers
		writeC(buf, channelHandler.getAccount().getLastServer());// last server
		for (GameServer gsi : servers)
		{
			writeC(buf, gsi.getId());// server id
			writeB(buf, gsi.getIPAddressForPlayer(channelHandler.getIP())); // server IP
			writeD(buf, gsi.getPort());// port
			writeC(buf, 0x00); // age limit
			writeC(buf, 0x01);// pvp=1
			writeH(buf, gsi.getCurrentPlayers());// currentPlayers
			writeH(buf, gsi.getMaxPlayers());// maxPlayers
			writeC(buf, gsi.isOnline() ? 1 : 0);// ServerStatus, up=1
			writeD(buf, 1);// bits);
			writeC(buf, 0);// server.brackets ? 0x01 : 0x00);
		}
	}
}
