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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.Version;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import java.lang.Math;

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
	public long maxhpc;
	public int maxhp;
	public int main_hand_accuracy;
	public int magic_accuracy;
	public long evasionc;
	public long blockc;
	public long parryc;
	public int evasion;
	public int block;
	public int parry;
	//unknown yet
	public int maxdp = 100;
	public int maxmp = 100;
	public int magic_boost = 0;
	public int pdef = 0;
	public int mres = 0;
	/**
	 * Constructs new <tt>SM_UI</tt> packet
	 * 
	 * @param player
	 */
	public SM_STATS_INFO(Player player)
	{
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		PlayerCommonData pcd = player.getCommonData();

	////////////////////////////////////////////////////////
//////Sexy calculations of player stats. 
//////TODO: Find out some missing packet structures.
//////TODO: Move calculations to some other class
//////TODO: Find out formulas for other stat calculations.
///////////////////////////////////////////////////

		//warrior
		if (pcd.getPlayerClass().getClassId() == 0) {
			maxhpc = Math.round ( 1.1688 * (player.getLevel() - 1) * (player.getLevel() - 1) + 45.149 * (player.getLevel() -1) + 284 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 110;// change
			health = 110;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 90; // change
			will = 90; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}



		//gladiator
		if (pcd.getPlayerClass().getClassId() == 1) {
			maxhpc = Math.round ( 1.3393 * (player.getLevel() - 1) * (player.getLevel() - 1) + 48.246 * (player.getLevel() -1) + 342 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 115;// change
			health = 115;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 90; // change
			will = 90; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//templar
			if (pcd.getPlayerClass().getClassId() == 2) {
			maxhpc = Math.round ( 1.3288 * (player.getLevel() - 1) * (player.getLevel() - 1) + 51.878 * (player.getLevel() -1) + 281 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 115;// change
			health = 100;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 90; // change
			will = 105; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//scout
		if (pcd.getPlayerClass().getClassId() == 3) {
			maxhpc = Math.round ( 1.0297 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.823 * (player.getLevel() -1) + 219 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 100;// change
			health = 100;// change
			agility = 110;// change
			accuracy = 110; // change
			knowledge = 90; // change
			will = 90; // change
			main_hand_attack=18; // change
			main_hand_crit_rate=3; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//assasin
		if (pcd.getPlayerClass().getClassId() == 4) {
			maxhpc = Math.round ( 1.0488 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.38 * (player.getLevel() -1) + 222 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 110;// change
			health = 100;// change
			agility = 110;// change
			accuracy = 110; // change
			knowledge = 90; // change
			will = 90; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=3; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//ranger
		if (pcd.getPlayerClass().getClassId() == 5) {
			maxhpc = Math.round ( 0.5 * (player.getLevel() - 1) * (player.getLevel() - 1) + 38.5 * (player.getLevel() -1) + 133 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 90;// change
			health = 90;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 120; // change
			will = 110; // change
			main_hand_attack=18; // change
			main_hand_crit_rate=3; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//mage
		if (pcd.getPlayerClass().getClassId() == 6) {
			maxhpc = Math.round ( 0.7554 * (player.getLevel() - 1) * (player.getLevel() - 1) + 29.457 * (player.getLevel() -1) + 132 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 90;// change
			health = 90;// change
			agility = 95;// change
			accuracy = 95; // change
			knowledge = 115; // change
			will = 115; // change
			main_hand_attack=16; // change
			main_hand_crit_rate=1; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//sorcerer
		if (pcd.getPlayerClass().getClassId() == 7) {
			maxhpc = Math.round ( 0.6352 * (player.getLevel() - 1) * (player.getLevel() - 1) + 24.852 * (player.getLevel() -1) + 112 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 90;// change
			health = 90;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 120; // change
			will = 110; // change
			main_hand_attack=16; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//spirit master
		if (pcd.getPlayerClass().getClassId() == 8) {
			maxhpc = Math.round ( 1 * (player.getLevel() - 1) * (player.getLevel() - 1) + 20.6 * (player.getLevel() -1) + 157 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 90;// change
			health = 90;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 115; // change
			will = 115; // change
			main_hand_attack=16; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//priest
		if (pcd.getPlayerClass().getClassId() == 9) {
			maxhpc = Math.round ( 1.0303 * (player.getLevel() - 1) * (player.getLevel() - 1) + 40.824 * (player.getLevel() -1) + 201 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 95;// change
			health = 95;// change
			agility = 100;// change
			accuracy = 100; // change
			knowledge = 100; // change
			will = 110; // change
			main_hand_attack=17; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//cleric
		if (pcd.getPlayerClass().getClassId() == 10) {
			maxhpc = Math.round ( 0.9277 * (player.getLevel() - 1) * (player.getLevel() - 1) + 35.988 * (player.getLevel() -1) +229 );
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 105;// change
			health = 110;// change
			agility = 90;// change
			accuracy = 90; // change
			knowledge = 105; // change
			will = 110; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=2; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//chanter
		if (pcd.getPlayerClass().getClassId() == 11) {
			maxhpc = Math.round ( 0.9277 * (player.getLevel() - 1) * (player.getLevel() - 1) + 35.988 * (player.getLevel() -1) + 229 ) - 3*player.getLevel();// not retail like, needs some fixes.
			Long lObj = new Long(maxhpc);
			maxhp = lObj.intValue();

			power = 110;// change
			health = 105;// change
			agility = 90;// change
			accuracy = 90; // change
			knowledge = 105; // change
			will = 110; // change
			main_hand_attack=19; // change
			main_hand_crit_rate=1; // change

			evasionc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj2 = new Long(evasionc);
			evasion = lObj2.intValue();
			parryc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj3 = new Long(parryc);
			parry = lObj3.intValue();
			blockc = Math.round(agility*3.1 - 248.5 + 12.4 * player.getLevel());
			Long lObj4 = new Long(blockc);
			block = lObj4.intValue();
			main_hand_accuracy = (accuracy * 2) - 10 + 8 * player.getLevel();
			magic_accuracy = (will * 2) - 10 + 8 * player.getLevel();
		}
		//////////////////////////////////////////////
		///// THE END of calcs////////////////////////
		//////////////////////////////////////////////


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

		writeD(buf, 1);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 2); // [unk]
		writeD(buf, maxhp);// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, 0); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 3); // [unk]
		writeD(buf, maxhp); // [max hp]
		writeD(buf, 0);// [unk]

		writeD(buf, maxmp);// [max mana]
		writeD(buf, maxmp);// [current mana]

		writeH(buf, maxdp);// [max dp]
		writeH(buf, 0);// [current dp]

		writeD(buf, 4);// [unk]

		writeD(buf, fly_time);// [current fly time]

		writeH(buf, 5);// [unk]

		writeH(buf, main_hand_attack); // [current main hand attack]
		writeH(buf, main_hand_attack); // [off hand attack]

		writeH(buf, pdef);// [current pdef]

		writeH(buf, 6);// [unk]

		writeH(buf, mres); // [current mres]

		writeH(buf, 7);// [unk]
		writeH(buf, 8);// [unk]
		writeH(buf, 9);// [unk]
		writeH(buf, evasion);// [current evasion]
		writeH(buf, parry );// [current parry]
		writeH(buf, block);// [current block]

		writeH(buf, main_hand_crit_rate);// [current main hand crit rate]
		writeH(buf, main_hand_crit_rate);// [current off hand crit rate]

		writeH(buf, main_hand_accuracy);// [current main_hand_accuracy]
		writeH(buf, main_hand_accuracy);// [current off_hand_accuracy]

		writeH(buf, 10);// [unk]
		writeH(buf, magic_accuracy);// [current magic accuracy]
		writeH(buf, 11); // [unk]
		writeH(buf, magic_boost); // [current magic boost]

		writeH(buf, 12);// [unk]
		writeH(buf, 13);// [unk]

		writeD(buf, 14);// [unk]
		writeD(buf, 15);// [unk]
		writeD(buf, 16);// [unk]
		writeD(buf, pcd.getPlayerClass().getClassId());// [Player Class id]
		writeD(buf, 17);// [unk]

		writeH(buf, 18);// [unk]
		writeH(buf, 19);// [unk]
		writeH(buf, 20);// [unk]
		writeH(buf, 21);// [unk]
		writeH(buf, 22);// [unk]
		writeH(buf, 23); // [unk]

		writeH(buf, 24);//  [unk]
		writeH(buf, 25);// [unk]

		writeH(buf, power);// [base power]
		writeH(buf, health);// [base health]

		writeH(buf, accuracy);// [base accuracy]
		writeH(buf, agility);// [base agility]

		writeD(buf, knowledge);// [base knowledge]

		writeD(buf, water);// [base water res]
		
		writeD(buf, earth);// [base earth resist]

		writeD(buf, 26);// [unk]

		writeD(buf, maxhp);// [base hp]

		writeD(buf, maxmp);// [base mana]

		writeD(buf, 27);// [unk]
		writeD(buf, 28);// [unk]

		writeH(buf, main_hand_attack);// [base main hand attack]
		writeH(buf, main_hand_attack);// [base off hand attack]

		writeH(buf, 29); // [unk] 
		writeH(buf, pdef); // [base pdef]

		writeH(buf, mres); // [base magic res]

		writeH(buf, 30); // [unk]

		writeD(buf, 31);// [unk]

		writeH(buf, evasion); // [base evasion]

		writeH(buf, parry); // [base parry]
 
		writeH(buf, block); // [base block]

		writeH(buf, main_hand_crit_rate); // [base main hand crit rate]
		writeH(buf, main_hand_crit_rate); // [base off hand crit rate]

		writeH(buf, main_hand_accuracy); // [base main hand accuracy]
		writeH(buf, main_hand_accuracy); // [base off hand accuracy]

		writeH(buf, 32); // [unk]

		writeH(buf, magic_accuracy);// [base magic accuracy]

		writeH(buf, 33); // [unk]
		writeH(buf, magic_boost);// [base magic boost]

		writeH(buf, 34); // [unk]

		writeH(buf, 35); // [unk]

		// still missing packet structure for base will, base fire, base wind, base and current atk speed, max and current DP level.

	}
}
