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
package com.aionemu.gameserver.model.gameobjects.stats.modifiers;

import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.gameobjects.stats.StatModifierPriority;

/**
 * @author xavier
 * 
 */

public class RateModifier extends SimpleModifier
{
	@Override
	public int apply(int baseStat, int currentStat)
	{
		int minLimit = 0;
		int maxLimit = 0;
		int chkValue;
		
		switch(getStat())
		{
			case ATTACK_SPEED:
			case SPEED:
			case FLY_SPEED:
				minLimit = 600;
				maxLimit = 12000;
				break;			
		}
		
		if(isBonus())
		{
			chkValue = Math.round(value * baseStat / 100f);
			if(minLimit == 0 && maxLimit == 0)
			{
			
				if(chkValue + currentStat < 0)
					return -currentStat;
				else
					return chkValue;
			}
			else
			{
				if(chkValue + currentStat < minLimit)
				{
					chkValue = currentStat - minLimit;
					return -chkValue;
				}
				else if (chkValue + currentStat > maxLimit)
				{
					chkValue = maxLimit - currentStat;
					return chkValue;
				}
				else
					return chkValue;
			}
		}
		else
		{
			chkValue = Math.round(baseStat * (1 + value / 100f));			
			if(minLimit == 0 && maxLimit == 0)
			{
				if(chkValue < 0)
					return 0;
				else
					return chkValue;
			}
			else
			{
				if(chkValue + currentStat < minLimit)
				{
					chkValue = currentStat - minLimit;
					return -chkValue;
				}
				else if (chkValue + currentStat > maxLimit)
				{
					chkValue = maxLimit - currentStat;
					return chkValue;
				}
				else
					return chkValue;
			}
		}
	}

	@Override
	public StatModifierPriority getPriority()
	{
		return StatModifierPriority.LOW;
	}
	
	public static RateModifier newInstance (StatEnum stat, int value, boolean isBonus)
	{
		RateModifier m = new RateModifier();
		m.setStat(stat);
		m.setValue(value);
		m.setBonus(isBonus);
		m.nextId();
		return m;
	}
}
