/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.utils.rates;

import com.aionemu.gameserver.configs.main.RateConfig;

/**
 * @author ATracer
 *
 */
public class RegularRates extends Rates
{
	@Override
	public int getDropRate()
	{
		return RateConfig.DROP_RATE;
	}

	@Override
	public float getApRate()
	{
		return RateConfig.AP_RATE;
	}

	@Override
	public int getQuestKinahRate()
	{
		return RateConfig.QUEST_KINAH_RATE;
	}

	@Override
	public int getQuestXpRate()
	{
		return RateConfig.QUEST_XP_RATE;
	}

	@Override
	public int getXpRate()
	{
		return RateConfig.XP_RATE;
	}
}
