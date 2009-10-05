package com.aionemu.gameserver.model.quests;

import com.aionemu.gameserver.model.quests.types.ConditionOperation;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Blakkky
 */
public abstract class QuestCondition
{
	private final ConditionOperation op;

	protected QuestCondition(NamedNodeMap attr)
	{
		this.op = ConditionOperation.valueOf(attr.getNamedItem("op").getNodeValue());
	}

	protected ConditionOperation getOp()
	{
		return op;
	}

	public abstract String getName();

	protected abstract boolean doCheck(final QuestState state) throws QuestEngineException;

	public boolean check(final QuestState state) throws QuestEngineException
	{
		return doCheck(state);
	}
}
