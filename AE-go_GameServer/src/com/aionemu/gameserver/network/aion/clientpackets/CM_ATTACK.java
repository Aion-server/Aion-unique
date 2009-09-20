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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author -Avol-
 * 
 */
public class CM_ATTACK extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_ATTACK.class);

	private int			unk2;
	private int			unk3;
	private int	senderObjectId;
	private int	unknown = 0x24;
	private int	emotion = 0;
	private int	targetId;
	private int	timer;

	public CM_ATTACK(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		targetId = readH();
		unk2 = readH();
		timer = readH();
		unk3 = readH();
	}


	@Override
	protected void runImpl()
	{	
		final Player activePlayer = getConnection().getActivePlayer();
		//log.info(String.format("TEST: %s %s %s %s", targetId, unk2,unk3, timer));
		Player player = getConnection().getActivePlayer();
    		if (timer % 40 == 0) {
                	PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player.getObjectId(), unknown, emotion), true);
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(targetId, unknown, emotion), true);
			PacketSendUtility.broadcastPacket(player, new SM_ATTACK(targetId), true);
		}

	}
}