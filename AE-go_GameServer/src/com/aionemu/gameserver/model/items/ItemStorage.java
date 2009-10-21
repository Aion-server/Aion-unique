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
	private List<Item> storageItems ;

	private int limit = 0;

	public ItemStorage(int limit)
	{
		this.limit = limit;
		storageItems = new ArrayList<Item>(limit);
	}

	/**
	 * @return the storageItems
	 * 	Returns new reference to storageItems. Null values are removed.
	 */
	public List<Item> getStorageItems()
	{
		List<Item> readOnlyList =  new ArrayList<Item>(storageItems);
		readOnlyList.removeAll(Collections.singleton(null));
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
	 * @return index of available slot
	 *  If storage is null - return -1
	 */
	public int getNextAvailableSlot()
	{
		int size = storageItems.size();
		for(int i = 0; i < size; i++)
		{
			if(storageItems.get(i) == null && i < limit)
			{
				return i;
			}
		}
		
		if(size < limit)
		{
			return size;
		}
		return -1;
	}

	protected boolean isSlotEmpty(int slot)
	{
		return slot <= limit && storageItems.get(slot) != null;
	}

	/**
	 * @param item
	 * @return
	 */
	public Item addItemToStorage(Item item)
	{
		Item existingItem = getItemFromStorageByItemId(item.getItemTemplate().getItemId());

		if(existingItem != null && existingItem.getItemCount() < existingItem.getItemTemplate().getMaxStackCount())
		{
			//TODO overflow check
			existingItem.increaseItemCount(item.getItemCount());
			return existingItem;
		}
		int availableSlot = getNextAvailableSlot();
		if(availableSlot != -1)
		{
			storageItems.add(availableSlot, item);
			item.setEquipmentSlot(availableSlot);
			return item;
		}
		return null;
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean removeItemFromStorage(Item item)
	{
		int slot = getSlotIdByObjId(item.getObjectId());
		if(slot != -1)
		{
			storageItems.set(slot, null);
			return true;
		}
		
		return false;
	}
}
