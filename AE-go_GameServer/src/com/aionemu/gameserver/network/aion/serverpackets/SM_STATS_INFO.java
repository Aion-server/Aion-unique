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
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
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

		writeH(buf, pgs.getCurrentStat(StatEnum.POWER));// [current power]
		writeH(buf, pgs.getCurrentStat(StatEnum.HEALTH));// [current health]
		writeH(buf, pgs.getCurrentStat(StatEnum.ACCURACY));// [current accuracy]
		writeH(buf, pgs.getCurrentStat(StatEnum.AGILITY));// [current agility]
		writeH(buf, pgs.getCurrentStat(StatEnum.KNOWLEDGE));// [current knowledge]
		writeH(buf, pgs.getCurrentStat(StatEnum.WILL));// [current will]

		writeH(buf, pgs.getCurrentStat(StatEnum.WATER_RESISTANCE));// [current water]
		writeH(buf, pgs.getCurrentStat(StatEnum.WIND_RESISTANCE));// [current wind]
		writeH(buf, pgs.getCurrentStat(StatEnum.EARTH_RESISTANCE));// [current earth]
		writeH(buf, pgs.getCurrentStat(StatEnum.FIRE_RESISTANCE));// [current fire]
		writeH(buf, 0);// [current unknown resistance]
		writeH(buf, 0);// [current unknown resistance]

		writeH(buf, player.getLevel());// [level]

		// something like very dynamic
		writeH(buf, 0); // [unk]
		writeH(buf, 0);// [unk]
		writeH(buf, 0);// [unk]

		writeQ(buf, pcd.getExpNeed());// [xp till next lv]
		writeQ(buf, pcd.getExpRecoverable()); // [recoverable exp]
		writeQ(buf, pcd.getExpShown()); // [current xp]

		writeD(buf, 0); // [unk]
		writeD(buf, pgs.getCurrentStat(StatEnum.MAXHP)); // [max hp]
		writeD(buf, pls.getCurrentHp());// [unk]

		writeD(buf, pgs.getCurrentStat(StatEnum.MAXMP));// [max mana]
		writeD(buf, pls.getCurrentMp());// [current mana]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAXDP));// [max dp]
		writeH(buf, pcd.getDp());// [current dp]

		writeD(buf, pgs.getCurrentStat(StatEnum.FLY_TIME));// [max fly time]

		writeD(buf, pgs.getCurrentStat(StatEnum.FLY_TIME));// [current fly time]

		writeC(buf, player.getFlyState());// [fly state]
		writeC(buf, 0);// [unk]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAIN_HAND_POWER)); // [current main hand attack]
		writeH(buf, pgs.getCurrentStat(StatEnum.OFF_HAND_POWER)); // [off hand attack]

		writeH(buf, pgs.getCurrentStat(StatEnum.PHYSICAL_DEFENSE));// [current pdef]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAIN_HAND_POWER));// [current magic attack ?]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAGICAL_RESIST)); // [current mres]

		writeF(buf, pgs.getCurrentStat(StatEnum.ATTACK_RANGE) / 1000f);// attack range
		writeH(buf, pgs.getCurrentStat(StatEnum.ATTACK_SPEED));// attack speed 
		writeH(buf, pgs.getCurrentStat(StatEnum.EVASION));// [current evasion]
		writeH(buf, pgs.getCurrentStat(StatEnum.PARRY));// [current parry]
		writeH(buf, pgs.getCurrentStat(StatEnum.BLOCK));// [current block]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAIN_HAND_CRITICAL));// [current main hand crit rate]
		writeH(buf, pgs.getCurrentStat(StatEnum.OFF_HAND_CRITICAL));// [current off hand crit rate]

		writeH(buf, pgs.getCurrentStat(StatEnum.MAIN_HAND_ACCURACY));// [current main_hand_accuracy]
		writeH(buf, pgs.getCurrentStat(StatEnum.OFF_HAND_ACCURACY));// [current off_hand_accuracy]

		writeH(buf, 0);// [unk]
		writeH(buf, pgs.getCurrentStat(StatEnum.MAGICAL_ACCURACY));// [current magic accuracy]
		writeH(buf, 0); // [current concentration]
		writeH(buf, pgs.getCurrentStat(StatEnum.MAGICAL_ATTACK)+pgs.getCurrentStat(StatEnum.BOOST_MAGICAL_SKILL)); // [current magic boost]

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

		writeH(buf, pgs.getBaseStat(StatEnum.POWER));// [base power]
		writeH(buf, pgs.getBaseStat(StatEnum.HEALTH));// [base health]

		writeH(buf, pgs.getBaseStat(StatEnum.ACCURACY));// [base accuracy]
		writeH(buf, pgs.getBaseStat(StatEnum.AGILITY));// [base agility]

		writeH(buf, pgs.getBaseStat(StatEnum.KNOWLEDGE));// [base knowledge]
		writeH(buf, pgs.getBaseStat(StatEnum.WILL));// [base will]

		writeH(buf, pgs.getBaseStat(StatEnum.WATER_RESISTANCE));// [base water res]
		writeH(buf, pgs.getBaseStat(StatEnum.WIND_RESISTANCE));// [base water res]
		
		writeH(buf, pgs.getBaseStat(StatEnum.EARTH_RESISTANCE));// [base earth resist]
		writeH(buf, pgs.getBaseStat(StatEnum.FIRE_RESISTANCE));// [base water res]

		writeD(buf, 0);// [unk]

		writeD(buf, pgs.getBaseStat(StatEnum.MAXHP));// [base hp]

		writeD(buf, pgs.getBaseStat(StatEnum.MAXMP));// [base mana]

		writeD(buf, pgs.getBaseStat(StatEnum.MAXDP));// [base dp]
		writeD(buf, pgs.getBaseStat(StatEnum.FLY_TIME));// [fly time]

		writeH(buf, pgs.getBaseStat(StatEnum.MAIN_HAND_POWER));// [base main hand attack]
		writeH(buf, pgs.getBaseStat(StatEnum.OFF_HAND_POWER));// [base off hand attack]

		writeH(buf, pgs.getBaseStat(StatEnum.MAIN_HAND_POWER)); // [base magic attack ?] 
		writeH(buf, pgs.getBaseStat(StatEnum.PHYSICAL_DEFENSE)); // [base pdef]

		writeH(buf, pgs.getBaseStat(StatEnum.MAGICAL_RESIST)); // [base magic res]

		writeH(buf, 0); // [unk]

		writeF(buf, pgs.getCurrentStat(StatEnum.ATTACK_RANGE) / 1000f);// [current attack range]

		writeH(buf, pgs.getBaseStat(StatEnum.EVASION)); // [base evasion]

		writeH(buf, pgs.getBaseStat(StatEnum.PARRY)); // [base parry]
 
		writeH(buf, pgs.getBaseStat(StatEnum.BLOCK)); // [base block]

		writeH(buf, pgs.getBaseStat(StatEnum.MAIN_HAND_CRITICAL)); // [base main hand crit rate]
		writeH(buf, pgs.getBaseStat(StatEnum.OFF_HAND_CRITICAL)); // [base off hand crit rate]

		writeH(buf, pgs.getBaseStat(StatEnum.MAIN_HAND_ACCURACY)); // [base main hand accuracy]
		writeH(buf, pgs.getBaseStat(StatEnum.OFF_HAND_ACCURACY)); // [base off hand accuracy]

		writeH(buf, 0); // [unk]

		writeH(buf, pgs.getBaseStat(StatEnum.MAGICAL_ACCURACY));// [base magic accuracy]

		writeH(buf, 0); // [unk]
		writeH(buf, pgs.getBaseStat(StatEnum.MAGICAL_ATTACK)+pgs.getBaseStat(StatEnum.BOOST_MAGICAL_SKILL));// [base magic boost]

		writeH(buf, 0); // [unk]

	}
}
