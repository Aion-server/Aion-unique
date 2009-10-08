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

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dataholders.ItemData;
import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author dragoon112
 *
 */

public class Add extends AdminCommand
{
    public Add()
    {
        super("add");
    }

    /*
    *  (non-Javadoc)
    * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
    */
    @Override
    public void executeCommand(Player admin, String... params)
    {
        if(params == null)
        {
            PacketSendUtility.sendMessage(admin, "syntax //add itemID");
            return;
        }

        int parameter = 0;
        try
        {
            parameter = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e)
        {
            PacketSendUtility.sendMessage(admin, "Param needs to be a number.");

            return;
        }
        int activePlayer = admin.getObjectId();
        try{
        	try{
        		PreparedStatement ps = DB.prepareStatement("SELECT id FROM item_list WHERE `id`=" + parameter);
        		ResultSet rs = ps.executeQuery();
        		rs.last();
        		parameter = rs.getInt("id");
        	}catch(Exception e)
        	{
        		parameter = 0;
        	}
        	
        	if(parameter!=0){
			Inventory itemsDbOfPlayerCount = new Inventory(); // wrong
			itemsDbOfPlayerCount.getInventoryFromDb(activePlayer);
			int totalItemsCount = itemsDbOfPlayerCount.getItemsCount();

			Inventory equipedItems = new Inventory();
			equipedItems.getEquipedItemsFromDb(activePlayer);
			int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();
			
			
			int cubes = 1;
			int cubesize = 27;
			int allowItemsCount = cubesize*cubes-1;
			
			if (totalItemsCount<=allowItemsCount){
			Inventory items = new Inventory();
			items.putItemToDb(activePlayer, parameter, 1);
			items.getLastUniqueIdFromDb();
			int newItemUniqueId = items.getnewItemUniqueIdValue();
				
			PacketSendUtility.sendPacket(admin, new SM_INVENTORY_INFO(newItemUniqueId, parameter, 1, 1, 8));
	        PacketSendUtility.sendMessage(admin, "Added Item.");
			}else{
		        PacketSendUtility.sendMessage(admin, "Inventory is Full.");
			}
        	}else{
        		PacketSendUtility.sendMessage(admin, "Invalid Item.");
        	}
			
        }catch(Exception e)
        {
            PacketSendUtility.sendMessage(admin, "There was an error in the Code.");

            return;
        }
        
    }
}
