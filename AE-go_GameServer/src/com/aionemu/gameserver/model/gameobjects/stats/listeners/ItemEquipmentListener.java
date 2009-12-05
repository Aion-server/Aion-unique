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
package com.aionemu.gameserver.model.gameobjects.stats.listeners;

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.AddModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.PercentModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.PowerModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.ReplaceModifier;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemStat;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

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
			pgs.endEffect(item.getObjectId());
			log.debug("Changed stats after equipment change of item " + item + " to player #"
				+ inventory.getOwner().getObjectId() + ":" + pgs);
			return;
		}

		if(it.getItemStats() == null)
		{
			log.debug("cannot get item stats from item");
		}
		else
		{
			List<ItemSlot> slots = ItemSlot.getSlotsFor(slotId);
			for(ItemStat stat : it.getItemStats().getStat())
			{
				StatEnum statToModify = stat.getStatEnum().getMainOrSubHandStat(slots.get(0));
				if(stat.getValue().contains("%"))
				{
					pgs.addModifierOnStat(statToModify, new PercentModifier(item.getObjectId(), stat.isBonus(), stat.getValue(), statToModify.getSign()));
				}
				else
				{
					if(statToModify.isReplace())
					{
						pgs.addModifierOnStat(statToModify, new ReplaceModifier(item.getObjectId(), Integer.parseInt(stat.getValue())));
					}
					else
					{
						if ((statToModify==StatEnum.MAX_DAMAGES)||(statToModify==StatEnum.MIN_DAMAGES)) 
						{
							pgs.addModifierOnStat(statToModify, new AddModifier(item.getObjectId(), stat.isBonus(), Integer.parseInt(stat.getValue()), statToModify.getSign()));
							log.debug("testing damages : max:"+pgs.getBaseStat(StatEnum.MAX_DAMAGES)+",min:"+pgs.getBaseStat(StatEnum.MIN_DAMAGES));
							if ((pgs.getBaseStat(StatEnum.MAX_DAMAGES)!=0)&&(pgs.getBaseStat(StatEnum.MIN_DAMAGES)!=0)) 
							{
								pgs.addModifierOnStat(StatEnum.MAIN_HAND_POWER, new PowerModifier (item.getObjectId(), pgs.getBaseStat(StatEnum.MIN_DAMAGES), pgs.getBaseStat(StatEnum.MAX_DAMAGES)));
							}
						} else {
							pgs.addModifierOnStat(statToModify, new AddModifier(item.getObjectId(), stat.isBonus(), Integer.parseInt(stat.getValue()), statToModify.getSign()));
						}
					}
				}
			}
			
		}

		// TODO Convert theses attributes to <stat ...> elements
		if(it.getAttackType() != null)
		{
			pgs.addModifierOnStat(StatEnum.IS_MAGICAL_ATTACK, new ReplaceModifier(item.getObjectId(), (it.getAttackType()
				.contains("magic")) ?  1 : 0));
		}

		log.debug("Changed stats after equipment change of item " + item + " to player #"
			+ inventory.getOwner().getObjectId() + ":" + pgs);
	}
}
