/*
 * This file is part of aion-emu <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ZoneService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class Zone extends AdminCommand 
{
	@Inject
	private ZoneService zoneService;
	
    public Zone() {
        super("zone");
    }


    /**
     * Execute admin command represented by this class, with a given list of parametrs.
     *
     * @param admin the player of the admin that requests the command
     * @param params the parameters of the command
     */
    @Override
    public void executeCommand(Player admin, String[] params) {

        if (admin.getAccessLevel() < AdminConfig.COMMAND_ZONE) {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }
        
        if (params.length == 0)
        {
        	ZoneInstance zoneInstance = admin.getZoneInstance();
        	if(zoneInstance == null)
        	{
        		 PacketSendUtility.sendMessage(admin, "You are out of any zone");
        	}
        	else
        	{
        		String zoneName = zoneInstance.getTemplate().getName().name();
        		PacketSendUtility.sendMessage(admin, "You are in zone: " + zoneName);
        	}     
        }
        else if("refresh".equalsIgnoreCase(params[0]))
        {
        	zoneService.findZoneInCurrentMap(admin);
        }
    }
}
