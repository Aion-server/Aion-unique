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
import org.w3c.dom.Node;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.QuestState;
import com.aionemu.gameserver.questEngine.types.QuestStatus;

/**
 * @author MrPoke
 */
public class QuestStatusCondition extends QuestCondition
{
	private static final String NAME = "quest_status";
	private final int value ;
	private final int questId;

	public QuestStatusCondition(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		Node tmp = attr.getNamedItem("quest_id");
		if (tmp == null)
			questId = quest.getId();
		else
			questId = Integer.parseInt(attr.getNamedItem("quest_id").getNodeValue());
		
		value = QuestStatus.valueOf(attr.getNamedItem("value").getNodeValue()).value();
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	protected boolean doCheck(Player player, int data) throws QuestEngineException
	{
		int qstatus = 0;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null)
			qstatus = qs.getStatus().value();
			
		switch (getOp())
		{
			case EQUAL:
				return qstatus == value;
			case GREATER:
				return qstatus > value;
			case GREATER_EQUAL:
				return qstatus >= value;
			case LESSER:
				return qstatus < value;
			case LESSER_EQUAL:
				return qstatus <= value;
			case NOT_EQUAL:
				return qstatus != value;
			default:
				return false;
		}
	}
}
