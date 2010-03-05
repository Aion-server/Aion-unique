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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Phantom
 *
 */
public class AddSkill extends AdminCommand
{

	public AddSkill()
	{
		super("addskill");
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.AdminCommand#executeCommand(com.aionemu.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_ADDSKILL)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if(params.length != 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //addskill <skillId> <skillLevel>");
			return;
		}
		
		VisibleObject target = admin.getTarget();
		
		int skillId = 0;
		int skillLevel = 0;
		
		try
		{
			skillId = Integer.parseInt(params[0]);
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
        {
            Player player = (Player) target;
            player.getSkillList().addSkill(player, skillId, skillLevel, true);
            PacketSendUtility.sendMessage(admin, "You have success add skill");
            PacketSendUtility.sendMessage(player, "You have acquire a new skill");
        }
	}

}
