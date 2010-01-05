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
import com.aionemu.gameserver.model.gameobjects.player.PlayerState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneManager;

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
	 * Emotion number
	 */
	int emotionType;

	/**
	 * Emotion number
	 */
	int emotion;

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
		emotionType = readC();
		switch(emotionType)
		{
			case 0x0://select target
			case 0x01:// jump
			case 0x02:// resting
			case 0x03:// end resting
			case 0x4://Sit (Nothing to do) ?? check
			case 0x5://standing (Nothing to do) ?? check
			case 0x7: //fly land
			case 0x8:// fly up
			case 0x9:// land
			case 0x11:// Nothing here
			case 0x13://emotion = readH();
			case 0x14:// duel end
			case 0x21://get equip weapon
			case 0x22://remove equip weapon
			case 0x1F://powershard
				break;
			case 0x10:
				emotion = readH();
				break;
			default:
				log.info("Unknown emotion type? 0x" + Integer.toHexString(emotionType).toUpperCase());
				break;
		}
	}


	/**
	 * Send emotion packet
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		switch(emotionType)
		{
			case 0x2:
				player.setState(PlayerState.RESTING);
				break;
			case 0x3:
				player.setState(PlayerState.STANDING);
				break;
			case 0x7:
				PacketSendUtility.broadcastPacket(player, new SM_PLAYER_INFO(player, false));
				ZoneManager.getInstance().findZoneInCurrentMap(player);
				break;
			case 0x8:
				//TODO move to player controller? but after states working
				ZoneInstance currentZone = player.getZoneInstance();
				if(currentZone != null)
				{
					boolean flightAllowed = currentZone.getTemplate().isFlightAllowed();
					if(!flightAllowed)
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLYING_FORBIDDEN_HERE);
						return;
					}
				}
				//player.getCommonData().setFlying(true);
				break;
			case 0x9:
				//player.getCommonData().setFlying(false);
				break;
			case 21:
			case 22:
//				if (player.getCommonData().isFlying() == true)
//					return;
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, emotionType, emotion, player.getTarget()== null?0:player.getTarget().getObjectId()), true);
	}
}
