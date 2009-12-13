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

import static com.aionemu.gameserver.configs.Config.QUEST_KINAH_RATE;
import static com.aionemu.gameserver.configs.Config.QUEST_XP_RATE;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.RewardItem;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class Quest
{
	private ItemService itemService;

	QuestTemplate template;
	QuestEnv env;

	public Quest(QuestTemplate template, QuestEnv env)
	{
		super();
		this.template = template;
		this.env = env;
	}

	public boolean checkStartCondition ()
	{
		Player player = env.getPlayer();
		if (player.getLevel() < template.getMinlevelPermitted())
		{
			return false;
		}

		if (template.getRacePermitted() != null)
		{
			if (Race.valueOf(template.getRacePermitted()) != player.getCommonData().getRace())
				return false;
		}

		if (template.getClassPermitted().size() != 0)
		{
			if (!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
				return false;
		}

		if (template.getGenderPermitted() != null)
		{
			if (template.getGenderPermitted() != player.getGender())
				return false;
		}

		for (int questId : template.getFinishedQuestConds())
		{
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null || qs.getStatus() != QuestStatus.COMPLITE)
				return false;
		}

		QuestState qs = player.getQuestStateList().getQuestState(template.getId());
		if (qs != null && qs.getStatus().value() > 0)
			return false;
		
		return (template.getConditions()== null? true : template.getConditions().checkConditionOfSet(env));
	}

	public boolean startQuest()
	{
		if (!checkStartCondition ())
			return false;

		Player player = env.getPlayer();
		int id = env.getQuestId();

    	PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(id, 3, 0));
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null)
		{
			qs = new QuestState(template.getId(), QuestStatus.START, 0, 0);
			player.getQuestStateList().addQuest(id, qs);
		}
		else
		{
			if (template.getMaxRepeatCount() >= qs.getCompliteCount())
			{
				qs.setStatus(QuestStatus.START);
				qs.getQuestVars().setQuestVar(0);
			}
		}
    	player.updateNearbyQuests();
		return true;
	}

	public boolean questFinish()
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false;

		Inventory inventory = player.getInventory();
		Rewards rewards = template.getRewards().get(0);
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
		int dialogId = env.getDialogId();
		if (dialogId != 17 && dialogId != 0)
		{
			//TODO: Need support other reward qroup.
			RewardItem rewardItem = rewards.getRewardItem().get(dialogId-8);
			if (rewardItem != null)
			{
				addItem(rewardItem.getItemId(), rewardItem.getCount());
			}
		}
		qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
		qs.setStatus(QuestStatus.COMPLITE);
		qs.setCompliteCount(qs.getCompliteCount()+1);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus() , qs.getQuestVars().getQuestVars()));
		player.updateNearbyQuests();
		return true;
	}

	public boolean questComplite()
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus() , qs.getQuestVars().getQuestVars()));
		player.updateNearbyQuests();
		return true;
	}

	public boolean collectItemCheck()
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null)
			return false;
		CollectItems collectItems = template.getCollectItems();
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

	public void addItem(int itemId, int count)
	{
		Player player = env.getPlayer();
		Inventory inventory = player.getInventory();
		if (itemId == ItemId.KINAH.value())
		{
			inventory.increaseKinah(count);
			PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(inventory.getKinahItem()));
		}
		else
		{
			int currentItemCount = count;
			while (currentItemCount > 0)
			{
				Item newItem = itemService.newItem(itemId, currentItemCount);
				
				Item existingItem = inventory.getItemByItemId(itemId);
				
				//item already in cube
				if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
				{
					int oldItemCount = existingItem.getItemCount();
					Item addedItem = inventory.addToBag(newItem);
					if(addedItem != null)
					{
						currentItemCount -= addedItem.getItemCount() - oldItemCount;
						PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(addedItem));
					}
				}
				// new item and inventory is not full
				else if (!inventory.isFull())
				{
					Item addedItem = inventory.addToBag(newItem);
					if(addedItem != null)
					{
						currentItemCount -= addedItem.getItemCount();
						PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(addedItem));
					}
				}
			}
		}
	}
}