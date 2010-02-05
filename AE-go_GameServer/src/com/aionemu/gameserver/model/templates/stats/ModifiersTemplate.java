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
package com.aionemu.gameserver.model.templates.stats;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.stats.modifiers.AddModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.MeanModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.RateModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.SetModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.SubModifier;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "modifiers")
public class ModifiersTemplate 
{
	@XmlElements ({
			@XmlElement(name = "sub", type=SubModifier.class),
			@XmlElement(name = "add", type=AddModifier.class),
			@XmlElement(name = "rate", type=RateModifier.class),
			@XmlElement(name = "set", type=SetModifier.class),
			@XmlElement(name = "mean", type=MeanModifier.class)
	})
	private TreeSet<StatModifier> modifiers;
	
	public TreeSet<StatModifier> getModifiers()
	{
		return modifiers;
	}
}
