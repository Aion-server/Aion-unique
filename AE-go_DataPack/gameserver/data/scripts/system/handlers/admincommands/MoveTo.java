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
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Admin moveto command
 * 
 * @author KID
 */
public class MoveTo extends AdminCommand
{
	private final World		world;

	/**
	 * Constructor.
	 */
	@Inject
	public MoveTo(World world)
	{
		super("moveto");

		this.world = world; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
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
		world.despawn(admin);
		// TODO! this should go to PlayerController.teleportTo(...)
		// more todo: when teleporting to the same map then SM_UNKF5 should not be send, but something else
		world.setPosition(admin, worldId, x, y, z, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
	}
}
