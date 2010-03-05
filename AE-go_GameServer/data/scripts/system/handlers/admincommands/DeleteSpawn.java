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
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Luno
 * 
 */

public class DeleteSpawn extends AdminCommand
{
	private final SpawnsData		spawnsData;

	@Inject
	public DeleteSpawn(SpawnsData spawnsData)
	{
		super("delete");
		this.spawnsData = spawnsData;
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_DELETESPAWN)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		VisibleObject cre = admin.getTarget();
		if(!(cre instanceof Npc))
		{
			PacketSendUtility.sendMessage(admin, "Wrong target");
			return;
		}
		Npc npc = (Npc) cre;
		spawnsData.removeSpawn(npc.getSpawn());
		npc.getController().delete();
		PacketSendUtility.sendMessage(admin, "Spawn removed");
	}
}
