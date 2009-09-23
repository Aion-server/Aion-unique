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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Luno
 * 
 */
public class SpawnNpc extends AdminCommand
{
	private final SpawnData		spawnData;

	private final SpawnEngine	spawnService;

	/**
	 * @param spawnData
	 * @param spawnService
	 */
	@Inject
	public SpawnNpc(SpawnData spawnData, SpawnEngine spawnService)
	{
		super("spawn");
		this.spawnData = spawnData;
		this.spawnService = spawnService;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.
	 * gameobjects.Player, java.lang.String[])
	 */
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //spawn <npc_id>");
			return;
		}
		int npcId = Integer.parseInt(params[0]);
		float x = admin.getX();
		float y = admin.getY();
		float z = admin.getZ();
		byte heading = admin.getHeading();
		int worldId = admin.getWorldId();

		SpawnTemplate spawn = spawnData.addNewSpawn(worldId, npcId, x, y, z, heading);

		if(spawn == null)
		{
			PacketSendUtility.sendMessage(admin, "There is no npc with id " + npcId);
			return;
		}
		Npc npc = spawnService.spawnNpc(spawn);

		PacketSendUtility.sendMessage(admin, npc.getTemplate().getName()
			+ " spawned. //save_spawn   command will save whole spawndata to file");
	}
}
