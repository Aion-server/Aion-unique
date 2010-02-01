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
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Luno
 */

public class SpawnNpc extends AdminCommand {

    private final SpawnsData spawnsData;

    private final SpawnEngine spawnEngine;

    /**
     * @param spawnData
     * @param spawnService
     */

    @Inject
    public SpawnNpc(SpawnsData spawnsData, SpawnEngine spawnEngine) {
        super("spawn");
        this.spawnsData = spawnsData;
        this.spawnEngine = spawnEngine;
    }

    @Override
    public void executeCommand(Player admin, String[] params) {
        if (admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_SPAWNNPC)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

        if (params.length < 1)
        {
            PacketSendUtility.sendMessage(admin, "syntax //spawn <template_id> {norespawn}");
            return;
        }
        
        boolean respawn = true;
        
        if(params.length == 2 && "norespawn".equalsIgnoreCase(params[1]))
        	respawn = false;

        int templateId = Integer.parseInt(params[0]);
        float x = admin.getX();
        float y = admin.getY();
        float z = admin.getZ();
        byte heading = admin.getHeading();
        int worldId = admin.getWorldId();

        SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, 1, templateId, x, y, z, heading, 0, 0, respawn, true);

        if (spawn == null)
        {
            PacketSendUtility.sendMessage(admin, "There is no template with id " + templateId);
            return;
        }

        VisibleObject visibleObject = spawnEngine.spawnObject(spawn, 1);
        String objectName = "";
        if (visibleObject instanceof Npc)
        {
            objectName = ((Npc) visibleObject).getTemplate().getName();
        }
        else if (visibleObject instanceof Gatherable)
        {
            objectName = ((Gatherable) visibleObject).getTemplate().getName();
        }

        PacketSendUtility.sendMessage(admin, objectName + " spawned");
    }
}
