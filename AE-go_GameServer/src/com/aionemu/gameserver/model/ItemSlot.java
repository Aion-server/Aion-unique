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
package com.aionemu.gameserver.model;

/**
 * This enum is defining inventory slots, to which items can be equipped.
 * @author Luno
 *
 */
public enum ItemSlot
{
	MAIN_HAND(0),
	SUB_HAND(1),
	HELMET(2),
	TORSO(3),
	GLOVES(4),
	BOOTS(5),
	EARRINGS_LEFT(6),
	EARRINGS_RIGHT(7),
	RING_LEFT(8),
	RING_RIGHT(9),
	NECKLACE(10),
	SHOULDER(11),
	PANTS(12),
	POWER_SHARD_RIGHT(13),
	POWER_SHARD_LEFT(14),
	WINGS(15),
	WAIST(16),
	
	//combo
	MAIN_OR_SUB(0),
	EARRING_RIGHT_OR_LEFT(6),
	RING_RIGHT_OR_LEFT(8),
	SHARD_RIGHT_OR_LEFT(13),
	TORSO_GLOVE_FOOT_SHOULDER_LEG(19);
	
	private short slotIdMask;
	
	private ItemSlot(int slotId)
	{
		this.slotIdMask = (short) (Math.pow(2, slotId));
	}
	
	public short getSlotIdMask()
	{
		return slotIdMask;
	}
	
	public static ItemSlot getValue(int slotIdMask)
	{
		int slotIdConverted = convertSlot(slotIdMask);
		for(ItemSlot itemSlot : values())
		{
			if(itemSlot.slotIdMask == slotIdConverted)
			{
				return itemSlot;
			}
		}

		throw new IllegalArgumentException("Invalid provided slotIdMask "+slotIdMask);
	}
	
	//TODO
	// This is a temporary solution and will be removed after fix of templates in XML
	public static int convertSlot(int slotId)
	{
		switch(slotId)
		{
			case 5 :
				return 1;// or 2 weapon
			case 6 :
				return 8192;//or 16384 power shard
			case 7 :
				return 256;// 512 rings
			case 9 :
				return 64;// 128 earrings
			default : 
				return slotId;
		}
	}
}
