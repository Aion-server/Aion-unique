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

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Admin movetoplayer command.
 *
 * @author Tanelorn
 */

public class MoveToPlayer extends AdminCommand
{
	@Inject
	private TeleportService teleportService;
	@Inject
	private World	world;

	/**
	 * Constructor.
	 */

	public MoveToPlayer()
	{
		super("movetoplayer");
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_MOVETOPLAYER)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //movetoplayer characterName");
			return;
		}

		Player player = world.findPlayer(Util.convertName(params[0]));
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		if (player == admin)
		{
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}

		teleportService.teleportTo(admin, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading(), 0);
		PacketSendUtility.sendMessage(admin, "Teleported to player " + player.getName() + ".");
	}
}
