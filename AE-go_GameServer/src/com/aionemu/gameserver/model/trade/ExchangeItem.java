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
package com.aionemu.gameserver.model.trade;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * @author ATracer
 *
 */
public class ExchangeItem
{
	private PersistentState persistentState;
	private int itemObjId;
	private int itemCount;
	private int itemId;
	private int itemDesc;
	private Item newItem;
	private Item originalItem;

	/**
	 * Used when exchange item != original item
	 * 
	 * @param itemObjId
	 * @param itemCount
	 * @param item
	 */
	public ExchangeItem(int itemObjId, int itemCount, Item newItem, Item originalItem)
	{
		this.itemObjId = itemObjId;
		this.itemCount = itemCount;
		this.newItem = newItem;
		this.originalItem = originalItem;
		this.itemId = newItem.getItemTemplate().getTemplateId();
		this.itemDesc = newItem.getItemTemplate().getNameId();
		this.persistentState = PersistentState.NEW;
	}

	/**
	 *  Used when exchange item is == original item
	 *  
	 * @param item
	 */
	public ExchangeItem(Item item)
	{
		this.persistentState = PersistentState.UPDATE_REQUIRED;
		this.originalItem = item;
		this.newItem = item;
		this.itemObjId = item.getObjectId();
		this.itemId = item.getItemTemplate().getTemplateId();
		this.itemCount = item.getItemCount();
		this.itemDesc = item.getItemTemplate().getNameId();
	}

	/**
	 * @param countToAdd
	 */
	public void addCount(int countToAdd)
	{
		this.itemCount += countToAdd;
		this.newItem.increaseItemCount(countToAdd);
	}

	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}

	/**
	 * @return the newItem
	 */
	public Item getNewItem()
	{
		return newItem;
	}

	/**
	 * @return the originalItem
	 */
	public Item getOriginalItem()
	{
		return originalItem;
	}

	/**
	 * @return the itemObjId
	 */
	public int getItemObjId()
	{
		return itemObjId;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount()
	{
		return itemCount;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @return the itemDesc
	 */
	public int getItemDesc()
	{
		return itemDesc;
	}

	/**
	 * @param persistentState the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
}
