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

import org.w3c.dom.Node;

/**
 * @author Blackmouse
 */
public abstract class QuestOperation
{

	protected QuestOperation(Node node)
	{
	}

	public void operate(QuestState state) throws QuestEngineException
	{
		doOperate(state);
	}

	protected abstract void doOperate(QuestState state) throws QuestEngineException;

	public abstract String getName();
}
