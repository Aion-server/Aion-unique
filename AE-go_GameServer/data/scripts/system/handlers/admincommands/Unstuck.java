package admincommands;

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.BindPointTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
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
        world.despawn(admin);
        world.setPosition(admin, worldId, x, y, z, admin.getHeading());
        admin.setProtectionActive(true);

        PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
    }
}
