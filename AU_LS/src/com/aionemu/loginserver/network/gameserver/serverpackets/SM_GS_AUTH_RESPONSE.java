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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

import java.nio.ByteBuffer;

/**
 * This packet is response for CM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 * 
 * @author -Nemesiss-
 */
public class SM_GS_AUTH_RESPONSE extends GsServerPacket
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
	protected void writeImpl(GsConnection con, ByteBuffer buf)
	{
		writeC(buf, getOpcode());
		writeC(buf, response.getResponseId());
	}
}
