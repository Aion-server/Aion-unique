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
package com.aionemu.gameserver.questEngine.types;

import javolution.util.FastSet;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.conditions.QuestCondition;

/**
 * @author Blackmouse
 */
@SuppressWarnings("serial")
public class ConditionSet extends FastSet<QuestCondition>
{

	private final ConditionUnionType unionType;

	public ConditionSet()
	{
		this(ConditionUnionType.AND);
	}

	public ConditionSet(ConditionUnionType unionType)
	{
		super();
		this.unionType = unionType;
	}

	public boolean checkConditionOfSet(Player player, int data) throws QuestEngineException
	{
		boolean inCondition = (unionType == ConditionUnionType.AND);
		synchronized (player)
		{
			for (QuestCondition cond : this)
			{
				boolean bCond = cond.check(player, data);
				switch (unionType)
				{
					case AND:
						if (!bCond) return false;
						inCondition = inCondition && bCond;
						break;
					case OR:
						if (bCond) return true;
						inCondition = inCondition || bCond;
						break;
				}
			}
		}
		return inCondition;
	}
}
