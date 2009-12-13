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
package com.aionemu.gameserver.model.templates.modifier;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "modifiers")
public class ModifiersTemplate 
{
	@XmlElements ({
			@XmlElement(name = "sub", type=SubModifierTemplate.class),
			@XmlElement(name = "add", type=AddModifierTemplate.class),
			@XmlElement(name = "rate", type=RateModifierTemplate.class),
			@XmlElement(name = "set", type=SetModifierTemplate.class),
			@XmlElement(name = "mean", type=MeanModifierTemplate.class)
	})
	private List<ModifierTemplate> modifierTemplates;
	
	private StatEffect effect;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		effect = new StatEffect();
		for (ModifierTemplate modifierTemplate : modifierTemplates)
		{
			effect.add(modifierTemplate.getModifier());
		}
	}

	public StatEffect getEffect()
	{
		return effect;
	}
}
