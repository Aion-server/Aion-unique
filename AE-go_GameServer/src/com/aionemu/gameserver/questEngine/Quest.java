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
package com.aionemu.gameserver.questEngine;


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class Quest
{

	private QuestTemplate	template;
	private QuestEnv		env;

	public Quest(QuestTemplate template, QuestEnv env)
	{
		super();
		this.template = template;
		this.env = env;
	}

	public boolean checkStartCondition()
	{
		Player player = env.getPlayer();

		if(template.getRacePermitted() != null)
		{
			if(template.getRacePermitted() != player.getCommonData().getRace())
				return false;
		}

		if(player.getLevel() < template.getMinlevelPermitted())
		{
			return false;
		}

		if(template.getClassPermitted().size() != 0)
		{
			if(!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
				return false;
		}

		if(template.getGenderPermitted() != null)
		{
			if(template.getGenderPermitted() != player.getGender())
				return false;
		}

		for(int questId : template.getFinishedQuestConds())
		{
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if(qs == null || qs.getStatus() != QuestStatus.COMPLITE)
				return false;
		}
		
		if (template.getCombineSkill() != null)
		{
			SkillListEntry skill = player.getSkillList().getSkillEntry(template.getCombineSkill());
			if (skill == null)
				return false;
			if (skill.getSkillLevel() < template.getCombineSkillPoint() || skill.getSkillLevel()-40 > template.getCombineSkillPoint())
				return false;
			return true;
		}

		QuestState qs = player.getQuestStateList().getQuestState(template.getId());
		if(qs != null && qs.getStatus().value() > 0)
			return false;

		return true;
	}

	public boolean startQuest(QuestStatus questStatus)
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();

		if(questStatus != QuestStatus.LOCKED)
		{
			if(!checkStartCondition())
				return false;

			if(player.getLevel() < template.getMinlevelPermitted())
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(0x13D866, template.getMinlevelPermitted()));
				return false;
			}
		}
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(id, questStatus.value(), 0));
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null)
		{
			qs = new QuestState(template.getId(), questStatus, 0, 0);
			player.getQuestStateList().addQuest(id, qs);
		}
		else
		{
			if(template.getMaxRepeatCount() >= qs.getCompliteCount())
			{
				qs.setStatus(questStatus);
				qs.getQuestVars().setQuestVar(0);
			}
		}

		player.updateNearbyQuests();
		return true;
	}

	public boolean questComplite()
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0) + 1);
		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		player.updateNearbyQuests();
		return true;
	}

	public boolean collectItemCheck()
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null)
			return false;
		CollectItems collectItems = template.getCollectItems();
		if(collectItems == null)
			return true;
		for(CollectItem collectItem : collectItems.getCollectItem())
		{
			int count = player.getInventory().getItemCountByItemId(collectItem.getItemId());
			if(collectItem.getCount() > count)
				return false;
		}
		return true;
	}
}