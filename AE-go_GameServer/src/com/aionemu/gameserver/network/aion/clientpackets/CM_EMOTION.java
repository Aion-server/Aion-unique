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
	int							emotionType;

	/**
	 * Emotion number
	 */
	int							emotion;

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
			case 0x0:// select target
			case 0x01:// jump
			case 0x02:// resting
			case 0x03:// end resting
			case 0x4:// Sit (Nothing to do) ?? check
			case 0x5:// standing (Nothing to do) ?? check
			case 0x7: // fly teleport land
			case 0x8:// fly up
			case 0x9:// land
			case 0x11:// Nothing here
			case 0x13:// emotion = readH();
			case 0x14:// duel end
			case 0x15:// walk on
			case 0x16:// walk off
			case 0x1F:// powershard on
			case 0x20:// powershard off
			case 0x21:// get equip weapon
			case 0x22:// remove equip weapon
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
				player.setState(CreatureState.RESTING);
				break;
			case 0x3:
				player.unsetState(CreatureState.RESTING);
				break;
			case 0x7:
				player.unsetState(CreatureState.FLYING);
				player.setState(CreatureState.ACTIVE);
				PacketSendUtility.broadcastPacket(player, new SM_PLAYER_INFO(player, false));
				ZoneManager.getInstance().findZoneInCurrentMap(player);
				break;
			case 0x8:
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
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, 30, 0, 0), true);
				player.setState(CreatureState.FLYING);
				player.getController().startFly();
				break;
			case 9:
				player.unsetState(CreatureState.FLYING);
				player.getController().endFly();
				break;
			case 0x21:
			case 0x13:
				player.setState(CreatureState.WEAPON_EQUIPPED);
				break;
			case 0x22:
			case 0x14:
				player.unsetState(CreatureState.WEAPON_EQUIPPED);
				break;
			case 0x15:
				// cannot toggle walk when you flying
				if(player.isInState(CreatureState.FLYING))
					return;
				player.setState(CreatureState.WALKING);
				break;
			case 0x16:
				player.unsetState(CreatureState.WALKING);
				break;
			case 0x1F:
				if(!player.getEquipment().isPowerShardEquipped())
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.NO_POWER_SHARD_EQUIPPED());
					return;
				}
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.ACTIVATE_THE_POWER_SHARD());
				player.setState(CreatureState.POWERSHARD);
				break;
			case 0x20:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DEACTIVATE_THE_POWER_SHARD());
				player.unsetState(CreatureState.POWERSHARD);
				break;
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, emotionType, emotion,
			player.getTarget() == null ? 0 : player.getTarget().getObjectId()), true);
	}
}
