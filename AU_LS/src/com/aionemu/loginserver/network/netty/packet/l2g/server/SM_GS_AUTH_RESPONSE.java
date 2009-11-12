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
package com.aionemu.loginserver.network.netty.packet.l2g.server;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.netty.handler.GameChannelHandler;
import com.aionemu.loginserver.network.netty.packet.l2g.AbstractGameServerPacket;

/**
 * This packet is response for CM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 * 
 * @author -Nemesiss-
 */
public class SM_GS_AUTH_RESPONSE extends AbstractGameServerPacket
{
	/**
	 * Response for Gameserver authentication
	 */
	private final GsAuthResponse	response;

	/**
	 * Constructor.
	 * 
	 * @param response
	 */
	public SM_GS_AUTH_RESPONSE(GsAuthResponse response)
	{
		super(0x00);

		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(GameChannelHandler handler, ChannelBuffer buf)
	{
		writeC(buf, getOpCode());
		writeC(buf, response.getResponseId());
	}
}
