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
package com.aionemu.gameserver.model.gameobjects.stats;

/**
 * @author xavier
 *
 */
public class Stat
{
	private StatEnum type;
	private int origin;
	private int base;
	private int bonus;
	
	public Stat(StatEnum type, int origin)
	{
		this.type = type;
		this.origin = origin;
		this.base = origin;
		this.bonus = 0;
	}
	
	public Stat(StatEnum type)
	{
		this(type,0);
	}
	
	public StatEnum getType()
	{
		return type;
	}
	
	public void increase(int amount, boolean bonus)
	{
		if (bonus)
		{
			this.bonus += amount;
		}
		else
		{
			this.base = amount;
		}
	}
	
	public void set(int value, boolean bonus)
	{
		if (bonus)
		{
			this.bonus = value;
		}
		else
		{
			this.base = value;
		}
	}
	
	public int getOrigin()
	{
		return origin;
	}
	
	public int getBase()
	{
		return base;
	}
	
	public int getBonus()
	{
		return bonus;
	}
	
	public int getCurrent()
	{
		return base+bonus;
	}
	
	public void reset()
	{
		base = origin;
		bonus = 0;
	}
	
	@Override
	public String toString()
	{
		final String s = type+":"+base+"+"+bonus;
		return s;
	}
}
