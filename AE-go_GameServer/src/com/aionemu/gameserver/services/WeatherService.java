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
package com.aionemu.gameserver.services;

import com.aionemu.commons.utils.Rnd;

/**
 * @author ATracer
 * This service in future should schedule job that is changing weather
 * sometimes in region and probably sends to all players
 */
public class WeatherService
{
	public enum WeatherType
	{
		//TODO need to decode different types of weather
		RAIN(1026),
		CLEAR(0);
		
		private int weatherMaskId;
		
		private WeatherType(int weatherMaskId)
		{
			this.weatherMaskId = weatherMaskId;
		}
		
		public int getWeatherMaskId()
		{
			return weatherMaskId;
		}
	}
	
	public static int getRandomWeather()
	{
		boolean random = Rnd.nextBoolean();
		if(random)
		{
			return WeatherType.RAIN.weatherMaskId;
		}
		return WeatherType.CLEAR.weatherMaskId;
	}
}
