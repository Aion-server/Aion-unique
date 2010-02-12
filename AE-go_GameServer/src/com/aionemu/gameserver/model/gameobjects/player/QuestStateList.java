/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.questEngine.model.QuestState;

/**
 * @author MrPoke
 *
 */
public class QuestStateList
{
	private static final Logger log = Logger.getLogger(QuestStateList.class);
	
	private final SortedMap<Integer, QuestState>	_quests;
	
	/**
	 * Creates an empty quests list
	 */
	public QuestStateList()
	{
		_quests = new TreeMap<Integer, QuestState>();
	}

	public synchronized boolean addQuest(int questId,  QuestState questState)
	{
		if (_quests.containsKey(questId))
		{
			log.warn("Duplicate quest. ");
			return false;
		}
		_quests.put(questId, questState);
		return true;
	}

	public synchronized boolean removeQuest(int questId)
	{
		if (_quests.containsKey(questId))
		{
			_quests.remove(questId);
			return true;
		}
		return false;
	}
	
	public QuestState getQuestState(int questId)
	{
		return _quests.get(questId);
	}

	public Collection <QuestState> getAllQuestState()
	{
		return _quests.values();
	}
}
