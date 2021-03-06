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
package admincommands;

import java.util.NoSuchElementException;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author lord_rex
 * Command: //sprison <player> <delay>(minutes)
 * This command is sending player to prison.
 * 
 */
public class SendToPrison extends AdminCommand
{
	@Inject
	private PunishmentService punishmentService;
	@Inject
	private World	world;

	public SendToPrison()
	{
		super("sprison");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_PRISON)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command!");
			return;
		}

		if(params.length == 0 || params.length > 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //sprison <player> <delay>");
			return;
		}

		try
		{
			Player playerToPrison = world.findPlayer(Util.convertName(params[0]));
			int delay = Integer.parseInt(params[1]);

			if(playerToPrison != null)
			{
				punishmentService.setIsInPrison(playerToPrison, true, delay);
				PacketSendUtility.sendMessage(admin, "Player " + playerToPrison.getName() + " sent to prison for "
					+ delay + ".");
			}
		}
		catch(NoSuchElementException nsee)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //sprison <player> <delay>");
		}
		catch(Exception e)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //sprison <player> <delay>");
		}
	}
}
