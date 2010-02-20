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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.aionemu.gameserver.skillengine.model.DispelType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * 
 */
public class DispelEffect extends EffectTemplate
{
	@XmlElement(type = Integer.class)
	protected List<Integer>	effectids;
	@XmlAttribute
	protected DispelType	dispeltype;
	@XmlAttribute
	protected Integer		value;

	@Override
	public void applyEffect(Effect effect)
	{
		if(effectids == null)
			return;

		switch(dispeltype)
		{
			case EFFECTID:
				for(Integer effectId : effectids)
				{
					effect.getEffected().getEffectController().removeEffectByEffectId(effectId);
				}
				break;
			case EFFECTTYPE:
				// TODO
				break;
		}
	}

	@Override
	public void calculate(Effect effect)
	{
		effect.increaseSuccessEffect();
	}
}
