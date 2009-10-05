package com.aionemu.gameserver.model.quests;

import com.aionemu.gameserver.model.quests.types.ConditionSet;
import com.aionemu.gameserver.model.quests.types.QuestStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

import java.util.Map;

/**
 * @author Blakkky
 */
public class Quest
{

	private ConditionSet baseConditions;
	private final long id;
	private final String name;
	private Map<Long, QuestStep> steps;

	public Quest(long id, String name)
	{
		this.id = id;
		this.name = name;
		this.baseConditions = new ConditionSet();
	}

	public long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setQuestConditions(ConditionSet conditions)
	{
		this.baseConditions = conditions;
	}

	public QuestStep getNextStep(QuestState state)
	{
		return steps.get(state.getQuestStep()).getNextStep();
	}

	public QuestState startQuest(Player player) throws QuestEngineException
	{
		QuestState state = new QuestState(player, this);
		if (baseConditions.checkConditionOfSet(state))
		{
			return state;
		}
		throw new QuestEngineException("Cann't start Quest (id = " + id + ", name = '" + name + "').");
	}

	public void processQuest(QuestState state) throws QuestEngineException
	{
		if (state.getStatus() != QuestStatus.COMPLITE && state.getStatus() != QuestStatus.TERMINATE)
		{
			state.setStatus(QuestStatus.PROCESS);
			try
			{
				QuestStep step = steps.get(state.getQuestStep());
				step.processQuestStep(state);
			}
			catch (Exception ex)
			{
				throw new QuestEngineException(ex);
			}
		}
	}

	public void terminateQuest(QuestState state)
	{
		state.setStatus(QuestStatus.TERMINATE);
	}

	public void compliteQuest(QuestState state)
	{
		state.setStatus(QuestStatus.COMPLITE);
	}
}
