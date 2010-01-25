/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author Nemiroff
 *         Date: 11.01.2010
 */
public class Unstuck extends AdminCommand {


    @Inject
    private World world;
    private float x, y, z;
    private BindPointTemplate bplist;
    private PlayerInitialData.LocationData locationData;
    private int worldId;

    public Unstuck() {
        super("unstuck");
    }


    /**
     * Execute admin command represented by this class, with a given list of parametrs.
     *
     * @param admin
     * @param params
     */
    @Override
    public void executeCommand(Player admin, String[] params) {

        if (admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_UNSTUCK) {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

        /**
         * get place where to spawn.
         */

        int bindPointId = admin.getCommonData().getBindPoint();

        if (bindPointId != 0) {
            bplist = DataManager.BIND_POINT_DATA.getBindPointTemplate2(bindPointId);
            worldId = bplist.getZoneId();
            x = bplist.getX();
            y = bplist.getY();
            z = bplist.getZ();
        } else {
            locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(admin.getCommonData().getRace());
            worldId = locationData.getMapId();
            x = locationData.getX();
            y = locationData.getY();
            z = locationData.getZ();
        }

        /**
         * Spawn player.
         */
        admin.getController().teleportTo(worldId, x, y, z, 0);
    }
}
