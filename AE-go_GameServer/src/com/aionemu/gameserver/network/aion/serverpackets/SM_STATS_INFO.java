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
		
		writeD(buf, player.getObjectId());

		writeD(buf, GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

		writeH(buf, 91);// current power [confirmed]
		writeH(buf, 90);// current health [confirmed]
		writeH(buf, 96);// current accuracy [confirmed]
		writeH(buf, 50);// current agility [confirmed]
		writeH(buf, 115);// current knowledge [confirmed]
		writeH(buf, 114);// current will [confirmed]

		writeH(buf, 1);// water res [confirmed]
		writeH(buf, 2);// wind res [confirmed]
		writeH(buf, 3);// earth res [confirmed]
		writeH(buf, 4);// fire res [confirmed]

		writeD(buf, 0);// unk 0
		writeH(buf, 5);// level [confirmed] ***
		writeH(buf, 0); //
		writeD(buf, 0);//

		writeQ(buf, 10L * Integer.MAX_VALUE);// max xp [confirmed]
		writeQ(buf, 2L * Integer.MAX_VALUE); // recoverable xp [confirmed]
		writeQ(buf, 3L * Integer.MAX_VALUE); // current xp [confirmed]

		writeD(buf, 0); //
		writeD(buf, 40); // max hp [confirmed] {cur hp is as % in CI packet}
		writeD(buf, 1);// if set to 0, then client thinks you're dead! [confirmed] (why there is whole int ?)

		writeD(buf, 5000000);// max mp [confirmed]
		writeD(buf, 3000000);// cur mp [confirmed]

		writeH(buf, 8000);// max dp [confirmed]
		writeH(buf, 7000);// cur dp [confirmed]

		writeD(buf, 0);// unk 60

		writeD(buf, 0);// unk1 0

		writeH(buf, 0);// unk2

		writeH(buf, 18457); // Main-Hand attack [confirmed]
		writeH(buf, 0xFEDC); // Off-Hand attack [confirmed]

		writeH(buf, 0xEFAA);// Physical defence [confirmed]

		writeH(buf, 0);//

		writeH(buf, 0xFAAC); // Magical resistance [confirmed]

		writeH(buf, 0);//
		writeH(buf, 0);//
		writeH(buf, 0);//
		writeH(buf, 1758);// evasion [confirmed]
		writeH(buf, 1987);// parry [confirmed]
		writeH(buf, 2457);// block [confirmed]

		writeH(buf, 21852);// Main-Hand Crit Rate [confirmed]
		writeH(buf, 23823);// 0ff-Hand Crit Rate [confirmed]

		writeH(buf, 25478);// Main-Hand Accuracy [confirmed]
		writeH(buf, 20145);// Off-Hand Accuracy [confirmed]

		writeH(buf, 0);// unk10
		writeH(buf, 415);// Magic Accuracy [confirmed]
		writeH(buf, 0); // unk
		writeH(buf, 688); // Magic Boost [confirmed]

		writeH(buf, 0);// unk12
		writeH(buf, 0);// unk12

		writeD(buf, 2);// unk13 2
		writeD(buf, 0);// unk14 0
		writeD(buf, 0);// unk15 0
		writeD(buf, pcd.getPlayerClass().getClassId());// class id [ should be rather H or even C]
		writeD(buf, 0);// unk17 0

		writeH(buf, 20);// base power [confirmed]
		writeH(buf, 30);// base health [confirmed]
		writeH(buf, 40);// base accuracy [confirmed]
		writeH(buf, 50);// base agility [confirmed]
		writeH(buf, 60);// base knowledge [confirmed]
		writeH(buf, 70); // base will [confirmed]

		writeH(buf, 901);// base water res [confirmed]
		writeH(buf, 902);// base wind res [confirmed]
		writeH(buf, 903);// base earth res [confirmed]
		writeH(buf, 904);// base fire res [confirmed]

		writeD(buf, 0);// unk23 0 ? ***

		writeD(buf, 205);// base hp [confirmed]
		writeD(buf, 415);// base mp [confirmed]

		writeD(buf, 0);// unk26
		writeD(buf, 60);// unk27 60

		writeH(buf, 305);// base Main-Hand Attack [confirmed]
		writeH(buf, 306);// base Off-Hand Attack [confirmed]

		writeH(buf, 0); // ***
		writeH(buf, 6789); // base Physical Def [confirmed]

		writeH(buf, 1800); // base Magical res [confirmed]
		writeH(buf, 0); // *** ??

		writeD(buf, 0);// unk31 1069547520 3FC00000 ** co to ?

		writeH(buf, 248); // base evasion [confirmed]
		writeH(buf, 172); // base parry [confirmed]
		writeH(buf, 75); // base block [confirmed]

		writeH(buf, 13); // base Main-Hand crit rate [confirmed]
		writeH(buf, 208); // base Off-Hand crit rate [confirmed]

		writeH(buf, 1122); // base Main-Hand Accuracy [confirmed]
		writeH(buf, 1133); // base Off-Hand Accuracy [confirmed]

		writeH(buf, 0);// unk35 co to ? ***

		writeH(buf, 666); // base Magic Acc [confirmed]
		writeH(buf, 0);// unk36 ***

		writeH(buf, 80); // base Magic Boost [confirmed]

		writeH(buf, 0); // ***
	}
}
