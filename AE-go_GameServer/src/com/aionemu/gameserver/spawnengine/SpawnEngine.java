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

import com.aionemu.gameserver.controllers.CitizenController;
import com.aionemu.gameserver.controllers.EffectController;
import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.MonsterController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.gameobjects.Citizen;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.services.DropService;
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
 * modified by ATracer
 * 
 */
public class SpawnEngine
{
	private static Logger log = Logger.getLogger(SpawnEngine.class);
	
	/** In this world VisibleObjects are spawned by this SpawnEngine */
	private World	world;

	private IDFactory aionObjectsIDFactory;

	@Inject
	private DropService dropService;
	
	/**
	 * Constructor creating <tt>SpawnEngine</tt> instance.
	 * 
	 * @param world
	 *            a {@link World} instance which VisibleObjects are spawned in.
	 */
	@Inject
	public SpawnEngine(World world, @IDFactoryAionObject IDFactory aionObjectsIDFactory)
	{
		this.world = world;
		this.aionObjectsIDFactory = aionObjectsIDFactory;
	}

	/**
	 * Creates VisibleObject instance and spawns it using given {@link SpawnTemplate} instance.
	 * 
	 * @param spawn
	 * @return created and spawned VisibleObject
	 */
	public VisibleObject spawnObject(SpawnTemplate spawn)
	{

		if(spawn.getObjectTemplate() instanceof NpcTemplate)
		{
			NpcType npcType = ((NpcTemplate)spawn.getObjectTemplate()).getNpcType();	
			NpcController npcController = null;
			Npc npc = null;
			
			switch(npcType)
			{
				case ATTACKABLE:
					npcController = new MonsterController();
					npcController.setDropService(dropService);
					npc = new Monster(spawn, aionObjectsIDFactory.nextId(), npcController);
					break;
				case NON_ATTACKABLE:
					npcController = new CitizenController();	
					npc = new Citizen(spawn, aionObjectsIDFactory.nextId(), npcController);
					break;
				default: 
					npcController = new NpcController();
					npc = new Npc(spawn, aionObjectsIDFactory.nextId(), npcController);
						
			}

			npc.setKnownlist(new KnownList(npc));
			npc.setEffectController(new EffectController(npc));
			
			npc.getController().onRespawn();
			bringIntoWorld(npc, spawn);
			return npc;
		}
		else if(spawn.getObjectTemplate() instanceof GatherableTemplate)
		{
			GatherableController gatherableController = new GatherableController();
			Gatherable gatherable = new Gatherable(spawn, aionObjectsIDFactory.nextId(), gatherableController);
			gatherable.setKnownlist(new KnownList(gatherable));
			bringIntoWorld(gatherable, spawn);
			return gatherable;
		}
		return null;
	}

	private void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn)
	{
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, spawn.getWorldId(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		world.spawn(visibleObject);
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
			spawnObject(spawn);
		}
	}
}
