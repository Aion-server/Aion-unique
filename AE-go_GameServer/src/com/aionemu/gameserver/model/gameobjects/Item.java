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

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author ATracer
 */
public class Item extends AionObject
{
	private static final Logger log = Logger.getLogger(Item.class);
	
	private int itemCount = 1;
	
	private ItemTemplate itemTemplate;
	
	private boolean isEquipped = false;
	
	private int equipmentSlot = 0;
	
	/**
	 * @param objId
	 */
	public Item(int objId)
	{
		super(objId);
	}
	
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
	}
	
	/**
	 * @param objId
	 * @param itemId
	 * @param itemCount
	 * @param isEquipped
	 * @param equipmentSlot
	 */
	public Item(int objId, int itemId, int itemCount, boolean isEquipped, int equipmentSlot)
	{
		super(objId);
		
		this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.itemCount = itemCount;
		this.isEquipped = isEquipped;
		this.equipmentSlot = equipmentSlot;
	}

	@Override
	public String getName()
	{
		//TODO
		//item description should return probably string and not id
		return String.valueOf(itemTemplate.getDescription());
	}

	/**
	 * @return the itemTemplate
	 */
	public ItemTemplate getItemTemplate()
	{
		return itemTemplate;
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
	}
	
	/**
	 *  Every increase operation should be persisted immediately in caller method
	 *  Use <code>setItemCount</code> method for non-persisted operations
	 *  
	 * @param addCount 
	 */
	public void increaseItemCount(int addCount)
	{
		//TODO overflow check
		this.itemCount += addCount;
	}

	/**
	 *  Every decrease operation should be persisted immediately in caller method
	 *  Use <code>setItemCount</code> method for non-persisted operations
	 *  
	 * @param remCount
	 */
	public boolean decreaseItemCount(int remCount)
	{
		if( this.itemCount - remCount >= 0 )
		{
			this.itemCount -= remCount;
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
	}
	
	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		Class<?> clazz = Item.class;
		for (Field fi : clazz.getDeclaredFields()) {
			sb.append(fi.getName()); sb.append(':');
			try { sb.append(fi.getInt(this)); }
			catch(Exception e) { try { sb.append(fi.getBoolean(this)); }
			catch(Exception f) { try { sb.append(fi.getFloat(this)); }
			catch(Exception g) { try { sb.append(fi.getDouble(this)); }
			catch(Exception h) { try { sb.append(fi.get(this).toString()); }
			catch(Exception i) { sb.append('?'); } } } } }
			sb.append(':');
		}
		sb.append('}');
		return sb.toString();
	}
}
