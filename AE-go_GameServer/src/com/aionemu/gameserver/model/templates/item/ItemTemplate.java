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
package com.aionemu.gameserver.model.templates.item;

import java.util.TreeSet;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.itemengine.actions.ItemActions;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * @author Luno modified by ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item_templates")
public class ItemTemplate extends VisibleObjectTemplate
{
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String				id;

	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate	modifiers;
	
	@XmlElement(name = "actions", required = false)
	protected ItemActions actions;

	@XmlAttribute(name = "slot")
	private int					itemSlot;

	@XmlAttribute(name = "equipment_type")
	private EquipType			equipmentType;

	@XmlAttribute(name = "cash_item")
	private int					cashItem;

	@XmlAttribute(name = "dmg_decal")
	private int					dmgDecal;
	
	@XmlAttribute(name = "weapon_boost")
	private int					weaponBoost;

	@XmlAttribute(name = "price")
	private int					price;
	
	@XmlAttribute(name = "ap")
	private int					abyssPoints;
	
	@XmlAttribute(name = "ai")
	private int					abyssItem;
	
	@XmlAttribute(name = "aic")
	private int					abyssItemCount;

	@XmlAttribute(name = "max_stack_count")
	private int					maxStackCount;

	@XmlAttribute(name = "level")
	private int					level;

	@XmlAttribute(name = "quality")
	private ItemQuality			itemQuality;

	@XmlAttribute(name = "item_type")
	private String				itemType;						// TODO enum

	@XmlAttribute(name = "weapon_type")
	private WeaponType				weaponType;

	@XmlAttribute(name = "armor_type")
	private ArmorType				armorType;

	@XmlAttribute(name = "attack_type")
	private String				attackType;					// TODO enum

	@XmlAttribute(name = "attack_gap")
	private float				attackGap;						// TODO enum

	@XmlAttribute(name = "lore")
	private boolean				lore;

	@XmlAttribute(name = "desc")
	private String				description;					// TODO string or int

	@XmlAttribute(name = "can_exchange")
	private boolean				canExchange;

	@XmlAttribute(name = "can_sell_to_npc")
	private boolean				canSellToNpc;

	@XmlAttribute(name = "cdcw")
	private boolean				canDepositToCharacterWarehouse;

	@XmlAttribute(name = "cdaw")
	private boolean				canDepositToAccountWarehouse;

	@XmlAttribute(name = "cdgw")
	private boolean				canDepositToGuildWarehouse;

	@XmlAttribute(name = "breakable")
	private boolean				breakable;

	@XmlAttribute(name = "soul_bind")
	private boolean				soulBind;

	@XmlAttribute(name = "remove_when_logout")
	private boolean				removeWhenLogout;

	@XmlAttribute(name = "gender")
	private String				genderPermitted;				// enum

	@XmlAttribute(name = "option_slot_bonus")
	private int					optionSlotBonus;

	@XmlAttribute(name = "bonus_apply")
	private String				bonusApply;					// enum

	@XmlAttribute(name = "no_enchant")
	private boolean				noEnchant;

	@XmlAttribute(name = "can_proc_enchant")
	private boolean				canProcEnchant;

	@XmlAttribute(name = "can_split")
	private boolean				canSplit;

	@XmlAttribute(name = "drop")
	private boolean				itemDropPermitted;
	
	@XmlAttribute(name = "dye")
	private boolean				itemDyePermitted;

	@XmlAttribute(name = "race")
	private String				racePermitted;

	private int					itemId;
	
	@XmlElement(name = "godstone")
	private GodstoneInfo godstoneInfo;

	public int getItemSlot()
	{
		return itemSlot;
	}

	/**
	 * @return the modifiers
	 */
	public TreeSet<StatModifier> getModifiers()
	{
		if (modifiers!=null)
		{
			return modifiers.getModifiers();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return the actions
	 */
	public ItemActions getActions()
	{
		return actions;
	}

	/**
	 * 
	 * @return the equipmentType
	 */
	public EquipType getEquipmentType()
	{
		return equipmentType;
	}

	/**
	 * @return the price
	 */
	public int getPrice()
	{
		return price;
	}

	/**
	 * @return the abyssPoints
	 */
	public int getAbyssPoints()
	{
		return abyssPoints;
	}

	/**
	 * @return the abyssItem
	 */
	public int getAbyssItem()
	{
		return abyssItem;
	}

	/**
	 * @return the abyssItemCount
	 */
	public int getAbyssItemCount()
	{
		return abyssItemCount;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the quality
	 */
	public ItemQuality getItemQuality()
	{
		return itemQuality;
	}

	/**
	 * @return the itemType
	 */
	public String getItemType()
	{
		return itemType;
	}

	/**
	 * @return the weaponType
	 */
	public WeaponType getWeaponType()
	{
		return weaponType;
	}

	/**
	 * @return the armorType
	 */
	public ArmorType getArmorType()
	{
		return armorType;
	}

	/**
	 * @return the description
	 */
	public int getNameId()
	{
		return Integer.parseInt(description);
	}

	/**
	 * @return the cashItem
	 */
	public int getCashItem()
	{
		return cashItem;
	}

	/**
	 * @return the dmgDecal
	 */
	public int getDmgDecal()
	{
		return dmgDecal;
	}

	/**
	 * @return the maxStackCount
	 */
	public int getMaxStackCount()
	{
		return maxStackCount;
	}

	/**
	 * @return the attackType
	 */
	public String getAttackType()
	{
		return attackType;
	}

	/**
	 * @return the attackGap
	 */
	public float getAttackGap()
	{
		return attackGap;
	}

	/**
	 * @return the lore
	 */
	public boolean isLore()
	{
		return lore;
	}

	/**
	 * @return the canExchange
	 */
	public boolean isCanExchange()
	{
		return canExchange;
	}

	/**
	 * @return the canSellToNpc
	 */
	public boolean isCanSellToNpc()
	{
		return canSellToNpc;
	}

	/**
	 * @return the canDepositToCharacterWarehouse
	 */
	public boolean isCanDepositToCharacterWarehouse()
	{
		return canDepositToCharacterWarehouse;
	}

	/**
	 * @return the canDepositToAccountWarehouse
	 */
	public boolean isCanDepositToAccountWarehouse()
	{
		return canDepositToAccountWarehouse;
	}

	/**
	 * @return the canDepositToGuildWarehouse
	 */
	public boolean isCanDepositToGuildWarehouse()
	{
		return canDepositToGuildWarehouse;
	}

	/**
	 * @return the breakable
	 */
	public boolean isBreakable()
	{
		return breakable;
	}

	/**
	 * @return the soulBind
	 */
	public boolean isSoulBind()
	{
		return soulBind;
	}

	/**
	 * @return the removeWhenLogout
	 */
	public boolean isRemoveWhenLogout()
	{
		return removeWhenLogout;
	}

	/**
	 * @return the genderPermitted
	 */
	public String getGenderPermitted()
	{
		return genderPermitted;
	}

	/**
	 * @return the optionSlotBonus
	 */
	public int getOptionSlotBonus()
	{
		return optionSlotBonus;
	}

	/**
	 * @return the bonusApply
	 */
	public String getBonusApply()
	{
		return bonusApply;
	}

	/**
	 * @return the noEnchant
	 */
	public boolean isNoEnchant()
	{
		return noEnchant;
	}

	/**
	 * @return the canProcEnchant
	 */
	public boolean isCanProcEnchant()
	{
		return canProcEnchant;
	}

	/**
	 * @return the canSplit
	 */
	public boolean isCanSplit()
	{
		return canSplit;
	}

	/**
	 * @return the dyePermitted
	 */
	public boolean isItemDyePermitted()
	{
		return itemDyePermitted;
	}

	/**
	 * @return the itemDropPermitted
	 */
	public boolean isItemDropPermitted()
	{
		return itemDropPermitted;
	}

	/**
	 * @return the racePermitted
	 */
	public String getRacePermitted()
	{
		return racePermitted;
	}
	
	/**
	 * @return the weaponBoost
	 */
	public int getWeaponBoost()
	{
		return weaponBoost;
	}

	/**
	 * @return true or false
	 */
	public boolean isWeapon()
	{
		return equipmentType == EquipType.WEAPON;
	}

	/**
	 * @return true or false
	 */
	public boolean isArmor()
	{
		return equipmentType == EquipType.ARMOR;
	}
	
	public boolean isKinah()
	{
		return itemId == ItemId.KINAH.value();
	}
	
	void afterUnmarshal (Unmarshaller u, Object parent)
	{
		setItemId(Integer.parseInt(id));
	}

	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return the godstoneInfo
	 */
	public GodstoneInfo getGodstoneInfo()
	{
		return godstoneInfo;
	}

	@Override
	public String getName()
	{
		return "NONAME";
	}

	@Override
	public int getTemplateId()
	{
		return itemId;
	}
	
	
}
