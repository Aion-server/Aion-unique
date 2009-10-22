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
package com.aionemu.gameserver.services;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer , orz
 *
 */
public class TeleportService
{
	private static final Logger log = Logger.getLogger(TeleportService.class);

	private static final int TELEPORT_DEFAULT_DELAY = 700;

	private static TeleportService instance = new TeleportService();
	
	public void scheduleTeleportTask(final Player activePlayer, final int mapid, final float x, final float y, final float z)
	{
		final World world = activePlayer.getPosition().getWorld();

		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				world.despawn(activePlayer);
				world.setPosition(activePlayer, mapid, x, y, z, activePlayer.getHeading());
				activePlayer.setProtectionActive(true);
				PacketSendUtility.sendPacket(activePlayer, new SM_UNKF5(activePlayer));
				
			}
		}, TELEPORT_DEFAULT_DELAY);

	}
	
	public static TeleportService getInstance()
	{
		return instance;
	}

}

