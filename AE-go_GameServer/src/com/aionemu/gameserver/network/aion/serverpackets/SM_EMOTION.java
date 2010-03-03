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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Emotion packet
 * 
 * @author SoulKeeper
 */
public class SM_EMOTION extends AionServerPacket
{
	/**
	 * Object id of emotion sender
	 */
	private int					senderObjectId;

	/**
	 * Some unknown variable
	 */
	private int					emotionType;

	/**
	 * ID of emotion
	 */
	private int					emotion;

	/**
	 * Object id of emotion target
	 */
	private int					targetObjectId;


	/**
	 * Temporary Speed..
	 */
	private float				speed = 6.0f;
	
	private int					state;
	/**
	 * Constructs new server packet with specified opcode
	 * 
	 * @param senderObjectId
	 *            who sended emotion
	 * @param unknown
	 *            Dunno what it is, can be 0x10 or 0x11
	 * @param emotionId
	 *            emotion to play
	 * @param emotionId
	 *            who target emotion
	 */
	public SM_EMOTION(Creature creature, int emotionType, int emotion, int targetObjectId)
	{
		this.senderObjectId = creature.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.targetObjectId = targetObjectId;
		this.state = creature.getState();
		this.speed = creature.getGameStats().getCurrentStat(StatEnum.SPEED) / 1000f;
	}

	/**
	 * New
	 *
	 */
	public SM_EMOTION(Player player, int emotionType, int emotion, int targetObjectId)
	{
		this.senderObjectId = player.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.targetObjectId = targetObjectId;

		if (player.isInState(CreatureState.FLYING))
			this.speed = player.getGameStats().getCurrentStat(StatEnum.FLY_SPEED) / 1000f;
		else
			this.speed = player.getGameStats().getCurrentStat(StatEnum.SPEED) / 1000f;

		this.state = player.getState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, senderObjectId);
		writeC(buf, emotionType);
		switch(emotionType)
		{
			case 0:
				// select target
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 1:
				// jump
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 2:
				// sit
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 3:
				// stand
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 4:
				// sit (chair)
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 5:
				// stand (chair)
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 6:
				// fly teleport (start)
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, emotion); // teleport Id
				break;
			case 7:
				// fly teleport (land)
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 8:
				// toggle flight mode
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 9:
				// toggle land mode
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 10:
				// ??
				writeH(buf, state);
				writeF(buf, speed);
			case 13:
				// die
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
				break;
			case 15:
				//walk ?
				writeH(buf, state);
				writeF(buf, speed);
			case 16:
				// emote
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
				writeH(buf, emotion);
				writeC(buf, 1);
				break;
			case 17:
				// unknown
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 19:
				// toggle attack mode (moving)
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 20:
				// toggle normal mode (moving)
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 21:
				// toggle walk
				writeH(buf, state);
				writeF(buf, (speed - (speed * 75f) / 100f));
				break;
			case 22:
				// toggle run
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 28:
				// private shop open
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 29:
				// private shop close
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 30:
				// emote startloop
				writeH(buf, state);
				writeF(buf, speed);
				writeH(buf, 2142); // attack speed?
				writeH(buf, 2142); // attack speed?
				break;
			case 31:
				// powershard on
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 32:
				// powershard off
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 33:
				// toggle attack mode
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 34:
				// toggle normal mode
				writeH(buf, state);
				writeF(buf, speed);
				break;
			case 35:
				// looting start
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
                break;
			case 36:
				// looting end
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
                break;
			case 37:
				// looting start (quest)
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
				break;
			case 38:
				// looting end (quest)
				writeH(buf, state);
				writeF(buf, speed);
				writeD(buf, targetObjectId);
				break;
			default:
				writeH(buf, state);
				writeF(buf, speed);
				if(targetObjectId != 0)
				{
					writeD(buf, targetObjectId);
				}
		}
	}
}
