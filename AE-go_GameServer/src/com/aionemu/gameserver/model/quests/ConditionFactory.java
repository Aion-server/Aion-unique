package com.aionemu.gameserver.model.quests;

import javolution.util.FastMap;
import com.aionemu.gameserver.model.quests.conditions.PcLevelCondition;
import com.aionemu.gameserver.model.quests.conditions.PcRaceCondition;
import com.aionemu.gameserver.model.quests.types.ConditionSet;
import org.w3c.dom.NamedNodeMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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
