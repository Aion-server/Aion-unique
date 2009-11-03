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
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
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
	private PlayerGameStats pgs;
	private PlayerGameStats bgs;
	private PlayerLifeStats pls;
	private PlayerCommonData pcd;
	
	/**
	 * Constructs new <tt>SM_UI</tt> packet
	 * 
	 * @param player
	 */
	public SM_STATS_INFO(Player player)
	{
		this.player = player;
		this.pcd = player.getCommonData();
		this.pgs = player.getGameStats();
		this.bgs = pgs.getBaseGameStats();
		this.pls = player.getLifeStats();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());
		writeD(buf, GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

		writeH(buf, pgs.getPower());// [current power]
		writeH(buf, pgs.getHealth());// [current health]
		writeH(buf, pgs.getAccuracy());// [current accuracy]
		writeH(buf, pgs.getAgility());// [current agility]
		writeH(buf, pgs.getKnowledge());// [current knowledge]
		writeH(buf, pgs.getWill());// [current will]

		writeH(buf, pgs.getWater());// [current water]
		writeH(buf, pgs.getWind());// [current wind]
		writeH(buf, pgs.getEarth());// [current earth]
		writeH(buf, pgs.getFire());// [current fire]

		writeD(buf, 0);// [unk]
		writeH(buf, player.getLevel());// [level]
		writeH(buf, 0); // [unk]
		writeD(buf, pls.getCurrentHp());// [current hp]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, 0); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, pls.getMaxHp()); // [max hp]
		writeD(buf, pls.getCurrentHp());// [unk]

		writeD(buf, pls.getMaxMp());// [max mana]
		writeD(buf, pls.getCurrentMp());// [current mana]

		writeH(buf, 0);// [max dp]
		writeH(buf, 0);// [current dp]

		writeD(buf, 0);// [unk]

		writeD(buf, pgs.getFlyTime());// [current fly time]

		writeH(buf, 0);// [unk]

		writeH(buf, pgs.getMainHandAttack()); // [current main hand attack]
		writeH(buf, pgs.getOffHandAttack()); // [off hand attack]

		writeH(buf, pgs.getPhysicalDefense());// [current pdef]

		writeH(buf, 0);// [unk]

		writeH(buf, pgs.getMagicResistance()); // [current mres]

		writeH(buf, 0);// [unk]
		writeH(buf, pgs.getAttackRange());// attack range
		writeH(buf, pgs.getAttackSpeed());// attack speed 
		writeH(buf, pgs.getEvasion());// [current evasion]
		writeH(buf, pgs.getParry());// [current parry]
		writeH(buf, pgs.getBlock());// [current block]

		writeH(buf, pgs.getMainHandCritRate());// [current main hand crit rate]
		writeH(buf, pgs.getOffHandCritRate());// [current off hand crit rate]

		writeH(buf, pgs.getMainHandAccuracy());// [current main_hand_accuracy]
		writeH(buf, pgs.getOffHandAccuracy());// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, pgs.getMagicAccuracy());// [current magic accuracy]
		writeH(buf, 0); // [unk]
		writeH(buf, pgs.getMagicBoost()); // [current magic boost]

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

		writeH(buf, bgs.getPower());// [base power]
		writeH(buf, bgs.getHealth());// [base health]

		writeH(buf, bgs.getAccuracy());// [base accuracy]
		writeH(buf, bgs.getAgility());// [base agility]

		writeH(buf, bgs.getKnowledge());// [base knowledge]
		writeH(buf, bgs.getWill());// [base water res]

		writeH(buf, bgs.getWater());// [base water res]
		writeH(buf, bgs.getWind());// [base water res]
		
		writeH(buf, bgs.getEarth());// [base earth resist]
		writeH(buf, bgs.getFire());// [base water res]

		writeD(buf, 0);// [unk]

		writeD(buf, pls.getMaxHp());// [base hp]

		writeD(buf, pls.getMaxMp());// [base mana]

		writeD(buf, 0);// [unk]
		writeD(buf, 60);// [unk]

		writeH(buf, bgs.getMainHandAttack());// [base main hand attack]
		writeH(buf, bgs.getOffHandAttack());// [base off hand attack]

		writeH(buf, 0); // [unk] 
		writeH(buf, bgs.getPhysicalDefense()); // [base pdef]

		writeH(buf, bgs.getMagicResistance()); // [base magic res]

		writeH(buf, 0); // [unk]

		writeD(buf, 1086324736);// [unk]

		writeH(buf, bgs.getEvasion()); // [base evasion]

		writeH(buf, bgs.getParry()); // [base parry]
 
		writeH(buf, bgs.getBlock()); // [base block]

		writeH(buf, bgs.getMainHandCritRate()); // [base main hand crit rate]
		writeH(buf, bgs.getOffHandCritRate()); // [base off hand crit rate]

		writeH(buf, bgs.getMainHandAccuracy()); // [base main hand accuracy]
		writeH(buf, bgs.getOffHandAccuracy()); // [base off hand accuracy]

		writeH(buf, 0); // [unk]

		writeH(buf, bgs.getMagicAccuracy());// [base magic accuracy]

		writeH(buf, 0); // [unk]
		writeH(buf, bgs.getMagicBoost());// [base magic boost]

		writeH(buf, 0); // [unk]

	}
}
