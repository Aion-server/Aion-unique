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
package com.aionemu.gameserver.questEngine.conditions;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;

/**
 * @author Blackmouse
 */
public class PcRaceCondition extends QuestCondition
{
	private static final String NAME = "pc_race";
	private final Race race;

	public PcRaceCondition(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		race = Race.valueOf(attr.getNamedItem("value").getNodeValue());
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	protected boolean doCheck(Player player, int data) throws QuestEngineException
	{
		switch (getOp())
		{
			case EQUAL:
				return player.getCommonData().getRace() == race;
			case NOT_EQUAL:
				return player.getCommonData().getRace() != race;
			default:
				return false;
		}
	}
}
