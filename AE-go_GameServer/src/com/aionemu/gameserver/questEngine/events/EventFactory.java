/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.questEngine.events;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javolution.util.FastMap;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;

/**
 * @author MrPoke
 */
public class EventFactory
{

	private static final Map<String, Class<? extends QuestEvent>> events = new FastMap<String, Class<? extends QuestEvent>>();


	static
	{
		events.put("on_kill", OnKillEvent.class);
		events.put("on_talk", OnTalkEvent.class);
	}

	public static QuestEvent newEvent(String name, Quest quest, NamedNodeMap attr) throws QuestEngineException
	{
		try
		{
			if (events.containsKey(name))
			{
				Constructor<? extends QuestEvent> op= events.get(name).getConstructor(Quest.class, NamedNodeMap.class);
				return (QuestEvent) op.newInstance(quest, attr);
			}
		}
		catch (InstantiationException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		catch (IllegalArgumentException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		catch (SecurityException ex)
		{
			throw new QuestEngineException("Error on QuestEvent \"" + name + "\".", ex);
		}
		throw new QuestEngineException("Unhandled QuestEvent \"" + name + "\".");
	}
}
