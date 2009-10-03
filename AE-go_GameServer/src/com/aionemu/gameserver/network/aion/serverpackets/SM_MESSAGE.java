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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Massage [chat, etc]
 * 
 * @author -Nemesiss-
 * 
 */
public class SM_MESSAGE extends AionServerPacket
{
	/**
	 * Object that is saying smth or null.
	 */
	private int			senderObjectId;

	/**
	 * Message.
	 */
	private String		message;

	/**
	 * Name of the sender
	 */
	private String		senderName;

	/**
	 * Sender race
	 */
	private Race		race;

	/**
	 * Chat type
	 */
	private ChatType	chatType;

	/**
	 * Constructs new <tt>SM_MESSAGE </tt> packet
	 * 
	 * @param player
	 *            who sent message
	 * @param message
	 *            actual message
	 * @param chatType
	 *            what chat type should be used
	 */
	public SM_MESSAGE(Player player, String message, ChatType chatType)
	{
		this.senderObjectId = player.getObjectId();
		this.senderName = player.getName();
		this.message = message;
		this.race = player.getCommonData().getRace();
		this.chatType = chatType;
	}

	/**
	 * Manual creation of chat message.<br>
	 * 
	 * @param senderObjectId
	 *            - can be 0 if system message(like announcements)
	 * @param senderName
	 *            - used for shout ATM, can be null in other cases
	 * @param message
	 *            - actual text
	 * @param race
	 *            - sender race, if null - chat will be visible both for Eylos and Asmodians
	 * @param chatType
	 *            type of chat, Normal, Shout, Announcements, Etc...
	 */
	public SM_MESSAGE(int senderObjectId, String senderName, String message, Race race, ChatType chatType)
	{
		this.senderObjectId = senderObjectId;
		this.senderName = senderName;
		this.message = message;
		this.race = race;
		this.chatType = chatType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		boolean canRead = true;
		if(race != null)
		{
			canRead = race.equals(con.getActivePlayer().getCommonData().getRace());
		}

		writeC(buf, chatType.toInteger()); // type
		writeC(buf, canRead ? 0 : 1); // is race valid? In other case we will get bullshit instead of valid chat;
		writeD(buf, senderObjectId); // sender object id

		switch(chatType)
		{
			case NORMAL: // normal chat
			case UNKNOWN_0x18: // unknown, sent by official server
			case ANNOUNCEMENTS: // announcements
				writeH(buf, 0x00); // unknown
				writeS(buf, message);
				break;
			case SYSTEM_NOTICE: // system announcements
				writeH(buf, 0x00); // unknown
				writeS(buf, message);
				break;
			case SHOUT: // shout
				writeS(buf, senderName);
				writeS(buf, message);
				writeD(buf, 0x00); // unknwon
				writeD(buf, 0x00); // unknown
				writeD(buf, 0x00); // unknown
				break;
			case WHISPER:
				writeS(buf, senderName);
				writeS(buf, message);
		}
	}
}
