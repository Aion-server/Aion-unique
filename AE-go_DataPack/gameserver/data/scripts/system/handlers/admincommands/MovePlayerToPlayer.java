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
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Admin moveplayertoplayer command.
 *
 * @author Tanelorn
 */
public class MovePlayerToPlayer extends AdminCommand
{
	
	@Inject
	private World	world;
/**
	 * Constructor.
	 */
	public MovePlayerToPlayer()
	{
		super("moveplayertoplayer");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeCommand(Player admin, String... params)
	{
		if (params == null || params.length < 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //moveplayertoplayer characterNameToMove characterNameDestination");
			return;
		}
		
		Player playerToMove = world.findPlayer(params[0]);
		if (playerToMove == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		Player playerDestination = world.findPlayer(params[1]);
		if (playerDestination == null)
		{
			PacketSendUtility.sendMessage(admin, "The destination player is not online.");
			return;
		}
		
		if (playerToMove == playerDestination) {
			PacketSendUtility.sendMessage(admin, "Cannot move the specified player to their own position.");
			return;
		}

		world.setPosition(playerToMove, playerDestination.getWorldId(), playerDestination.getX(), playerDestination.getY(), playerDestination.getZ(), playerDestination.getHeading());
		PacketSendUtility.sendPacket(playerToMove, new SM_UNKF5(playerToMove));
		
		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to the location of player " + playerDestination.getName() + ".");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by an administrator.");
	}
}
