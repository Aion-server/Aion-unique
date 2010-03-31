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
package quest.poeta;

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.google.inject.Inject;

/**
 * @author Nephis http://www.evolution-fr.com/
 * 
 */
public class _1114TheNymphsGrown extends QuestHandler
{
	@Inject
	ItemService itemService;
	
	private final static int	questId	= 1114;

	public _1114TheNymphsGrown()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(700008).addOnTalkEvent(questId);
		qe.setNpcQuestData(203075).addOnTalkEvent(questId);
		qe.setNpcQuestData(203058).addOnTalkEvent(questId);
		qe.setQuestItemIds(182200214).add(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId == 0)
		{
			if(env.getDialogId() == 1002)
			{
				questService.startQuest(env, QuestStatus.START);
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
				return true;
			}
		}
		else if(targetId == 203075)
		{
			if(qs != null)
			{
				if(env.getDialogId() == 25 && qs.getStatus() == QuestStatus.START)
				{
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
				}
				else if(env.getDialogId() == 10000)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					updateQuestStatus(player, qs);			
					return true;					
				}
				else if(env.getDialogId() == 10001)
				{		
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 5));
					return false;
				}
			}

				if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3)
				{
					qs.setQuestVar(3);
					updateQuestStatus(player, qs);	
					qs.setStatus(QuestStatus.REWARD);
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
				}
				else if(env.getDialogId() == 1009)
				{
					player.getInventory().removeFromBagByItemId(182200217, 1);
					qs.setQuestVar(3);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					return defaultQuestEndDialog(env);
				}
				else if(env.getDialogId() == 10001)
				{		
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
				}
				else
					return defaultQuestStartDialog(env);
		}
				else if(targetId == 203058)
		{
			if(qs != null)
			{
			if(qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3)
					qs.setQuestVar(3);
					updateQuestStatus(player, qs);	
					qs.setStatus(QuestStatus.REWARD);
			{
				if(env.getDialogId() == 1009)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
				else if(env.getDialogId() == 5)
				{
					qs.setQuestVar(3);
					player.getInventory().removeFromBagByItemId(182200217, 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					return defaultQuestEndDialog(env);
				}
				else
					return defaultQuestEndDialog(env);
			}
						}
		}
		
		{
			switch(targetId)
			{
				case 700008:
				{

					if (qs.getQuestVarById(0) == 1 && env.getDialogId() == -1)
					{
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 2);
						updateQuestStatus(player, qs);
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
									return;
								PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
									targetObjectId, 3000, 0));
								PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,
									targetObjectId), true);
								itemService.addItems(player, Collections.singletonList(new QuestItems(182200217, 1)));
								((Npc)player.getTarget()).getController().onDie(null);
								
							}
						}, 3000);
					}
				}

			}
		}
		return false;
	}

	@Override
	public boolean onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if(id != 182200214)
			return false;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id,
			3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId,
					id, 0, 1, 0), true);
				sendQuestDialog(player, 0, 4);
			}
		}, 3000);
		return true;
	}
}
