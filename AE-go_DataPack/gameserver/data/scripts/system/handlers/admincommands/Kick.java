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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Elusive
 *
 */
public class Kick extends AdminCommand
{
	@Inject
	private World	world;
/**
	 * @param commandName
	 */
	public Kick()
	{
		super("kick");
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String... params)
	{
		if(params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //kick <character_name>");
			return;
		}
		Player player = world.findPlayer(params[0]);
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		player.getClientConnection().close(new SM_QUIT_RESPONSE(), true);
		PacketSendUtility.sendMessage(admin, "Kicked player : " + player.getName());
	}

}
