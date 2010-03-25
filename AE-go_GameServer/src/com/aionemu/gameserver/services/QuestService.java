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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.drop.DropTemplate;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Mr. Poke
 *
 */
public class QuestService
{
	@Inject
	ItemService itemService;
	@Inject
	SpawnEngine spawnEngine;
	@Inject
	QuestEngine questEngine;
	@Inject
	AbyssService abyssService;

	public boolean questFinish(QuestEnv env)
	{
		return questFinish(env, 0);
	}

	public boolean questFinish(QuestEnv env, int reward)
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false;
		QuestTemplate	template = DataManager.QUEST_DATA.getQuestById(id);
		Storage inventory = player.getInventory();
		Rewards rewards = template.getRewards().get(reward);
		List<QuestItems> questItems = new ArrayList<QuestItems>();
		questItems.addAll(rewards.getRewardItem());

		int dialogId = env.getDialogId();
		if(dialogId != 17 && dialogId != 0)
		{
			if (template.isUseClassReward())
			{
				QuestItems classRewardItem = null;
				PlayerClass playerClass = player.getCommonData().getPlayerClass();
				switch (playerClass)
				{
					case ASSASSIN :
						classRewardItem = template.getAssassinSelectableReward().get(dialogId - 8);
						break;
					case CHANTER :
						classRewardItem = template.getChanterSelectableReward().get(dialogId - 8);
						break;
					case CLERIC :
						classRewardItem = template.getPriestSelectableReward().get(dialogId - 8);
						break;
					case GLADIATOR :
						classRewardItem = template.getFighterSelectableReward().get(dialogId - 8);
						break;
					case RANGER :
						classRewardItem = template.getRangerSelectableReward().get(dialogId - 8);
						break;
					case SORCERER :
						classRewardItem = template.getWizardSelectableReward().get(dialogId - 8);
						break;
					case SPIRIT_MASTER :
						classRewardItem = template.getElementalistSelectableReward().get(dialogId - 8);
						break;
					case TEMPLAR :
						classRewardItem = template.getKnightSelectableReward().get(dialogId - 8);
						break;
				}
				if (classRewardItem != null)
					questItems.add(classRewardItem);
			}
			else
			{
				QuestItems selectebleRewardItem = rewards.getSelectableRewardItem().get(dialogId - 8);
				if(selectebleRewardItem != null)
					questItems.add(selectebleRewardItem);
			}
		}
		if (itemService.addItems(player, questItems))
		{
			if(rewards.getGold() != null)
			{
				inventory.increaseKinah((int)(player.getRates().getQuestKinahRate() * rewards.getGold()));
			}
			if(rewards.getExp() != null)
			{
				int rewardExp = (int)(player.getRates().getQuestXpRate() * rewards.getExp());
				player.getCommonData().addExp(rewardExp);
			}

			if(rewards.getTitle() != null)
			{
				player.getTitleList().addTitle(rewards.getTitle());
			}
			
			if (rewards.getRewardAbyssPoint() != null)
			{
				abyssService.doReward(player, rewards.getRewardAbyssPoint());
			}

			qs.setStatus(QuestStatus.COMPLITE);
			qs.setCompliteCount(qs.getCompliteCount() + 1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			player.getController().updateNearbyQuests();
			questEngine.onLvlUp(env);
			return true;
		}
		return true;
	}
	
	public boolean checkStartCondition(QuestEnv env)
	{
		
		Player player = env.getPlayer();
		QuestTemplate	template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
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

	public boolean startQuest(QuestEnv env, QuestStatus questStatus)
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestTemplate	template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
		if(questStatus != QuestStatus.LOCKED)
		{
			if(!checkStartCondition(env))
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
				qs.setQuestVar(0);
			}
		}

		player.getController().updateNearbyQuests();
		return true;
	}

	public boolean questComplite(QuestEnv env)
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		player.getController().updateNearbyQuests();
		return true;
	}

	public boolean collectItemCheck(QuestEnv env)
	{
		Player player = env.getPlayer();
		int id = env.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if(qs == null)
			return false;
		QuestTemplate	template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
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

	public VisibleObject addNewSpawn(int worldId, int instanceId, int templateId, float x, float y, float z, byte heading, boolean noRespawn)
	{
		SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, instanceId, templateId, x, y, z, heading, 0, 0, noRespawn);
		return spawnEngine.spawnObject(spawn, instanceId);
	}
	
	public void getQuestDrop(Set<DropItem> dropItems, int index, Npc npc, Player player)
	{
		List<QuestDrop> drops = questEngine.getQuestDrop(npc.getNpcId());
		if (drops.isEmpty())
			return;
		List<Player> players = new ArrayList<Player>();
		if (player.isInGroup())
		{
			for (Player member : player.getPlayerGroup().getMembers())
			{
				if(MathUtil.isInRange(member, player, GroupConfig.GROUP_MAX_DISTANCE))
				{
					players.add(member);
				}
			}
		}
		else
		{
			players.add(player);
		}
		for (QuestDrop drop: drops)
		{
			for (Player member : players)
			{
				if (isDrop(member, drop))
				{
					DropItem item = new DropItem(new DropTemplate(drop.getNpcId(), drop.getItemId(), 1, 1, drop.getChance()));
					item.setPlayerObjId(member.getObjectId());
					item.setIndex(index++);
					item.setCount(1);
					dropItems.add(item);
					if (drop.isDropEachMember())
						break;
				}
			}
		}
	}

	private boolean isDrop(Player player, QuestDrop drop)
	{
		if(Rnd.get() * 100 > drop.getChance())
			return false;
		int questId = drop.getQuestId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		QuestTemplate	template = DataManager.QUEST_DATA.getQuestById(questId);
		CollectItems collectItems = template.getCollectItems();
		if(collectItems == null)
			return true;

		for(CollectItem collectItem : collectItems.getCollectItem())
		{
			int collectItemId = collectItem.getItemId();
			int dropItemId    = drop.getItemId();
			if (collectItemId != dropItemId)
				continue;
			int count = player.getInventory().getItemCountByItemId(collectItemId);
			if(collectItem.getCount() > count)
				return true;
		}
		return false;
	}
}
