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
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMap;
import com.google.inject.Inject;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
/**
 * Admin promote command.
 *
 * @author Cyrakuse
 */
public class Promote extends AdminCommand
{
	@Inject
	private World	world;

	/**
	 * Constructor.
	 */
	public Promote()
	{
	super("promote");
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public void executeCommand(Player admin, String... params)
	{
		if (params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //promote characterName");
			return;
		}

		
		
		Player player = world.findPlayer(params[0]);
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		player.getCommonData().setAdmin(true);
		PacketSendUtility.sendMessage(admin, player.getName() + " has been promoted Administrator");
		PacketSendUtility.sendMessage(player, "You have been promoted Administrator.");
	}
}
