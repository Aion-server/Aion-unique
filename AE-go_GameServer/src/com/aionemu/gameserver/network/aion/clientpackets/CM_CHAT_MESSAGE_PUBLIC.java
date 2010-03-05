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

import org.apache.log4j.Logger;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
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
	 * 
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

		final Player player = getConnection().getActivePlayer();

		log.info(String.format("Public Message [%s]: %s, Type: %s", player.getName(), message, type));

		for(ChatHandler chatHandler : chatHandlers)
		{
			ChatHandlerResponse response = chatHandler.handleChatMessage(type, message, player);
			if(response.isBlocked())
				return;

			message = response.getMessage();
		}

		if(RestrictionsManager.canChat(player))
		{
			switch(this.type)
			{
				case GROUP:
					broadcastToGroupMembers(player);
					break;
				case GROUP_LEADER:
					broadcastToGroupMembers(player);
					break;
				case LEGION:
					broadcastToLegionMembers(player);
					break;
				default:
					broadcastToNonBlockedPlayers(player);
			}
		}
	}

	/**
	 * Sends message to all players that are not in blocklist
	 * 
	 * @param player
	 */
	private void broadcastToNonBlockedPlayers(final Player player)
	{
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true,
			new ObjectFilter<Player>(){

				@Override
				public boolean acceptObject(Player object)
				{
					return !object.getBlockList().contains(player.getObjectId());
				}
			});
	}

	/**
	 * Sends message to all group members
	 * 
	 * @param player
	 */
	private void broadcastToGroupMembers(final Player player)
	{
		PlayerGroup group = player.getPlayerGroup();
		if(group != null)
		{
			for(Player groupPlayer : group.getMembers())
			{
				PacketSendUtility.sendPacket(groupPlayer, new SM_MESSAGE(player, message, type));
			}
		}
	}

	/**
	 * Sends message to all legion members
	 * 
	 * @param player
	 */
	private void broadcastToLegionMembers(final Player player)
	{
		if(player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_MESSAGE(player, message, type), player
				.getPosition().getWorld());
		}
	}
}
