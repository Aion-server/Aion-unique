/*
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
 * @author xavier
 *
 */
public class SM_GUILD_INFO extends AionServerPacket
{
	private String guildName;
	private String[] messages;
	
	public SM_GUILD_INFO (String guildName, String[] messages) {
		this.guildName = guildName;
		this.messages = messages;
	}
	
	@Override
	public void writeImpl (AionConnection con, ByteBuffer buf) {
		writeS(buf, guildName);
		writeD(buf, 0x01); // unk
		writeD(buf, 0x400C7C00); // unk
		writeC(buf, messages.length);
		writeD(buf, 0xA77B); // unk
		writeD(buf, 0x30D40); // unk
		writeD(buf, 0x00); // unk
		writeD(buf, 0x00); // unk
		for (String msg : messages)
		{
			writeS(buf, msg);
			writeD(buf, 0x00); // time???
		}
	}
}
