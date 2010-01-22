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
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
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
public class _1005BarringtheGate extends QuestHandler
{

	private final static int	questId	= 1005;

	public _1005BarringtheGate()
	{
		super(questId);
		int[] talkNpcs = {203067, 203081, 790001, 203085, 203086, 700080, 700081, 700082, 700083, 203067};
		QuestEngine.getInstance().addQuestLvlUp(questId);
		for (int id : talkNpcs)
			QuestEngine.getInstance().setNpcQuestData(id).addOnTalkEvent(questId);
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
			if(targetId == 203067)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 0)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					case 10000:
						if(var == 0)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 203081)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 1)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					case 10001:
						if(var == 1)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 790001)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 2)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					case 10002:
						if(var == 2)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 203085)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 3)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					case 10002:
						if(var == 3)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 203086)
			{
				switch(env.getDialogId())
				{
					case 25:
						if(var == 4)
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
					case 10002:
						if(var == 4)
						{
							qs.getQuestVars().setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
							.getObjectId(), 10));
							return true;
						}
				}
			}
			else if(targetId == 700081)
			{
				if (var == 5)
				{
					destroy(6, env);
					return true;
				}
			}
			else if(targetId == 700082)
			{
				if (var == 6)
				{
					destroy(7, env);
					return true;
				}
			}
			else if(targetId == 700082)
			{
				if (var == 7)
				{
					destroy(8, env);
					return true;
				}
			}
			else if(targetId == 700080)
			{
				if (var == 8)
				{
					destroy(-1, env);
					return true;
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
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		int[] quests = {1001, 1002, 1003, 1004};
		for (int id : quests)
		{
			QuestState qs2 = player.getQuestStateList().getQuestState(id);
			if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLITE)
				return false;
		}

		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

	private void destroy(final int var, QuestEnv env)
	{
		final int targetObjectId = env.getVisibleObject().getObjectId();
		
		final Player player = env.getPlayer();
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
				PacketSendUtility.broadcastPacket(player.getTarget(), new SM_EMOTION((Creature)player.getTarget(), 16, 128, 0));
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (var != -1)
					qs.getQuestVars().setQuestVarById(0, var);
				else
					qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(player, qs);
			}
		}, 3000);
	}
}
