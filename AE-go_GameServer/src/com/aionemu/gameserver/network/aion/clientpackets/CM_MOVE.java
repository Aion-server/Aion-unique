/**
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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Packet about player movement.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_MOVE extends AionClientPacket
{
	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(CM_MOVE.class);
	@Inject
	private World				world;

	/**
	 * Constructs new instance of <tt>CM_MOVE </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_MOVE(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		Player player = getConnection().getActivePlayer();

		float x, y, z, x2 = 0, y2 = 0, z2 = 0;
		x = readF();
		y = readF();
		z = readF();

		byte heading = (byte) readC();
		MovementType type = MovementType.getMovementTypeById((byte)readC());

		switch(type)
		{
			case MOVEMENT_START_MOUSE:
			case MOVEMENT_START_KEYBOARD:
				x2 = readF();
				y2 = readF();
				z2 = readF();
				PacketSendUtility.broadcastPacket(player, new SM_MOVE(player, x, y, z, x2, y2, z2, heading, type), false);
				break;
			case VALIDATE_MOUSE:
			case VALIDATE_KEYBOARD:
				world.updatePosition(player, x, y, z, heading);
				break;
			case MOVEMENT_STOP:
				PacketSendUtility.broadcastPacket(player, new SM_MOVE(player, x, y, z, x2, y2, z2, heading, type), false);
				world.updatePosition(player, x, y, z, heading);
				break;
			default:
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();

		if(player.isProtectionActive())
			player.setProtectionActive(false);
	}
}
