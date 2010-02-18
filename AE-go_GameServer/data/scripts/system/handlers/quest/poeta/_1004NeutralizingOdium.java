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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author MrPoke
 *
 */
public class _1004NeutralizingOdium extends QuestHandler
{
	private final static int	questId	= 1004;

	public _1004NeutralizingOdium()
	{
		super(questId);
		QuestEngine.getInstance().addQuestLvlUp(questId);
		QuestEngine.getInstance().setNpcQuestData(203082).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(700030).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(790001).addOnTalkEvent(questId);
		QuestEngine.getInstance().setNpcQuestData(203067).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203082)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else if (var == 5)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
					case 1013:
						if(var == 0)
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 19));
						return false;
					case 10000:
						if(player.getInventory().getItemCountByItemId(182200005) == 0)
							if (!QuestEngine.getInstance().addItem(player, new QuestItems(182200005, 1)))
								return true;
						qs.getQuestVars().setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
						return true;
					case 10002:
						if(var == 5)
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 700030 && var  == 1 || var == 4)
			{
				switch(env.getDialogId())
				{
					case -1:
						final int targetObjectId = env.getVisibleObject().getObjectId();
						PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
							1));
						PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 37, 0,
							targetObjectId), true);
						ThreadPoolManager.getInstance().schedule(new Runnable(){
							@Override
							public void run()
							{
								if(player.getTarget().getObjectId() != targetObjectId)
									return;
								PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
									targetObjectId, 3000, 0));
								PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 38, 0,
									targetObjectId), true);
								QuestState qs = player.getQuestStateList().getQuestState(questId);
								qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0)+1);
								updateQuestStatus(player, qs);
								player.getInventory().removeFromBagByItemId(182200005, 1);
								PacketSendUtility.broadcastPacket(player.getTarget(), new SM_EMOTION((Creature)player.getTarget(), 16, 128, 0));
							}
						}, 3000);
						return true;
				}
			}
			else if(targetId == 790001)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 2)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						else if (var == 3)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
						else if (var == 11)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1694);
					case 10001:
						if(var == 2)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
					case 10002:
						if(var == 11)
						{
							qs.getQuestVars().setQuestVarById(0, 4);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
					case 33:
						{
							if(collectItemCheck(env))
							{
								qs.getQuestVars().setQuestVarById(0, 11);
								updateQuestStatus(player, qs);
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1694);
							}
							else
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 203067)
				return defaultQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		if(player.getCommonData().getLevel() < 4
			|| qs.getStatus() != QuestStatus.LOCKED)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
}
