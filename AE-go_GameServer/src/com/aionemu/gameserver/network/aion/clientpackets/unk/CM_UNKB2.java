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
package com.aionemu.gameserver.network.aion.clientpackets.unk;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKBD;

public class CM_UNKB2 extends AionClientPacket
{
	/**
	 * Constructor.
	 * 
	 * @param buf
	 * @param client
	 */
	public CM_UNKB2(ByteBuffer buf, AionConnection client)
	{
		super(buf, client, 0xB6);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		sendPacket(new SM_UNKBD());
	}
}
