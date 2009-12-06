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
package com.aionemu.gameserver.questEngine.conditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javolution.util.FastMap;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.types.ConditionSet;

/**
 * @author Blackmouse
 */
public class ConditionFactory
{

	private static final Map<String, Class<? extends QuestCondition>> conditions = new FastMap<String, Class<? extends QuestCondition>>();

	static
	{
		conditions.put("dialog_id", DialogIdCondition.class);
		conditions.put("npc_id", NpcIdCondition.class);
		conditions.put("pc_inventory", PcInventoryCondition.class);
		conditions.put("pc_level", PcLevelCondition.class);
		conditions.put("pc_race", PcRaceCondition.class);
		conditions.put("quest_status", QuestStatusCondition.class);
		conditions.put("quest_var", QuestVarCondition.class);
	}

	public static QuestCondition newCondition(final String name , final NamedNodeMap attr, final Quest quest) throws QuestEngineException
	{
		try
		{
			if (conditions.containsKey(name))
			{
				Constructor<? extends QuestCondition> con = conditions.get(name).getConstructor(NamedNodeMap.class, Quest.class);
				return con.newInstance(attr, quest);
			}
		}
		catch (InstantiationException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		catch (IllegalArgumentException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		catch (NoSuchMethodException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		catch (SecurityException ex)
		{
			throw new QuestEngineException("Error on QuestCondition \"" + name + "\".", ex);
		}
		throw new QuestEngineException("Unhandled QuestCondition \"" + name + "\".");
	}

	public static ConditionSet newConditionSet() throws QuestEngineException
	{
		return null;
	}
}
