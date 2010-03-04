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
@XmlType(name = "weapon_type")
@XmlEnum
public enum WeaponType
{
	DAGGER_1H(new int[]{30, 9}, 1),
	MACE_1H(new int[]{3, 10}, 1),
	SWORD_1H(new int[]{1, 8}, 1),
	TOOLHOE_1H(new int[]{}, 1),
	BOOK_2H(new int[]{64}, 2),
	ORB_2H(new int[]{64}, 2),
	POLEARM_2H(new int[]{16}, 2),
	STAFF_2H(new int[]{53}, 2),
	SWORD_2H(new int[]{1, 15}, 2),
	TOOLPICK_2H(new int[]{}, 2),
	TOOLROD_2H(new int[]{}, 2),
	BOW(new int[]{17}, 2);

	private int[] requiredSkill;
	private int slots;

	private WeaponType(int[] requiredSkills, int slots)
	{
		this.requiredSkill = requiredSkills;
		this.slots = slots;
	}

	public int[] getRequiredSkills()
	{
		return requiredSkill;
	}
	
	public int getRequiredSlots()
	{
		return slots;
	}

	/**
	 * @return int
	 */
	public int getMask()
	{
		return 1 << this.ordinal();
	}
}
