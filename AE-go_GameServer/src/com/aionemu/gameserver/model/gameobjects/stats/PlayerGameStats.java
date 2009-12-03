/*
 * This file is part of aion-unique <aion-unique.com>.
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * @author xavier
 * 
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	public PlayerGameStats(Player owner)
	{
		super(owner);
		this.setInitialized(true);
	}

	public PlayerGameStats(PlayerStatsData playerStatsData, Player owner)
	{
		super(owner);
		PlayerStatsTemplate pst = playerStatsData.getTemplate(owner.getPlayerClass(), owner.getLevel());
		initStats(pst);
		log.debug("loading base game stats for player " + owner.getName() + " (id " + owner.getObjectId() + "): "
			+ this);
	}

	private void initStats(PlayerStatsTemplate pst)
	{
		super.initStats(pst.getMaxHp(), pst.getMaxMp(), pst.getPower(), pst.getHealth(), pst.getAgility(), pst
			.getAccuracy(), pst.getKnowledge(), pst.getWill(), pst.getMainHandAttack(), pst.getMainHandCritRate(), Math
			.round(pst.getAttackSpeed() * 1000), 1500);
		setAttackCounter(1);
		setStat(StatEnum.PARRY, pst.getParry());
		setStat(StatEnum.BLOCK, pst.getBlock());
		setStat(StatEnum.EVASION, pst.getEvasion());
		setStat(StatEnum.MAGICAL_ACCURACY, pst.getMagicAccuracy());
		setStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed()*1000f));
		setStat(StatEnum.MAIN_HAND_ACCURACY, pst.getMainHandAccuracy());
		// TODO find good values for fly time
		setStat(StatEnum.FLY_TIME, 60);
	}

	public void doLevelUpgrade (PlayerStatsData playerStatsData, int level) {
		PlayerStatsTemplate pst = playerStatsData.getTemplate(((Player)getOwner()).getPlayerClass(), level);
		initStats(pst);
		recomputeStats();
	}
}
