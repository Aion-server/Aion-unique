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
import com.aionemu.gameserver.model.quests.operations.NextStateOperation;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Blackmouse
 */
public class OperationFactory
{

	private static final Map<String, Class<? extends QuestOperation>> operations = new FastMap<String, Class<? extends QuestOperation>>();


	static
	{
		operations.put("next_state", NextStateOperation.class);
	}

	public static QuestOperation newOperation(String name, Node node) throws QuestEngineException
	{
		try
		{
			if (operations.containsKey(name))
			{
				Constructor con = operations.get(name).getConstructor(new Class<?>[] {Node.class});
				return (QuestOperation) con.newInstance(new Object[] {node});
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