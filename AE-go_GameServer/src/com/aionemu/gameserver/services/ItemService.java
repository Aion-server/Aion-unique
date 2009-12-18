/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.services;

import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;

import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *	This class is used for Item manipulations (creation, disposing, modification)
 *	Can be used as a factory for Item objects
 */
public class ItemService
{ 
	private static Logger log = Logger.getLogger(ItemService.class);

	private IDFactory aionObjectsIDFactory;
	private World world;

	@Inject
	public ItemService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
	}

	/**
	 * @param itemId
	 * @param count
	 * @return
	 * 
	 * Creates new Item instance.
	 * If count is greater than template maxStackCount, count value will be cut to maximum allowed
	 * This method will return null if ItemTemplate for itemId was not found.
	 */
	public Item newItem(int itemId, int count)
	{
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if(itemTemplate == null)
		{
			log.error("Item was not populated correctly. Item template is missing for item id: " + itemId);
			return null;
		}

		int maxStackCount = itemTemplate.getMaxStackCount();	
		if(count > maxStackCount && maxStackCount != 0)
		{
			count = maxStackCount;
		}

		//TODO if Item object will contain ownerId - item can be saved to DB before return
		return new Item(aionObjectsIDFactory.nextId(), itemTemplate, count, false, 0);
	}

	/**
	 * @param itemId
	 * @param itemUniqueId
	 * @param count
	 * @param isEquipped
	 * @param slot
	 * @return
	 * 
	 *  Loads Item instance with specified itemUniqueId
	 */
	public Item loadItem(int itemId, int itemUniqueId, int count, boolean isEquipped, int slot)
	{
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if(itemTemplate == null)
		{
			log.error("Item was not populated correctly. Item template is missing for item id: " + itemId);
		}

		return new Item(itemUniqueId, itemTemplate, count, isEquipped, slot);
	}       

	/**
	 *  Used to split item into 2 items
	 *  
	 * @param player
	 * @param itemObjId
	 * @param splitAmount
	 * @param slotNum
	 */
	public void splitItem (Player player, int itemObjId, int splitAmount, int slotNum)
	{
		Inventory inventory = player.getInventory();

		Item itemToSplit = inventory.getItemByObjId(itemObjId);
		int oldItemCount = itemToSplit.getItemCount() - splitAmount;

		if(itemToSplit.getItemCount()<splitAmount || oldItemCount == 0)
			return;

		itemToSplit.decreaseItemCount(splitAmount);

		Item newItem = this.newItem(itemToSplit.getItemTemplate().getItemId(), splitAmount);

		if(inventory.putToBag(newItem) != null)
		{
			List<Item> itemsToUpdate = new ArrayList<Item>();
			itemsToUpdate.add(newItem);
			itemsToUpdate.add(itemToSplit);

			DAOManager.getDAO(InventoryDAO.class).store(itemToSplit, player.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(itemsToUpdate));
		}		
	}
}
