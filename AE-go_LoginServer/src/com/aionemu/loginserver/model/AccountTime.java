/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.loginserver.model;

import java.sql.Timestamp;

/**
 * Class for storing account time data (last login time,
 * last session duration time, accumulated online time today,
 * accumulated rest time today)
 *
 * @author EvilSpirit
 */
public class AccountTime
{
	private Timestamp	lastLoginTime;
	private Timestamp	expirationTime;
	private Timestamp	penaltyEnd;
	private long		sessionDuration;
	private long		accumulatedOnlineTime;
	private long		accumulatedRestTime;

	/**
	 * Default constructor. Set the lastLoginTime to current time
	 */
	public AccountTime()
	{
		this.lastLoginTime = new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getLastLoginTime()
	{
		return lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime)
	{
		this.lastLoginTime = lastLoginTime;
	}

	public long getSessionDuration()
	{
		return sessionDuration;
	}

	public void setSessionDuration(long sessionDuration)
	{
		this.sessionDuration = sessionDuration;
	}

	public long getAccumulatedOnlineTime()
	{
		return accumulatedOnlineTime;
	}

	public void setAccumulatedOnlineTime(long accumulatedOnlineTime)
	{
		this.accumulatedOnlineTime = accumulatedOnlineTime;
	}

	public long getAccumulatedRestTime()
	{
		return accumulatedRestTime;
	}

	public void setAccumulatedRestTime(long accumulatedRestTime)
	{
		this.accumulatedRestTime = accumulatedRestTime;
	}

	public Timestamp getExpirationTime()
	{
		return expirationTime;
	}

	public void setExpirationTime(Timestamp expirationTime)
	{
		this.expirationTime = expirationTime;
	}

	public Timestamp getPenaltyEnd()
	{
		return penaltyEnd;
	}

	public void setPenaltyEnd(Timestamp penaltyEnd)
	{
		this.penaltyEnd = penaltyEnd;
	}
}
