/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.questEngine.handlers;

import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author MrPoke
 *
 */
public abstract class QuestHandler
{
	private final Integer questId;

	/**
	 * @param questId
	 * @return 
	 */
	protected QuestHandler(Integer questId)
	{
		this.questId = questId;
	}
	
	/**
	 * @return the questId
	 */
	public Integer getQuestId()
	{
		return questId;
	}
	
	public abstract boolean onEvent(QuestEnv questEnv, QuestEvent event);
}
