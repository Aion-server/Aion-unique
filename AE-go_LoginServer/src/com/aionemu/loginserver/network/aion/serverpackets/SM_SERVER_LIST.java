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
package com.aionemu.loginserver.network.aion.serverpackets;

import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.aion.AionConnection;
import com.aionemu.loginserver.network.aion.AionServerPacket;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author -Nemesiss-
 */
public class SM_SERVER_LIST extends AionServerPacket
{
	/**
	 * Logger for this class.
	 */
	protected static Logger	log	= Logger.getLogger(SM_SERVER_LIST.class);

	/**
	 * Constructs new instance of <tt>SM_SERVER_LIST</tt> packet.
	 */
	public SM_SERVER_LIST()
	{
		super(0x04);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		Collection<GameServerInfo> servers = GameServerTable.getGameServers();

		writeC(buf, getOpcode());
		writeC(buf, servers.size());// servers
		writeC(buf, con.getAccount().getLastServer());// last server
		for (GameServerInfo gsi : servers)
		{
			writeC(buf, gsi.getId());// server id
			writeB(buf, gsi.getIPAddressForPlayer(con.getIP())); // server IP
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
