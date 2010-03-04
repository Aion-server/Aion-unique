/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 *
 * @author Avol, ATracer, kosyachok
 */
public class Equipment
{
	private SortedMap<Integer, Item>        equipment = Collections.synchronizedSortedMap(new TreeMap<Integer, Item>());
	private Player owner;
	private static final Logger log = Logger.getLogger(Storage.class);

	public Equipment(Player player)
	{
		this.owner = player;
	}

	public boolean equipItem(int itemUniqueId, int slot)
	{
		synchronized(this)
		{
			Item item = owner.getInventory().getItemByObjId(itemUniqueId);

			if(item == null)
			{
				return false;
			}
			//don't allow to wear items of higher level
			if(item.getItemTemplate().getLevel() > owner.getCommonData().getLevel())
			{
				PacketSendUtility.sendPacket(owner, 
					SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(
						item.getItemTemplate().getLevel(), new DescriptionId(Integer.parseInt(item.getName()))));
				return false;
			}

			int itemSlotToEquip = 0;

			Item itemInMainHand = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
			Item itemInSubHand = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());

			switch(item.getItemTemplate().getEquipmentType())
			{
				case ARMOR:
					if(!validateEquippedArmor(item, itemInMainHand))
						return false;
					break;
				case WEAPON:
					if(!validateEquippedWeapon(item, itemInMainHand, itemInSubHand))
						return false;
					break;
			}

			//remove item first from inventory to have at least one slot free
			owner.getInventory().removeFromBag(item, false);
			//check whether there is already item in specified slot
			int itemSlotMask = item.getItemTemplate().getItemSlot();

			List<ItemSlot> possibleSlots = ItemSlot.getSlotsFor(itemSlotMask);
			for(ItemSlot possibleSlot : possibleSlots)
			{
				if(equipment.get(possibleSlot.getSlotIdMask()) == null)
				{
					itemSlotToEquip = possibleSlot.getSlotIdMask();
					break;
				}
			}
			// equip first occupied slot if there is no free
			if(itemSlotToEquip == 0)
			{
				itemSlotToEquip = possibleSlots.get(0).getSlotIdMask();
			}

			Item equippedItem = equipment.get(itemSlotToEquip);
			if(equippedItem != null)
			{
				unEquip(equippedItem);
			}

			if(itemSlotToEquip == 0)
				return false;

			item.setEquipped(true);

			equip(itemSlotToEquip, item);
		}
		return true;
	}


	private void equip(int slot, Item item)
	{
		if(equipment.get(slot) != null)
			log.error("CHECKPOINT : putting item to already equiped slot. Info slot: " +
				slot + " new item: " + item.getItemTemplate().getTemplateId() + " old item: "
				+ equipment.get(slot).getItemTemplate().getTemplateId());

		equipment.put(slot, item);
		item.setEquipmentSlot(slot);
		if (owner.getGameStats()!=null)
		{
			ItemEquipmentListener.onItemEquipment(item, owner);
		}
		owner.getLifeStats().updateCurrentStats();
		PacketSendUtility.sendPacket(owner, new SM_UPDATE_ITEM(item));
	}


	/**
	 *	Called when CM_EQUIP_ITEM packet arrives with action 1
	 *
	 * @param itemUniqueId
	 * @param slot
	 * @return true or false
	 */
	public boolean unEquipItem(int itemUniqueId, int slot)
	{
		//if inventory is full unequip action is disabled
		if(owner.getInventory().isFull())
		{
			return false;
		}
		synchronized(this)
		{
			Item itemToUnequip = null;

			for(Item item : equipment.values())
			{
				if(item.getObjectId() == itemUniqueId)
				{
					itemToUnequip = item;
				}
			}

			if(itemToUnequip == null || !itemToUnequip.isEquipped())
			{
				return false;
			}

			//if unequip bow - unequip arrows also
			if(itemToUnequip.getItemTemplate().getWeaponType() == WeaponType.BOW)
			{
				Item possibleArrows = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if(possibleArrows != null && possibleArrows.getItemTemplate().getArmorType() == ArmorType.ARROW)
				{
					//TODO more wise check here is needed
					if(owner.getInventory().getNumberOfFreeSlots() < 1)
						return false;
					unEquip(possibleArrows);
				}
			}
			//if unequip power shard
			if(itemToUnequip.getItemTemplate().isArmor() && itemToUnequip.getItemTemplate().getArmorType() == ArmorType.SHARD)
			{
				owner.unsetState(CreatureState.POWERSHARD);
				PacketSendUtility.sendPacket(owner, new SM_EMOTION(owner, 32, 0, 0));
			}

			unEquip(itemToUnequip);
		}

		return true;
	}

	private void unEquip(Item itemToUnequip)
	{
		equipment.remove(itemToUnequip.getEquipmentSlot());
		itemToUnequip.setEquipped(false);
		if (owner.getGameStats()!=null)
		{
			ItemEquipmentListener.onItemUnequipment(itemToUnequip, owner);
		}
		owner.getLifeStats().updateCurrentStats();
		itemToUnequip = owner.getInventory().putToBag(itemToUnequip, false);
		PacketSendUtility.sendPacket(owner, new SM_UPDATE_ITEM(itemToUnequip));
	}


	/**
	 *  Used during equip process and analyzes equipped slots
	 *
	 * @param item
	 * @param itemInMainHand
	 * @param itemInSubHand
	 * @return
	 */
	private boolean validateEquippedWeapon(Item item, Item itemInMainHand, Item itemInSubHand)
	{
		// check present skill
		int[] requiredSkills = item.getItemTemplate().getWeaponType().getRequiredSkills();

		if(!checkAvaialbeEquipSkills(requiredSkills))
			return false;

		switch(item.getItemTemplate().getWeaponType().getRequiredSlots())
		{
			case 2:
				switch(item.getItemTemplate().getWeaponType())
				{
					//if bow and arrows are equipped + new item is bow - dont uneqiup arrows
					case BOW:
						if(itemInSubHand != null &&
							itemInSubHand.getItemTemplate().getArmorType() != ArmorType.ARROW)
						{
							//TODO more wise check here is needed
							if(owner.getInventory().getNumberOfFreeSlots() < 1)
								return false;
							unEquip(itemInSubHand);
						}
						break;
						//if new item is not bow - unequip arrows
					default:
						if(itemInSubHand != null)
						{
							//TODO more wise check here is needed
							if(owner.getInventory().getNumberOfFreeSlots() < 1)
								return false;
							//remove 2H weapon
							unEquip(itemInSubHand);
						}
				}//no break
			case 1:
				//check dual skill
				if(itemInMainHand != null && !owner.getSkillList().isSkillPresent(19))
				{
					if(owner.getInventory().getNumberOfFreeSlots() < 1)
						return false;
					unEquip(itemInMainHand);
				}
				//check 2h weapon in main hand
				if(itemInMainHand != null && itemInMainHand.getItemTemplate().getWeaponType().getRequiredSlots() == 2)
				{
					if(owner.getInventory().getNumberOfFreeSlots() < 1)
						return false;
					unEquip(itemInMainHand);
				}

				//unequip arrows if bow+arrows were equipeed
				Item possibleArrows = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if(possibleArrows != null && possibleArrows.getItemTemplate().getArmorType() == ArmorType.ARROW)
				{
					//TODO more wise check here is needed
					if(owner.getInventory().getNumberOfFreeSlots() < 1)
						return false;
					unEquip(possibleArrows);
				}
				break;
		}
		return true;
	}

	/**
	 *
	 * @param requiredSkills
	 * @return
	 */
	private boolean checkAvaialbeEquipSkills(int[] requiredSkills)
	{
		boolean isSkillPresent = false;

		//if no skills required - validate as true
		if(requiredSkills.length == 0)
			return true;

		for(int skill : requiredSkills)
		{
			if(owner.getSkillList().isSkillPresent(skill))
			{
				isSkillPresent = true;
				break;
			}
		}
		return isSkillPresent;
	}

	/**
	 *  Used during equip process and analyzes equipped slots
	 *
	 * @param item
	 * @param itemInMainHand
	 * @return
	 */
	private boolean validateEquippedArmor(Item item, Item itemInMainHand)
	{
		//allow wearing of jewelry etc stuff
		ArmorType armorType = item.getItemTemplate().getArmorType();
		if(armorType == null)
			return true;

		// check present skill
		int[] requiredSkills = armorType.getRequiredSkills();
		if(!checkAvaialbeEquipSkills(requiredSkills))
			return false;

		switch(item.getItemTemplate().getArmorType())
		{
			case ARROW:
				if(itemInMainHand == null
					|| itemInMainHand.getItemTemplate().getWeaponType() != WeaponType.BOW)
					return false;
				break;
			case SHIELD:
				if(itemInMainHand != null
					&& itemInMainHand.getItemTemplate().getWeaponType().getRequiredSlots() == 2)
				{
					//TODO more wise check here is needed
					if(owner.getInventory().isFull())
						return false;
					//remove 2H weapon
					unEquip(itemInMainHand);
				}
				break;
		}
		return true;
	}

	/**
	 *  Will look item in equipment item set
	 *
	 * @param value
	 * @return Item
	 */
	public Item getEquippedItemByObjId(int value)
	{
		for(Item item : equipment.values())
		{
			if(item.getObjectId() == value)
				return item;
		}
		return null;
	}

	/**
	 * 
	 * @param value
	 * @return List<Item>
	 */
	public List<Item> getEquippedItemsByItemId(int value)
	{
		List<Item> equippedItemsById = new ArrayList<Item>();
		for(Item item : equipment.values())
		{
			if(item.getItemTemplate().getTemplateId() == value)
				equippedItemsById.add(item);
		}
		return equippedItemsById;
	}

	/**
	 * 
	 * @return List<Item>
	 */
	public List<Item> getEquippedItems()
	{
		List<Item> equippedItems = new ArrayList<Item>();
		equippedItems.addAll(equipment.values());

		return equippedItems;
	}

	/**
	 *  Should be called only when loading from DB for items isEquipped=1
	 *  
	 * @param item
	 */
	public void onLoadHandler(Item item)
	{
		if(equipment.containsKey(item.getEquipmentSlot()))
		{
			log.warn("Duplicate equipped item in slot : " + item.getEquipmentSlot() + " " + owner.getObjectId());
			return;
		}
		equipment.put(item.getEquipmentSlot(), item);
	}

	/**
	 * Should be called only when equipment object totaly constructed on player loading
	 * Applies every equipped item stats modificators
	 */
	public void onLoadApplyEquipmentStats()
	{
		for(Item item : equipment.values())
		{
			if (owner.getGameStats() != null)
			{
				if(item.getEquipmentSlot() != ItemSlot.MAIN_OFF_HAND.getSlotIdMask()
					&& item.getEquipmentSlot() != ItemSlot.SUB_OFF_HAND.getSlotIdMask())
					ItemEquipmentListener.onItemEquipment(item, owner);
			}
			if(owner.getLifeStats() != null)
			{
				if(item.getEquipmentSlot() != ItemSlot.MAIN_OFF_HAND.getSlotIdMask()
					&& item.getEquipmentSlot() != ItemSlot.SUB_OFF_HAND.getSlotIdMask())
					owner.getLifeStats().synchronizeWithMaxStats();
			}
		}
	}

	/**
	 * @return true or false
	 */
	public boolean isShieldEquipped()
	{
		Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		return subHandItem != null && subHandItem.getItemTemplate().getArmorType() == ArmorType.SHIELD;
	}

	/**
	 *
	 * @return <tt>WeaponType</tt> of current weapon in main hand or null
	 */
	public WeaponType getMainHandWeaponType()
	{
		Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		if(mainHandItem == null)
			return null;

		return mainHandItem.getItemTemplate().getWeaponType();
	}

	/**
	 *
	 * @return <tt>WeaponType</tt> of current weapon in off hand or null
	 */
	public WeaponType getOffHandWeaponType()
	{
		Item offHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if(offHandItem != null && offHandItem.getItemTemplate().isWeapon())
			return offHandItem.getItemTemplate().getWeaponType();

		return null;
	}


	public boolean isPowerShardEquipped()
	{
		for(Item item : equipment.values())
		{
			if(item.getItemTemplate().isArmor() && item.getItemTemplate().getArmorType() == ArmorType.SHARD)
				return true;
		}
		return false;
	}

	public Item getMainHandPowerShard()
	{
		Item mainHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_RIGHT.getSlotIdMask());
		if(mainHandPowerShard != null)
			return mainHandPowerShard;

		return null;
	}

	public Item getOffHandPowerShard()
	{
		Item offHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_LEFT.getSlotIdMask());
		if(offHandPowerShard != null)
			return offHandPowerShard;

		return null;
	}

	/**
	 * @param powerShardItem
	 * @param count
	 */
	public void usePowerShard(Item powerShardItem, int count)
	{
		decreaseEquippedItemCount(powerShardItem.getObjectId(), count);

		if(powerShardItem.getItemCount() <= 0)
		{// Search for next same power shards stack
			List<Item> powerShardStacks = owner.getInventory().getItemsByItemId(powerShardItem.getItemTemplate().getTemplateId());
			if(powerShardStacks.size() != 0)
			{
				equipItem(powerShardStacks.get(0).getObjectId(), powerShardItem.getEquipmentSlot());
			}
			else
			{
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.NO_POWER_SHARD_LEFT());
				owner.unsetState(CreatureState.POWERSHARD);
			}
		}
	}

	public void useArrow()
	{
		Item arrow = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());

		if(arrow == null || !arrow.getItemTemplate().isArmor() && arrow.getItemTemplate().getArmorType() != ArmorType.ARROW)
			return;

		decreaseEquippedItemCount(arrow.getObjectId(), 1);
	}


	private void decreaseEquippedItemCount(int itemObjId, int count)
	{
		Item equippedItem = getEquippedItemByObjId(itemObjId);

		// Only Arrows and Shards can be decreased
		if(equippedItem.getItemTemplate().getArmorType() != ArmorType.SHARD
			&& equippedItem.getItemTemplate().getArmorType() != ArmorType.ARROW)
			return;

		if(equippedItem.getItemCount() >= count)
			equippedItem.decreaseItemCount(count);
		else
			equippedItem.decreaseItemCount(equippedItem.getItemCount());

		if(equippedItem.getItemCount() == 0)
		{
			equipment.remove(equippedItem.getEquipmentSlot());
			PacketSendUtility.sendPacket(owner, new SM_DELETE_ITEM(equippedItem.getObjectId()));
			DAOManager.getDAO(InventoryDAO.class).store(equippedItem, owner.getObjectId());
		}

		PacketSendUtility.sendPacket(owner, new SM_UPDATE_ITEM(equippedItem));
	}

	/**
	 *
	 * @return true or false
	 */
	public boolean switchHands()
	{
		Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		Item mainOffHandItem = equipment.get(ItemSlot.MAIN_OFF_HAND.getSlotIdMask());
		Item subOffHandItem = equipment.get(ItemSlot.SUB_OFF_HAND.getSlotIdMask());

		List<Item> equippedWeapon = new ArrayList<Item>();

		if(mainHandItem != null)
			equippedWeapon.add(mainHandItem);
		if(subHandItem != null)
			equippedWeapon.add(subHandItem);
		if(mainOffHandItem != null)
			equippedWeapon.add(mainOffHandItem);
		if(subOffHandItem != null)
			equippedWeapon.add(subOffHandItem);

		for(Item item : equippedWeapon)
		{
			equipment.remove(item.getEquipmentSlot());
			item.setEquipped(false);
			PacketSendUtility.sendPacket(owner, new SM_UPDATE_ITEM(item, true));
		}

		if (owner.getGameStats() != null)
		{
			for(Item item : equippedWeapon)
			{
				if(item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()
					|| item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask())
					ItemEquipmentListener.onItemUnequipment(item, owner);
			}
		}

		for(Item item : equippedWeapon)
		{
			if(item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.MAIN_OFF_HAND.getSlotIdMask());

			else if(item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.SUB_OFF_HAND.getSlotIdMask());

			else if(item.getEquipmentSlot() == ItemSlot.MAIN_OFF_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.MAIN_HAND.getSlotIdMask());

			else if(item.getEquipmentSlot() == ItemSlot.SUB_OFF_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.SUB_HAND.getSlotIdMask());
		}

		for(Item item : equippedWeapon)
		{
			equipment.put(item.getEquipmentSlot(), item);
			item.setEquipped(true);
			PacketSendUtility.sendPacket(owner, new SM_UPDATE_ITEM(item, true));
		}

		if (owner.getGameStats() != null)
		{
			for(Item item : equippedWeapon)
			{
				if(item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()
					|| item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask())
					ItemEquipmentListener.onItemEquipment(item, owner);
			}
		}

		owner.getLifeStats().updateCurrentStats();

		return true;
	}

	/**
	 * @param weaponType
	 */
	public boolean isWeaponEquipped(WeaponType weaponType)
	{
		if(equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()) != null &&
			equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType)
		{
			return true;
		}
		if(equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()) != null &&
			equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType)
		{
			return true;
		}
		return false;
	}
	
	public Item getMainHandWeapon()
	{
		return equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
	}
	
	public Item getOffHandWeapon()
	{
		return equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
	}
}
