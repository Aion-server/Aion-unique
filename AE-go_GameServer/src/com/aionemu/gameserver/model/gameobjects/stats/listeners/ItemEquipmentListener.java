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
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
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

	public static void onItemEquipment(ItemTemplate template, int slotId, CreatureGameStats<?> cgs)
	{
		StatEffect effect = template.getEffect();
		if (effect==null)
		{
			if (cgs instanceof PlayerGameStats)
			{
				log.debug("No effect was found for item "+template.getItemId());
			}
			return;
		}
		
		List<ItemSlot> slots = ItemSlot.getSlotsFor(slotId);
		StatEffect slotEffect = effect.getEffectForSlot(slots.get(0));
		if (cgs instanceof PlayerGameStats)
		{
			log.debug("Adding "+slotEffect+" for slot "+slots.get(0));
		}
		cgs.addEffect(slotEffect);
		
		// TODO Convert theses attributes to <stat ...> elements
		if(template.getAttackType() != null)
		{
			SetModifier sm = new SetModifier(StatEnum.IS_MAGICAL_ATTACK, (template.getAttackType().contains("magic")) ? 1
				: 0);
			sm.setOwner(effect);
			cgs.addModifier(sm);
		}
	}
	
	public static void onItemUnequipment(ItemTemplate template, CreatureGameStats<?> cgs)
	{
		if (template.getEffect()!=null)
		{
			cgs.endEffect(template.getEffect());
		}
		else
		{
			log.debug("No effect was found for item "+template.getItemId());
		}
	}
	
	public static void onItemEquipmentChange(Inventory inventory, Item item, int slotId)
	{
		ItemTemplate it = item.getItemTemplate();

		if(inventory.getOwner() == null)
		{
			log.debug("No owner set for inventory, not changing stats");
			return;
		}

		PlayerGameStats pgs = inventory.getOwner().getGameStats();
		if(pgs == null)
		{
			log.debug("No GameStats set for inventory owner, skipping stats change");
			return;
		}

		if((!it.isArmor()) && (!it.isWeapon()))
		{
			log.debug("Item #" + item.getObjectId() + " isn't an equipment, not changing stats");
			return;
		}

		log.debug("changing game stats " + pgs + " for equipment change of player #"
			+ inventory.getOwner().getObjectId());

		if(!item.isEquipped())
		{
			onItemUnequipment(it, pgs);
		}
		else
		{
			onItemEquipment(it, slotId, pgs);
		}

		log.debug("Changed stats after equipment change of item " + item + " to player #"
			+ inventory.getOwner().getObjectId() + ":" + pgs);
	}
}
