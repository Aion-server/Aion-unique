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
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

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
	
	private PlayerStatsTemplate statsTemplate;
	
	//////base part////////////////////////

	//static.
	
	//TODO remove all fields below
	
	public int water = 0;
	public int wind = 0;
	public int earth = 0;
	public int fire = 0;
	public int fly_time = 60;

	//unknown yet

	public int maxdp = 100;
	public int magic_boost = 0;
	public int pdef = 0;
	public int mres = 0;
	public int attack_range = 0;
	
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
		this.statsTemplate = player.getPlayerStatsTemplate();
		
		PlayerClass playerClass = player.getPlayerClass();
		
		//TODO move all default and bonus stats to stat data structure
		//TODO remove everything below
		// Every stat will be available in statTemplate as getter
		// Every bonus stat will also be available and already calculated
		
		//base part
		water = ClassStats.getWaterResistFor(playerClass);
		wind = ClassStats.getWindResistFor(playerClass);
		earth = ClassStats.getEarthResistFor(playerClass);
		fire = ClassStats.getFireResistFor(playerClass);
		
		attack_range = ClassStats.getAttackRangeFor(playerClass);

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

		writeH(buf, statsTemplate.getPower() + powerBonus);// [current power]
		writeH(buf, statsTemplate.getHealth() + healthBonus);// [current health]
		writeH(buf, statsTemplate.getAccuracy() + accuracyBonus);// [current accuracy]
		writeH(buf, statsTemplate.getAgility() + agilityBonus);// [current agility]
		writeH(buf, statsTemplate.getKnowledge() + knowledgeBonus);// [current knowledge]
		writeH(buf, statsTemplate.getWill() + willBonus);// [current will]

		writeH(buf, water + waterBonus);// [current water]
		writeH(buf, wind + windBonus);// [current wind]
		writeH(buf, earth + earthBonus);// [current earth]
		writeH(buf, fire + fireBonus);// [current fire]

		writeD(buf, 0);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 0); // [unk]
		writeD(buf, statsTemplate.getMaxHp()  + maxhpBonus);// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, pcd.getExpRecoverable()); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, statsTemplate.getMaxHp()); // [max hp]
		writeD(buf, statsTemplate.getMaxHp());// [unk]

		writeD(buf, statsTemplate.getMaxMp());// [max mana]
		writeD(buf, statsTemplate.getMaxMp() + maxmpBonus);// [current mana]

		writeH(buf, maxdp);// [max dp]
		writeH(buf, 0);// [current dp]

		writeD(buf, 0);// [unk]

		writeD(buf, fly_time + fly_timeBonus);// [current fly time]

		writeH(buf, 0);// [unk]

		writeH(buf, statsTemplate.getMainHandAttack() + main_hand_attackBonus); // [current main hand attack]
		writeH(buf, statsTemplate.getMainHandAttack() + main_hand_attackBonus); // [off hand attack]

		writeH(buf, pdef + pdefBonus);// [current pdef]

		writeH(buf, 0);// [unk]

		writeH(buf, mres + mresBonus); // [current mres]

		writeH(buf, 0);// [unk]
		writeH(buf, attack_range);// attack range
		writeH(buf, (int) (1000 * statsTemplate.getAttackSpeed()));// attack speed 
		writeH(buf, statsTemplate.getEvasion() + evasionBonus);// [current evasion]
		writeH(buf, statsTemplate.getParry() + parryBonus);// [current parry]
		writeH(buf, statsTemplate.getBlock() + blockBonus);// [current block]

		writeH(buf, statsTemplate.getMainHandCritRate() + main_hand_crit_rateBonus);// [current main hand crit rate]
		writeH(buf, statsTemplate.getMainHandCritRate() + main_hand_crit_rateBonus);// [current off hand crit rate]

		writeH(buf, statsTemplate.getMainHandAccuracy() + main_hand_accuracyBonus);// [current main_hand_accuracy]
		writeH(buf, statsTemplate.getMainHandAccuracy() + main_hand_accuracyBonus);// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, statsTemplate.getMagicAccuracy() + magic_accuracyBonus);// [current magic accuracy]
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

		writeH(buf, statsTemplate.getPower());// [base power]
		writeH(buf, statsTemplate.getHealth());// [base health]

		writeH(buf, statsTemplate.getAccuracy());// [base accuracy]
		writeH(buf, statsTemplate.getAgility());// [base agility]

		writeH(buf, statsTemplate.getKnowledge());// [base knowledge]
		writeH(buf, statsTemplate.getWill());// [base will]

		writeH(buf, water);// [base water res]
		writeH(buf, wind);// [base water res]
		
		writeH(buf, earth);// [base earth resist]
		writeH(buf, fire);// [base water res]

		writeD(buf, 0);// [unk]

		writeD(buf, statsTemplate.getMaxHp());// [base hp]

		writeD(buf, statsTemplate.getMaxMp());// [base mana]

		writeD(buf, 0);// [unk]
		writeD(buf, 60);// [unk]

		writeH(buf, statsTemplate.getMainHandAttack());// [base main hand attack]
		writeH(buf, statsTemplate.getMainHandAttack());// [base off hand attack]

		writeH(buf, 0); // [unk] 
		writeH(buf, pdef); // [base pdef]

		writeH(buf, mres); // [base magic res]

		writeH(buf, 0); // [unk]

		writeD(buf, 1086324736);// [unk]

		writeH(buf, statsTemplate.getEvasion()); // [base evasion]

		writeH(buf, statsTemplate.getParry()); // [base parry]
 
		writeH(buf, statsTemplate.getBlock()); // [base block]

		writeH(buf, statsTemplate.getMainHandCritRate()); // [base main hand crit rate]
		writeH(buf, statsTemplate.getMainHandCritRate()); // [base off hand crit rate]

		writeH(buf, statsTemplate.getMainHandAccuracy()); // [base main hand accuracy]
		writeH(buf, statsTemplate.getMainHandAccuracy()); // [base off hand accuracy]

		writeH(buf, 0); // [unk]

		writeH(buf, statsTemplate.getMagicAccuracy());// [base magic accuracy]

		writeH(buf, 0); // [unk]
		writeH(buf, magic_boost);// [base magic boost]

		writeH(buf, 0); // [unk]

	}
}
