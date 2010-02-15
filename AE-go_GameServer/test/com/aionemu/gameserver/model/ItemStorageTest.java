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
package com.aionemu.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.items.ItemStorage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class ItemStorageTest
{
	List<Item> itemList;
	@Before
	public void setup()
	{
		itemList = new ArrayList<Item>();

		for(int i = 0; i < 30; i++)
		{
			ItemTemplate template = new ItemTemplate();
			template.setItemId(i);
			Item item = new Item(i, template, 3, false, 2);
			itemList.add(item);
		}
	}
	@Test
	public void testStorageFull()
	{
		Storage inventory = new Storage(StorageType.CUBE);
		Assert.assertEquals(false, inventory.isFull());	

		for(Item item : itemList)
		{
			inventory.addToBag(item);
		}
		Assert.assertEquals(true, inventory.isFull());	
		Assert.assertEquals(28, inventory.getAllItems().size()); //cube + kinah
	}

	@Test
	public void testStoragePlacement()
	{
		ItemStorage storage = new ItemStorage(27);

		for(Item item : itemList)
		{
			storage.addItemToStorage(item);
			if(item.getObjectId() < 27)
			{
				Assert.assertEquals(storage.getItemFromStorageByItemObjId(item.getObjectId()), item);	
			}		
		}
	}

	@Test
	public void testNextAvailableSlot()
	{
		ItemStorage storage = new ItemStorage(27);	
		Assert.assertEquals(0, storage.getNextAvailableSlot());	

		storage.addItemToStorage(itemList.get(1));
		Assert.assertEquals(1, storage.getNextAvailableSlot());	

		for(int i = 0; i < 27; i++)
		{
			storage.addItemToStorage(itemList.get(i));
		}
		Assert.assertEquals(-1, storage.getNextAvailableSlot());		
	}

	@Test
	public void testRemoveFromStorage()
	{
		ItemStorage storage = new ItemStorage(27);

		storage.addItemToStorage(itemList.get(1));
		Assert.assertEquals(1, storage.getNextAvailableSlot());	

		storage.removeItemFromStorage(itemList.get(1));
		Assert.assertEquals(0, storage.getNextAvailableSlot());	

		for(int i = 0; i < 27; i++)
		{
			storage.addItemToStorage(itemList.get(i));
		}
		Assert.assertEquals(-1, storage.getNextAvailableSlot());
		Assert.assertEquals(27, storage.getStorageItems().size());
		storage.removeItemFromStorage(itemList.get(1));
		Assert.assertEquals(1, storage.getNextAvailableSlot());
	}

	@Test
	public void testSlotIdPlacement()
	{
		ItemStorage storage = new ItemStorage(27);

		storage.addItemToStorage(itemList.get(1));
		Assert.assertEquals(0, itemList.get(1).getEquipmentSlot());	

		storage.addItemToStorage(itemList.get(2));
		Assert.assertEquals(1, itemList.get(2).getEquipmentSlot());
	}

	@Test
	public void testRemoveFromStorage2()
	{
		ItemStorage storage = new ItemStorage(27);

		for(int i = 0; i < 27; i++)
		{
			storage.addItemToStorage(itemList.get(i));
		}

		Assert.assertEquals(-1, storage.getNextAvailableSlot());
		Assert.assertEquals(27, storage.getStorageItems().size());
		storage.removeItemFromStorage(itemList.get(1));
		storage.removeItemFromStorage(itemList.get(15));
		storage.removeItemFromStorage(itemList.get(23));

		storage.addItemToStorage(itemList.get(1));
		storage.addItemToStorage(itemList.get(1));
		storage.addItemToStorage(itemList.get(1));
		storage.addItemToStorage(itemList.get(1));
		storage.addItemToStorage(itemList.get(1));

		Assert.assertEquals(-1, storage.getNextAvailableSlot());
		Assert.assertEquals(27, storage.getStorageItems().size());
	}


}
