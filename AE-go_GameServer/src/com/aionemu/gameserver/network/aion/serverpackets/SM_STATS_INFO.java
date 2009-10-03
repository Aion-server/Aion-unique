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

/**
 * In this packet Server is sending User Info?
 * 
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket
{

	/**
	 * Player that stats info will be send
	 */
	private Player	player;

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

		writeH(buf, power);// [current power]
		writeH(buf, health);// [current health]
		writeH(buf, accuracy);// [current accuracy]
		writeH(buf, agility);// [current agility]
		writeH(buf, knowledge);// [current knowledge]
		writeH(buf, will);// [current will]

		writeH(buf, water);// [current water]
		writeH(buf, wind);// [current wind]
		writeH(buf, earth);// [current earth]
		writeH(buf, fire);// [current fire]

		writeD(buf, 0);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 0); // [unk]
		writeD(buf, maxhp);// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, 0); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, maxhp); // [max hp]
		writeD(buf, maxhp);// [unk]

		writeD(buf, maxmp);// [max mana]
		writeD(buf, maxmp);// [current mana]

		writeH(buf, maxdp);// [max dp]
		writeH(buf, 0);// [current dp]

		writeD(buf, 0);// [unk]

		writeD(buf, fly_time);// [current fly time]

		writeH(buf, 0);// [unk]

		writeH(buf, main_hand_attack); // [current main hand attack]
		writeH(buf, main_hand_attack); // [off hand attack]

		writeH(buf, pdef);// [current pdef]

		writeH(buf, 0);// [unk]

		writeH(buf, mres); // [current mres]

		writeH(buf, 0);// [unk]
		writeH(buf, attack_range);// attack range
		writeH(buf, attack_speed);// attack speed 
		writeH(buf, evasion);// [current evasion]
		writeH(buf, parry );// [current parry]
		writeH(buf, block);// [current block]

		writeH(buf, main_hand_crit_rate);// [current main hand crit rate]
		writeH(buf, main_hand_crit_rate);// [current off hand crit rate]

		writeH(buf, main_hand_accuracy);// [current main_hand_accuracy]
		writeH(buf, main_hand_accuracy);// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, magic_accuracy);// [current magic accuracy]
		writeH(buf, 0); // [unk]
		writeH(buf, magic_boost); // [current magic boost]

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
