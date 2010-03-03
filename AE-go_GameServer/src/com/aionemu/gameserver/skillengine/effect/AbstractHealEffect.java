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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;



/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractHealEffect")
public abstract class AbstractHealEffect extends EffectTemplate
{
	@XmlAttribute(required = true)
	protected int value;

	@XmlAttribute
	protected int delta;

	@XmlAttribute
	protected boolean percent;

	@Override
	public void calculate(Effect effect)
	{
		int valueWithDelta = value + delta * effect.getSkillLevel();

		int healValue = valueWithDelta;
		if(percent)
		{
			int currentValue = getCurrentStatValue(effect);
			int maxValue = getMaxStatValue(effect);
			int possibleHealValue = maxValue * valueWithDelta / 100;
			healValue = maxValue - currentValue < possibleHealValue ? maxValue - currentValue : possibleHealValue;
		}
		effect.setReserved1(-healValue);
	}
	
	/**
	 * 
	 * @param effect
	 * @return
	 */
	protected int getCurrentStatValue(Effect effect)
	{
		return effect.getEffected().getLifeStats().getCurrentHp();
	}
	
	/**
	 * 
	 * @param effect
	 * @return
	 */
	protected int getMaxStatValue(Effect effect)
	{
		return effect.getEffected().getGameStats().getCurrentStat(StatEnum.MAXHP);
	}

}
