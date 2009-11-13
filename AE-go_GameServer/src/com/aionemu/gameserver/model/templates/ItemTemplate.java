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
package com.aionemu.gameserver.model.templates;

import java.lang.reflect.Field;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.items.ItemQuality;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemBonus;
import com.aionemu.gameserver.model.templates.item.ItemStats;

/**
 * @author Luno
 * modified by ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item_templates")
public class ItemTemplate
{
	
	private int			itemId;
	
	@XmlElement(name = "stats", required = false)
    protected ItemStats itemStats;

	@XmlAttribute(name = "equipment_slots")
	private int itemSlot; 
	
	@XmlAttribute(name = "equipment_type")
	private EquipType equipmentType; 
	
	@XmlAttribute(name = "min_damage")
	private int minDamage;
	
	@XmlAttribute(name = "max_damage")
	private int maxDamage;
	
	@XmlAttribute(name = "cash_item")
	private int cashItem;
	
	@XmlAttribute(name = "dmg_decal")
	private int dmgDecal;
	
	@XmlAttribute(name = "price")
	private int price;
	
	@XmlAttribute(name = "max_stack_count")
	private int maxStackCount;
	
	@XmlAttribute(name = "level")
	private int level;
	
	@XmlAttribute(name = "quality")
	private ItemQuality itemQuality;
	
	@XmlAttribute(name = "item_type")
	private String itemType;//TODO enum
	
	@XmlAttribute(name = "weapon_type")
	private String weaponType; //TODO enum
	
	@XmlAttribute(name = "armor_type")
	private String armorType; //TODO enum
	
	@XmlAttribute(name = "attack_range")
	private float attackRange;
	
	@XmlAttribute(name = "hit_count")
	private int hitCount;
	
	@XmlAttribute(name = "hit_accuracy")
	private int hitAccuracy;
	
	@XmlAttribute(name = "attack_type")
	private String attackType; //TODO enum
	
	@XmlAttribute(name = "attack_gap")
	private float attackGap; //TODO enum
	
	@XmlAttribute(name = "attack_delay")
	private int attackDelay;
	
	@XmlAttribute(name = "lore")
	private boolean lore;
	
	@XmlAttribute(name = "magical_hit_accuracy")
	private int magicalHitAccuracy;
	
	@XmlAttribute(name = "magical_skill_boost")
	private int magicalSkillBoost;
	
	@XmlAttribute(name = "parry")
	private int parrry;
	
	@XmlAttribute(name = "critical")
	private int critical;
	
	@XmlAttribute(name = "kno")
	private int knowledge;
	
	@XmlAttribute(name = "agi")
	private int agility;
	
	@XmlAttribute(name = "str")
	private int strength;
	
	@XmlAttribute(name = "desc")
	private String description; //TODO string or int
	
	@XmlAttribute(name = "can_exchange")
	private boolean canExchange;
	
	@XmlAttribute(name = "can_sell_to_npc")
	private boolean canSellToNpc;
	
	@XmlAttribute(name = "can_deposit_to_character_warehouse")
	private boolean canDepositToCharacterWarehouse;
	
	@XmlAttribute(name = "can_deposit_to_account_warehouse")
	private boolean canDepositToAccountWarehouse;
	
	@XmlAttribute(name = "can_deposit_to_guild_warehouse")
	private boolean canDepositToGuildWarehouse;
	
	@XmlAttribute(name = "breakable")
	private boolean breakable;
	
	@XmlAttribute(name = "soul_bind")
	private boolean soulBind;
	
	@XmlAttribute(name = "remove_when_logout")
	private boolean removeWhenLogout;
	
	@XmlAttribute(name = "gender_permitted")
	private String genderPermitted; //enum
	
	@XmlAttribute(name = "warrior")
	private int warrior;
	
	@XmlAttribute(name = "scout")
	private int scout;
	
	@XmlAttribute(name = "mage")
	private int mage;
	
	@XmlAttribute(name = "cleric")
	private int cleric;
	
	@XmlAttribute(name = "fighter")
	private int fighter;
	
	@XmlAttribute(name = "knight")
	private int knight;
	
	@XmlAttribute(name = "assassin")
	private int assassin;
	
	@XmlAttribute(name = "ranger")
	private int ranger;
	
	@XmlAttribute(name = "wizard")
	private int wizard;
	
	@XmlAttribute(name = "elementalist")
	private int elementalist;
	
	@XmlAttribute(name = "chanter")
	private int chanter;
	
	@XmlAttribute(name = "priest")
	private int priest;
	
	@XmlAttribute(name = "option_slot_bonus")
	private int optionSlotBonus;
	
	@XmlAttribute(name = "bonus_apply")
	private String bonusApply; //enum
	
	@XmlAttribute(name = "no_enchant")
	private boolean noEnchant;
	
	@XmlAttribute(name = "can_proc_enchant")
	private boolean canProcEnchant;
	
	@XmlAttribute(name = "cannot_changeskin")
	private int cannotChangeSkin; //TODO check if needed
	
	@XmlAttribute(name = "can_split")
	private boolean canSplit;
	
	@XmlAttribute(name = "item_drop_permitted")
	private boolean itemDropPermitted;
	
	@XmlAttribute(name = "race_permitted")
	private String racePermitted;
	
	@XmlAttribute(name = "magical_resist")
	private int magicalResist;
	
	@XmlAttribute(name = "physical_defend")
	private int physicalDefend;

	public int getItemId()
	{
		return itemId;
	}

	public int getItemSlot()
	{
		return itemSlot;
	}
	
	@SuppressWarnings("unused")
	@XmlID
	@XmlAttribute(name = "id", required = true)
	private void setXmlUid(String uid)
	{
		/*
		 * This method is used only by JAXB unmarshaller.
		 * I couldn't set annotations at field, because
		 * ID must be a string. 
		 */
		itemId = Integer.parseInt(uid);
	}
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		Logger log = Logger.getLogger(ItemTemplate.class);
		if (this.itemId==110100733) {
			if (itemStats==null) {
				log.debug("item 11010073 has <stats> but itemStats is null...");
			} else {
				if (itemStats.getStat().size()==0) {
					log.debug("item 11010073 has several <stat ...> but stat count is 0 ...");
				}
			}
			
		}
	}

	/**
	 * @return the itemStats
	 */
	public ItemStats getItemStats()
	{
		return itemStats;
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
	 * @return the minDamage
	 */
	public int getMinDamage()
	{
		return minDamage;
	}

	/**
	 * @return the maxDamage
	 */
	public int getMaxDamage()
	{
		return maxDamage;
	}

	/**
	 * @return the price
	 */
	public int getPrice()
	{
		return price;
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
	public String getWeaponType()
	{
		return weaponType;
	}

	/**
	 * @return the armorType
	 */
	protected String getArmorType()
	{
		return armorType;
	}

	/**
	 * @return the attackRange
	 */
	public float getAttackRange()
	{
		return attackRange;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount()
	{
		return hitCount;
	}

	/**
	 * @return the attackDelay
	 */
	public int getAttackDelay()
	{
		return attackDelay;
	}

	/**
	 * @return the magicalHitAccuracy
	 */
	public int getMagicalHitAccuracy()
	{
		return magicalHitAccuracy;
	}

	/**
	 * @return the magicalSkillBoost
	 */
	public int getMagicalSkillBoost()
	{
		return magicalSkillBoost;
	}

	/**
	 * @return the parrry
	 */
	public int getParrry()
	{
		return parrry;
	}

	/**
	 * @return the critical
	 */
	public int getCritical()
	{
		return critical;
	}

	/**
	 * @return the knowledge
	 */
	public int getKnowledge()
	{
		return knowledge;
	}

	/**
	 * @return the agility
	 */
	public int getAgility()
	{
		return agility;
	}

	/**
	 * @return the strength
	 */
	public int getStrength()
	{
		return strength;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
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
	 * @return the hitAccuracy
	 */
	public int getHitAccuracy()
	{
		return hitAccuracy;
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
	 * @return the warrior
	 */
	public int getWarrior()
	{
		return warrior;
	}

	/**
	 * @return the scout
	 */
	public int getScout()
	{
		return scout;
	}

	/**
	 * @return the mage
	 */
	public int getMage()
	{
		return mage;
	}

	/**
	 * @return the cleric
	 */
	public int getCleric()
	{
		return cleric;
	}

	/**
	 * @return the fighter
	 */
	public int getFighter()
	{
		return fighter;
	}

	/**
	 * @return the knight
	 */
	public int getKnight()
	{
		return knight;
	}

	/**
	 * @return the assassin
	 */
	public int getAssassin()
	{
		return assassin;
	}

	/**
	 * @return the ranger
	 */
	public int getRanger()
	{
		return ranger;
	}

	/**
	 * @return the wizard
	 */
	public int getWizard()
	{
		return wizard;
	}

	/**
	 * @return the elementalist
	 */
	public int getElementalist()
	{
		return elementalist;
	}

	/**
	 * @return the chanter
	 */
	public int getChanter()
	{
		return chanter;
	}

	/**
	 * @return the priest
	 */
	public int getPriest()
	{
		return priest;
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
	 * @return the cannotChangeSkin
	 */
	public int getCannotChangeSkin()
	{
		return cannotChangeSkin;
	}

	/**
	 * @return the canSplit
	 */
	public boolean isCanSplit()
	{
		return canSplit;
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
	 * @return
	 */
	public boolean isWeapon()
	{
		return  equipmentType == EquipType.WEAPON;
	}
	
	/**
	 * @return
	 */
	public boolean isArmor()
	{
		return equipmentType == EquipType.ARMOR;
	}

	/** For testcase only
	 * 
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return the magicalResist
	 */
	public int getMagicalResist()
	{
		return magicalResist;
	}

	/**
	 * @return the physicalDefend
	 */
	public int getPhysicalDefend()
	{
		return physicalDefend;
	}
}
