package com.aionemu.gameserver.model.quests.types;

import javolution.util.FastSet;
import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestState;

/**
 * @author Blakkky
 */
@SuppressWarnings("serial")
public class ConditionSet extends FastSet<QuestCondition>
{

	private final ConditionUnionType unionType;

	public ConditionSet()
	{
		this(ConditionUnionType.AND);
	}

	public ConditionSet(ConditionUnionType unionType)
	{
		super();
		this.unionType = unionType;
	}

	public boolean checkConditionOfSet(final QuestState state) throws QuestEngineException
	{
		boolean inCondition = (unionType == ConditionUnionType.AND);
		synchronized (state)
		{
			for (QuestCondition cond : this)
			{
				boolean bCond = cond.check(state);
				//TODO: optimize by skiping conditions depends from UnionType
				switch (unionType)
				{
					case AND:
						inCondition = inCondition && bCond;
						break;
					case OR:
						inCondition = inCondition || bCond;
						break;
				}
			}
		}
		return inCondition;
	}
}
