/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.events.OnEnterZoneEvent;
import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quests")
public class QuestsData
{

	@XmlElement(name = "quest", required = true)
	protected List<QuestTemplate>		questsData;
	private Map<Integer, QuestTemplate>	questData	= new HashMap<Integer, QuestTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(QuestTemplate quest : questsData)
		{
			if(quest.getStartNpcId() != null)
				QuestEngine.getInstance().setNpcQuestData(quest.getStartNpcId()).addOnQuestStart(quest.getId());
			if(quest.getEndNpcId() != null)
				QuestEngine.getInstance().setNpcQuestData(quest.getEndNpcId()).addOnQuestEnd(quest.getId());
			for(QuestEvent event : quest.getOnKillEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setNpcQuestData(id).addOnKillEvent(quest.getId());
				}
			}

			for(QuestEvent event : quest.getOnTalkEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setNpcQuestData(id).addOnTalkEvent(quest.getId());
				}
			}

			for(QuestEvent event : quest.getOnItemUseEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setQuestItemIds(id).add(quest.getId());
				}
			}

			if(!quest.getOnLvlUpEvent().isEmpty())
			{
				QuestEngine.getInstance().addQuestLvlUp(quest.getId());
			}

			for(OnEnterZoneEvent event : quest.getOnEnterZoneEvent())
			{
				for(ZoneName zoneName : event.getNames())
				{
					QuestEngine.getInstance().setQuestEnterZone(zoneName).add(quest.getId());
				}
			}

			for(QuestEvent event : quest.getOnMovieEndEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setQuestMovieEndIds(id).add(quest.getId());
				}
			}
			questData.put(quest.getId(), quest);
		}
		questsData.clear();
		questsData = null;
	}

	public QuestTemplate getQuestById(int id)
	{
		return questData.get(id);
	}

	public int size()
	{
		return questData.size();
	}
}
