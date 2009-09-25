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
	public int maxdp = 0;
	public int maxmp = 0;
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
//////TODO: show player stats in white. Not red or green.
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
			if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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
		if (pcd.getPlayerClass().getClassId() == 0) {
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

		writeH(buf, power);// bonus power [confirmed]
		writeH(buf, health);// bonus health [confirmed]
		writeH(buf, accuracy);// bonus accuracy [confirmed]
		writeH(buf, agility);// bonus agility [confirmed]
		writeH(buf, knowledge);// bonus knowledge [confirmed]
		writeH(buf, will);// bonus will [confirmed]

		writeH(buf, water);// bonus water res [confirmed]
		writeH(buf, wind);// bonus wind res [confirmed]
		writeH(buf, earth);// bonus earth res [confirmed]
		writeH(buf, fire);// bonus fire res [confirmed]

		writeD(buf, 0);// unk 0
		writeH(buf, player.getLevel());// level [confirmed] ***
		writeH(buf, 0); //
		writeD(buf, 0);//

		writeQ(buf, pcd.getExpNeed());//max xp [confirmed]
		writeQ(buf, 0); // recoverable xp [confirmed]
		writeQ(buf, pcd.getExpShown()); // current xp [confirmed]

		writeD(buf, 0); //
		writeD(buf, maxhp); // max hp [confirmed] {cur hp is as % in CI packet}
		writeD(buf, 1);// if set to 0, then client thinks you're dead! [confirmed] (why there is whole int ?)

		writeD(buf, maxmp);// max mp [confirmed]
		writeD(buf, 0);// cur mp [confirmed]

		writeH(buf, maxdp);// max dp [confirmed]
		writeH(buf, 0);// cur dp [confirmed]

		writeD(buf, 0);// unk 60

		writeD(buf, 0);// unk1 0

		writeH(buf, 0);// unk2

		writeH(buf, main_hand_attack); // bonus Main-Hand attack [confirmed]
		writeH(buf, main_hand_attack); // bonus Off-Hand attack [confirmed]

		writeH(buf, pdef);// bonus Physical defence [confirmed]

		writeH(buf, 0);//

		writeH(buf, mres); // bonus Magical resistance [confirmed]

		writeH(buf, 0);//
		writeH(buf, 0);//
		writeH(buf, 0);//
		writeH(buf, evasion);// bonus evasion [confirmed]
		writeH(buf, parry);// bonus parry [confirmed]
		writeH(buf, block);// bonus block [confirmed]

		writeH(buf, main_hand_crit_rate);// bonus Main-Hand Crit Rate [confirmed]
		writeH(buf, main_hand_crit_rate);// bonus 0ff-Hand Crit Rate [confirmed]

		writeH(buf, main_hand_accuracy);// bonus Main-Hand Accuracy [confirmed]
		writeH(buf, main_hand_accuracy);// bonus Off-Hand Accuracy [confirmed]

		writeH(buf, 0);// unk10
		writeH(buf, magic_accuracy);// bonus Magic Accuracy [confirmed]
		writeH(buf, 0); // unk
		writeH(buf, magic_boost); // bonus Magic Boost [confirmed]

		writeH(buf, 0);// unk12
		writeH(buf, 0);// unk12

		writeD(buf, 2);// unk13 2
		writeD(buf, 0);// unk14 0
		writeD(buf, 0);// unk15 0
		writeD(buf, pcd.getPlayerClass().getClassId());// class id [ should be rather H or even C]
		writeD(buf, 0);// unk17 0

		writeH(buf, power);// base power [confirmed]
		writeH(buf, health);// base health [confirmed]
		writeH(buf, accuracy);// base accuracy [confirmed]
		writeH(buf, agility);// base agility [confirmed]
		writeH(buf, knowledge);// base knowledge [confirmed]
		writeH(buf, will); // base will [confirmed]

		writeH(buf, water);// base water res [confirmed]
		writeH(buf, wind);// base wind res [confirmed]
		writeH(buf, earth);// base earth res [confirmed]
		writeH(buf, fire);// base fire res [confirmed]

		writeD(buf, 0);// unk23 0 ? ***

		writeD(buf, maxhp);// base hp [confirmed]
		writeD(buf, maxmp);// base mp [confirmed]

		writeD(buf, 0);// unk26
		writeD(buf, 60);// unk27 60

		writeH(buf, main_hand_attack);// base Main-Hand Attack [confirmed]
		writeH(buf, main_hand_attack);// base Off-Hand Attack [confirmed]

		writeH(buf, 0); // ***
		writeH(buf, pdef); // base Physical Def [confirmed]

		writeH(buf, mres); // base Magical res [confirmed]
		writeH(buf, 0); // *** ??

		writeD(buf, 0);// unk31 1069547520 3FC00000 ** co to ?

		writeH(buf, evasion); // base evasion [confirmed]
		writeH(buf, parry); // base parry [confirmed]
		writeH(buf, block); // base block [confirmed]

		writeH(buf, main_hand_crit_rate); // base Main-Hand crit rate [confirmed]
		writeH(buf, main_hand_crit_rate); // base Off-Hand crit rate [confirmed]

		writeH(buf, main_hand_accuracy); // base Main-Hand Accuracy [confirmed]
		writeH(buf, main_hand_accuracy); // base Off-Hand Accuracy [confirmed]

		writeH(buf, 0);// unk35 co to ? ***

		writeH(buf, magic_accuracy); // base Magic Acc [confirmed]
		writeH(buf, 0);// unk36 ***

		writeH(buf, magic_boost); // base Magic Boost [confirmed]

		writeH(buf, 0); // ***
	}
}
