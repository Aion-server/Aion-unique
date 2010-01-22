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
package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * 
 */
public class ItemCollecting extends QuestHandler
{

	private final int	questId;
	private final int	startNpcId;
	private final int	actionItemId;
	private final int	endNpcId;

	/**
	 * @param questId
	 * @param startNpcId
	 * @param endNpcId
	 * @param actionItemId
	 */
	public ItemCollecting(int questId, int startNpcId, int actionItemId, int endNpcId)
	{
		super(questId);
		this.questId = questId;
		this.startNpcId = startNpcId;
		this.actionItemId = actionItemId;
		if (endNpcId != 0)
			this.endNpcId = endNpcId;
		else
			this.endNpcId = startNpcId;
		QuestEngine.getInstance().setNpcQuestData(startNpcId).addOnQuestStart(questId);
		QuestEngine.getInstance().setNpcQuestData(startNpcId).addOnTalkEvent(questId);
		if(actionItemId != startNpcId)
			QuestEngine.getInstance().setNpcQuestData(actionItemId).addOnTalkEvent(questId);
		if (endNpcId != 0)
			QuestEngine.getInstance().setNpcQuestData(endNpcId).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == startNpcId)
		{
			if(qs == null)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if (targetId == endNpcId)
		{
			if(qs != null && qs.getStatus() == QuestStatus.START)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else if(env.getDialogId() == 33)
				{
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
						qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0) + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(player, qs);
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
					}
					else
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
				}
			}
			else if(qs.getStatus() == QuestStatus.REWARD)
			{
				return defaultQuestEndDialog(env);
			}
			return false;
		}
		else if(targetId == actionItemId && targetId != 0)
		{
			if(qs != null && qs.getStatus() == QuestStatus.START)
			{
				return true;
			}
		}
		return false;
	}
}
