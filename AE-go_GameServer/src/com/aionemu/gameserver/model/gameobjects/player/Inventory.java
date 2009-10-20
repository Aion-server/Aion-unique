/**
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

package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ItemStorage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 * modified by ATracer
 */
public class Inventory
{
	
	private static final Logger log = Logger.getLogger(Inventory.class);

	private Player owner;

	private final ItemStorage defaultItemBag;

	private SortedMap<Integer, Item> equipment = Collections.synchronizedSortedMap(new TreeMap<Integer, Item>());
	
	private Item kinahItem;

	/**
	 *  Will be enhanced during development.
	 */
	public Inventory()
	{
		defaultItemBag = new ItemStorage(27); //TODO check this amount
	}
	
	/**
	 * @param owner
	 */
	public Inventory(Player owner)
	{
		this();
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
		//TODO checks
		if(item.isEquipped())
		{
			equipment.put(item.getEquipmentSlot(), item);
		}
		else if(item.getItemTemplate().getItemId() == ItemId.KINAH.value())
		{
			kinahItem = item;
		}
		else
		{
			defaultItemBag.addItemToStorage(item);
		}	
	}

	/**
	 * 	Return is the result of add operation. It can be null if item was not stored, it can be existing 
	 *  item reference if stack count was increased or new item in bag
	 *  
	 * @param item
	 */
	public Item addToBag(Item item)
	{
		return defaultItemBag.addItemToStorage(item);
	}

	/**
	 * @param item
	 */
	public void removeFromBag(Item item)
	{
		defaultItemBag.removeItemFromStorage(item);
	}
	
	/**
	 *  Method primarily used when saving to DB
	 *  
	 * @return
	 */
	public List<Item> getAllItems()
	{
		List<Item> allItems = new ArrayList<Item>();
		allItems.add(kinahItem);
		allItems.addAll(defaultItemBag.getStorageItems());
		allItems.addAll(equipment.values());
		return allItems;
	}
	
	/**
	 * 	Only equipped items
	 * 
	 * @return
	 */
	public List<Item> getEquippedItems()
	{
		List<Item> equippedItems = new ArrayList<Item>();

		synchronized(equipment)
		{	
			for(Item item : equipment.values())
			{
				if(item != null)
				{
					equippedItems.add(item);
				}
			}
		}

		return equippedItems;
	}
	
	/**
	 *	Items from all boxes + kinah item
	 * 
	 * @return
	 */
	public List<Item> getUnquippedItems()
	{
		List<Item> unequipedItems = new ArrayList<Item>();
		unequipedItems.add(kinahItem);
		unequipedItems.addAll(defaultItemBag.getStorageItems());
		return unequipedItems;
	}
	/**
	 *	Called when CM_EQUIP_ITEM packet arrives with action 0
	 * 
	 * @param itemUniqueId
	 * @param slot
	 * @return
	 */
	public boolean equipItem(int itemUniqueId, int slot)
	{
		synchronized(this)
		{
			Item item = defaultItemBag.getItemFromStorageByItemUniqueId(itemUniqueId);

			if(item == null)
			{
				return false;
			}
			
			//check whether there is already item in specified slot
			Item equippedItem = equipment.get(slot);
			if(equippedItem != null)
			{
				unEquip(equippedItem);
			}
			
			item.setEquipped(true);
			
			equip(slot, item);
		}
		return true;
	}

	private void equip(int slot, Item item)
	{
		defaultItemBag.removeItemFromStorage(item);
		equipment.put(slot, item);

		item.setEquipmentSlot(slot);

		PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_ITEM(item));
	}
	
	/**
	 *	Called when CM_EQUIP_ITEM packet arrives with action 1
	 * 
	 * @param itemUniqueId
	 * @param slot
	 * @return
	 */
	public boolean unEquipItem(int itemUniqueId, int slot)
	{
		synchronized(this)
		{
			Item itemToUnequip = null;

			for(Item item : equipment.values())
			{
				if(item.getObjectId() == itemUniqueId)
				{
					itemToUnequip = item;
				}
			}

			if(itemToUnequip == null || !itemToUnequip.isEquipped())
			{
				return false;
			}
			unEquip(itemToUnequip);
		}

		return true;	
	}

	private void unEquip(Item itemToUnequip)
	{
		equipment.remove(itemToUnequip.getEquipmentSlot());
		itemToUnequip.setEquipped(false);
		addToBag(itemToUnequip);
		PacketSendUtility.sendPacket(getOwner(), new SM_UPDATE_ITEM(itemToUnequip));
	}

	public Item getItemByObjId(int value)
	{
		return defaultItemBag.getItemFromStorageByItemUniqueId(value);
	}
	
	public Item getItemByItemId(int value)
	{
		return defaultItemBag.getItemFromStorageByItemId(value);
	}
	
	public boolean isFull()
	{
		return defaultItemBag.getNextAvailableSlot() == -1;
	}
}