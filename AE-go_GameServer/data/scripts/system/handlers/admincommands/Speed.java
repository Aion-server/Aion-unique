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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * 
 * @author ATracer
 *
 */

public class Speed extends AdminCommand
{
	public Speed()
	{
		super("speed");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_SPEED)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Syntax //speed <percent>");
			return;
		}

		int parameter = 0;
		try
		{
			parameter = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Parameter should number");
			return;
		}
		
		if(parameter < 0 || parameter > 200)
		{
			PacketSendUtility.sendMessage(admin, "Valid values are in 0-200 range");
			return;
		}

		int speed = 6000;
		int flyspeed = 9000;

		admin.getGameStats().setStat(StatEnum.SPEED, (speed + (speed * parameter) / 100));
		admin.getGameStats().setStat(StatEnum.FLY_SPEED, (flyspeed + (flyspeed * parameter) / 100));
		PacketSendUtility.broadcastPacket(admin, new SM_EMOTION(admin, 30, 0, 0), true);
	}
}
