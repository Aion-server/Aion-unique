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

import com.aionemu.gameserver.model.quests.types.ConditionSet;
import com.aionemu.gameserver.model.quests.types.QuestStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

import java.util.Map;

/**
 * @author Blakmouse
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

	public QuestState startQuest(PlayerCommonData player) throws QuestEngineException
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
