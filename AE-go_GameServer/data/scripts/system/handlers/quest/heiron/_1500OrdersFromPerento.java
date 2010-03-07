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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke + Dune11
 * 
 */
public class _1500OrdersFromPerento extends QuestHandler
{

	private final static int	questId	= 1500;

	public _1500OrdersFromPerento()
	{
		super(questId);
		QuestEngine.getInstance().setNpcQuestData(204500).addOnTalkEvent(questId);
		QuestEngine.getInstance().setQuestEnterZone(ZoneName.NEW_HEIRON_GATE).add(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 30 || qs.getStatus() != QuestStatus.LOCKED)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(targetId != 204500)
			return false;
		if(qs.getStatus() == QuestStatus.START)
		{
			if(env.getDialogId() == 25)
			{
				qs.getQuestVars().setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(player, qs);
				return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
			}
			else
				return defaultQuestStartDialog(env);
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(env.getDialogId() == 17)
			{
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1051, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1052, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1054, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1053, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1055, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1056, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1059, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1057, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1058, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1062, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
				QuestEngine.getInstance().getQuest(
					new QuestEnv(env.getVisibleObject(), env.getPlayer(), 1063, env.getDialogId())).startQuest(
					QuestStatus.LOCKED);
			}
			return defaultQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		if(zoneName != ZoneName.NEW_HEIRON_GATE)
			return false;
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
			return false;
		env.setQuestId(questId);
		QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START);
		return true;
	}
}
