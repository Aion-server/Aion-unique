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
package com.aionemu.gameserver.model.quests;

import javolution.util.FastMap;
import com.aionemu.gameserver.model.quests.conditions.PcLevelCondition;
import com.aionemu.gameserver.model.quests.conditions.PcRaceCondition;
import com.aionemu.gameserver.model.quests.types.ConditionSet;
import org.w3c.dom.NamedNodeMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Blackmouse
 */
public class ConditionFactory
{

	private static final Map<String, Class<? extends QuestCondition>> conditions = new FastMap<String, Class<? extends QuestCondition>>();


	static
	{
		conditions.put("pc_level", PcLevelCondition.class);
		conditions.put("pc_race", PcRaceCondition.class);
	}

	public static QuestCondition newCondition(String name, NamedNodeMap attr) throws QuestEngineException
	{
		try
		{
			if (conditions.containsKey(name))
			{
				Constructor con = conditions.get(name).getConstructor(new Class<?>[] {NamedNodeMap.class});
				return (QuestCondition) con.newInstance(new Object[] {attr});
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
