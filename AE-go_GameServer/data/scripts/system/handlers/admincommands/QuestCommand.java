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
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_STEP;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke
 *
 */
public class QuestCommand extends AdminCommand
{
	public QuestCommand()
	{
		super("quest");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_QUESTCOMMAND)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set>");
			return;
		}
		Player target = null;
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Player)
		{
			target = (Player)creature;
		}
			
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "Incorrect target!");
			return;
		}

		if(params[0].equals("start"))
		{
			if (params.length != 2)
			{
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}
			int id;
			try
			{
				id = Integer.valueOf(params[1]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}

			Quest quest;
			QuestEnv env = new QuestEnv(null, target, id, 0);
			quest = QuestEngine.getInstance().getQuest(env);
			if (quest == null)
			{
				PacketSendUtility.sendMessage(admin, "Incorrect quest Id.");
				return;
			}

				if (quest.startQuest(QuestStatus.START))
				{
					PacketSendUtility.sendMessage(admin, "Quest started.");
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "Quest not started.");
				}
		}
		else if(params[0].equals("set"))
		{
			int questId,var;
			QuestStatus questStatus;
			try
			{
				questId = Integer.valueOf(params[1]);
				questStatus = QuestStatus.valueOf(params[2]);
				var = Integer.valueOf(params[3]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "syntax //quest set <questId status var>");
				return;
			}
			QuestState qs = target.getQuestStateList().getQuestState(questId);
			if (qs == null)
			{
				PacketSendUtility.sendMessage(admin, "syntax //quest set <questId status var>");
				return;
			}
			qs.setStatus(questStatus);
			qs.getQuestVars().setQuestVar(var);
			PacketSendUtility.sendPacket(target, new SM_QUEST_STEP(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
		else 
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set>");
		return;

	}
}
