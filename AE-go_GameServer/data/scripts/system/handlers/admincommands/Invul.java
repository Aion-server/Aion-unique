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

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Andy
 * @author Divinity - update
 */

public class Invul extends AdminCommand
{
	public Invul()
	{
		super("invul");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_INVUL)
		{
			PacketSendUtility.sendMessage(admin, "You do not have sufficient rights to execute this command");
			return;
		}

		if(admin.isInvul())
		{
			admin.setInvul(false);
			PacketSendUtility.sendMessage(admin, "You are now mortal.");
		}
		else
		{
			admin.setInvul(true);
			PacketSendUtility.sendMessage(admin, "You are now immortal.");
		}
	}
}
