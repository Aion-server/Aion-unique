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
package com.aionemu.gameserver.questEngine.operations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javolution.util.FastMap;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;

/**
 * @author Blackmouse
 */
public class OperationFactory
{

	private static final Map<String, Class<? extends QuestOperation>> operations = new FastMap<String, Class<? extends QuestOperation>>();


	static
	{
		operations.put("give_exp", GiveExpOperation.class);
		operations.put("give_item", GiveItemOperation.class);
		operations.put("incrase_quest_var", IncraseQuestVarOperation.class);
		operations.put("npc_dialog", NpcDialogOperation.class);
		operations.put("set_quest_status", SetQuestStatusOperation.class);
		operations.put("start_quest", StartQuestOperation.class);
		operations.put("take_item", TakeItemOperation.class);
	}

	public static QuestOperation newOperation(final String name , final NamedNodeMap attr, final Quest quest) throws QuestEngineException
	{
		try
		{
			if (operations.containsKey(name))
			{
				Constructor<? extends QuestOperation> op= operations.get(name).getConstructor(NamedNodeMap.class, Quest.class);
				return (QuestOperation) op.newInstance(attr, quest);
			}
		}
		catch (InstantiationException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		catch (IllegalArgumentException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		catch (SecurityException ex)
		{
			throw new QuestEngineException("Error on QuestOperation \"" + name + "\".", ex);
		}
		throw new QuestEngineException("Unhandled QuestOperation \"" + name + "\".");
	}
}
