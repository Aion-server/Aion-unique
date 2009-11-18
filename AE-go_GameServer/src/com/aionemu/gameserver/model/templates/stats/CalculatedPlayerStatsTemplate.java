/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.stats;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.utils.stats.ClassStats;

/**
 * @author ATracer
 *
 */
public class CalculatedPlayerStatsTemplate extends PlayerStatsTemplate
{
	
	private PlayerClass playerClass;
	
	public CalculatedPlayerStatsTemplate(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getAccuracy()
	 */
	@Override
	public int getAccuracy()
	{
		return ClassStats.getAccuracyFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getAgility()
	 */
	@Override
	public int getAgility()
	{
		return ClassStats.getAgilityFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getHealth()
	 */
	@Override
	public int getHealth()
	{
		return ClassStats.getHealthFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getKnowledge()
	 */
	@Override
	public int getKnowledge()
	{
		return ClassStats.getKnowledgeFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getPower()
	 */
	@Override
	public int getPower()
	{
		return ClassStats.getPowerFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate#getWill()
	 */
	@Override
	public int getWill()
	{
		return ClassStats.getWillFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getAttackSpeed()
	 */
	@Override
	public float getAttackSpeed()
	{
		return ClassStats.getAttackSpeedFor(playerClass) / 1000f;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getBlock()
	 */
	@Override
	public int getBlock()
	{
		return ClassStats.getBlockFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getEvasion()
	 */
	@Override
	public int getEvasion()
	{
		return ClassStats.getEvasionFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getFlySpeed()
	 */
	@Override
	public float getFlySpeed()
	{
		// TODO Auto-generated method stub
		return super.getFlySpeed();
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMagicAccuracy()
	 */
	@Override
	public int getMagicAccuracy()
	{
		return ClassStats.getMagicAccuracyFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMainHandAccuracy()
	 */
	@Override
	public int getMainHandAccuracy()
	{
		return ClassStats.getMainHandAccuracyFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMainHandAttack()
	 */
	@Override
	public int getMainHandAttack()
	{
		return ClassStats.getMainHandAttackFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMainHandCritRate()
	 */
	@Override
	public int getMainHandCritRate()
	{
		return ClassStats.getMainHandCritRateFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMaxHp()
	 */
	@Override
	public int getMaxHp()
	{
		return ClassStats.getMaxHpFor(playerClass, 10); // level is hardcoded
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getMaxMp()
	 */
	@Override
	public int getMaxMp()
	{
		return 1000;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getParry()
	 */
	@Override
	public int getParry()
	{
		return ClassStats.getParryFor(playerClass);
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getRunSpeed()
	 */
	@Override
	public float getRunSpeed()
	{
		return 1.0f;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.model.templates.stats.StatsTemplate#getWalkSpeed()
	 */
	@Override
	public float getWalkSpeed()
	{
		return 1.0f;
	}

}
