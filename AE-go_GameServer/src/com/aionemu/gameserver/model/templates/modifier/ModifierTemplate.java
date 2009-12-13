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

import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.model.gameobjects.stats.modifiers.StatModifier;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Modifier")
public abstract class ModifierTemplate
{
	@XmlAttribute
	private StatEnum name;
	
	@XmlAttribute
	private boolean bonus;
	
	public StatEnum getStat ()
	{
		return this.name;
	}
	
	public boolean isBonus ()
	{
		return bonus;
	}
	
	public abstract StatModifier getModifier ();
}
