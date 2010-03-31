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

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.CreatureGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.id.ItemSetStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.id.ItemStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.id.StoneStatEffectId;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.itemset.PartBonus;

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
		
		// Check if belongs to ItemSet
		if(itemTemplate.isItemSet())
			onItemSetPartEquipment(itemTemplate.getItemSet(), owner);

		if(item.hasManaStones())
			addStonesStats(item.getItemStones(), owner.getGameStats());
		
		addGodstoneEffect(owner, item);
		
		if(item.getItemTemplate().isWeapon())
			recalculateWeaponMastery(owner);
		
		if(item.getItemTemplate().isArmor())
			recalculateArmorMastery(owner);
	}

	/**
	 * 
	 * @param itemSetTemplate
	 * @param player
	 */
	private static void onItemSetPartEquipment(ItemSetTemplate itemSetTemplate, Player player)
	{
		if(itemSetTemplate == null)
			return;

		// 1.- Check equipment for items already equip with this itemSetTemplate id
		int itemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId());

		// 2.- Check Item Set Parts and add effects one by one if not done already
		for(PartBonus itempartbonus : itemSetTemplate.getPartbonus())
		{
			ItemSetStatEffectId setEffectId = ItemSetStatEffectId.getInstance(itemSetTemplate.getId(), itempartbonus
				.getCount());
			// If the partbonus was not applied before, do it now
			if(itempartbonus.getCount() <= itemSetPartsEquipped
				&& !player.getGameStats().effectAlreadyAdded(setEffectId))
			{
				player.getGameStats().addModifiers(setEffectId, itempartbonus.getModifiers());
			}
		}

		// 3.- Finally check if all items are applied and set the full bonus if not already applied
		if(itemSetTemplate.getFullbonus() != null && itemSetPartsEquipped == itemSetTemplate.getFullbonus().getCount())
		{
			ItemSetStatEffectId setEffectId = ItemSetStatEffectId.getInstance(itemSetTemplate.getId(),
				itemSetPartsEquipped + 1);
			if(!player.getGameStats().effectAlreadyAdded(setEffectId))
			{
				// Add the full bonus with index = total parts + 1 to avoid confusion with part bonus equal to number of
				// objects
				player.getGameStats().addModifiers(setEffectId, itemSetTemplate.getFullbonus().getModifiers());
			}
		}
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
	 * @param owner
	 */
	private static void recalculateArmorMastery(Player owner)
	{
		//don't calculate for not initialized equipment
		if(owner.getEquipment() == null)
			return;
		
		for(ArmorType armorType : ArmorType.values())
		{
			boolean masterySet = owner.getEffectController().isArmorMasterySet(armorType);
			boolean armorEquiped = owner.getEquipment().isArmorEquipped(armorType);
			Integer skillId = owner.getSkillList().getArmorMasterySkill(armorType);
			if(skillId == null)
				continue;
			//remove effect if no armor is equiped
			
			if(masterySet && !armorEquiped)
			{
				owner.getEffectController().removePassiveEffect(skillId);
			}
			//add effect if armor is equiped
			if(!masterySet && armorEquiped)
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
	private static void addStonesStats(Set<ManaStone> itemStones, CreatureGameStats<?> cgs)
	{
		if(itemStones == null || itemStones.size() == 0)
			return;
		
		for(ManaStone stone : itemStones)
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
	private static void removeStoneStats(Set<ManaStone> itemStones, CreatureGameStats<?> cgs)
	{
		if(itemStones == null || itemStones.size() == 0)
			return;
		
		for(ManaStone stone : itemStones)
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
	public static void addStoneStats(ManaStone stone, CreatureGameStats<?> cgs)
	{
		TreeSet<StatModifier> modifiers = stone.getModifiers();
		if(modifiers != null)
		{
			cgs.addModifiers(StoneStatEffectId.getInstance(stone.getItemObjId(), stone.getSlot()), modifiers);
		}	
	}
	
	/**
	 * 
	 * @param item
	 * @param owner
	 */
	public static void onItemUnequipment(Item item, Player owner)
	{
		// Check if belongs to an ItemSet
		if(item.getItemTemplate().isItemSet())
			onItemSetPartUnequipment(item.getItemTemplate().getItemSet(), owner);

		owner.getGameStats().endEffect(
			ItemStatEffectId.getInstance(item.getItemTemplate().getTemplateId(), item.getEquipmentSlot()));

		if(item.hasManaStones())
			removeStoneStats(item.getItemStones(), owner.getGameStats());

		removeGodstoneEffect(owner, item);
		
		if(item.getItemTemplate().isWeapon())
			recalculateWeaponMastery(owner);
		
		if(item.getItemTemplate().isArmor())
			recalculateArmorMastery(owner);
	}
	
	/**
	 * 
	 * @param itemSetTemplate
	 * @param player
	 */
	private static void onItemSetPartUnequipment(ItemSetTemplate itemSetTemplate, Player player)
	{
		if(itemSetTemplate == null)
			return;

		// 1.- Check number of item parts equipped before the removal (i.e. current + 1)
		int previousItemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId()) + 1;

		// 2.- Check if removed one item from the full set and if so remove the full bonus
		if(itemSetTemplate.getFullbonus() != null
			&& previousItemSetPartsEquipped == itemSetTemplate.getFullbonus().getCount())
		{
			// Full bonus was added with index = total parts + 1 to avoid confusion with part bonus equal to total
			// number of item set parts
			player.getGameStats()
				.endEffect(
					ItemSetStatEffectId.getInstance(itemSetTemplate.getId(),
						itemSetTemplate.getFullbonus().getCount() + 1));
		}

		// 3.- Check Item Set Parts and remove appropriate effects
		for(PartBonus itempartbonus : itemSetTemplate.getPartbonus())
		{
			// Remove modifier if not applicable anymore
			if(itempartbonus.getCount() == previousItemSetPartsEquipped)
			{
				player.getGameStats().endEffect(
					ItemSetStatEffectId.getInstance(itemSetTemplate.getId(), itempartbonus.getCount()));
			}
		}
	}

	
	/**
	 * @param item
	 */
	private static void addGodstoneEffect(Player player, Item item)
	{
		if(item.getGodStone() != null)
		{
			item.getGodStone().onEquip(player);
		}
	}
	
	/**
	 * @param item
	 */
	private static void removeGodstoneEffect(Player player, Item item)
	{
		if(item.getGodStone() != null)
		{
			item.getGodStone().onUnEquip(player);
		}
	}
}
