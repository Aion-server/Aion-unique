/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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
package com.aionemu.gameserver.model.quests.qholder;

import javolution.util.FastMap;
import com.aionemu.gameserver.model.quests.Quest;
import com.aionemu.gameserver.model.quests.QuestEngineException;

/**
 * @autor: Blackmouse
 */
public class QuestHolder extends AbstractHolder
{
	private static QuestHolder _instance;
	private FastMap<Long, Quest> _quests;

	public static QuestHolder getInstance()
	{
		if (_instance == null)
		{
			_instance = new QuestHolder();
		}
		return _instance;
	}

	QuestHolder()
	{
		_quests = new FastMap<Long, Quest>();
	}

	public Quest getQuest(long id) throws QuestEngineException
	{
		if (!_quests.containsKey(id))
		{
			throw new QuestEngineException("Quest (id = " + id + ") not found.");
		}

		return _quests.get(id);
	}

	public void newQuest(long id, String name) throws QuestEngineException
	{
		if (_quests.containsKey(id))
		{
			throw new QuestEngineException("Duplicate Quest (id = " + id + ").");
		}

		_quests.put(id, new Quest(id, name));
	}

	public int size()
	{
		return _quests.size();
	}

	@Override
	public void clear()
	{
		_quests.clear();
	}
}
