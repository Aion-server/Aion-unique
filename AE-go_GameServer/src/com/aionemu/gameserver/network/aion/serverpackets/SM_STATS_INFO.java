/**
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.stats.ClassStats;
import java.util.List;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;

import org.apache.log4j.Logger;

/**
 * In this packet Server is sending User Info
 * 
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket
{
	private static final Logger log = Logger.getLogger(SM_STATS_INFO.class);

	/**
	 * Player that stats info will be send
	 */
	private Player	player;
	
	//////base part////////////////////////

	//static.

	public int power;
	public int health;
	public int agility;
	public int accuracy;
	public int knowledge;
	public int will;
	public int main_hand_attack;
	public int main_hand_crit_rate;
	public int water = 0;
	public int wind = 0;
	public int earth = 0;
	public int fire = 0;
	public int fly_time = 60;

	// needs calculations.

	public int maxhp;
	public int main_hand_accuracy;
	public int magic_accuracy;
	public int evasion;
	public int block;
	public int parry;

	//unknown yet

	public int maxdp = 100;
	public int maxmp = 100;
	public int magic_boost = 0;
	public int pdef = 0;
	public int mres = 0;
	public int attack_range = 0;
	public int attack_speed = 0;
	
	///////////bonus part//////////////////////
	
	//static.

	public int powerBonus = 0;
	public int healthBonus = 0;
	public int agilityBonus = 0;
	public int accuracyBonus = 0;
	public int knowledgeBonus = 0;
	public int willBonus = 0;
	public int main_hand_attackBonus = 0;
	public int main_hand_crit_rateBonus = 0;
	public int waterBonus = 0;
	public int windBonus = 0;
	public int earthBonus = 0;
	public int fireBonus = 0;
	public int fly_timeBonus = 0;

	// needs calculations.

	public int maxhpBonus = 0;
	public int main_hand_accuracyBonus = 0;
	public int magic_accuracyBonus = 0;
	public int evasionBonus = 0;
	public int blockBonus = 0;
	public int parryBonus = 0;

	//unknown yet

	public int maxdpBonus = 0;
	public int maxmpBonus = 0;
	public int magic_boostBonus = 0;
	public int pdefBonus = 0;
	public int mresBonus = 0;
	public int attack_rangeBonus = 0;
	public int attack_speedBonus = 0;
	

	/**
	 * Constructs new <tt>SM_UI</tt> packet
	 * 
	 * @param player
	 */
	public SM_STATS_INFO(Player player)
	{
		
		this.player = player;
		PlayerClass playerClass = player.getPlayerClass();
		int level = player.getLevel();

		//base part

		power = ClassStats.getPowerFor(playerClass);
		health = ClassStats.getHealthFor(playerClass);
		agility = ClassStats.getAgilityFor(playerClass);
		accuracy = ClassStats.getAccuracyFor(playerClass);
		knowledge = ClassStats.getKnowledgeFor(playerClass);
		will = ClassStats.getWillFor(playerClass);
		main_hand_attack = ClassStats.getMainHandAttackFor(playerClass);
		main_hand_crit_rate = ClassStats.getMainHandCritRateFor(playerClass);
		main_hand_accuracy = ClassStats.getMainHandAccuracyFor(playerClass);
		water = ClassStats.getWaterResistFor(playerClass);
		wind = ClassStats.getWindResistFor(playerClass);
		earth = ClassStats.getEarthResistFor(playerClass);
		fire = ClassStats.getFireResistFor(playerClass);
		
		maxhp = ClassStats.getMaxHpFor(playerClass, level);
		magic_accuracy = ClassStats.getMagicAccuracyFor(playerClass);
		evasion = ClassStats.getEvasionFor(playerClass);
		block = ClassStats.getBlockFor(playerClass);
		parry = ClassStats.getParryFor(playerClass);
		
		attack_range = ClassStats.getAttackRangeFor(playerClass);
		attack_speed = ClassStats.getAttackSpeedFor(playerClass);

		//Bonus part

		Inventory inventory = player.getInventory();
		List<Item> items = inventory.getEquippedItems();

		for(Item item : items)
		{		
			if (item.getItemTemplate().getItemSlot() ==1) {
				//attack_range = attack_range + item.getItemTemplate().getAttackRange();
				
			}
			else if (item.getItemTemplate().getItemSlot() ==2) {
				//attack_range = attack_range + item.getItemTemplate().getAttackRange();
			}
			
		}

		//Item item = inventory.getItemByObjId(itemUniqueId);
		//itemId = item.getItemTemplate().getItemId();

		powerBonus = 0;
		healthBonus = 0;
		agilityBonus = 0;
		accuracyBonus = 0;
		knowledgeBonus = 0;
		willBonus =0;
		main_hand_attackBonus = 0;
		main_hand_crit_rateBonus = 0;
		main_hand_accuracyBonus = 0;
		waterBonus = 0;
		windBonus = 0;
		earthBonus = 0;
		fireBonus = 0;
		
		maxhpBonus = 0;
		magic_accuracyBonus = 0;
		evasionBonus = 0;
		blockBonus = 0;
		parryBonus = 0;
			
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		PlayerCommonData pcd = player.getCommonData();

		writeD(buf, player.getObjectId());
		writeD(buf, GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

		writeH(buf, power + powerBonus);// [current power]
		writeH(buf, health + healthBonus);// [current health]
		writeH(buf, accuracy + accuracyBonus);// [current accuracy]
		writeH(buf, agility + agilityBonus);// [current agility]
		writeH(buf, knowledge + knowledgeBonus);// [current knowledge]
		writeH(buf, will + willBonus);// [current will]

		writeH(buf, water + waterBonus);// [current water]
		writeH(buf, wind + windBonus);// [current wind]
		writeH(buf, earth + earthBonus);// [current earth]
		writeH(buf, fire + fireBonus);// [current fire]

		writeD(buf, 0);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 0); // [unk]
		writeD(buf, maxhp  + maxhpBonus);// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, 0); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, maxhp); // [max hp]
		writeD(buf, maxhp);// [unk]

		writeD(buf, maxmp);// [max mana]
		writeD(buf, maxmp + maxmpBonus);// [current mana]

		writeH(buf, maxdp);// [max dp]
		writeH(buf, 0);// [current dp]

		writeD(buf, 0);// [unk]

		writeD(buf, fly_time + fly_timeBonus);// [current fly time]

		writeH(buf, 0);// [unk]

		writeH(buf, main_hand_attack + main_hand_attackBonus); // [current main hand attack]
		writeH(buf, main_hand_attack + main_hand_attackBonus); // [off hand attack]

		writeH(buf, pdef + pdefBonus);// [current pdef]

		writeH(buf, 0);// [unk]

		writeH(buf, mres + mresBonus); // [current mres]

		writeH(buf, 0);// [unk]
		writeH(buf, attack_range);// attack range
		writeH(buf, attack_speed);// attack speed 
		writeH(buf, evasion + evasionBonus);// [current evasion]
		writeH(buf, parry + parryBonus);// [current parry]
		writeH(buf, block + blockBonus);// [current block]

		writeH(buf, main_hand_crit_rate + main_hand_crit_rateBonus);// [current main hand crit rate]
		writeH(buf, main_hand_crit_rate + main_hand_crit_rateBonus);// [current off hand crit rate]

		writeH(buf, main_hand_accuracy + main_hand_accuracyBonus);// [current main_hand_accuracy]
		writeH(buf, main_hand_accuracy + main_hand_accuracyBonus);// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, magic_accuracy + magic_accuracyBonus);// [current magic accuracy]
		writeH(buf, 0); // [unk]
		writeH(buf, magic_boost + magic_boostBonus); // [current magic boost]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeD(buf, 2);// [unk]
		writeD(buf, 0);// [unk]
		writeD(buf, 0);// [unk]
		writeD(buf, pcd.getPlayerClass().getClassId());// [Player Class id]
		writeD(buf, 0);// [unk]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeH(buf, power);// [base power]
		writeH(buf, health);// [base health]

		writeH(buf, accuracy);// [base accuracy]
		writeH(buf, agility);// [base agility]

		writeH(buf, knowledge);// [base knowledge]
		writeH(buf, will);// [base water res]

		writeH(buf, water);// [base water res]
		writeH(buf, wind);// [base water res]
		
		writeH(buf, earth);// [base earth resist]
		writeH(buf, fire);// [base water res]

		writeD(buf, 0);// [unk]

		writeD(buf, maxhp);// [base hp]

		writeD(buf, maxmp);// [base mana]

		writeD(buf, 0);// [unk]
		writeD(buf, 60);// [unk]

		writeH(buf, main_hand_attack);// [base main hand attack]
		writeH(buf, main_hand_attack);// [base off hand attack]

		writeH(buf, 0); // [unk] 
		writeH(buf, pdef); // [base pdef]

		writeH(buf, mres); // [base magic res]

		writeH(buf, 0); // [unk]

		writeD(buf, 1086324736);// [unk]

		writeH(buf, evasion); // [base evasion]

		writeH(buf, parry); // [base parry]
 
		writeH(buf, block); // [base block]

		writeH(buf, main_hand_crit_rate); // [base main hand crit rate]
		writeH(buf, main_hand_crit_rate); // [base off hand crit rate]

		writeH(buf, main_hand_accuracy); // [base main hand accuracy]
		writeH(buf, main_hand_accuracy); // [base off hand accuracy]

		writeH(buf, 0); // [unk]

		writeH(buf, magic_accuracy);// [base magic accuracy]

		writeH(buf, 0); // [unk]
		writeH(buf, magic_boost);// [base magic boost]

		writeH(buf, 0); // [unk]

	}
}
