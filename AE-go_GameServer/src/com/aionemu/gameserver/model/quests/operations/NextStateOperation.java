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
package com.aionemu.gameserver.model.quests.operations;

import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestOperation;
import com.aionemu.gameserver.model.quests.QuestState;
import com.aionemu.gameserver.model.quests.QuestStep;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Blackmouse
 */
public class NextStateOperation extends QuestOperation
{

	private static final String NAME = "next_state";
	private final long nextState; //state parameter

	public NextStateOperation(Node node)
	{
		super(node);
		NamedNodeMap attr = node.getAttributes();
		long state;
		try
		{
			state = Long.parseLong(attr.getNamedItem("state").getNodeValue());
		}
		catch (Exception e)
		{
			state = 0;
		}
		this.nextState = state;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	protected void doOperate(QuestState state) throws QuestEngineException
	{
		try
		{
			if (nextState > 0)
			{ // goto state N
				state.setQuestStep(nextState);
			}
			else if (nextState == 0)
			{ // goto next state
				try
				{
					QuestStep nextSt = state.getQuest().getNextStep(state);
					state.setQuestStep(nextSt.getNum());
				}
				catch (NullPointerException e)
				{
					state.getQuest().compliteQuest(state);
				}
			}
			else
			{ // state < 0, terminate quest
				state.getQuest().terminateQuest(state);
			}
		}
		catch (Exception e)
		{
			throw new QuestEngineException(e);
		}
	}
}
