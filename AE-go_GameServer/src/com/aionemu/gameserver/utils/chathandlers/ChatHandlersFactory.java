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
package com.aionemu.gameserver.utils.chathandlers;

import java.io.File;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.services.ScriptService;
import com.aionemu.gameserver.GameServerError;
import com.google.inject.Injector;

/**
 * This factory is responsible for creating class tree starting with {@link ChatHandlers}
 * 
 * @author Luno, Aquanox
 */
public class ChatHandlersFactory
{
	public static final File CHAT_DESCRIPTOR_FILE = new File("./data/scripts/system/handlers.xml");

	private final Injector	injector;

	/**
	 * @param injector
	 */
	public ChatHandlersFactory(Injector injector)
	{
		this.injector = injector;
	}

	/**
	 * Creates and return object of {@link ChatHandlers} class
	 * 
	 * @return ChatHandlers
	 */
	public ChatHandlers createChatHandlers()
	{
		ChatHandlers handlers = new ChatHandlers();

		final AdminCommandChatHandler adminCCH = new AdminCommandChatHandler();

		handlers.addChatHandler(adminCCH);

		ScriptManager sm = new ScriptManager();

		// set global loader
		sm.setGlobalClassListener(new ChatHandlersLoader(injector, adminCCH));

		try
		{
			sm.load(CHAT_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize chat handlers.", e);
		}

		// save reference to script manager instance.
		injector.getInstance(ScriptService.class).addScriptManager(sm, CHAT_DESCRIPTOR_FILE);

		return handlers;
	}
}
