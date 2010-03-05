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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;
import com.google.inject.Inject;

/**
 * Admin moveto command
 * 
 * @author KID
 */

public class MoveTo extends AdminCommand
{
	@SuppressWarnings("unused")
	private final World		world;
	private TeleportService teleportService;

	/**
	 * Constructor.
	 */

	@Inject
	public MoveTo(World world, TeleportService teleportService)
	{
		super("moveto");
		this.world = world; 
		this.teleportService = teleportService;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_MOVETO)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 4)
		{
			PacketSendUtility.sendMessage(admin, "syntax //moveto worldId X Y Z");
			return;
		}

		int worldId;
		float x, y, z;

		try
		{
			worldId = Integer.parseInt(params[0]);
			x = Float.parseFloat(params[1]);
			y = Float.parseFloat(params[2]);
			z = Float.parseFloat(params[3]);
		}
		catch(NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "All the parameters should be numbers");
			return;
		}
		
		if( WorldMapType.getWorld(worldId) == null)
		{
			PacketSendUtility.sendMessage(admin, "Illegal WorldId %d " + worldId );
		}
		else
		{
			teleportService.teleportTo(admin, worldId, x, y, z, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
		}
	}
}
