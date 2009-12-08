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
package com.aionemu.gameserver.controllers.movement;

/**
 * Contains all possible movement types. Its used by CM_MOVE, SM_MOVE and controller.
 * 
 * @author -Nemesiss-
 * 
 */
public enum MovementType
{
	/**
	 * Movement by mouse.
	 */
	MOVEMENT_START_MOUSE(-32),
	/**
	 * Movement by keyboard.
	 */
	MOVEMENT_START_KEYBOARD(-64),
	/**
	 * Movement by glide(press space while flying).
	 */
	MOVEMENT_START_GLIDE(-124), //TODO confirm
	/**
	 * Validation (movement by mouse).
	 */
	VALIDATE_MOUSE(-96),
	/**
	 * Validation (movement by keyboard).
	 */
	VALIDATE_KEYBOARD(-128),
	/**
	 * Validation (jump).
	 */
	VALIDATE_JUMP(8),
	/**
	 * Validation (jump while moving).
	 */
	VALIDATE_JUMP_WHILE_MOVING(72),
	/**
	 * Validation (glide).
	 */
	VALIDATE_GLIDE(-60), //TODO confirm
	/**
	 * Movement stop.
	 */
	MOVEMENT_STOP(0),
	
	MOVEMENT_STAYIN_ELEVATOR(24),
	MOVEMENT_JUMPIN_ELEVATOR(-48), //sometimes not jump
	MOVEMENT_VALIDATEIN_ELEVATOR(-112), //unk
	MOVEMEMNT_MOVIN_ELEVATOR(16),
	
	UNKNOWN(1);

	private int	typeId;

	/**
	 * Constructor.
	 * 
	 * @param typeId
	 */
	private MovementType(int typeId)
	{
		this.typeId = typeId;
	}

	/**
	 * Get id of this MovementType
	 * @return id.
	 */
	public int getMovementTypeId()
	{
		return typeId;
	}

	/**
	 * Return MovementType by id.
	 * @param id
	 * @return MovementType
	 */
	public static MovementType getMovementTypeById(int id)
	{
		for(MovementType mt : values())
		{
			if(mt.typeId == id)
				return mt;
		}
		return UNKNOWN;
		//throw new IllegalArgumentException("Unsupported movement type: " + id);
	}
}