/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;


public class PrintId extends AdminCommand
{
    public PrintId()
    {
        super("printid");
    }

    /*
    *  (non-Javadoc)
    * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
    */
    @Override
    public void executeCommand(Player admin, String... params)
    {
        if(params == null || params.length < 1)
        {
            PacketSendUtility.sendMessage(admin, "syntax //printid 0 (self) 1 (selected target)");

            return;
        }

        int parameter = 0;
        try
        {
            parameter = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e)
        {
            PacketSendUtility.sendMessage(admin, "parameter should be 0 or 1");

            return;
        }
        
        if(parameter == 0)
        {
            PacketSendUtility.sendMessage(admin, "Your object id is : " + admin.getObjectId());
        }
        else if (parameter == 1 && admin.getTarget() != null)
        {     
            PacketSendUtility.sendMessage(admin, "Your target id is : " + admin.getTarget().getObjectId());
        }

        
    }
}
