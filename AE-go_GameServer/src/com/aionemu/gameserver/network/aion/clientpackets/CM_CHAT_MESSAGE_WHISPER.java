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

package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Packet that reads Whisper chat messages.<br>
 * 
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_WHISPER extends AionClientPacket
{
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_CHAT_MESSAGE_WHISPER.class);

	/**
	 * To whom this message is sent
	 */
	private String				name;

	/**
	 * Message text
	 */
	private String				message;

	@Inject
	private World				world;

	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_CHAT_MESSAGE_WHISPER(int opcode)
	{
		super(opcode);

	}

	/**
	 * Read message
	 */
	@Override
	protected void readImpl()
	{
		name = readS();
		message = readS();
	}

	/**
	 * Print debug info
	 */
	@Override
	protected void runImpl()
	{
		log.info(String.format("Whisper To: %s, Message: %s", name, message));
		Player sender = getConnection().getActivePlayer();
		Player receiver = world.findPlayer(name);

		if(receiver == null)
		{
			sendPacket(SM_SYSTEM_MESSAGE.PLAYER_IS_OFFLINE(name));
		}
		else if (receiver.getBlockList().contains(sender.getObjectId()))
		{
			sendPacket(SM_SYSTEM_MESSAGE.YOU_ARE_BLOCKED_BY(receiver.getName()));
		}
		else
		{
			PacketSendUtility.sendPacket(receiver, new SM_MESSAGE(sender, message, ChatType.WHISPER));
		}
	}
}
