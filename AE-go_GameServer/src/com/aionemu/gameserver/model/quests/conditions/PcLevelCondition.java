package com.aionemu.gameserver.model.quests.conditions;

import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestState;
import org.w3c.dom.NamedNodeMap;


public class PcLevelCondition extends QuestCondition
{
	private static final String NAME = "pc_level";
	private final byte level;

	public PcLevelCondition(NamedNodeMap attr)
	{
		super(attr);
		level = Byte.parseByte(attr.getNamedItem("value").getNodeValue());
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	protected boolean doCheck(QuestState state) throws QuestEngineException
	{
		switch (getOp())
		{
			case EQUAL:
				return state.getPlayer().getLevel() == level;
			case GREATER:
				return state.getPlayer().getLevel() > level;
			case GREATER_EQUAL:
				return state.getPlayer().getLevel() >= level;
			case LESSER:
				return state.getPlayer().getLevel() < level;
			case LESSER_EQUAL:
				return state.getPlayer().getLevel() <= level;
			case NOT_EQUAL:
				return state.getPlayer().getLevel() != level;
			default:
				return false;
		}
	}

}
