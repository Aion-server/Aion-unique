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

import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Luno
 * 
 */
public class ReloadSpawns extends AdminCommand
{
	@Inject
	private SpawnData	spawnData;
	@Inject
	private World		world;
	@Inject
	private SpawnEngine	spawnEngine;

	/**
	 * @param commandName
	 */
	public ReloadSpawns()
	{
		super("reload_spawn");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.model
	 * .gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		Iterator<AionObject> it = world.getObjectsIterator(); // despawn all
		while(it.hasNext())
		{
			AionObject obj = it.next();
			if(obj instanceof Npc)
			{
				((Npc) obj).getController().delete();
			}
		}
		spawnData.reloadData(); // reload spawns from files
		spawnEngine.spawnAll(spawnData); // spawn all;
	}
}
