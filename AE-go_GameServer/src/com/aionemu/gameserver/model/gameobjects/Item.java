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
package com.aionemu.gameserver.model.gameobjects;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemStorage;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class Item extends AionObject
{	
	private int itemCount = 1;

	private int itemColor = 0;

	private ItemTemplate itemTemplate;

	private boolean isEquipped = false;

	private int equipmentSlot = ItemStorage.FIRST_AVAILABLE_SLOT;

	private PersistentState persistentState;

	private Set<ManaStone> manaStones;
	
	private GodStone godStone;

	private int itemLocation;

	/**
	 * @param objId
	 * @param itemTemplate
	 * @param itemCount
	 * @param isEquipped
	 * @param equipmentSlot
	 * 
	 * This constructor should be called from ItemService
	 * for newly created items and loadedFromDb
	 */
	public Item(int objId, ItemTemplate itemTemplate, int itemCount, boolean isEquipped, int equipmentSlot)
	{
		super(objId);

		this.itemTemplate = itemTemplate;
		this.itemCount = itemCount;
		this.isEquipped = isEquipped;
		this.equipmentSlot = equipmentSlot;
		this.persistentState = PersistentState.NEW;
	}

	/**
	 * @param objId
	 * @param itemId
	 * @param itemCount
	 * @param isEquipped
	 * @param equipmentSlot
	 * 
	 * This constructor should be called only from DAO while loading from DB
	 */
	public Item(int objId, int itemId, int itemCount, int itemColor, boolean isEquipped, int equipmentSlot, int itemLocation)
	{
		super(objId);

		this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.itemCount = itemCount;
		this.itemColor = itemColor;
		this.isEquipped = isEquipped;
		this.equipmentSlot = equipmentSlot;
		this.itemLocation = itemLocation;
	}

	@Override
	public String getName()
	{
		//TODO
		//item description should return probably string and not id
		return String.valueOf(itemTemplate.getNameId());
	}

	/**
	 * @return the itemTemplate
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
	}

	/**
	 *@return the itemColor
	 */
	public int getItemColor()
	{
		return itemColor;
	}

	/**
	 * @param itemColor the itemColor to set
	 */
	public void setItemColor(int itemColor)
	{
		this.itemColor = itemColor;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param itemTemplate the itemTemplate to set
	 */
	public void setItemTemplate(ItemTemplate itemTemplate)
	{
		this.itemTemplate = itemTemplate;
	}

	/**
	 * @return the itemCount
	 *  Number of this item in stack. Should be not more than template maxstackcount ?
	 */
	public int getItemCount()
	{
		return itemCount;
	}

	/**
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 *  This method should be called ONLY from Storage class
	 *  In all other ways it is not guaranteed to be udpated in a regular update service
	 *  It is allowed to use this method for newly created items which are not yet in any storage 
	 *  
	 * @param addCount 
	 */
	public void increaseItemCount(int addCount)
	{
		//TODO overflow check
		this.itemCount += addCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 *  This method should be called ONLY from Storage class
	 *  In all other ways it is not guaranteed to be udpated in a regular update service
	 *  It is allowed to use this method for newly created items which are not yet in any storage 
	 *  
	 * @param remCount
	 */
	public boolean decreaseItemCount(int remCount)
	{
		if( this.itemCount - remCount >= 0 )
		{
			this.itemCount -= remCount;
			if(itemCount == 0 && !this.itemTemplate.isKinah())
			{
				setPersistentState(PersistentState.DELETED);
			}
			else
			{
				setPersistentState(PersistentState.UPDATE_REQUIRED);
			}
			return true;
		}

		return false;
	}

	/**
	 * @return the isEquipped
	 */
	public boolean isEquipped()
	{
		return isEquipped;
	}

	/**
	 * @param isEquipped the isEquipped to set
	 */
	public void setEquipped(boolean isEquipped)
	{
		this.isEquipped = isEquipped;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @return the equipmentSlot
	 *  Equipment slot can be of 2 types - one is the ItemSlot enum type if equipped, second - is position in cube
	 */
	public int getEquipmentSlot()
	{
		return equipmentSlot;
	}

	/**
	 * @param equipmentSlot the equipmentSlot to set
	 */
	public void setEquipmentSlot(int equipmentSlot)
	{
		this.equipmentSlot = equipmentSlot;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * This method should be used to lazy initialize empty manastone list
	 * @return the itemStones
	 */
	public Set<ManaStone> getItemStones()
	{
		if(manaStones == null)
			this.manaStones = new TreeSet<ManaStone>(new Comparator<ManaStone>(){

				@Override
				public int compare(ManaStone o1, ManaStone o2)
				{
					if(o1.getSlot() == o2.getSlot())
						return 0;
					return o1.getSlot() > o2.getSlot() ? 1 : -1;
				}
				
			});
		return manaStones;
	}
	
	/**
	 * Check manastones without initialization
	 * 
	 * @return
	 */
	public boolean hasManaStones()
	{
		return manaStones != null && manaStones.size() > 0;
	}
	
	/**
	 * @return the goodStone
	 */
	public GodStone getGodStone()
	{
		return godStone;
	}
	
	/**
	 * 
	 * @param itemId
	 * @return
	 */
	public GodStone addGodStone(int itemId)
	{
		this.godStone = new GodStone(getObjectId(), itemId, PersistentState.NEW);
		return this.godStone;
	}
	
	/**
	 * @param goodStone the goodStone to set
	 */
	public void setGoodStone(GodStone goodStone)
	{
		this.godStone = goodStone;
	}

	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}

	/**
	 *  Possible changes:
	 *  NEW -> UPDATED
	 *  NEW -> UPDATE_REQURIED
	 *  UPDATE_REQUIRED -> DELETED
	 *  UPDATE_REQUIRED -> UPDATED
	 *  UPDATED -> DELETED
	 *  UPDATED -> UPDATE_REQUIRED
	 * @param persistentState the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch(persistentState)
		{
			case DELETED:
				if(this.persistentState == PersistentState.NEW)
					this.persistentState = PersistentState.NOACTION;
				else
					this.persistentState = PersistentState.DELETED;
				break;
			case UPDATE_REQUIRED:
				if(this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}

	}

	public void setItemLocation(int storageType)
	{
		this.itemLocation = storageType;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public int getItemLocation()
	{
		return itemLocation;
	}
	
	public int getItemMask()
	{
		return itemTemplate.getMask();
	}
	
	@Override
	public String toString()
	{
		return "Item [equipmentSlot=" + equipmentSlot + ", godStone=" + godStone + ", isEquipped=" + isEquipped
			+ ", itemColor=" + itemColor + ", itemCount=" + itemCount + ", itemLocation="
			+ itemLocation + ", itemTemplate=" + itemTemplate + ", manaStones=" + manaStones + ", persistentState="
			+ persistentState + "]";
	}
}
