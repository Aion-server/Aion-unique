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
package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.WorkOrdersData;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke
 *
 */
public class WorkOrders extends QuestHandler
{
	private final WorkOrdersData workOrdersData;
	/**
	 * @param questId
	 */
	public WorkOrders(WorkOrdersData workOrdersData)
	{
		super(workOrdersData.getId());
		this.workOrdersData = workOrdersData;
		QuestEngine.getInstance().setNpcQuestData(workOrdersData.getStartNpcId()).addOnQuestStart(workOrdersData.getId());
		QuestEngine.getInstance().setNpcQuestData(workOrdersData.getStartNpcId()).addOnTalkEvent(workOrdersData.getId());
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != workOrdersData.getStartNpcId())
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(workOrdersData.getId());
		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLITE)
		{
			switch(env.getDialogId())
			{
				case 25:
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 4);
				case 1002:
					if (QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START))
					{
						if (QuestEngine.getInstance().addItems(player, workOrdersData.getGiveComponent()))
						{
							player.getRecipeList().addRecipe(player, DataManager.RECIPE_DATA.getRecipeTemplateById(workOrdersData.getRecipeId()));
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						}
						return true;
					}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START)
		{
			if (env.getDialogId() == 25)
				return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
			else if (env.getDialogId() == 17)
			{
				if (collectItemCheck(env))
				{
					//TODO: Random rewards
					qs.setStatus(QuestStatus.COMPLITE);
					qs.setCompliteCount(qs.getCompliteCount() + 1);
					updateQuestStatus(player, qs);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					return true;
				}
			}
		}
		return false;
	}
}
