/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.LinkedHashMap;

import com.aionemu.gameserver.model.gameobjects.Item;

/**
 * @author Xav
 * Modified by Simple
 */
public class PrivateStore
{
	private Player					owner;
	private LinkedHashMap<Item, Integer>	items;
	private String					storeMessage;

	/**
	 * This method binds a player to the store and creates a list of items
	 * @param owner
	 */
	public PrivateStore(Player owner)
	{
		this.owner = owner;
		this.items = new LinkedHashMap<Item, Integer>();
	}

	/**
	 * This method will return the owner of the store
	 * @return
	 */
	public Player getOwner()
	{
		return owner;
	}

	/**
	 * This method will return the items being sold
	 * @return
	 */
	public LinkedHashMap<Item, Integer> getSoldItems()
	{
		return items;
	}

	/**
	 * This method will add an item to the list and price
	 * @param item
	 * @param price
	 */
	public void addItemToSell(Item item, int price)
	{
		items.put(item, price);
	}

	/**
	 * This method will remove an item from the list
	 * @param item
	 */
	public void removeItem(Item item)
	{
		if(items.containsKey(item))
		{
			items.remove(item);
		}
	}

	/**
	 * @param storeMessage the storeMessage to set
	 */
	public void setStoreMessage(String storeMessage)
	{
		this.storeMessage = storeMessage;
	}

	/**
	 * @return the storeMessage
	 */
	public String getStoreMessage()
	{
		return storeMessage;
	}
}
