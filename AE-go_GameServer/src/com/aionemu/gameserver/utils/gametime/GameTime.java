/*
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
package com.aionemu.gameserver.utils.gametime;

import java.security.InvalidParameterException;

import com.aionemu.commons.callbacks.Enhancable;
import com.aionemu.gameserver.utils.gametime.listeners.DayTimeListener;

/**
 * Represents the internal clock for the time in aion world
 * 
 * @author Ben
 * 
 */
public class GameTime
{
	public static final int	MINUTES_IN_YEAR		= 60 * 24 * 31 * 12;
	public static final int	MINUTES_IN_MONTH	= 60 * 24 * 31;
	public static final int	MINUTES_IN_DAY		= 60 * 24;
	public static final int	MINUTES_IN_HOUR		= 60;

	private int				gameTime			= 0;
	
	private DayTime dayTime;

	/**
	 * Constructs a GameTime with the given time in minutes since midnight 1/1/00
	 * 
	 * @param time
	 *            Minutes since midnight 1/1/00
	 */
	public GameTime(int time)
	{
		if(time < 0)
			throw new InvalidParameterException("Time must be >= 0");
		gameTime = time;
	}

	/**
	 * Gets the ingame time in minutes
	 * 
	 * @return The number of minutes since 1/1/00 00:00:00
	 */
	public int getTime()
	{
		return gameTime;
	}

	/**
	 * Increases game time by a minute
	 */
	protected void increase()
	{
		gameTime++;
		if(getMinute() == 0)
		{
			analyzeDayTime();
		}
	}

	/**
	 * 
	 */
	private void analyzeDayTime()
	{
		DayTime newDateTime = null;
		int hour = getHour();
		if(hour > 21 || hour < 4)
			newDateTime = DayTime.NIGHT;
		else if(hour > 16)
			newDateTime = DayTime.EVENING;
		else if (hour > 8)
			newDateTime = DayTime.AFTERNOON;
		else
			newDateTime = DayTime.MORNING;
		
		if(newDateTime != this.dayTime)
		{
			this.dayTime = newDateTime;
			this.onDayTimeChange();
		}
	}
	
	@Enhancable(callback = DayTimeListener.class)
	public void onDayTimeChange()
	{

	}

	/**
	 * Gets the year in the game
	 * 
	 * @return Year
	 */
	public int getYear()
	{
		return gameTime / MINUTES_IN_YEAR;
	}

	/**
	 * Gets the month in the game, 1-12
	 * 
	 * @return Month 1-12
	 */
	public int getMonth()
	{
		return (gameTime % MINUTES_IN_YEAR) / MINUTES_IN_MONTH + 1;
	}

	/**
	 * Gets the day in the game, 1-31
	 * 
	 * @return Day 1-31
	 */
	public int getDay()
	{
		return (gameTime % MINUTES_IN_MONTH) / MINUTES_IN_DAY + 1;
	}

	/**
	 * Gets the hour in the game, 0-23
	 * 
	 * @return Hour 0-23
	 */
	public int getHour()
	{
		return (gameTime % MINUTES_IN_DAY) / (MINUTES_IN_HOUR);
	}

	/**
	 * Gets the minute in the game, 0-59
	 * 
	 * @return Minute 0-59
	 */
	public int getMinute()
	{
		return (gameTime % MINUTES_IN_HOUR);
	}

	/**
	 * @return the dayTime
	 */
	public DayTime getDayTime()
	{
		return dayTime;
	}
}
