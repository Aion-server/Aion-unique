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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * 
 * @author Simple
 * 
 */
public class CM_LEGION_EDIT extends AionClientPacket
{
	/**
	 * exOpcode and the rest
	 */
	@SuppressWarnings("unused")
	private int	unk1;
	@SuppressWarnings("unused")
	private int	unk2;

	/**
	 * Constructs new instance of CM_LEGION packet
	 * 
	 * @param opcode
	 */
	public CM_LEGION_EDIT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		unk1 = readD();
		unk2 = readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
	}
}
