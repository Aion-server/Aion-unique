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
package com.aionemu.gameserver.model.quests;

import com.aionemu.gameserver.model.quests.types.ConditionOperation;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Blackmouse
 */
public abstract class QuestCondition
{
	private final ConditionOperation op;

	protected QuestCondition(NamedNodeMap attr)
	{
		this.op = ConditionOperation.valueOf(attr.getNamedItem("op").getNodeValue());
	}

	protected ConditionOperation getOp()
	{
		return op;
	}

	public abstract String getName();

	protected abstract boolean doCheck(final QuestState state) throws QuestEngineException;

	public boolean check(final QuestState state) throws QuestEngineException
	{
		return doCheck(state);
	}
}
