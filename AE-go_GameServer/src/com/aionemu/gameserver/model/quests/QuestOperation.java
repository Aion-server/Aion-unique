package com.aionemu.gameserver.model.quests;

import org.w3c.dom.Node;

/**
 * @author Blakkky
 */
public abstract class QuestOperation
{

	protected QuestOperation(Node node)
	{
	}

	public void operate(QuestState state) throws QuestEngineException
	{
		doOperate(state);
	}

	protected abstract void doOperate(QuestState state) throws QuestEngineException;

	public abstract String getName();
}
