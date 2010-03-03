/**
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

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Simple
 */

public class LegionCommand extends AdminCommand
{
	@Inject
	private LegionService	legionService;

	/**
	 * @param commandName
	 */

	public LegionCommand()
	{
		super("legion");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_LEGION)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if(params.length < 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //legion <disband|setlevel|setpoints|setname> <legion name> <value>");
			return;
		}
		Legion legion = legionService.getCachedLegion(params[1].toLowerCase());
		if(legion == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified legion is not in cached.");
			return;
		}

		if(params[0].toLowerCase().equals("disband"))
		{
			legionService.disbandLegion(legion);
			PacketSendUtility.sendMessage(admin, "The following legion has been disbanded: " + legion.getLegionName());
		}
		else if(params[0].toLowerCase().equals("setlevel"))
		{
			int newLevel = Integer.parseInt(params[2]);

			if(newLevel < 1 || newLevel > 5)
			{
				PacketSendUtility.sendMessage(admin, "Please use a valid legion level. (1 - 5)");
				return;
			}
			legionService.changeLevel(legion, newLevel);
			PacketSendUtility.sendMessage(admin, "The " + legion.getLegionName() + " legion has been leveled up to " + newLevel);
		}
		else if(params[0].toLowerCase().equals("setpoints"))
		{
			int newPoints = Integer.parseInt(params[2]);

			if(newPoints <= 0 || newPoints > 2000000000)
			{
				PacketSendUtility.sendMessage(admin, "Please use valid points amount. (0 - 2.000.000.000)");
				return;
			}
			legionService.setContributionPoints(legion, newPoints);
			PacketSendUtility.sendMessage(admin, "The " + legion.getLegionName() + " legion points have been changed to " + newPoints);
		}
		else if(params[0].toLowerCase().equals("setname"))
		{
			String newLegionName = params[2];

			if(!legionService.isValidName(newLegionName))
			{
				PacketSendUtility.sendMessage(admin, "Please use a valid legion name!");
				return;
			}
			legionService.setLegionName(legion, newLegionName);
			PacketSendUtility.sendMessage(admin, "The " + legion.getLegionName() + " legion's name has been changed to " + newLegionName);
		}

	}
}
