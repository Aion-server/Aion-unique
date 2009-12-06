/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.events.QuestEvent;

/**
 * @author MrPoke
 *
 */
public class NpcQuestData
{
	private List<Quest> onQuestStart = new ArrayList<Quest>();
	private List<Quest> onQuestEnd = new ArrayList<Quest>();
	private List<QuestEvent> onKillEvent = new ArrayList<QuestEvent>();
	private List<QuestEvent> onTalkEvent = new ArrayList<QuestEvent>();
	
	public NpcQuestData()
	{
	}

	public void addOnQuestStart(Quest quest)
	{
		if (!onQuestStart.contains(quest))
		{
			onQuestStart.add(quest);
		}
	}
	public List<Quest> getOnQuestStart()
	{
		return onQuestStart;
	}

	public void addOnQuestEnd(Quest quest)
	{
		if (!onQuestEnd.contains(quest))
		{
			onQuestEnd.add(quest);
		}
	}
	public List<Quest> getOnQuestEnd()
	{
		return onQuestEnd;
	}

	public void addOnKillEvent(QuestEvent event)
	{
		if (!onKillEvent.contains(event))
		{
			onKillEvent.add(event);
		}
	}
	public List<QuestEvent> getOnKillEvent()
	{
		return onKillEvent;
	}

	public void addOnTalkEvent(QuestEvent event)
	{
		if (!onTalkEvent.contains(event))
		{
			onTalkEvent.add(event);
		}
	}
	public List<QuestEvent> getOnTalkEvent()
	{
		return onTalkEvent;
	}
}
