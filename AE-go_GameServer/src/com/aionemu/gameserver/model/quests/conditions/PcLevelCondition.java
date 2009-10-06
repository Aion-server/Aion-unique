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
package com.aionemu.gameserver.model.quests.conditions;

import com.aionemu.gameserver.model.quests.QuestCondition;
import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestState;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Blackmouse
 */
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
