/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

/**
 * @author ATracer
 *
 */
public class CreatureLifeStats
{
	private int currentHp;
	
	private int currentMp;
	
	private int maxHp;
	
	private int maxMp;

	public CreatureLifeStats(int currentHp, int currentMp, int maxHp, int maxMp)
	{
		super();
		this.currentHp = currentHp;
		this.currentMp = currentMp;
		this.maxHp = maxHp;
		this.maxMp = maxMp;
	}
	
	public boolean isAlive()
	{
		return currentHp > 0;
	}

	/**
	 * @return the currentHp
	 */
	public int getCurrentHp()
	{
		return currentHp;
	}

	/**
	 * @param currentHp the currentHp to set
	 */
	public void setCurrentHp(int currentHp)
	{
		this.currentHp = currentHp;
	}

	/**
	 * @return the currentMp
	 */
	public int getCurrentMp()
	{
		return currentMp;
	}

	/**
	 * @param currentMp the currentMp to set
	 */
	public void setCurrentMp(int currentMp)
	{
		this.currentMp = currentMp;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp()
	{
		return maxHp;
	}

	/**
	 * @param maxHp the maxHp to set
	 */
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}

	/**
	 * @return the maxMp
	 */
	public int getMaxMp()
	{
		return maxMp;
	}

	/**
	 * @param maxMp the maxMp to set
	 */
	public void setMaxMp(int maxMp)
	{
		this.maxMp = maxMp;
	}
	
	public int reduceHp(int value)
	{
		synchronized(this)
		{
			this.currentHp -= value;
			if(currentHp < 0)
			{
				currentHp = 0;
			}
			return currentHp;
		}	
	}
}
