/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.utils.stats;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.utils.stats.enums.MAXHP;
import com.aionemu.gameserver.utils.stats.enums.POWER;


/**
 * @author ATracer
 *
 */
public class ClassStats
{
	/**
	 * @param playerClass
	 * @param level
	 * @return maximum HP stat for player class and level
	 */
	public static int getMaxHpFor(PlayerClass playerClass, int level)
	{
		return MAXHP.valueOf(playerClass.toString()).getMaxHpFor(level);
	}
	
	/**
	 * @param playerClass
	 * @param level
	 * @return power stat for player class and level
	 */
	public static int getPowerFor(PlayerClass playerClass, int level)
	{
		return POWER.valueOf(playerClass.toString()).getPowerFor(level);
	}
}









































