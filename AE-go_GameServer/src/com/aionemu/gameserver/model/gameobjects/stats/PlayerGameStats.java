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
 * @author  xavier
 *
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	private PlayerStatsData	playerStatsData;

	public PlayerGameStats(PlayerStatsData playerStatsData, Player owner)
	{
		super(owner, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		this.playerStatsData = playerStatsData;
		PlayerStatsTemplate pst = playerStatsData.getTemplate(owner.getPlayerClass(), owner.getLevel());
		setAttackCounter(1);
		setPower(pst.getPower());
		setHealth(pst.getHealth());
		setAgility(pst.getAgility());
		setAccuracy(pst.getAccuracy());
		setKnowledge(pst.getKnowledge());
		setWill(pst.getWill());
		setMainHandAttack(pst.getMainHandAttack());
		setMainHandCritRate(pst.getMainHandCritRate());
		// TODO find off hand attack and crit rate values
		setOffHandAttack(pst.getMainHandAttack());
		setOffHandCritRate(pst.getMainHandCritRate());
		setWater(0);
		setWind(0);
		setEarth(0);
		setFire(0);
		// TODO find good values for attack range
		setAttackRange(1500);
		setAttackSpeed(Math.round(pst.getAttackSpeed() * 1000));
		// TODO find good values for fly time
		setFlyTime(60);
		setInitialized(true);
		log.debug("loading base game stats for player " + owner.getName() + " (id " + owner.getObjectId() + "): "
			+ this);
	}

	public PlayerGameStats getBaseGameStats()
	{
		int level = this.getOwner().getLevel();
		final PlayerGameStats pgs = new PlayerGameStats(playerStatsData,(Player)getOwner());
		log.debug("Loading base game stats for player " + getOwner().getName() + "(id " + getOwner().getObjectId()
			+ ") for level " + level + ": " + pgs);
		return pgs;
	}

	public void setPlayerStatsData(PlayerStatsData playerStatsData)
	{
		this.playerStatsData = playerStatsData;
	}
}
