/*
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
package com.aionemu.gameserver.spawnengine;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.StatsTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.KnownList;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * This class is responsible for NPCs spawn management. Current implementation is temporal and will be replaced in the
 * future.
 * 
 * @author Luno
 * 
 */
public class SpawnEngine
{
	private static Logger log = Logger.getLogger(SpawnEngine.class);
	
	/** In this world NPCs are spawned by this SpawnEngine */
	private World	world;

	private IDFactory aionObjectsIDFactory;
	/**
	 * Constructor creating <tt>SpawnEngine</tt> instance.
	 * 
	 * @param world
	 *            a {@link World} instance which NPCs are spawned in.
	 */
	@Inject
	public SpawnEngine(World world, @IDFactoryAionObject IDFactory aionObjectsIDFactory)
	{
		this.world = world;
		this.aionObjectsIDFactory = aionObjectsIDFactory;
	}

	/**
	 * Creates NPCs instance and spawns it using given {@link SpawnTemplate} instance.
	 * 
	 * @param spawn
	 * @return created and spawned NPC
	 */
	public Npc spawnNpc(SpawnTemplate spawn)
	{
		Npc npc = new Npc(spawn, aionObjectsIDFactory.nextId(), new NpcController());

		npc.setKnownlist(new KnownList(npc));
		
		npc.getController().onRespawn();
		
		world.storeObject(npc);
		world.setPosition(npc, spawn.getWorldId(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		world.spawn(npc);

		return npc;
	}

	/**
	 * Creates and spawn NPCs based on given {@link SpawnData} instance.
	 * 
	 * @param spawnData
	 *            <tt>SpawnData</tt> which is holding information about spawns.
	 */
	public void spawnAll(SpawnData spawnData)
	{
		for(SpawnTemplate spawn : spawnData)
		{
			spawnNpc(spawn);
		}
	}
}
