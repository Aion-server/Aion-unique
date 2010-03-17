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
package quest.ascension;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author MrPoke +Dune11
 *
 */
public class _1914DispatchtoVerteron extends QuestHandler
{
	@Inject
	TeleportService teleportService;
	private final static int	questId	= 1914;

	@Inject
	public _1914DispatchtoVerteron()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		if(CustomConfig.ENABLE_SIMPLE_2NDCLASS)
			return;
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203726).addOnTalkEvent(questId);
		qe.setNpcQuestData(203097).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203726)
			{
				switch(env.getDialogId())
				{
					case 25:
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
					case 1007:
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
					case 10000:
						{
							qs.setQuestVar(1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(),0));
							
							teleportService.teleportTo(player, 210030000, 1, 1646, 1498, 119, 0);
							qs.setStatus(QuestStatus.REWARD);
							return true;
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 203097 && var == 1)
			{
				switch (env.getDialogId())
				{
					case -1:
					case 1009:
						return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 5);
					case 8:
						return defaultQuestEndDialog(env);
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 17:
				}
			}
		}
		return false;
	}
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs != null)
			return false;

		QuestState qs2 = player.getQuestStateList().getQuestState(1007);
		if(qs2 == null || qs2.getStatus() != QuestStatus.COMPLITE)
			return false;
		env.setQuestId(questId);
		questService.startQuest(env, QuestStatus.START);
		return true;
	}
}
