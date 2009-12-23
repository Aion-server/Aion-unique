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

package com.aionemu.gameserver.utils.chathandlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This chat handler is responsible for handling admin commands, starting with //
 * 
 * @author Luno
 * 
 */
public class AdminCommandChatHandler implements ChatHandler
{
	private static final Logger			log			= Logger.getLogger(AdminCommandChatHandler.class);

	private Map<String, AdminCommand>	commands	= new HashMap<String, AdminCommand>();

	AdminCommandChatHandler()
	{

	}

	void registerAdminCommand(AdminCommand command)
	{
		if(command == null)
			throw new NullPointerException("Command instance cannot be null");

		String commandName = command.getCommandName();

		AdminCommand old = commands.put(commandName, command);
		if(old != null)
		{
			log.warn("Overriding handler for command " + commandName + " from " + old.getClass().getName() + " to "
				+ command.getClass().getName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatHandlerResponse handleChatMessage(ChatType chatType, String message, Player sender)
	{
		if(!message.startsWith("//"))
		{
			return new ChatHandlerResponse(false, message);
		}
		else
		{
			String[] commandAndParams = message.split(" ", 2);

			String command = commandAndParams[0].substring(2);
			AdminCommand admc = commands.get(command);
			if(admc == null)
			{
				PacketSendUtility.sendMessage(sender, "<There is no such admin command: " + command + ">");
				return ChatHandlerResponse.BLOCKED_MESSAGE;
			}

			String[] params = new String[] {};
			if(commandAndParams.length > 1)
				params = commandAndParams[1].split(" ", admc.getSplitSize());

			admc.executeCommand(sender, params);
			return ChatHandlerResponse.BLOCKED_MESSAGE;
		}
	}

	/**
	 * Clear all registered handlers (before reload). 
	 */
	void clearHandlers()
	{
		this.commands.clear();
	}

	/**
	 * Returns count of available admin command handlers.
	 * @return count of available admin command handlers.
	 */
	public int getSize()
	{
		return this.commands.size();
	}
}
