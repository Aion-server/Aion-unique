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
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.id.ItemStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.id.StoneStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponType;

/**
 * @author xavier
 */
public class ItemEquipmentListener
{
	private static final Logger	log	= Logger.getLogger(ItemEquipmentListener.class);

	/**
	 * 
	 * @param itemTemplate
	 * @param slot
	 * @param cgs
	 */
	private static void onItemEquipment(ItemTemplate itemTemplate, int slot, CreatureGameStats<?> cgs)
	{
		TreeSet<StatModifier> modifiers = itemTemplate.getModifiers();
		if (modifiers==null)
		{
			if (cgs instanceof PlayerGameStats)
			{
				log.debug("No effect was found for item "+itemTemplate.getTemplateId());
			}
		}
		
		cgs.addModifiers(ItemStatEffectId.getInstance(itemTemplate.getTemplateId(), slot), modifiers);
	}
	
	/**
	 * @param item
	 * @param cgs
	 */
	public static void onItemEquipment(Item item, Player owner)
	{
		ItemTemplate itemTemplate = item.getItemTemplate();
		onItemEquipment(itemTemplate,item.getEquipmentSlot(),owner.getGameStats());
		addStonesStats(item.getItemStones(), owner.getGameStats());
		recalculateWeaponMastery(owner);	
	}
	
	/**
	 * @param owner
	 */
	private static void recalculateWeaponMastery(Player owner)
	{
		//don't calculate for not initialized equipment
		if(owner.getEquipment() == null)
			return;
		
		for(WeaponType weaponType : WeaponType.values())
		{
			boolean masterySet = owner.getEffectController().isWeaponMasterySet(weaponType);
			boolean weaponEquiped = owner.getEquipment().isWeaponEquipped(weaponType);
			Integer skillId = owner.getSkillList().getWeaponMasterySkill(weaponType);
			if(skillId == null)
				continue;
			//remove effect if no weapon is equiped
			
			if(masterySet && !weaponEquiped)
			{
				owner.getEffectController().removePassiveEffect(skillId);
			}
			//add effect if weapon is equiped
			if(!masterySet && weaponEquiped)
			{
				owner.getController().useSkill(skillId);
			}
		}
	}

	/**
	 * All modifiers of stones will be applied to character
	 * 
	 * @param itemStones
	 * @param cgs
	 */
	private static void addStonesStats(List<ItemStone> itemStones, CreatureGameStats<?> cgs)
	{
		if(itemStones == null || itemStones.size() == 0)
			return;
		
		for(ItemStone stone : itemStones)
		{
			addStoneStats(stone, cgs);
		}
	}
	
	/**
	 * All modifiers of stones will be removed
	 * 
	 * @param itemStones
	 * @param cgs
	 */
	private static void removeStoneStats(List<ItemStone> itemStones, CreatureGameStats<?> cgs)
	{
		if(itemStones == null || itemStones.size() == 0)
			return;
		
		for(ItemStone stone : itemStones)
		{
			TreeSet<StatModifier> modifiers = stone.getModifiers();
			if(modifiers != null)
			{
				cgs.endEffect(StoneStatEffectId.getInstance(stone.getItemObjId(), stone.getSlot()));
			}
		}
	}
	
	/**
	 *  Used when socketing of equipped item
	 *  
	 * @param stone
	 * @param cgs
	 */
	public static void addStoneStats(ItemStone stone, CreatureGameStats<?> cgs)
	{
		TreeSet<StatModifier> modifiers = stone.getModifiers();
		if(modifiers != null)
		{
			cgs.addModifiers(StoneStatEffectId.getInstance(stone.getItemObjId(), stone.getSlot()), modifiers);
		}	
	}

	public static void onItemUnequipment(Item item, Player owner)
	{
		owner.getGameStats().endEffect(ItemStatEffectId.getInstance(item.getItemTemplate().getTemplateId(), item.getEquipmentSlot()));
		removeStoneStats(item.getItemStones(), owner.getGameStats());
		recalculateWeaponMastery(owner);
	}
}
