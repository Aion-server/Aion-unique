package com.aionemu.gameserver.model.quests.conditions;

import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestState;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.Race;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Blakkky
 */
public class PcRaceCondition extends QuestCondition
{
	private static final String NAME = "test1";
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
				return state.getPlayer().getRace() == race;	//----- not done
			case NOT_EQUAL:
				return state.getPlayer().getRace() != race;	//----- not done
			default:
				return false;
		}
	}
	

}
