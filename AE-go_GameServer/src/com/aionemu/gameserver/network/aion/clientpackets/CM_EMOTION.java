/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author SoulKeeper
 */
public class CM_EMOTION extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_EMOTION.class);

	/**
	 * Can 0x11 or 0x10
	 */
	int unknown;

	/**
	 * Emotion number
	 */
	int emotion;
	
	int ObjID;
	
	private int monsterToAttackId;


	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_EMOTION(int opcode)
	{
		super(opcode);
	}

	/**
	 * Read data
	 */
	@Override
	protected void readImpl()
	{
		unknown = readC();
		switch(unknown){
        case 0x0:
        //select target
        case 0x01:
		// jump
		case 0x4:
		//Sit (Nothing to do)
		case 0x5:
		//standing (Nothing to do)
        case 0x8:
		// fly up
		case 0x9:
		// land
		case 0x11:
		// Nothing here
        case 0x13:
		//emotion = readH();
        case 0x14:
		// duel end
        case 0x21:
        //get equip weapon
        case 0x22:
        //remove equip weapon
		break;
		case 0x10:
		emotion = readH();
		break;
		default:
			log.info("Unknown emotion type? 0x" + Integer.toHexString(unknown).toUpperCase());
		}
        }


	/**
	 * Send emotion packet
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player.getObjectId(), unknown, emotion), true);
	}
}
