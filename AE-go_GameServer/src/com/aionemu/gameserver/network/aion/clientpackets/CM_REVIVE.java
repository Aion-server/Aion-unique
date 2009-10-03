/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNK72;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.stats.ClassStats;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer, orz
 *
 */
public class CM_REVIVE extends AionClientPacket
{
	@Inject
	private World	world;
    private float   x ,y,z;
    
	/**
	 * Constructs new instance of <tt>CM_REVIVE </tt> packet
	 * @param opcode
	 */
	public CM_REVIVE(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{

		Player activePlayer = getConnection().getActivePlayer();

		activePlayer.setLifeStats(new PlayerLifeStats(
			ClassStats.getMaxHpFor(activePlayer.getPlayerClass(), activePlayer.getLevel()), 650));
		
		sendPacket(SM_SYSTEM_MESSAGE.REVIVE);	
		sendPacket(new SM_UNK72());
		sendPacket(new SM_STATS_INFO(activePlayer));			
		sendPacket(new SM_PLAYER_INFO(activePlayer, true));	
		
		/**
		 * Spawn player into the world.
		 */
		int worldId = activePlayer.getWorldId();
		x = activePlayer.getX();
		y = activePlayer.getY();
		z = activePlayer.getZ();
		
		world.despawn(activePlayer);
		// TODO! this should go to PlayerController.teleportTo(...)
		// more todo: when teleporting to the same map then SM_UNKF5 should not be send, but something else
		world.setPosition(activePlayer, worldId, x, y, z, activePlayer.getHeading());
		activePlayer.setProtectionActive(true);
		//world.spawn(activePlayer);
		
		sendPacket(new SM_UNKF5(activePlayer));
	}

}
