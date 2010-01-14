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
package com.aionemu.gameserver.model.items;

import java.util.TreeSet;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 *
 */
public class ItemStone
{
	private int itemObjId;
	
	private int itemId;
	
	private int slot;
	
	private TreeSet<StatModifier> modifiers;
	
	private PersistentState persistentState;

	/**
	 * 
	 * @param itemObjId
	 * @param itemId
	 * @param statEnum
	 * @param enchantValue
	 * @param persistentState
	 */
	public ItemStone(int itemObjId, int itemId, int slot, PersistentState persistentState)
	{
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.slot = slot;
		this.persistentState = persistentState;
		
		ItemTemplate stoneTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if(stoneTemplate != null && stoneTemplate.getModifiers() != null)
		{
			this.modifiers = stoneTemplate.getModifiers();
		}	
	}

	/**
	 * @return the itemObjId
	 */
	public int getItemObjId()
	{
		return itemObjId;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @return the slot
	 */
	public int getSlot()
	{
		return slot;
	}

	/**
	 * @return modifiers
	 */
	public TreeSet<StatModifier> getModifiers()
	{
		return modifiers;
	}
	
	public StatModifier getFirstModifier()
	{
		return modifiers != null ? modifiers.first() : null;
	}

	/**
	 * @return the pState
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}

	/**
	 * @param persistentState the pState to set
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
}
