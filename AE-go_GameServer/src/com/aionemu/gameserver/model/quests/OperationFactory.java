package com.aionemu.gameserver.model.quests;

import javolution.util.FastMap;
import com.aionemu.gameserver.model.quests.operations.NextStateOperation;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Blakkky
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