/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package admincommands;

import java.util.Iterator;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Admin announce faction
 *
 * @author Divinity
 */

public class AnnounceFaction extends AdminCommand
{
	public AnnounceFaction()
	{
		super("announcefaction");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if (admin.getAccessLevel() < AdminConfig.COMMAND_ANNOUNCE_FACTION)
		{
			PacketSendUtility.sendMessage(admin, "You don't have enough rights to execute this command.");
			return;
		}
		
		if (params.length < 2)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //announcefaction <ely | asmo> <message>");
		}
		else
		{
			Iterator<Player> iter = admin.getActiveRegion().getWorld().getPlayersIterator();
			String message = null;
			
			if (params[0].equals("ely"))
				message = "Elyos : ";
			else
				message = "Asmodians : ";
			
			// Add with space
			for (int i=1; i<params.length-1; i++)
				message += params[i] + " ";
			
			// Add the last without the end space
			message += params[params.length-1];
			
			Player player = null;
			
			while (iter.hasNext())
			{
				player = iter.next();
				
				if (player.getCommonData().getRace() == Race.ELYOS && params[0].equals("ely"))
					PacketSendUtility.sendSysMessage(player, message);
				else if (player.getCommonData().getRace() == Race.ASMODIANS && params[0].equals("asmo"))
					PacketSendUtility.sendSysMessage(player, message);
			}
		}
	}
}
