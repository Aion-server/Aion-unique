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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemStat;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

	public static void onItemEquipmentChange(Inventory inventory, Item item)
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
		log.debug("changing game stats "+pgs+" for equipment change of player #"+inventory.getOwner().getObjectId());
		int effectId;
		if (!item.isEquipped()) {
			effectId = item.getEffectId();
			pgs.endEffect(effectId);
			log.debug("Changed stats after equipment change of item "+ item +" to player #" + inventory.getOwner().getObjectId() + ":" + pgs);
			return;
		}
		try {
			effectId = pgs.getEffectId();
		} catch (IllegalAccessException e) {
			log.debug("cannot get an effect id");
			return;
		}
		item.setEffectId(effectId);
		ItemSlot itemSlot = null;
		try
		{
			itemSlot = ItemSlot.getValue(item.getEquipmentSlot());
		}
		catch(IllegalArgumentException e)
		{
		}
		if (it.getItemStats()==null) {
			log.debug("cannot get item stats from item");
		} else {
			for (ItemStat stat : it.getItemStats().getStat()) {
				pgs.addEffectOnStat(effectId, stat.getStatEnum().getMainOrSubHandStat(itemSlot), stat.getValue(), stat.isBonus());
			}
		}
		// TODO Convert theses attributes to <stat ...> elements
//		if (it.getAttackDelay()!=0) {
//			pgs.addEffectOnStat(effectId, StatEnum.ATTACK_SPEED, Integer.toString(it.getAttackDelay()));
//		}
//		if (it.getHitCount()!=0) {
//			pgs.addEffectOnStat(effectId, StatEnum.HIT_COUNT, Integer.toString(it.getHitCount()));
//		}
//		if (it.getAttackRange()!=0) {
//			pgs.addEffectOnStat(effectId, StatEnum.ATTACK_RANGE, Integer.toString(it.getAttackRange()));
//		}
//		if (it.getMaxDamage()!=0) {
//			if ((itemSlot==ItemSlot.MAIN_HAND)||(itemSlot==ItemSlot.SUB_HAND)) {
//				pgs.addEffectOnStat(effectId, StatEnum.MAX_DAMAGES.getMainOrSubHandStat(itemSlot), Integer.toString(it.getMaxDamage()));
//			}
//			pgs.addEffectOnStat(effectId, StatEnum.MAX_DAMAGES, Integer.toString(it.getMaxDamage()));
//		}
//		if (it.getMinDamage()!=0) {
//			pgs.addEffectOnStat(effectId, StatEnum.MIN_DAMAGES, Integer.toString(it.getMinDamage()));
//		}
		if (it.getAttackType()!=null) {
			pgs.addEffectOnStat(effectId, StatEnum.IS_MAGICAL_ATTACK, (it.getAttackType().contains("magic"))?"1":"0");
		}
		
		log.debug("Changed stats after equipment change of item "+ item +" to player #" + inventory.getOwner().getObjectId() + ":" + pgs);
	}
	
	public static void onLevelChange (Inventory inventory) {
		for (Item item : inventory.getEquippedItems()) {
			onItemEquipmentChange(inventory, item);
		}
	}
}
