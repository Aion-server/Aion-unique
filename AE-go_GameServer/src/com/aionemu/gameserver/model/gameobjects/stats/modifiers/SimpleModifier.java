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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author xavier
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleModifier")
public abstract class SimpleModifier extends StatModifier
{
	@XmlAttribute
	protected int value;

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}

	protected void setValue(int value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		final String s = super.toString()+",v:"+value;
		return s;
	}
}
