package com.aionemu.gameserver.model.quests;

import javolution.util.FastSet;
import com.aionemu.gameserver.model.quests.operations.NextStateOperation;
import com.aionemu.gameserver.model.quests.types.ConditionSet;

import java.util.Set;

/**
 * @author Blakkky
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
			{ // если забыли перещелкнуть квест на след. шаг, под конец делаем это принудительно
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