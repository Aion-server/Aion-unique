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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 * 
 */
public class CM_SUMMON_MOVE extends AionClientPacket
{
	/**
	 * logger for this class
	 */
	private static final Logger	log	= Logger.getLogger(CM_MOVE.class);

	@Inject
	private World				world;

	private MovementType		type;

	private byte				heading;

	private byte				movementType;

	private float				x, y, z, x2, y2, z2;

	/**
	 * Constructs new instance of <tt>CM_MOVE </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_SUMMON_MOVE(int opcode)
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

		if(!player.isSpawned())
			return;

		readD();//object id

		x = readF();
		y = readF();
		z = readF();

		heading = (byte) readC();
		movementType = (byte) readC();
		type = MovementType.getMovementTypeById(movementType);

		switch(type)
		{
			case MOVEMENT_START_MOUSE:
			case MOVEMENT_START_KEYBOARD:
				x2 = readF();
				y2 = readF();
				z2 = readF();
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
		Summon summon = player.getSummon();
		if(summon == null)
			return;
		// packet was not read correctly
		if(type == null)
			return;

		switch(type)
		{
			case MOVEMENT_START_MOUSE:
			case MOVEMENT_START_KEYBOARD:
				world.updatePosition(summon, x, y, z, heading);
				PacketSendUtility.broadcastPacket(summon, new SM_MOVE(summon, x, y, z, x2, y2, z2, heading, type));
				break;
			case VALIDATE_MOUSE:
			case VALIDATE_KEYBOARD:
				PacketSendUtility.broadcastPacket(summon, new SM_MOVE(summon, x, y, z, x2, y2, z2, heading,
					(type == MovementType.VALIDATE_MOUSE) ? MovementType.MOVEMENT_START_MOUSE
						: MovementType.MOVEMENT_START_KEYBOARD));
				break;
			case MOVEMENT_STOP:
				PacketSendUtility.broadcastPacket(summon, new SM_MOVE(summon, x, y, z, heading, type));
				world.updatePosition(summon, x, y, z, heading);
				break;
			case UNKNOWN:
				StringBuilder sb = new StringBuilder();
				sb.append("Unknown movement type: ").append(movementType);
				sb.append("Coordinates: X=").append(x);
				sb.append(" Y=").append(y);
				sb.append(" Z=").append(z);
				sb.append(" player=").append(player.getName());
				log.warn(sb.toString());
				break;
			default:
				break;
		}
	}
}
