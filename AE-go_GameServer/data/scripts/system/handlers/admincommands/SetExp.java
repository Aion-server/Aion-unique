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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Admin setlevel command
 *
 * @author IceReaper
 */
public class SetExp extends AdminCommand
{

	public SetExp()
	{
		super("setexp");
	}

	/*
	 *  (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String... params)
	{
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //setexp level");

			return;
		}

		long	exp;

		try
		{
			exp = Long.parseLong(params[0]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "All the parameters should be numbers");

			return;
		}

		Creature cre = admin.getTarget();
		if(cre instanceof Player)
		{
			Player player = (Player)cre;
			player.getCommonData().setExp(exp);
		}
		else
		{
			admin.getCommonData().setExp(exp);
		}

		PacketSendUtility.sendMessage(admin, "Set your exp to " + exp);
	}
}