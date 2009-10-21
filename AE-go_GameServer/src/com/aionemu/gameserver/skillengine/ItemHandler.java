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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.itemengine.ItemTemplateLoader;
import com.aionemu.gameserver.itemengine.itemeffects.consumables.potions.*;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.model.gameobjects.Item;

import java.util.concurrent.Future;
/**
 * @author Avol
 * 
 */
public class ItemHandler
{
	private static final Logger log = Logger.getLogger(ItemHandler.class);

	public int itemId;
	public int itemObjId;
	public int deletable;
	public int type;
	public int value;
	public int value2;
	public int timerEnd;
	public int timerInterval;


	/**
	 * set itemId, itemUniqueId
	 */

	public void setItem(int itemId, int itemObjId) {
		this.itemId = itemId;
		this.itemObjId = itemObjId;
	}

	/**
	 * Do action: delete item from inventory, select action
	 */

	public void useItem(Player player) 
	{
		//get item template for id
		ItemTemplateLoader itemTemplate = new ItemTemplateLoader();
		itemTemplate.setItemId(itemId);
		itemTemplate.loadFromXml();
		if (itemTemplate.getCheckTemplate() == 1) 
		{
			type = itemTemplate.getType();
			deletable = itemTemplate.getDeletable();
			value = itemTemplate.getValue();
			value2 = itemTemplate.getValue2();
			timerEnd = itemTemplate.getTimerEnd();
			timerInterval = itemTemplate.getTimerInterval();

				/**
				 * select and execute action (hp pot etc.)
				 */

				ItemActionSelecter action = new ItemActionSelecter(type, value, value2, timerEnd, timerInterval, itemObjId, itemId);
				action.execute(player);

				/**
				 * delete item, if deletable.
				 */

				if (deletable ==0) {
					Inventory bag = player.getInventory();
					Item resultItem = bag.getItemByObjId(itemObjId);
					if (resultItem != null) 
						bag.removeFromBag(resultItem);
					PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(itemObjId));
				}


		} else 
		{
			log.info("No item effect template found for itemId: " + itemId);
		}
	}
}
