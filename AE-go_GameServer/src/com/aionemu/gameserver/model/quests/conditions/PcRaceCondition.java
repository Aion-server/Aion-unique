package com.aionemu.gameserver.model.quests.conditions;

import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestState;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.Race;
import org.w3c.dom.NamedNodeMap;

public class PcRaceCondition extends QuestCondition
{
	private static final String NAME = "pc_race";
	private final Race race;

	public PcRaceCondition(NamedNodeMap attr)
	{
		super(attr);
		this.race = Race.valueOf(attr.getNamedItem("value").getNodeValue());
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
				return state.getPlayer().getRace() == race;
			case NOT_EQUAL:
				return state.getPlayer().getRace() != race;
			default:
				return false;
		}
	}
	

}
