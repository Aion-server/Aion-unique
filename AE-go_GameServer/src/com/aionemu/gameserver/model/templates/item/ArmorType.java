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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 *
 */
@XmlType(name = "armor_type")
@XmlEnum
public enum ArmorType
{
	CHAIN(new int[]{6, 13}),
	CLOTHES(new int[]{4}),
	LEATHER(new int[]{5, 12}),
	PLATE(new int[]{18}),
	ROBE(new int[]{67, 70}),
	SHARD(new int[]{}),
	SHIELD(new int[]{7, 14}),
	ARROW(new int[]{});
	
	private int[] requiredSkills;
	
	private ArmorType(int[] requiredSkills)
	{
		this.requiredSkills = requiredSkills;
	}
	
	public int[] getRequiredSkills()
	{
		return requiredSkills;
	}
}
