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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.stats.modifiers.MeanModifier;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeanModifier")
public class MeanModifierTemplate extends ModifierTemplate
{
	@XmlAttribute
	private int min;
	
	@XmlAttribute
	private int max;

	@Override
	public StatModifier getModifier()
	{
		return new MeanModifier(getStat(),min,max,isBonus());
	}
}
