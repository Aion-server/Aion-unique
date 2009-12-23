/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;



public class QuestCommand extends AdminCommand
{
	public QuestCommand()
	{
		super("quest");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_QUESTCOMMAND)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|delete|step|info|vars>");
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

				if (quest.startQuest())
				{
					PacketSendUtility.sendMessage(admin, "Quest started.");
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "Quest not started.");
				}
		}
		else 
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|delete|step|info|vars>");
		return;

	}
}
