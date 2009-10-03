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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author -Nemesiss-
 * 
 */
public class SM_L2AUTH_LOGIN_CHECK extends AionServerPacket
{
	/**
	 * True if client is authed.
	 */
	private final boolean	ok;

	/**
	 * Constructs new <tt>SM_L2AUTH_LOGIN_CHECK </tt> packet
	 * 
	 * @param ok
	 */
	public SM_L2AUTH_LOGIN_CHECK(boolean ok)
	{
		this.ok = ok;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, ok ? 0x00 : 0x01);
		writeS(buf, "aionemu_rox!!!");
	}
}
