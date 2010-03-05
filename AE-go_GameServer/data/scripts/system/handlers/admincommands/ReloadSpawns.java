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

import java.util.Iterator;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author Luno
 * 
 */

public class ReloadSpawns extends AdminCommand
{
	@Inject
	private World		world;
	@Inject
	private SpawnEngine	spawnEngine;

	/**
	 * The constructor of Reload Spawns Command
	 */
	public ReloadSpawns()
	{
		super("reload_spawn");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_RELOADSPAWNS)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		// despawn all
		Iterator<AionObject> it = world.getObjectsIterator(); 
		while(it.hasNext())
		{
			AionObject obj = it.next();
			if(obj instanceof Npc)
			{
				((Npc) obj).getController().delete();
			}
		}
		
		// spawn all;
		spawnEngine.clearAll();
		spawnEngine.spawnAll(); 
	}
}
