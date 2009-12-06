/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.questEngine.events;

import java.util.HashSet;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.operations.QuestOperation;
import com.aionemu.gameserver.questEngine.types.ConditionSet;

/**
 * @author MrPoke
 */
public abstract class QuestEvent
{
	private final int questId;
	private ConditionSet conditions = new ConditionSet();
	private Set<QuestOperation> operations = new HashSet<QuestOperation>(); 
	protected QuestEvent(Quest quest)
	{
		this.questId = quest.getId();
	}

	public boolean operate(Player player, int data) throws QuestEngineException
	{
		if (conditions.checkConditionOfSet(player, data))
		{
			for (QuestOperation oper : operations)
			{
				oper.operate(player);
			}
			return true;
		}
		return false;
	}

	public void setConditions(ConditionSet cond)
	{
		conditions = cond;
	}

	public void addOperation(QuestOperation oper)
	{
		operations.add(oper);
	}

	public abstract String getName();
	
	public int getQuestId()
	{
		return questId;
	}
}
