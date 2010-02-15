/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.handlers;

import java.io.File;

import org.apache.log4j.Logger;

import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.google.inject.Injector;

/**
 * @author MrPoke
 *
 */
public class QuestHandlersManager
{
	private static final Logger logger = Logger.getLogger(QuestHandlersManager.class);

	private static ScriptManager 			scriptManager;

	public static final File QUEST_DESCRIPTOR_FILE = new File("./data/scripts/system/quest_handlers.xml");
	

	private QuestHandlersManager()
	{
	}

	public static QuestHandlers init(Injector injector)
	{
		scriptManager = new ScriptManager();
		QuestHandlers questHandlers = new QuestHandlers();
		scriptManager.setGlobalClassListener(new QuestHandlerLoader(injector));

		try
		{
			scriptManager.load(QUEST_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize quest handlers.", e);
		}

		logger.info("Loaded " + QuestHandlers.getSize() + " quest handler.");
		
		return questHandlers;
	}
	
	public static void shutdown()
	{
		scriptManager.shutdown();
		QuestHandlers.clearQuestHandlers();
		scriptManager = null;
		logger.info("Quests are shutdown...");
	}
}
