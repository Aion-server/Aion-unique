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
package com.aionemu.gameserver.model.gameobjects.stats.listeners;

import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.ItemStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

	public static void onItemEquipment(ItemTemplate itemTemplate, int slot, CreatureGameStats<?> cgs)
	{
		TreeSet<StatModifier> modifiers = itemTemplate.getModifiers();
		if (modifiers==null)
		{
			if (cgs instanceof PlayerGameStats)
			{
				log.debug("No effect was found for item "+itemTemplate.getItemId());
			}
		}
		
		cgs.addModifiers(ItemStatEffectId.getInstance(itemTemplate.getItemId(), slot), modifiers);
	}
	
	public static void onItemEquipment(Item item, CreatureGameStats<?> cgs)
	{
		ItemTemplate itemTemplate = item.getItemTemplate();
		onItemEquipment(itemTemplate,item.getEquipmentSlot(),cgs);
	}
	
	public static void onItemUnequipment(Item item, CreatureGameStats<?> cgs)
	{
		cgs.endEffect(ItemStatEffectId.getInstance(item.getItemTemplate().getItemId(), item.getEquipmentSlot()));
	}
}
