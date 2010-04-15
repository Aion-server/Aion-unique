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

import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class CM_SUMMON_EMOTION extends AionClientPacket
{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger(CM_SUMMON_EMOTION.class);
	
	@SuppressWarnings("unused")
	private int objId;
	private int emotionType;
	
	public CM_SUMMON_EMOTION(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		objId = readD();
		emotionType = readC();
		
		//log.info("Emotion Type: " + emotionType);
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		Summon summon = activePlayer.getSummon();
		
		switch(emotionType)
		{
			case 0x08: //start gliding
			case 0x09: //start gliding
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
				break;
			case 0x13: //start attacking
				summon.setState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
			case 0x14: //stop attacking
				summon.unsetState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, emotionType));
				break;			
		}	
	}
}
