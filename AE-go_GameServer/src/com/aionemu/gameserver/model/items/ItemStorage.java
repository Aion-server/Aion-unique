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
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 * reimplemented by RotO
 */
public class ItemStorage
{
	public static final int FIRST_AVAILABLE_SLOT = 65535;

	/**
	 * LinkedList storageItems
	 */
	private List<Item> storageItems;

	private int limit = 0;

	public ItemStorage(int limit)
	{
		this.limit = limit;
		storageItems = new LinkedList<Item>();
	}

	/**
	 * @return the storageItems
	 * 	Returns new reference to storageItems. Null values are removed.
	 */
	public List<Item> getStorageItems()
	{
		return Collections.unmodifiableList(storageItems);
	}

	/**
	 * @return the limit
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * @param itemId
	 * @return Item by itemId or null if there is no such item
	 */
	public Item getItemFromStorageByItemId(int itemId)
	{
		for(Item item : storageItems)
		{
			ItemTemplate itemTemplate = item.getItemTemplate();
			if(itemTemplate.getTemplateId() == itemId)
			{
				return item;
			}
		}

		return null;
	}
	
	/**
	 * 
	 * @param itemId
	 * @return list of items with specified itemId
	 */
	public List<Item> getItemsFromStorageByItemId(int itemId)
	{
		List<Item> itemList = new ArrayList<Item>();
		
		for(Item item : storageItems)
		{
			ItemTemplate itemTemplate = item.getItemTemplate();
			if(itemTemplate.getTemplateId() == itemId)
			{
				itemList.add(item);
			}
		}

		return itemList;
	}
	
	/**
	 * @param itemObjId
	 * @return Item
	 */
	public Item getItemFromStorageByItemObjId(int itemObjId)
	{
		for(Item item : storageItems)
		{
			if(item.getObjectId() == itemObjId)
			{
				return item;
			}
		}
		return null;
	}

	/**
	 * @param itemId
	 * @return int
	 */
	public int getSlotIdByItemId(int itemId)
	{
		for(Item item : storageItems)
		{
			ItemTemplate itemTemplate = item.getItemTemplate();
			if(itemTemplate.getTemplateId() == itemId)
			{
				return item.getEquipmentSlot();
			}
		}
		return -1;
	}
	
	/**
	 * @param objId
	 * @return int
	 */
	public int getSlotIdByObjId(int objId)
	{
		for(Item item : storageItems)
		{
			if(item.getObjectId() == objId)
			{
				return item.getEquipmentSlot();
			}
		}

		return -1;
	}

	/**
	 * If storage is null - return "-1"
	 * 
	 * @return index of available slot 
	 */
	public int getNextAvailableSlot()
	{
		return FIRST_AVAILABLE_SLOT;
	}

	/**
	 *  Add item logic:
	 *  - If there is already existing item - try to increase stack count
	 *  - If stack is full - put to next available slot
	 *  
	 *  - Return null if item was not added
	 *  - Return Item as the result of successful operation
	 *  
	 * @param item
	 * @return Item
	 */
	public Item addItemToStorage(Item item)
	{
		Item existingItem = getItemFromStorageByItemId(item.getItemTemplate().getTemplateId());

		if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
		{
			int maxValue = existingItem.getItemTemplate().getMaxStackCount();
			int sum = item.getItemCount() + existingItem.getItemCount();
			existingItem.setItemCount(sum >  maxValue ? maxValue : sum);
			
			return existingItem;
		}
		return putToNextAvailableSlot(item);
	}
	
	/**
	 * Put item logic:
	 *  - If there is available slot - put item there and return it back
	 *  - If no slot available - return null
	 *  
	 * @param item
	 * @return Item
	 */
	public Item putToNextAvailableSlot(Item item)
	{
		if (!isFull() && storageItems.add(item))
			return item;
		else
			return null;
	}

	/**
	 *  Return true if remove operation is successful
	 *  Return false if remove encountered some problems
	 *  
	 * @param item
	 * @return true or false
	 */
	public boolean removeItemFromStorage(Item item)
	{
		return storageItems.remove(item);
	}

	public boolean isFull()
	{
		return storageItems.size() >= limit;
	}

	/**
	 * @return int
	 */
	public int getNumberOfFreeSlots()
	{
		return limit - storageItems.size();
	}
}
