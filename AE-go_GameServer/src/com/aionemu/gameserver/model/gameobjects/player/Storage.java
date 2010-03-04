/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.items.ItemStorage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_WAREHOUSE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 * modified by ATracer, kosyachok
 */
public class Storage
{
	private Player owner;

	protected ItemStorage storage;

	private Item kinahItem;

	protected int storageType;

	/**
	 *  Will be enhanced during development.
	 */
	public Storage(StorageType storageType)
	{
		switch(storageType)
		{
			case CUBE:
				storage = new ItemStorage(108);
				this.storageType = storageType.getId();
				break;
			case REGULAR_WAREHOUSE:
				storage = new ItemStorage(104);
				this.storageType = storageType.getId();
				break;
			case ACCOUNT_WAREHOUSE:
				storage = new ItemStorage(16);
				this.storageType = storageType.getId();
				break;
			case LEGION_WAREHOUSE:
				storage = new ItemStorage(24); // TODO: FIND OUT WHAT MAX IS
				this.storageType = storageType.getId();
				break;
		}
	}

	/**
	 * @param owner
	 */
	public Storage(Player owner, StorageType storageType)
	{
		this(storageType);
		this.owner = owner;
	}	

	/**
	 * @return the owner
	 */
	public Player getOwner()
	{
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Player owner)
	{
		this.owner = owner;
	}

	/**
	 * @return the kinahItem
	 */
	public Item getKinahItem()
	{
		return kinahItem;
	}

	public int getStorageType()
	{
		return storageType;
	}
	/**
	 *  Increasing kinah amount is persisted immediately
	 *  
	 * @param amount
	 */
	public void increaseKinah(int amount)
	{
		kinahItem.increaseItemCount(amount);
		DAOManager.getDAO(InventoryDAO.class).store(kinahItem, getOwner().getObjectId());

		if(storageType == StorageType.CUBE.getId())
			PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_ITEM(kinahItem));

		if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
			PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_WAREHOUSE_ITEM(kinahItem, storageType));
	}
	/**
	 *  Decreasing kinah amount is persisted immediately
	 *  
	 * @param amount
	 */
	public boolean decreaseKinah(int amount)
	{
		boolean operationResult = kinahItem.decreaseItemCount(amount);
		if(operationResult)
		{
			DAOManager.getDAO(InventoryDAO.class).store(kinahItem, getOwner().getObjectId());
			if(storageType == StorageType.CUBE.getId())
				PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_ITEM(kinahItem));

			if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
				PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_WAREHOUSE_ITEM(kinahItem, storageType));
		}
		return operationResult;
	}

	/**
	 * 
	 *  This method should be called only for new items added to inventory (loading from DB)
	 *  If item is equiped - will be put to equipment
	 *  if item is unequiped - will be put to default bag for now
	 *  Kinah is stored separately as it will be used frequently
	 *  
	 *  @param item
	 */
	public void onLoadHandler(Item item)
	{
		if(item.isEquipped())
		{
			owner.getEquipment().onLoadHandler(item);
		}
		else if(item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else
		{
			storage.putToNextAvailableSlot(item);
		}	
	}

	/**
	 * 	Return is the result of add operation. It can be null if item was not stored, it can be existing 
	 *  item reference if stack count was increased or new item in bag
	 *  
	 *  
	 *  Every add operation is persisted immediately now
	 *  
	 *  DEPRECATED
	 *  
	 * @param item
	 */
	public Item addToBag(Item item)  // addToBag is old and have alot of bugs with item adding, suggest to remove it.
	{
		Item resultItem = storage.addItemToStorage(item);

		if(resultItem != null)
		{
			resultItem.setItemLocation(storageType);
			DAOManager.getDAO(InventoryDAO.class).store(item, getOwner().getObjectId());
		}
		return resultItem;
	}

	/**
	 *  Used to put item into storage cube at first avaialble slot (no check for existing item)
	 *  During unequip/equip process persistImmediately should be false
	 *  
	 * @param item
	 * @param persistImmediately
	 * @return Item
	 */
	public Item putToBag(Item item, boolean persistImmediately)
	{

		Item resultItem = storage.putToNextAvailableSlot(item);
		if(resultItem != null && persistImmediately)
		{
			resultItem.setItemLocation(storageType);
			DAOManager.getDAO(InventoryDAO.class).store(item, getOwner().getObjectId());
		}
		return resultItem;
	}

	/**
	 * Every put operation using this method is persisted immediately
	 * 
	 * @param item
	 * @return Item
	 */
	public Item putToBag(Item item)
	{
		return putToBag(item, true);
	}

	/**
	 *  Removes item completely from inventory.
	 *  Every remove operation is persisted immediately now
	 *  
	 * @param item
	 */
	public void removeFromBag(Item item, boolean persistOperation)
	{
		boolean operationResult = storage.removeItemFromStorage(item);
		if(operationResult && persistOperation)
		{
			item.setPersistentState(PersistentState.DELETED);

			DAOManager.getDAO(InventoryDAO.class).store(item, getOwner().getObjectId());
		}
	}


	/**
	 *  Used to reduce item count in bag or completely remove by ITEMID
	 *  This method operates in iterative manner overl all items with specified ITEMID.
	 *  Return value can be the following:
	 *  - true - item removal was successfull
	 *  - false - not enough amount of items to reduce 
	 *  or item is not present
	 *  
	 * @param itemId
	 * @param count
	 * @return true or false
	 */
	public boolean removeFromBagByItemId(int itemId, int count)
	{
		if(count < 1)
			return false;

		List<Item> items = storage.getItemsFromStorageByItemId(itemId);

		for(Item item : items)
		{
			count = decreaseItemCount(item, count);

			if(count == 0)
				break;
		}

		return count >= 0;
	}

	/**
	 * 
	 * @param itemObjId
	 * @param count
	 * @return true or false
	 */
	public boolean removeFromBagByObjectId(int itemObjId, int count)
	{
		return removeFromBagByObjectId(itemObjId, count, true);
	}

	/**
	 *  Used to reduce item count in bag or completely remove by OBJECTID
	 *  Return value can be the following:
	 *  - true - item removal was successfull
	 *  - false - not enough amount of items to reduce 
	 *  or item is not present
	 *  
	 * @param itemObjId
	 * @param count
	 * @return true or false
	 */
	public boolean removeFromBagByObjectId(int itemObjId, int count, boolean persist)
	{
		if(count < 1)
			return false;

		Item item = storage.getItemFromStorageByItemObjId(itemObjId);

		return decreaseItemCount(item, count, persist) >= 0;
	}

	/**
	 *   This method decreases inventory's item by count and sends
	 *   appropriate packets to owner.
	 *   Item will be saved in database after update or deleted if count=0 (and persist=true)
	 * 
	 * @param count should be > 0
	 * @param item
	 * @param persist
	 * @return
	 */
	private int decreaseItemCount(Item item, int count, boolean persist)
	{
		if(item.getItemCount() >= count)
		{
			item.decreaseItemCount(count);
			count = 0;
		}
		else
		{
			item.decreaseItemCount(item.getItemCount());
			count -= item.getItemCount();
		}
		if(item.getItemCount() == 0)
		{
			storage.removeItemFromStorage(item);
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE_ITEM(item.getObjectId()));
		}
		else
			PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_ITEM(item));

		if(persist || item.getItemCount() == 0)
		{
			DAOManager.getDAO(InventoryDAO.class).store(item, getOwner().getObjectId());
		}	

		return count;
	}

	/**
	 * 
	 * @param item
	 * @param count
	 * @return
	 */
	private int decreaseItemCount(Item item, int count)
	{
		return decreaseItemCount(item, count, true);
	}

	/**
	 *  Method primarily used when saving to DB
	 *  
	 * @return List<Item>
	 */
	public List<Item> getAllItems()
	{
		List<Item> allItems = new ArrayList<Item>();
		allItems.add(kinahItem);
		allItems.addAll(storage.getStorageItems());
		return allItems;
	}

	/**
	 *  Searches for item with specified itemId in equipment and cube
	 *  
	 * @param itemId
	 * @return List<Item>
	 */
	public List<Item> getAllItemsByItemId(int itemId)
	{
		List<Item> allItemsByItemId = new ArrayList<Item>();

		for (Item item : storage.getStorageItems())
		{
			if(item.getItemTemplate().getTemplateId() == itemId)
				allItemsByItemId.add(item);
		}
		return allItemsByItemId;
	}


	public List<Item> getStorageItems()
	{
		return storage.getStorageItems();
	}

	/**
	 *  Will look item in default item bag
	 *  
	 * @param value
	 * @return Item
	 */
	public Item getItemByObjId(int value)
	{
		return storage.getItemFromStorageByItemObjId(value);
	}

	/**
	 * 
	 * @param value
	 * @return List<Item>
	 */
	public List<Item> getItemsByItemId(int value)
	{
		return storage.getItemsFromStorageByItemId(value);
	}

	/**
	 *  
	 * @param itemId
	 * @return number of items using search by itemid
	 */
	public int getItemCountByItemId(int itemId)
	{
		List<Item> items = getItemsByItemId(itemId);
		int count = 0;
		for(Item item : items)
		{
			count += item.getItemCount();
		}
		return count;
	}

	/**
	 *  Checks whether default cube is full
	 *  
	 * @return true or false
	 */
	public boolean isFull()
	{
		return storage.isFull();
	}

	public int getNumberOfFreeSlots()
	{
		return storage.getNumberOfFreeSlots();
	}
	/**
	 *  Sets the Inventory Limit from Cube Size
	 *  
	 * @param Limit
	 */
	public void setLimit(int limit){
		this.storage.setLimit(limit);
	}
	public int getLimit(){
		return this.storage.getLimit();
	}
}
