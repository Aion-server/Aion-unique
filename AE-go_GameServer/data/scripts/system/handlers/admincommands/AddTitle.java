/*
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

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author xavier
 * 
 */
public class AddTitle extends AdminCommand
{
	@Inject
	private World	world;

	protected AddTitle()
	{
		super("addtitle");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_ADDTITLE)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if((params.length < 1) || (params.length > 2))
		{
			PacketSendUtility.sendMessage(admin, "syntax //addtitle title_id [playerName]");
			return;
		}

		int titleId = Integer.parseInt(params[0]);
		if((titleId > 50) || (titleId < 1))
		{
			PacketSendUtility.sendMessage(admin, "title id " + titleId + " is invalid (must be between 1 and 50)");
			return;
		}

		Player target = null;
		if(params.length == 2)
		{
			target = world.findPlayer(Util.convertName(params[1]));
			if(target == null)
			{
				PacketSendUtility.sendMessage(admin, "player " + params[1] + " was not found");
				return;
			}
		}
		else
		{
			VisibleObject o = admin.getTarget();
			if((!(o instanceof Player)) || (o == null))
			{
				target = admin;
			}
		}

		titleId = target.getCommonData().getRace().getRaceId() * 50 + titleId;
		if(!target.getTitleList().addTitle(titleId))
		{
			PacketSendUtility.sendMessage(admin, "you can't add title #" + titleId + " to "
				+ (target.equals(admin) ? "yourself" : target.getName()));
		}
		else
		{
			if(target.equals(admin))
			{
				PacketSendUtility.sendMessage(admin, "you added to yourself title #" + titleId);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "you added to " + target.getName() + " title #" + titleId);
				PacketSendUtility.sendMessage(target, admin.getName() + " gave you title #" + titleId);
			}
		}
	}

}
