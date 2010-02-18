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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke Like: Sleeping on the Job quest.
 */
public class ReportTo extends QuestHandler
{
	private final int	questId;
	private final int	startNpc;
	private final int	endNpc;
	private final int	itemId;

	/**
	 * @param questId
	 * @param startNpc
	 * @param endNpc
	 */
	public ReportTo(int questId, int startNpc, int endNpc, int itemId)
	{
		super(questId);
		this.startNpc = startNpc;
		this.endNpc = endNpc;
		this.questId = questId;
		this.itemId = itemId;

		QuestEngine.getInstance().setNpcQuestData(startNpc).addOnQuestStart(questId);
		QuestEngine.getInstance().setNpcQuestData(startNpc).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(endNpc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == startNpc)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				else if(env.getDialogId() == 1002 && itemId != 0)
				{
					if (QuestEngine.getInstance().addItem(player, new QuestItems(itemId, 1)))
						return defaultQuestStartDialog(env);
					return true;
				}
				else
					return defaultQuestStartDialog(env);
			}
		}
		else if(targetId == endNpc)
		{
			if(qs != null)
			{
				if(env.getDialogId() == 25 && qs.getStatus() == QuestStatus.START)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else if(env.getDialogId() == 1009)
				{
					if(itemId != 0)
						player.getInventory().removeFromBagByItemId(itemId, 1);
					qs.getQuestVars().setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					return defaultQuestEndDialog(env);
				}
				else
					return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
}
