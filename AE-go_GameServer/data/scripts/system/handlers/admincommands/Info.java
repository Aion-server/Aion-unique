package admincommands;

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Nemiroff
 *         Date: 28.12.2009
 */
public class Info extends AdminCommand {

    public Info() {
        super("info");
    }

    @Override
    public void executeCommand(Player admin, String[] params) {
        if (admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_INFO)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

        if (admin.getTarget() == null)
        {
            PacketSendUtility.sendMessage(admin, "Your object id is : " + admin.getObjectId());
        }
        else
        {
            if (admin.getTarget() instanceof Creature)
            {
                Npc npc = (Npc) admin.getTarget();
                PacketSendUtility.sendMessage(admin, "Info about target:\n" + "Name=" + npc.getName() + "\nId= " + npc.getNpcId() + " ObjectId= " + admin.getTarget().getObjectId() + "\nX=" + admin.getTarget().getX() + " Y=" + admin.getTarget().getY() + " Z=" + admin.getTarget().getZ() + "\nHeading=" + admin.getTarget().getHeading());
            }
            else
            {
                PacketSendUtility.sendMessage(admin, "Your target objectId is : " + admin.getTarget().getObjectId() + "\nX=" + admin.getTarget().getX() + " Y=" + admin.getTarget().getY() + " Z=" + admin.getTarget().getZ() + "\nHeading=" + admin.getTarget().getHeading());
            }
        }
    }

}
