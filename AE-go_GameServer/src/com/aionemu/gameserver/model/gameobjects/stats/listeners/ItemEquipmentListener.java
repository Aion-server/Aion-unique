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
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

	public static  void onItemEquipmentChange(Inventory inventory, Item item)
	{
		int sign = 1;
		ItemTemplate it = item.getItemTemplate();
		if (inventory.getOwner()==null) {
			log.debug("No owner set for inventory, not changing stats");
			return;
		}
		PlayerGameStats pgs = inventory.getOwner().getGameStats();
		if (pgs==null) {
			log.debug("No GameStats set for inventory owner, skipping stats change");
			return;
		}
		if ((!it.isArmor())&&(!it.isWeapon())) {
			log.debug("Item #"+item.getObjectId()+" isn't an equipment, not changing stats");
			return;
		}
		if (!item.isEquipped()) {
			sign = -1;
		}
		ItemSlot itemSlot = null;
		try {
			itemSlot = ItemSlot.getValue(item.getEquipmentSlot());
		} catch (IllegalArgumentException e) { }
		switch(itemSlot)
		{
			case MAIN_HAND:
				pgs.setMainHandAttack(pgs.getMainHandAttack() + sign*it.getMaxDamage());
				pgs.setMainHandCritRate(pgs.getMainHandCritRate() + sign*it.getCritical());
				pgs.setMainHandAccuracy(pgs.getMainHandAccuracy() + sign*it.getHitAccuracy());
				break;
			case SUB_HAND:
				pgs.setOffHandAttack(pgs.getOffHandAttack() + sign*it.getMaxDamage());
				pgs.setOffHandCritRate(pgs.getOffHandCritRate() + sign*it.getCritical());
				pgs.setOffHandAccuracy(pgs.getOffHandAccuracy() + sign*it.getHitAccuracy());
				break;
			default:
				break;
		}
		pgs.setAgility(pgs.getAgility() + sign*it.getAgility());
		pgs.setKnowledge(pgs.getKnowledge() + sign*it.getKnowledge());
		pgs.setPhysicalDefense(pgs.getPhysicalDefense() + sign*it.getPhysicalDefend());
		pgs.setParry(pgs.getParry() + sign*it.getParrry());
		pgs.setMagicBoost(pgs.getMagicBoost() + sign*it.getMagicalSkillBoost());
		pgs.setMagicAccuracy(pgs.getMagicAccuracy() + sign*it.getMagicalHitAccuracy());
		pgs.setMagicResistance(pgs.getMagicResistance() + sign*it.getMagicalResist());
		log.debug("Changed stats after equipment change of item "+"{#:"+it.getItemId()+",slot:"+it.getItemSlot()+"} to player #"+inventory.getOwner().getObjectId()+":"+pgs);
	}
}
