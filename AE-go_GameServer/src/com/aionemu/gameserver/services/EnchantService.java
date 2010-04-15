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
package com.aionemu.gameserver.services;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Storage;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 * 
 */
public class EnchantService
{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(EnchantService.class);
	@Inject
	private ItemService			itemService;

	/**
	 * @param player
	 * @param targetItem
	 * @param parentItem 
	 */
	public void breakItem(Player player, Item targetItem, Item parentItem)
	{
		Storage inventory = player.getInventory();

		ItemTemplate itemTemplate = targetItem.getItemTemplate();
		ItemQuality quality = itemTemplate.getItemQuality();

		int number = 0;
		int level = 0;
		switch(quality)
		{
			case COMMON:
			case JUNK:
				number = Rnd.get(1, 2);
				level = Rnd.get(0, 5);
				break;
			case RARE:
				number = Rnd.get(1, 3);
				level = Rnd.get(0, 10);
				break;
			case LEGEND:
			case MYTHIC:
				number = Rnd.get(1, 3);
				level = Rnd.get(0, 15);
				break;
			case EPIC:
			case UNIQUE:
				number = Rnd.get(1, 3);
				level = Rnd.get(0, 20);
				break;
		}

		int enchantItemLevel = targetItem.getItemTemplate().getLevel() + level;
		int enchantItemId = 166000000 + enchantItemLevel;

		inventory.removeFromBag(targetItem, true);
		PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(targetItem.getObjectId()));
		
		inventory.removeFromBagByObjectId(parentItem.getObjectId(), 1);

		itemService.addItem(player, enchantItemId, number);
	}

	/**
	 * @param player
	 * @param parentItem
	 * @param targetItem
	 */
	public boolean enchantItem(Player player, Item parentItem, Item targetItem)
	{
		int enchantStoneLevel = parentItem.getItemTemplate().getLevel();
		int targetItemLevel = targetItem.getItemTemplate().getLevel();

		if(targetItemLevel > enchantStoneLevel)
			return false;

		int qualityCap = 0;

		ItemQuality quality = targetItem.getItemTemplate().getItemQuality();

		switch(quality)
		{
			case COMMON:
			case JUNK:
				qualityCap = 0;
				break;
			case RARE:
				qualityCap = 5;
				break;
			case LEGEND:
			case MYTHIC:
				qualityCap = 10;
				break;
			case EPIC:
			case UNIQUE:
				qualityCap = 15;
				break;
		}

		int success = 50;

		int levelDiff = enchantStoneLevel - targetItemLevel;

		int extraSuccess = levelDiff - qualityCap;
		if(extraSuccess > 0)
		{
			success += extraSuccess * 5;
		}

		if(success >= 95)
			success = 95;

		boolean result = false;

		if(Rnd.get(0, 100) < success)
			result = true;

		int currentEnchant = targetItem.getEchantLevel();

		if(!result)
		{
			if(currentEnchant > 0)
				currentEnchant -= 1;
		}
		else
		{
			if(currentEnchant < 10)
				currentEnchant += 1;
		}
		
		targetItem.setEchantLevel(currentEnchant);
		PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(targetItem));
		
		if(targetItem.isEquipped())
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		else
			player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
		
		return result;
	}
}