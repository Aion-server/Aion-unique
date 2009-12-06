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
package com.aionemu.gameserver.questEngine.conditions;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.types.ConditionOperation;

/**
 * @author Blackmouse
 */
public abstract class QuestCondition
{
	private final ConditionOperation op;
	private final int questId;

	protected QuestCondition(NamedNodeMap attr, Quest quest)
	{
		this.op = ConditionOperation.valueOf(attr.getNamedItem("op").getNodeValue());
		this.questId = quest.getId();
	}

	protected ConditionOperation getOp()
	{
		return op;
	}

	public abstract String getName();

	protected abstract boolean doCheck(Player player, int data) throws QuestEngineException;

	public boolean check(Player player, int data) throws QuestEngineException
	{
		return doCheck(player, data);
	}

	public int getQuestId()
	{
		return questId;
	}
}
