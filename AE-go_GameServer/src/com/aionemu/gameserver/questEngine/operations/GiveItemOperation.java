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
package com.aionemu.gameserver.questEngine.operations;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.inject.Inject;

/**
 * @author Blackmouse
 */
public class GiveItemOperation extends QuestOperation
{
	@Inject
	private ItemService itemService;

	private static final String NAME = "give_item";
	private final int itemCount;
	private final int itemId;

	public GiveItemOperation(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		itemId = Integer.parseInt(attr.getNamedItem("item_id").getNodeValue());
		itemCount = Integer.parseInt(attr.getNamedItem("count").getNodeValue());
	}

	@Override
	protected void doOperate(Player player) throws QuestEngineException
	{
		Inventory inventory = player.getInventory();
		if (itemId == ItemId.KINAH.value())
		{
			inventory.increaseKinah(itemCount);
			PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(inventory.getKinahItem()));
		}
		else
		{
			int currentItemCount = itemCount;
			while (currentItemCount > 0)
			{
				Item newItem = itemService.newItem(itemId, currentItemCount);
				
				Item existingItem = inventory.getItemByItemId(itemId);
				
				//item already in cube
				if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
				{
					int oldItemCount = existingItem.getItemCount();
					Item addedItem = inventory.addToBag(newItem);
					if(addedItem != null)
					{
						currentItemCount -= addedItem.getItemCount() - oldItemCount;
						PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(addedItem));
					}
				}
				// new item and inventory is not full
				else if (!inventory.isFull())
				{
					Item addedItem = inventory.addToBag(newItem);
					if(addedItem != null)
					{
						currentItemCount -= addedItem.getItemCount();
						PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(addedItem));
					}
				}
			}
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
