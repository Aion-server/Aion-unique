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

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Luno
 * 
 */

public class SaveSpawnData extends AdminCommand
{
	@Inject
	private SpawnsData	spawnsData;

	public SaveSpawnData()
	{
		super("save_spawn");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_SAVESPAWNDATA)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		//TODO need to be reworked
		PacketSendUtility.sendMessage(admin, "Not implemented yet");
//		if(spawnData.saveData())
//		{
//			PacketSendUtility.sendMessage(admin, "new_spawndata.txt saved");
//		}
//		else
//		{
//			PacketSendUtility.sendMessage(admin, "Some error occured while saving spawndata.txt check server console");
//		}
	}
}
