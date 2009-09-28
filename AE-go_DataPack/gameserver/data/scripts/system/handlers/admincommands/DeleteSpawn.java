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

import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
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
	private final SpawnData		spawnData;

	@Inject
	public DeleteSpawn(SpawnData spawnData)
	{
		super("delete");

		this.spawnData = spawnData;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.
	 * gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		Creature cre = admin.getTarget();
		if(!(cre instanceof Npc))
		{
			PacketSendUtility.sendMessage(admin, "Wrong target");
			return;
		}
		Npc npc = (Npc) cre;
		spawnData.removeSpawn(npc.getSpawn());
		npc.getController().delete();
		PacketSendUtility.sendMessage(admin, "Spawn removed to save changes type //save_spawn");
	}
}
