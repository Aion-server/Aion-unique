package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Nemiroff
 *         Date: 28.12.2009
 */
public class Info extends AdminCommand
{

    public Info()
	{
        super("info");
    }

    @Override
    public void executeCommand(Player admin, String[] params)
	{
        if (admin.getAccessLevel() < AdminConfig.COMMAND_INFO)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }

		VisibleObject target = admin.getTarget();

        if (target == null || target.equals(admin))
        {
            PacketSendUtility.sendMessage(admin, "Your object id is : " + admin.getObjectId());
        }
        else
        {
			if (target instanceof Npc)
			{
                Npc npc = (Npc) admin.getTarget();
                PacketSendUtility.sendMessage(admin, "[Info about npc]\n" + "Name: " + npc.getName() + "\nId: " + npc.getNpcId() + " / ObjectId: " + admin.getTarget().getObjectId() + "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ() + " / Heading: " + admin.getTarget().getHeading());
            }
			else if (target instanceof Gatherable)
			{
                Gatherable gather = (Gatherable) target;
                PacketSendUtility.sendMessage(admin, "[Info about gather]\n" + "Name: " + gather.getName() + "\nId: " + gather.getObjectTemplate().getTemplateId() + " / ObjectId: " + admin.getTarget().getObjectId() + "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ() + " / Heading: " + admin.getTarget().getHeading());
            }
        }
    }
}
