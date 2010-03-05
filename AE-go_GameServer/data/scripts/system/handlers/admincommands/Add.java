/*
 * This file is part of aion-unique <aion-unique.com>.
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

import java.util.Collections;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.google.inject.Inject;

/**
 * @author Phantom, ATracer
 *
 */

public class Add extends AdminCommand
{
	@Inject
	private ItemService itemService;

	public Add()
	{
		super("add");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getAccessLevel() < AdminConfig.COMMAND_ADD)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}

		if(params.length == 0 || params.length > 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //add <item ID> <quantity>");
			return;
		}

		int itemId = 0;
		int itemCount = 1;

		try
		{
			itemId = Integer.parseInt(params[0]);
			if( params.length == 2 )
			{
				itemCount = Integer.parseInt(params[1]);
			}
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Parameters need to be an integer.");
			return;
		}

		Item item  = itemService.newItem(itemId, itemCount);

		if(item == null)
		{
			PacketSendUtility.sendMessage(admin, "Item template was not found for this itemId");
			return;
		}

		Storage inventory = admin.getInventory();
		Item addedItem = null;

		if(itemId == ItemId.KINAH.value())
		{
			addedItem = inventory.getKinahItem();
			addedItem.increaseItemCount(itemCount);
			itemService.releaseItemId(item);
		}
		else
		{
			addedItem = inventory.addToBag(item);
		}

		if(addedItem != null)
		{
			PacketSendUtility.sendPacket(admin, new SM_INVENTORY_UPDATE(Collections.singletonList(addedItem)));
			PacketSendUtility.sendMessage(admin, "Item added successfully");
		}
		else
		{
			itemService.releaseItemId(item);
			PacketSendUtility.sendMessage(admin, "Item couldn't be added");
		}
	}
}
