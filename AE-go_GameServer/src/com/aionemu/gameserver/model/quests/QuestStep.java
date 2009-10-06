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

import javolution.util.FastSet;
import com.aionemu.gameserver.model.quests.operations.NextStateOperation;
import com.aionemu.gameserver.model.quests.types.ConditionSet;

import java.util.Set;

/**
 * @author Blackmouse
 */
public class QuestStep
{

	private final Quest quest;
	private final long num;
	private ConditionSet conditions;
	private final Set<QuestOperation> operations;
	private QuestStep nextStep;

	public QuestStep(Quest quest, long num)
	{
		this.quest = quest;
		this.num = num;
		conditions = new ConditionSet();
		operations = new FastSet<QuestOperation>();
		nextStep = null;
	}

	public long getNum()
	{
		return num;
	}

	public void addCondition(ConditionSet set)
	{
		conditions = set;
	}

	public void addOperation(QuestOperation operation)
	{
		operations.add(operation);
	}

	public QuestStep getNextStep()
	{
		return nextStep;
	}

	public void setNextStep(QuestStep next)
	{
		nextStep = next;
	}

	public void processQuestStep(QuestState state) throws QuestEngineException
	{
		if (conditions.checkConditionOfSet(state))
		{
			boolean needIncStep = true;
			for (QuestOperation oper : operations)
			{
				if (oper instanceof NextStateOperation)
				{
					needIncStep = false;
				}
				oper.operate(state);
			}
			if (needIncStep)
			{
				if (nextStep != null)
				{
					state.setQuestStep(nextStep.getNum());
				}
				else
				{
					quest.terminateQuest(state);
				}
			}
		}

	}
}