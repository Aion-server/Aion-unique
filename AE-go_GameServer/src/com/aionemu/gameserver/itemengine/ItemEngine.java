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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
/**
 * @author Avol
 * 
 */
public class ItemEngine
{
	private static final Logger log = Logger.getLogger(ItemEngine.class);

	private int itemId;
	private int itemObjId;
	private int deletable;
	private int type;
	private int value;
	private int value2;
	private int timerEnd;
	private int timerInterval;
	private int effect;
	private Player player;

	/**
	 * select item
	 */

	public void setItem(int itemObjId, Player player) {
		Inventory inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		itemId = item.getItemTemplate().getItemId();
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.player = player;
	}

	/**
	 * get item name for itemId
	 */

	public String getItemName() {
		ItemTemplateLoader itemTemplate = new ItemTemplateLoader();
		itemTemplate.setItemId(itemId);
		itemTemplate.loadFromXml();
		return itemTemplate.getItemName();
	}

	/**
	 * delete item from inventory, select action
	 */

	public void useItem() 
	{
		/**
		 * get item template for itemId
		*/

		ItemTemplateLoader itemTemplate = new ItemTemplateLoader();
		itemTemplate.setItemId(itemId);
		itemTemplate.loadFromXml();

		/**
		 * check if item is found
		*/

		if (itemTemplate.getCheckTemplate() == 1) 
		{

			/**
			 * retrieve item effect templates.
			*/

			type = itemTemplate.getType();
			deletable = itemTemplate.getDeletable();
			value = itemTemplate.getValue();
			value2 = itemTemplate.getValue2();
			timerEnd = itemTemplate.getTimerEnd();
			timerInterval = itemTemplate.getTimerInterval();
			effect = itemTemplate.getItemAbnormalEffectId();

			/**
			 * select and execute action (hp pot etc.)
			*/

			ItemActionSelecter action = new ItemActionSelecter(type, value, value2, timerEnd, timerInterval, itemObjId, itemId, effect);
			action.execute(player);

			/**
			 * delete item, if deletable.
			 */

			if (deletable ==1) {
				/*Inventory bag = player.getInventory();
				Item resultItem = bag.getItemByObjId(itemObjId);
				if (resultItem != null) 
					bag.removeFromBag(resultItem);
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemObjId));*/
			}
		} 
		else 
		{
			log.info("No item effect template found for itemId: " + itemId);
		}
	}
}
