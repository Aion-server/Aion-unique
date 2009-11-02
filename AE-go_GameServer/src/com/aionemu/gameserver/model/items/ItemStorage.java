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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author ATracer
 */
public class ItemStorage
{

	/**
	 * In current implementation storageItems might contain Item object or null
	 */
	private Item[] storageItems ;

	private int limit = 0;

	public ItemStorage(int limit)
	{
		this.limit = limit;
		storageItems = new Item[limit];
	}

	/**
	 * @return the storageItems
	 * 	Returns new reference to storageItems. Null values are removed.
	 */
	public List<Item> getStorageItems()
	{
		List<Item> readOnlyList = new ArrayList<Item>();
		for(Item item : storageItems)
		{
			if(item != null)
			{
				readOnlyList.add(item);
			}
		}

		return readOnlyList;
	}

	/**
	 * @return the limit
	 */
	protected int getLimit()
	{
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	protected void setLimit(int limit)
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
			if(item != null)
			{
				ItemTemplate itemTemplate = item.getItemTemplate();
				if(itemTemplate.getItemId() == itemId)
				{
					return item;
				}
			}

		}

		return null;
	}
	
	/**
	 * @param itemUniqueId
	 * @return
	 */
	public Item getItemFromStorageByItemUniqueId(int itemUniqueId)
	{
		for(Item item : storageItems)
		{
			if(item != null && item.getObjectId() == itemUniqueId)
			{
				return item;
			}
		}
		return null;
	}

	/**
	 * @param itemId
	 * @return
	 */
	public int getSlotIdByItemId(int itemId)
	{
		for(Item item : storageItems)
		{
			if(item != null)
			{
				ItemTemplate itemTemplate = item.getItemTemplate();
				if(itemTemplate.getItemId() == itemId)
				{
					return item.getEquipmentSlot();
				}
			}
		}
		return -1;
	}
	
	/**
	 * @param objId
	 * @return
	 */
	public int getSlotIdByObjId(int objId)
	{
		for(Item item : storageItems)
		{
			if(item != null)
			{
				if(item.getObjectId() == objId)
				{
					return item.getEquipmentSlot();
				}
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
		int size = storageItems.length;
		for(int slot = 0; slot < size; slot++)
		{
			if(storageItems[slot] == null && slot < limit)
			{
				return slot;
			}
		}
		
		return -1;
	}

	protected boolean isSlotEmpty(int slot)
	{
		return slot <= limit && storageItems[slot] == null;
	}

	/**
	 *  Add item logic:
	 *  - If there is already existing item - try to increase stack count
	 *  - If stack is full - place into next available slot
	 *  
	 *  - Return null if item was not added
	 *  - Return Item as the result of successful operation
	 *  
	 * @param item
	 * @return
	 */
	public Item addItemToStorage(Item item)
	{
		Item existingItem = getItemFromStorageByItemId(item.getItemTemplate().getItemId());

		if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
		{
			int maxValue = existingItem.getItemTemplate().getMaxStackCount();
			int sum = item.getItemCount() + existingItem.getItemCount();
			existingItem.setItemCount(sum >  maxValue ? maxValue : sum);
			
			return existingItem;
		}
		int availableSlot = getNextAvailableSlot();
		if(availableSlot != -1)
		{
			storageItems[availableSlot] =  item;
			item.setEquipmentSlot(availableSlot);
			return item;
		}
		return null;
	}
	
	/**
	 *  Return true if remove operation is successful
	 *  Return false if remove encountered some problems
	 *  
	 * @param item
	 * @return
	 */
	public boolean removeItemFromStorage(Item item)
	{
		int slot = getSlotIdByObjId(item.getObjectId());
		if(slot != -1)
		{
			storageItems[slot] = null;
			return true;
		}	
		return false;
	}

}
