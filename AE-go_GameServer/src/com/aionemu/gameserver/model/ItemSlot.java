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
	OFF_HAND(1),
	HELMET(2),
	TORSO(3),
	GLOVES(4),
	BOOTS(5),
	EARRINGS_LEFT(6),
	EARRINGS_RIGHT(7),
	RING_LEFT(8),
	RING_RIGHT(9),
	NECKLACE(10),
	PAULDRON(11),
	PANTS(12),
	POWER_SHARD_RIGHT(13),
	POWER_SHARD_LEFT(14),
	WINGS(15);
	
	private short slotIdMask;
	
	private ItemSlot(int slotId)
	{
		this.slotIdMask = (short)(Math.pow(2, slotId));
	}
	
	public short getSlotIdMask()
	{
		return slotIdMask;
	}
}
