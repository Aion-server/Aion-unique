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

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * 
 */
public class MonsterHunt extends QuestHandler
{
	private final int							questId;
	private final int							startNpc;
	private final FastMap<Integer, MonsterInfo>	monsterInfo;

	/**
	 * @param questId
	 */
	public MonsterHunt(int questId, int startNpc, FastMap<Integer, MonsterInfo> monsterInfo)
	{
		super(questId);
		this.questId = questId;
		this.startNpc = startNpc;
		this.monsterInfo = monsterInfo;
		QuestEngine.getInstance().setNpcQuestData(startNpc).addOnQuestStart(questId);
		QuestEngine.getInstance().setNpcQuestData(startNpc).addOnTalkEvent(questId);
		for(int monsterId : monsterInfo.keySet())
			QuestEngine.getInstance().setNpcQuestData(monsterId).addOnKillEvent(questId);
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
				else
					return defaultQuestStartDialog(env);
			}
			else if(qs.getStatus() == QuestStatus.START)
			{
				for(MonsterInfo mi : monsterInfo.values())
				{
					if(mi.getMaxKill() < qs.getQuestVars().getQuestVarById(mi.getVarId()))
						return false;
				}
				if(env.getDialogId() == 25)
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
				else if(env.getDialogId() == 1009)
				{
					qs.setStatus(QuestStatus.REWARD);
					qs.getQuestVars().setQuestVarById(0, qs.getQuestVars().getQuestVarById(0) + 1);
					updateQuestStatus(player, qs);
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
				}
				else
					return defaultQuestEndDialog(env);
			}
			else if(qs.getStatus() == QuestStatus.REWARD)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() != QuestStatus.START)
			return false;
		MonsterInfo mi = monsterInfo.get(targetId);
		if(mi == null)
			return false;
		if(mi.getMaxKill() <= qs.getQuestVars().getQuestVarById(mi.getVarId()))
			return false;

		qs.getQuestVars().setQuestVarById(mi.getVarId(), qs.getQuestVars().getQuestVarById(mi.varId) + 1);
		updateQuestStatus(player, qs);
		return true;
	}

	public static class MonsterInfo
	{
		private final int	varId;
		private final int	maxKill;

		/**
		 * @param varId
		 * @param maxKill
		 */
		public MonsterInfo(int varId, int maxKill)
		{
			this.varId = varId;
			this.maxKill = maxKill;
		}

		/**
		 * @return the varId
		 */
		public int getVarId()
		{
			return varId;
		}

		/**
		 * @return the maxKill
		 */
		public int getMaxKill()
		{
			return maxKill;
		}
	}
}
