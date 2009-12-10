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
package com.aionemu.gameserver.questEngine;

import static com.aionemu.gameserver.configs.Config.QUEST_XP_RATE;
import static com.aionemu.gameserver.configs.Config.QUEST_KINAH_RATE;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.FinishedQuestConds;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.events.QuestEvent;
import com.aionemu.gameserver.questEngine.types.ConditionSet;
import com.aionemu.gameserver.questEngine.types.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Blakmouse, MrPoke
 */
public class Quest
{
	private ConditionSet baseConditions;
	private final int id;

	private List<QuestEvent> events = new ArrayList<QuestEvent>();

	public Quest(int id, int startNpcId, int endNpcId)
	{
		this.id = id;
		this.baseConditions = new ConditionSet();
		if (startNpcId > 0)
			QuestEngine.getInstance().setNpcQuestData(startNpcId).addOnQuestStart(this);
		if (endNpcId > 0)
			QuestEngine.getInstance().setNpcQuestData(endNpcId).addOnQuestEnd(this);
	}

	public int getId()
	{
		return id;
	}

	public void setQuestConditions(ConditionSet conditions)
	{
		this.baseConditions = conditions;
	}

	public boolean checkStartCondition (Player player) throws QuestEngineException
	{
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(id);
		if (player.getLevel() < questTemplate.getMinlevelPermitted())
		{
			return false;
		}
		if (Race.valueOf(questTemplate.getRacePermitted()) != null)
		{
			if (Race.valueOf(questTemplate.getRacePermitted()) != player.getCommonData().getRace())
				return false;
		}
		FinishedQuestConds fqc = questTemplate.getFinishedQuestConds();
		if (fqc != null)
		{
			for (int questId : fqc.getFinishedQuestCond())
			{
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs == null || qs.getStatus() != QuestStatus.COMPLITE)
					return false;
			}
		}

		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs != null && qs.getStatus().value() > 0)
			return false;

		return baseConditions.checkConditionOfSet(player, -1);
	}
	public boolean startQuest(Player player, int npcId) throws QuestEngineException
	{
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(id);
		if (player.getLevel() < questTemplate.getMinlevelPermitted())
		{
			//TODO: System message need???
			return false;
		}
		if (Race.valueOf(questTemplate.getRacePermitted()) != null)
		{
			if (Race.valueOf(questTemplate.getRacePermitted()) != player.getCommonData().getRace())
				return false;
		}
		
		FinishedQuestConds fqc = questTemplate.getFinishedQuestConds();
		if (fqc != null)
		{
			for (int questId : fqc.getFinishedQuestCond())
			{
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs == null || qs.getStatus() != QuestStatus.COMPLITE)
					return false;
			}
		}

    	PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(id, 3, 0));
		QuestState qs = player.getQuestStateList().getQuestState(getId());
		if (qs == null)
		{
			qs = new QuestState(this, QuestStatus.START, 0, 0);
			player.getQuestStateList().addQuest(getId(), qs);
		}
		else
		{
			if (questTemplate.getMaxRepeatCount() >= qs.getCompliteCount())
			{
				qs.setStatus(QuestStatus.START);
				qs.getQuestVars().setQuestVar(0);
			}
		}
    	player.updateNearbyQuests();
		return true;
	}

	public boolean questFinish(Player player)
	{
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false;
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(id);
		Inventory inventory = player.getInventory();
		Rewards rewards = questTemplate.getRewards().get(0);
		if (rewards.getGold()!= null)
		{
			inventory.increaseKinah(QUEST_KINAH_RATE*rewards.getGold());
			PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(inventory.getKinahItem()));
		}
		if (rewards.getExp() != null)
		{
			int rewardExp = (QUEST_XP_RATE * rewards.getExp());
			long currentExp = player.getCommonData().getExp();
			player.getCommonData().setExp(currentExp + rewardExp);
			PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.EXP(Integer.toString(rewardExp)));
		}

		qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
		qs.setStatus(QuestStatus.COMPLITE);
		qs.setCompliteCount(qs.getCompliteCount()+1);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus() , qs.getQuestVars().getQuestVars()));
		player.updateNearbyQuests();
		return true;
	}

	public boolean questComplite(Player player)
	{
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus() , qs.getQuestVars().getQuestVars()));
		player.updateNearbyQuests();
		return true;
	}

	public boolean collectItemCheck(Player player)
	{
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null)
			return false;
		CollectItems collectItems = DataManager.QUEST_DATA.getQuestById(id).getCollectItems();
		if (collectItems == null)
			return true;
		for (CollectItem collectItem : collectItems.getCollectItem())
		{
			Item item = player.getInventory().getItemByItemId(collectItem.getItemId());
			if (item == null || collectItem.getCount() > item.getItemCount())
				return false;
		}
		
		for (CollectItem collectItem : collectItems.getCollectItem())
		{
			List<Item> addedItems = new ArrayList<Item>();
			Item item = player.getInventory().getItemByItemId(collectItem.getItemId());
			player.getInventory().removeFromBag(item);
			if(item.getItemCount() > 0)
			{
				addedItems.add(item);
			}
			else
			{
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId()));
			}
			
			if (addedItems.size()> 0)
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(addedItems));
			
			qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
			qs.setStatus(QuestStatus.REWARD);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus() , qs.getQuestVars().getQuestVars()));
		}
		return true;
	}

	public void addEvent(QuestEvent qe)
	{
		events.add(qe);
	}

	public boolean getEventsByType(String type, Player player, int data) throws QuestEngineException
	{
		for (QuestEvent event : events)
		{
			if (event.getName().equals(type))
				if (event.operate(player, data))
					return true;
		}
		return false;
	}
}