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
package com.aionemu.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum is defining inventory slots, to which items can be equipped.
 * @author Luno
 *
 */
public enum ItemSlot
{
	MAIN_HAND(1),
	SUB_HAND(1<<1),
	HELMET(1<<2),
	TORSO(1<<3),
	GLOVES(1<<4),
	BOOTS(1<<5),
	EARRINGS_LEFT(1<<6),
	EARRINGS_RIGHT(1<<7),
	RING_LEFT(1<<8),
	RING_RIGHT(1<<9),
	NECKLACE(1<<10),
	SHOULDER(1<<11),
	PANTS(1<<12),
	POWER_SHARD_RIGHT(1<<13),
	POWER_SHARD_LEFT(1<<14),
	WINGS(1<<15),
	//non-NPC equips (slot > Short.MAX)
	WAIST(1<<16),
	MAIN_OFF_HAND(1<<17),
	SUB_OFF_HAND(1<<18),
	//combo
	MAIN_OR_SUB(MAIN_HAND.slotIdMask & SUB_HAND.slotIdMask), // 3
	EARRING_RIGHT_OR_LEFT(EARRINGS_LEFT.slotIdMask & EARRINGS_RIGHT.slotIdMask), //192
	RING_RIGHT_OR_LEFT(RING_LEFT.slotIdMask & RING_RIGHT.slotIdMask), //768
	SHARD_RIGHT_OR_LEFT(POWER_SHARD_LEFT.slotIdMask & POWER_SHARD_RIGHT.slotIdMask), //24576
	TORSO_GLOVE_FOOT_SHOULDER_LEG(0);//TODO

	private int slotIdMask;
	
	private ItemSlot(int mask)
	{
		this.slotIdMask = mask;
	}
	
	public int getSlotIdMask()
	{
		return slotIdMask;
	}
	
	public static List<ItemSlot> getSlotsFor(int slotIdMask)
	{
		List<ItemSlot> slots = new ArrayList<ItemSlot>();
		ItemSlot equalSlot = null;
		for(ItemSlot itemSlot : values())
		{
			int sumMask = itemSlot.slotIdMask & slotIdMask;
			/**
			 * possible values in this check
			 * - one of combo slots (MAIN, RIGHT_RING etc)
			 */
			if(sumMask > 0 && sumMask < slotIdMask)
				slots.add(itemSlot);
			/**
			 * possible values for equalSlot:
			 * - onlyone slots (TORSO etc)
			 * - combo slots (MAIN_AND_SUB)
			 */
			if(sumMask == slotIdMask)
				equalSlot = itemSlot;
		}
		
		// add "onlyone" slots and don't add combo slots
		if(slots.size() == 0 && equalSlot != null)
			slots.add(equalSlot);
		
		if(slots.size() == 0)
			throw new IllegalArgumentException("Invalid provided slotIdMask "+slotIdMask);
		
		return slots;
	}
}
