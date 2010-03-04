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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke
 *
 */
public class QuestHandler
{
	private final Integer questId;

	/**
	 * @param questId
	 */
	protected QuestHandler(Integer questId)
	{
		this.questId = questId;
	}
	
	public synchronized void updateQuestStatus(Player player, QuestState qs)
	{
		PacketSendUtility.sendPacket(player, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		if (qs.getStatus() == QuestStatus.COMPLITE)
			player.updateNearbyQuests();
	}
	
	public boolean sendQuestDialog(Player player, int objId, int dialogId)
	{
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(objId, dialogId, questId));
		return true;
	}
	
	public boolean collectItemCheck (QuestEnv env)
	{
		Player player = env.getPlayer();
		if(QuestEngine.getInstance().getQuest(env).collectItemCheck())
		{
			QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
			CollectItems collectItems = template.getCollectItems();
			if (collectItems != null)
			{
				for (CollectItem collectItem : collectItems.getCollectItem())
				{
					player.getInventory().removeFromBagByItemId(collectItem.getItemId(), collectItem.getCount());
				}
			}
			return true;
		}
		else
			return false;
	}

	public boolean defaultQuestStartDialog(QuestEnv env)
	{
		
		Player player = env.getPlayer();
		
		int targetObjId = env.getVisibleObject()==null?0 : env.getVisibleObject().getObjectId();
		switch (env.getDialogId())
		{
			case 1007:
				return sendQuestDialog(player, targetObjId, 4);
			case 1002:
				if (QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START))
					return sendQuestDialog(player, targetObjId, 1003);
				else 
					return false;
			case 1003:
				return sendQuestDialog(player, targetObjId, 1004);
		}
		return false;
	}
	
	public boolean defaultQuestEndDialog(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetObjId = env.getVisibleObject()==null ? 0 : env.getVisibleObject().getObjectId();
		switch (env.getDialogId())
		{
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
				if (QuestEngine.getInstance().getQuest(env).questFinish())
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjId, 10));
					return true;
				}
				return false;
			case 1009:
			case -1:
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs.getStatus() == QuestStatus.REWARD)
				{
					return sendQuestDialog(player, targetObjId, 5);
				}
		}
		return false;
	}
	/**
	 * @return the questId
	 */
	public Integer getQuestId()
	{
		return questId;
	}
	
	public boolean onDialogEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	public boolean onEnterWorldEvent(QuestEnv questEnv)
	{
		return false;
	}

	public boolean onEnterZoneEvent(QuestEnv questEnv, ZoneName zoneName)
	{
		return false;
	}

	public boolean onItemUseEvent(QuestEnv questEnv, Item item)
	{
		return false;
	}

	public boolean onKillEvent(QuestEnv questEnv)
	{
		return false;
	}

	public boolean onAttackEvent(QuestEnv questEnv)
	{
		return false;
	}

	public boolean onLvlUpEvent(QuestEnv questEnv)
	{
		return false;
	}
	
	public boolean onDieEvent(QuestEnv questEnv)
	{
		return false;
	}

	public boolean onMovieEndEvent(QuestEnv questEnv, int movieId)
	{
		return false;
	}
}
