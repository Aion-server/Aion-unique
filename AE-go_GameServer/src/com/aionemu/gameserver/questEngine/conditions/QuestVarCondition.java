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
import com.aionemu.gameserver.questEngine.QuestState;

/**
 * @author MrPoke
 */
public class QuestVarCondition extends QuestCondition
{
	private static final String NAME = "quest_var";
	private final int varId;
	private final int value;

	public QuestVarCondition(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		value = Integer.parseInt(attr.getNamedItem("value").getNodeValue());
		varId = Integer.parseInt(attr.getNamedItem("var_id").getNodeValue());
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	protected boolean doCheck(Player player, int data) throws QuestEngineException
	{
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs == null)
		{
			return false;
		}
		int var = qs.getQuestVars().getQuestVarById(varId);
		switch (getOp())
		{
			case EQUAL:
				return var == value;
			case GREATER:
				return var > value;
			case GREATER_EQUAL:
				return var >= value;
			case LESSER:
				return var < value;
			case LESSER_EQUAL:
				return var <= value;
			case NOT_EQUAL:
				return var != value;
			default:
				return false;
		}
	}
}
