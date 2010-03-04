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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.gameserver.ai.events.Event;
import com.aionemu.gameserver.ai.npcai.NpcAi;
import com.aionemu.gameserver.controllers.BindpointController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.controllers.factory.ControllerFactory;
import com.aionemu.gameserver.dataholders.BindPointData;
import com.aionemu.gameserver.dataholders.GatherableData;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Citizen;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Monster;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.GatherableTemplate;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.WorldMapTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.gametime.listeners.DayTimeListener;
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

	@Inject
	private World	world;
	@IDFactoryAionObject
	@Inject
	private IDFactory aionObjectsIDFactory;
	@Inject
	private SpawnsData spawnsData;
	@Inject
	private GatherableData gatherableData;
	@Inject
	private NpcData npcData;
	@Inject
	private RiftSpawnManager riftSpawnManager;
	@Inject
	private StaticObjectSpawnManager staticObjectSpawnManager;
	@Inject
	private WorldMapsData worldMapsData;
	@Inject
	private ControllerFactory controllerFactory;
	@Inject
	private BindPointData bindPointData;

	/** Counter counting number of npc spawns */
	private int npcCounter		= 0;
	/** Counter counting number of gatherable spawns */
	private int gatherableCounter		= 0;

	/**
	 * Creates VisibleObject instance and spawns it using given {@link SpawnTemplate} instance.
	 * 
	 * @param spawn
	 * @return created and spawned VisibleObject
	 */
	public VisibleObject spawnObject(SpawnTemplate spawn, int instanceIndex)
	{
		VisibleObjectTemplate template = null;
		int objectId = spawn.getSpawnGroup().getNpcid();

		if(objectId > 400000 && objectId < 499999)// gatherable
		{
			template = gatherableData.getGatherableTemplate(objectId);
			if(template == null)
				return null;
			gatherableCounter++;
		}
		else // npc
		{
			template = npcData.getNpcTemplate(objectId);
			if(template == null)
				return null;
			npcCounter++;
		}

		if(template instanceof NpcTemplate)
		{
			NpcType npcType = ((NpcTemplate)template).getNpcType();	
			Npc npc = null;

			switch(npcType)
			{
				case AGGRESSIVE:
				case ATTACKABLE:
					npc = new Monster(aionObjectsIDFactory.nextId(), controllerFactory.createMonsterController(), spawn, template);
					break;
				case NON_ATTACKABLE:
					npc = new Citizen(aionObjectsIDFactory.nextId(), controllerFactory.createCitizenController(), spawn, template);
					break;
				case POSTBOX:
					npc = new Npc(aionObjectsIDFactory.nextId(), controllerFactory.createPostboxController(), spawn, template);
					break;
				case RESURRECT:
					BindpointController bindPointController = controllerFactory.createBindpointController();
					bindPointController.setBindPointTemplate(bindPointData.getBindPointTemplate(objectId));
					npc = new Npc(aionObjectsIDFactory.nextId(), bindPointController, spawn, template);
					break;
				case USEITEM:
					npc = new Npc(aionObjectsIDFactory.nextId(), controllerFactory.createActionitemController(), spawn,
						template);
					break;
				default:
					npc = new Npc(aionObjectsIDFactory.nextId(), controllerFactory.createNpcController(), spawn,
						template);

			}

			npc.setKnownlist(new KnownList(npc));
			npc.setEffectController(new EffectController(npc));
			npc.getController().onRespawn();
			bringIntoWorld(npc, spawn, instanceIndex);
			return npc;
		}
		else if(template instanceof GatherableTemplate)
		{
			Gatherable gatherable = new Gatherable(spawn, template, aionObjectsIDFactory.nextId(), controllerFactory.createGatherableController());
			gatherable.setKnownlist(new KnownList(gatherable));
			bringIntoWorld(gatherable, spawn, instanceIndex);
			return gatherable;
		}
		return null;
	}

	/**
	 * 
	 * @param worldId
	 * @param objectId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param walkerid
	 * @param randomwalk
	 * @return
	 */
	private SpawnTemplate createSpawnTemplate(int worldId, int objectId, float x, float y, float z, byte heading, int walkerid, int randomwalk)
	{
		SpawnTemplate spawnTemplate = new SpawnTemplate(x, y, z, heading, walkerid, randomwalk);		

		SpawnGroup spawnGroup = new SpawnGroup(worldId, objectId, 105, 1);
		spawnTemplate.setSpawnGroup(spawnGroup);
		spawnGroup.getObjects().add(spawnTemplate);

		return spawnTemplate;
	}

	/**
	 *  Should be used when need to define whether spawn will be deleted after death
	 *  Using this method spawns will not be saved with //save_spawn command
	 *  
	 * @param worldId
	 * @param objectId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param walkerid
	 * @param randomwalk
	 * @param noRespawn
	 * @return SpawnTemplate
	 */
	public SpawnTemplate addNewSpawn(int worldId, int instanceId, int objectId, float x, float y, float z, byte heading, int walkerid, int randomwalk, boolean noRespawn)
	{
		return this.addNewSpawn(worldId, instanceId, objectId, x, y, z, heading, walkerid, randomwalk, noRespawn, false);
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceId
	 * @param objectId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param walkerid
	 * @param randomwalk
	 * @param noRespawn
	 * @param isNewSpawn
	 * @return SpawnTemplate
	 */
	public SpawnTemplate addNewSpawn(int worldId, int instanceId, int objectId, 
		float x, float y, float z, byte heading, int walkerid, int randomwalk,
		boolean noRespawn, boolean isNewSpawn)
	{
		SpawnTemplate spawnTemplate = createSpawnTemplate(worldId, objectId, x, y, z, heading, walkerid, randomwalk);

		if(spawnTemplate == null)
		{
			log.warn("Object couldn't be spawned");
			return null;
		}

		if(!noRespawn)
		{
			spawnsData.addNewSpawnGroup(spawnTemplate.getSpawnGroup(), worldId, objectId, isNewSpawn);
		}

		spawnTemplate.setNoRespawn(noRespawn, instanceId);	

		return spawnTemplate;
	}


	private void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex)
	{
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		world.spawn(visibleObject);
	}

	public void spawnAll()
	{
		for(WorldMapTemplate worldMapTemplate : worldMapsData)
		{
			if (worldMapTemplate.isInstance())
				continue;
			int maxTwin = worldMapTemplate.getTwinCount();
			final int mapId = worldMapTemplate.getMapId();
			int numberToSpawn = maxTwin > 0 ? maxTwin : 1;				

			for(int i = 1; i <= numberToSpawn; i++)
			{
				spawnInstance(mapId, i);
			}
		}

		log.info("Loaded " + npcCounter + " npc spawns");
		log.info("Loaded " + gatherableCounter + " gatherable spawns");

		riftSpawnManager.startRiftPool();

		((EnhancedObject)GameTimeManager.getGameTime()).addCallback(new DayTimeListener(){
			@Override
			protected void onDayTimeChange(GameTime gameTime)
			{
				sendDayTimeChangeEvents(gameTime.getDayTime());
			}

		});
	}

	/**
	 * 
	 * @param worldId
	 * @param instanceIndex
	 */
	public void spawnInstance(int worldId, int instanceIndex)
	{	

		List<SpawnGroup> worldSpawns = spawnsData.getSpawnsForWorld(worldId);

		if(worldSpawns == null || worldSpawns.size() == 0)
			return;

		int instanceSpawnCounter = 0;
		for(SpawnGroup spawnGroup : worldSpawns)
		{
			spawnGroup.resetLastSpawnCounter(instanceIndex);
			if(spawnGroup.getHandler() == null)
			{
				int pool = spawnGroup.getPool();
				for(int i = 0; i < pool; i++)
				{
					spawnObject(spawnGroup.getNextAvailableTemplate(instanceIndex), instanceIndex);

					instanceSpawnCounter++;
				}
			}
			else
			{
				switch(spawnGroup.getHandler())
				{
					case RIFT:
						riftSpawnManager.addRiftSpawnGroup(spawnGroup);
						break;
					case STATIC:
						staticObjectSpawnManager.spawnGroup(spawnGroup, instanceIndex);
					default:
						break;
				}
			}
		}
		log.info("Spawned " + worldId + " [" + instanceIndex + "] : " + instanceSpawnCounter); 
	}

	/**
	 * 
	 */
	public void clearAll()
	{
		spawnsData.clear();
	}
	
	/**
	 * 
	 * @param dayTime
	 */
	private void sendDayTimeChangeEvents(DayTime dayTime)
	{
		Iterator<AionObject> it = world.getObjectsIterator(); 
		while(it.hasNext())
		{
			AionObject obj = it.next();
			if(obj instanceof Npc)
			{
				NpcAi ai =  ((Npc) obj).getAi();
				if(ai != null)
					ai.handleEvent(Event.DAYTIME_CHANGE);
			}
		}
	}
}
