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
package com.aionemu.gameserver.utils.stats;

/**
 * @author ATracer, Jangan
 *
 */
public enum XPLossEnum
{
	LEVEL_8(8, 2.997997521),
	LEVEL_9(9, 2.998974359),
	LEVEL_10(10, 2.999872482),
	LEVEL_16(16, 2.999258215),
	LEVEL_20(20, 2.999859021),
	LEVEL_21(21, 2.999782255),
	LEVEL_22(22, 2.999856511),
	LEVEL_24(24, 2.999925915),
	LEVEL_33(33, 2.999791422),
	LEVEL_41(41, 1.369142798),
	LEVEL_44(44, 1.081953696),
	LEVEL_50(50, 1.041314239);
	
	private int level;
	private double param;
	
	private XPLossEnum(int level, double param)
	{
		this.level = level;
		this.param = param;
	}

	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @return the param
	 */
	public double getParam()
	{
		return param;
	}
	
	/**
	 * 
	 * @param level
	 * @param expNeed
	 * @return long
	 */
	public static long getExpLoss(int level, long expNeed)
	{
		if(level < 8)
			return 0;
		
		for(XPLossEnum xpLossEnum : values())
		{
			if(level <= xpLossEnum.getLevel())
				return Math.round(expNeed / 100 * xpLossEnum.getParam());
		}
		return 0;
	}	
	
}
