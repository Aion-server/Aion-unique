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
package com.aionemu.gameserver.model.templates.spawn;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.utils.gametime.DayTime;

/**
 * @author ATracer
 *
 */
@XmlType(name = "SpawnTime")
@XmlEnum
public enum SpawnTime
{
	DAY,
	NIGHT;

	/**
	 * @param dayTime
	 * @return true or false
	 */
	public boolean isAllowedDuring(DayTime dayTime)
	{
		switch(this)
		{
			case DAY:
				return dayTime == DayTime.AFTERNOON || dayTime == DayTime.MORNING
					|| dayTime == DayTime.EVENING;
			case NIGHT:
				return dayTime == DayTime.NIGHT;
		}
		return true;
	}
}
