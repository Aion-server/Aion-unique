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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class RespawnService
{
	private static final Logger log = Logger.getLogger(RespawnService.class);

	private static final int RESPAWN_DEFAULT_DELAY = 80000;

	private static RespawnService instance = new RespawnService();
	
	public void scheduleRespawnTask(final Npc npc)
	{
		final World world = npc.getActiveRegion().getWorld();
		//TODO separate thread executor for decay/spawns
		// or schedule separate decay runnable service with queue 
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				world.spawn(npc);	
				npc.getController().onRespawn();
			}
		}, RESPAWN_DEFAULT_DELAY);

	}
	
	public static RespawnService getInstance()
	{
		return instance;
	}
}
