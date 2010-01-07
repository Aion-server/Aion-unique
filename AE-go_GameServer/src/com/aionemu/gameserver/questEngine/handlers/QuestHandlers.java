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

import javolution.util.FastMap;

/**
 * @author MrPoke
 *
 */
public class QuestHandlers
{
	private static final FastMap<Integer, QuestHandler> questHandlers = new FastMap<Integer, QuestHandler>();
	
	QuestHandlers()
	{
	}
	
	public static void addQuestHandler (QuestHandler questHandler)
	{
		int questId = questHandler.getQuestId();
		questHandlers.put(questId, questHandler);
	}
	
	public static QuestHandler getQuestHandlerByQuestId(int questId)
	{
		return questHandlers.get(questId);
	}
	
	public static void clearQuestHandlers()
	{
		questHandlers.clear();
	}
	
	public static int getSize()
	{
		return questHandlers.size();
	}
}
