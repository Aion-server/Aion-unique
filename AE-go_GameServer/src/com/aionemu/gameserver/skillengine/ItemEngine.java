/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.itemengine;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemId;

/**
 * @author Avol
 *
 */
public class ItemEngine
{
	private static final Logger log = Logger.getLogger(ItemEngine.class);
	
	public int itemUniqueId;
	public int itemId;

	/**
	 * request effect handler for itemId
	 */

	public void useItem(Player player, int itemUniqueId) 
	{
		Inventory inventory = player.getInventory();
		
		Item item = inventory.getItemByObjId(itemUniqueId);
		itemId = item.getItemTemplate().getItemId();

		ItemHandler itemHandler = new ItemHandler();
		itemHandler.setItem(itemId, itemUniqueId);
		itemHandler.useItem(player);
	}

}
