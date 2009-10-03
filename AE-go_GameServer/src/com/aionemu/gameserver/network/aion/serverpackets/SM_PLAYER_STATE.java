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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * So far I've found only one usage for this packet - to stop character blinking ( just after login into game, player's
 * character is blinking )
 * 
 * @author Luno
 * 
 */
public class SM_PLAYER_STATE extends AionServerPacket
{
	private int	playerObjId;

	public SM_PLAYER_STATE(Player player)
	{
		playerObjId = player.getObjectId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, playerObjId);
		writeC(buf, 0x00);
		writeC(buf, 0x00);
		writeC(buf, 0x00);
		// When we write in all these C's 1 instead of 0 we got semitransparent char in crounch position. dunno what is
		// that
	}

}
