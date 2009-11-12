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

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.netty.handler.ChannelHandler;
import com.aionemu.loginserver.network.netty.packet.AbstractServerPacket;

/**
 * @author -Nemesiss-
 */
public class SM_LOGIN_OK extends AbstractServerPacket
{
	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private final int	accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private final int	loginOk;

	/**
	 * Constructs new instance of <tt>SM_LOGIN_OK</tt> packet.
	 * 
	 * @param key session key
	 */
	public SM_LOGIN_OK(SessionKey key)
	{
		super(0x03);
		
		this.accountId = key.accountId;
		this.loginOk = key.loginOk;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(ChannelHandler channelHandler, ChannelBuffer buf)
	{
		writeC(buf, getOpCode());
		writeD(buf, accountId);
		writeD(buf, loginOk);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, 0x000003ea);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeD(buf, 0x00);
		writeB(buf, new byte[16]);
	}
}
