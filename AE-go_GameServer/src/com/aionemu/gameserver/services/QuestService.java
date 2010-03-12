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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
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

			qs.setStatus(QuestStatus.COMPLITE);
			qs.setCompliteCount(qs.getCompliteCount() + 1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			player.updateNearbyQuests();
			QuestEngine.getInstance().onLvlUp(env);
			return true;
		}
		return true;
	}
}
