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
package quest.sanctum;

import java.util.Collections;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/* @author edynamic90 */
 
public class _1909SongOfPraise extends QuestHandler
	{
	private final static int questId = 1909;
	
	@Inject
	ItemService itemService; 
	
	public _1909SongOfPraise()
		{
		super(questId);
		}

	@Override
	public void register()
		{
		qe.setNpcQuestData(203739).addOnQuestStart(questId);
		qe.setNpcQuestData(203739).addOnTalkEvent(questId);
		qe.setNpcQuestData(203726).addOnTalkEvent(questId);
		qe.setNpcQuestData(203099).addOnTalkEvent(questId);
		}		
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
		{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc) targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 203739)
			{
			if(env.getDialogId() == 25)
				return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
			else
				return defaultQuestStartDialog(env);
			}
		else
			{
			int var = qs.getQuestVarById(0);
			if(targetId == 203099 && var >= 1)
				{
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
				else if(env.getDialogId() == 1009)
					{
					player.getInventory().removeFromBagByItemId(182206001, 1);
					qs.setQuestVar(2);
					updateQuestStatus(player, qs);
					qs.setStatus(QuestStatus.REWARD);
					return defaultQuestEndDialog(env);
					}
				else
					return defaultQuestEndDialog(env);
				}
			else if(qs != null && qs.getStatus() == QuestStatus.START && targetId == 203726)
				{
				switch(env.getDialogId())
					{
					case 25:
						if(var == 0) return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					case 10000:
						if(player.getInventory().getItemCountByItemId(182206001) == 0)
							if(!itemService.addItems(player, Collections.singletonList(new QuestItems(182206001, 1))))
								return true;
						qs.setQuestVar(1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					default:
						return defaultQuestStartDialog(env);
					}
				}
			}
		return false;
		}
	}

