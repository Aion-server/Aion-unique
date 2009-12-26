/**
 * This file is part of aion-emu <aion-unique.org>.
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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Called when entering the world and during group menanement
 * 
 * @author Lyahim
 * @author ATracer
 */

public class CM_PLAYER_STATUS_INFO extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_PLAYER_STATUS_INFO.class);
	@Inject	
	private World			world;

	private int status;
	private int playerObjId;

	public CM_PLAYER_STATUS_INFO(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		status = readC();
		playerObjId = readD();
	}


	@Override
	protected void runImpl()
	{	
		Player player = null;
		
		if(playerObjId == 0)
			player = getConnection().getActivePlayer();
		else
			player = world.findPlayer(playerObjId);
		
		if(player == null || player.getPlayerGroup() == null)
			return;

		
		switch(status)
		{
			case 2:
				player.getPlayerGroup().removePlayerFromGroup(player);
				break;
			case 3:
				player.getPlayerGroup().setGroupLeader(player);
				break;
			case 6:
				player.getPlayerGroup().removePlayerFromGroup(player);
				break;
		}

		log.info(String.valueOf(status));
	}
}