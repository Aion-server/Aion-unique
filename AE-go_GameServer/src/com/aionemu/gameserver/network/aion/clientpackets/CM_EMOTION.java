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
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;

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
	int							emotionType;

	/**
	 * Emotion number
	 */
	int							emotion;

	/**
	 *	Coordinates of player
	 */
	float						x;
	float						y;
	float						z;
	byte						heading;

	/**
	 * Constructs new client packet instance.
	 * 
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
			case 0:// select target
			case 1: // jump
			case 2: // resting
			case 3: // end resting
			case 7: // fly teleport land
			case 8: // fly up
			case 9: // land
			case 17: // Nothing here
			case 19: // emotion = readH();
			case 20: // duel end
			case 21: // walk on
			case 22: // walk off
			case 31: // powershard on
			case 32: // powershard off
			case 33: // get equip weapon
			case 34: // remove equip weapon
				break;
			case 16:
				emotion = readH();
				break;
			case 4: // sit on chair
			case 5: // stand on chair
				x = readF();
				y = readF();
				z = readF();
				heading = (byte)readC();
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
			case 0:
				return;
			case 2:
				player.setState(CreatureState.RESTING);
				break;
			case 3:
				player.unsetState(CreatureState.RESTING);
				break;
			case 4:
				player.unsetState(CreatureState.ACTIVE);
				player.setState(CreatureState.CHAIR);
				break;
			case 5:
				player.unsetState(CreatureState.CHAIR);
				player.setState(CreatureState.ACTIVE);
				break;
			case 7:
				player.getController().onFlyTeleportEnd();
				break;
			case 8:
				// TODO move to player controller? but after states working
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
				player.getFlyController().startFly();
				break;
			case 9:
				player.getFlyController().endFly();
				break;
			case 33:
			case 19:
				player.setState(CreatureState.WEAPON_EQUIPPED);
				break;
			case 34:
			case 20:
				player.unsetState(CreatureState.WEAPON_EQUIPPED);
				break;
			case 21:
				// cannot toggle walk when you flying or gliding
				if(player.getFlyState() > 0)
					return;
				player.setState(CreatureState.WALKING);
				break;
			case 22:
				player.unsetState(CreatureState.WALKING);
				break;
			case 31:
				if(!player.getEquipment().isPowerShardEquipped())
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.NO_POWER_SHARD_EQUIPPED());
					return;
				}
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.ACTIVATE_THE_POWER_SHARD());
				player.setState(CreatureState.POWERSHARD);
				break;
			case 32:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DEACTIVATE_THE_POWER_SHARD());
				player.unsetState(CreatureState.POWERSHARD);
				break;
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, emotionType, emotion, x, y, z, heading,
			player.getTarget() == null ? 0 : player.getTarget().getObjectId()), true);
	}
}
