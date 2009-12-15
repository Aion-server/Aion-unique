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

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.ItemEffect;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.SetModifier;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

	public static ItemEffect onItemEquipment(ItemTemplate itemTemplate, int slot, CreatureGameStats<?> cgs)
	{
		StatEffect effect = itemTemplate.getEffect();
		if (effect==null)
		{
			if (cgs instanceof PlayerGameStats)
			{
				log.debug("No effect was found for item "+itemTemplate.getItemId());
			}
			return null;
		}
		
		List<ItemSlot> slots = ItemSlot.getSlotsFor(slot);
		ItemEffect slotEffect = effect.getEffectForSlot(slots.get(0));
		
		// TODO Convert theses attributes to <stat ...> elements
		if(itemTemplate.getAttackType() != null)
		{
			SetModifier sm = new SetModifier(StatEnum.IS_MAGICAL_ATTACK, (itemTemplate.getAttackType().contains("magic")) ? 1
				: 0);
			slotEffect.add(sm);
		}
		
		if (cgs instanceof PlayerGameStats)
		{
			log.debug("Adding "+slotEffect+" for slot "+slots.get(0));
		}
		
		cgs.addEffect(slotEffect);
		
		return slotEffect;
	}
	
	public static void onItemEquipment(Item item, CreatureGameStats<?> cgs)
	{
		ItemTemplate itemTemplate = item.getItemTemplate();
		item.setEffect(onItemEquipment(itemTemplate,item.getEquipmentSlot(),cgs));
	}
	
	public static void onItemUnequipment(Item item, CreatureGameStats<?> cgs)
	{
		if (item.getEffect()!=null)
		{
			cgs.endEffect(item.getEffect());
		}
	}
}
