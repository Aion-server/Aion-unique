/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.ChatHandler;
import com.aionemu.gameserver.utils.chathandlers.ChatHandlerResponse;
import com.aionemu.gameserver.utils.chathandlers.ChatHandlers;
import com.google.inject.Inject;

/**
 * Packet that reads normal chat messages.<br>
 * 
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_PUBLIC extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_CHAT_MESSAGE_PUBLIC.class);

	/**
	 * Without legion client doesn't allow to send message
	 */
	// public static final int TYPE_LEGION = ?;
	/**
	 * Without group client doen't allow to send message
	 */
	// public static final int TYPE_GROUP = ?;
	/**
	 * Chat type
	 */
	private ChatType			type;

	/**
	 * Chat message
	 */
	private String				message;

	@Inject
	private ChatHandlers		chatHandlers;

	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_CHAT_MESSAGE_PUBLIC(int opcode)
	{
		super(opcode);
	}

	/**
	 * Reads chat message
	 */
	@Override
	protected void readImpl()
	{
		type = ChatType.getChatTypeByInt(readC());
		message = readS();
	}

	/**
	 * Prints debug info
	 */
	@Override
	protected void runImpl()
	{
		log.info(String.format("Public Message: %s, Type: %s", message, type));

		final Player player = getConnection().getActivePlayer();
		
		for(ChatHandler chatHandler : chatHandlers)
		{
			ChatHandlerResponse response = chatHandler.handleChatMessage(type, message, player);
			if(response.isBlocked())
				return;

			message = response.getMessage();
		}
		
		//Send packet to everyone not blocked
		
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>(){

			@Override
			public boolean acceptObject(Player object)
			{
				return !object.getBlockList().contains(player.getObjectId());
			}
			
		});
	}
}
