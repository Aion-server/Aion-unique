package com.aionemu.gameserver.model.quests.operations;

import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestOperation;
import com.aionemu.gameserver.model.quests.QuestState;
import com.aionemu.gameserver.model.quests.QuestStep;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Blakkky
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
