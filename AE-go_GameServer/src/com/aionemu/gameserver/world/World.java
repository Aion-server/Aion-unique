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
package com.aionemu.gameserver.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.callbacks.Enhancable;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.WorldMapTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.taskmanager.tasks.KnownListUpdateTask.KnownListUpdateMode;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.container.PlayerContainer;
import com.aionemu.gameserver.world.exceptions.AlreadySpawnedException;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.exceptions.NotSetPositionException;
import com.aionemu.gameserver.world.exceptions.WorldMapNotExistException;
import com.aionemu.gameserver.world.listeners.ObjectAddedToWorldListener;
import com.aionemu.gameserver.world.listeners.ObjectRemovedFromWorldListener;
import com.google.inject.Inject;

/**
 * World object for storing and spawning, despawning etc players and other in-game objects. It also manage WorldMaps and
 * instances.
 * 
 * @author -Nemesiss-
 * 
 */
public class World
{
	/**
	 * Logger for this class.
	 */
	private static final Logger				log			= Logger.getLogger(World.class);

	/**
	 * Container with all players that entered world.
	 */
	private final PlayerContainer			allPlayers	= new PlayerContainer();
	/**
	 * Container with all AionObjects in the world [ie Players, Npcs etc]
	 */
	private final Map<Integer, AionObject>	allObjects	= new ConcurrentHashMap<Integer, AionObject>();
	/**
	 * World maps supported by server.
	 */
	private final Map<Integer, WorldMap>	worldMaps	= new HashMap<Integer, WorldMap>();

	private IDFactory						aionObjectsIDFactory;
	@Inject
	private SpawnEngine						spawnEngine;

	/**
	 * Constructor.
	 * 
	 * @param worldMapsData
	 */
	@Inject
	public World(WorldMapsData worldMapsData, @IDFactoryAionObject IDFactory aionObjectsIDFactory)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		for(WorldMapTemplate template : worldMapsData)
		{
			worldMaps.put(template.getMapId(), new WorldMap(template, this));
		}
	}

	/**
	 * Store object in the world.
	 * 
	 * @param object
	 */
	@Enhancable(callback = ObjectAddedToWorldListener.class)
	public void storeObject(AionObject object)
	{
		if(allObjects.put(object.getObjectId(), object) != null)
			throw new DuplicateAionObjectException();

		if(object instanceof Player)
			allPlayers.add((Player) object);
	}

	/**
	 * Remove Object from the world.<br>
	 * If the given object is Npc then it also releases it's objId from IDFactory.
	 * 
	 * @param object
	 */
	@Enhancable(callback = ObjectRemovedFromWorldListener.class)
	public void removeObject(AionObject object)
	{
		allObjects.remove(object.getObjectId());
		if(object instanceof Player)
			allPlayers.remove((Player) object);
		if(object instanceof Npc)
			aionObjectsIDFactory.releaseId(object.getObjectId());
	}

	/**
	 * Returns Players iterator.
	 * 
	 * @return Players iterator.
	 */
	public Iterator<Player> getPlayersIterator()
	{
		return allPlayers.iterator();
	}

	/**
	 * Returns AionObjects iterator.
	 * 
	 * @return AionObjects iterator.
	 */
	public Iterator<AionObject> getObjectsIterator()
	{
		return allObjects.values().iterator();
	}

	/**
	 * Finds player by player name.
	 * 
	 * @param name
	 *            - name of player
	 * @return Player
	 */
	public Player findPlayer(String name)
	{
		return allPlayers.get(name);
	}

	/**
	 * Finds player by player objectId.
	 * 
	 * @param objectId
	 *            - objectId of player
	 * @return Player
	 */
	public Player findPlayer(int objectId)
	{
		return allPlayers.get(objectId);
	}

	/**
	 * Finds AionObject by objectId.
	 * 
	 * @param objectId
	 *            - objectId of AionOabject
	 * @return AionObject
	 */
	public AionObject findAionObject(int objectId)
	{
		return allObjects.get(objectId);
	}

	/**
	 * @return the spawnEngine
	 */
	public SpawnEngine getSpawnEngine()
	{
		return spawnEngine;
	}

	/**
	 * Return World Map by id
	 * 
	 * @param id
	 *            - id of world map.
	 * @return World map.
	 */
	public WorldMap getWorldMap(int id)
	{
		WorldMap map = worldMaps.get(id);
		/**
		 * Check if world map exist
		 */
		if(map == null)
			throw new WorldMapNotExistException("Map: " + id + " not exist!");
		return map;
	}
	
	/**
	 * 
	 * @param worldId
	 * @return WorldMapInstance
	 */
	public WorldMapInstance getNextAvailableInstance(int worldId)
	{
		WorldMap map = worldMaps.get(worldId);
		return map.getNextFreeInstance();
	}

	public void destroyInstance(int worldId, int instanceId)
	{
		WorldMap map = worldMaps.get(worldId);
		map.removeWorldMapInstance(instanceId);
	}

	/**
	 * Update position of VisibleObject [used when object is moving on one map instance]. Check if active map region
	 * changed and do all needed updates.
	 * 
	 * @param object
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading)
	{
		this.updatePosition(object, newX, newY, newZ, newHeading, true);
	}
	
	/**
	 *  
	 * @param object
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading, boolean updateKnownList)
	{
		//prevent updating object position in despawned state
		if(!object.isSpawned())
			return;
		
		object.getPosition().setXYZH(newX, newY, newZ, newHeading);

		MapRegion oldRegion = object.getActiveRegion();
		if(oldRegion == null)
		{
			log.warn(String.format("CHECKPOINT: oldregion is null, object coordinates - %d %d %d", object.getX(), object.getY(), object.getY()));
			return;
		}
		
		MapRegion newRegion = oldRegion.getParent().getRegion(object);

		if(newRegion != oldRegion)
		{
			oldRegion.remove(object);
			newRegion.add(object);
			object.getPosition().setMapRegion(newRegion);
		}
		
		if(updateKnownList)
		{
			//object.updateKnownlist();
			object.addKnownListUpdateMask(KnownListUpdateMode.KNOWNLIST_UPDATE);
		}
	}

	/**
	 * Set position of VisibleObject without spawning [object will be invisible]. If object is spawned it will be
	 * despawned first.
	 * 
	 * @param object
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @throws NotSetPositionException
	 *             when object has not set position before.
	 */
	public void setPosition(VisibleObject object, int mapId, float x, float y, float z, byte heading)
	{
		int instanceId = 1;	
		if(object.getWorldId() == mapId)
		{
			instanceId = object.getInstanceId();
		}
		this.setPosition(object, mapId, instanceId, x, y, z, heading);
	}
	
	/**
	 * 
	 * @param object
	 * @param mapId
	 * @param instance
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	public void setPosition(VisibleObject object, int mapId, int instance, float x, float y, float z, byte heading)
	{
		if(object.isSpawned())
			despawn(object);
		object.getPosition().setXYZH(x, y, z, heading);
		object.getPosition().setMapRegion(getWorldMap(mapId).getWorldMapInstanceById(instance).getRegion(object));
	}
		

	/**
	 * Creates and return {@link WorldPosition} object, representing position with given parameters.
	 * 
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @return WorldPosition
	 */
	public WorldPosition createPosition(int mapId, float x, float y, float z, byte heading)
	{
		WorldPosition position = new WorldPosition();
		position.setXYZH(x, y, z, heading);
		position.setMapRegion(getWorldMap(mapId).getWorldMapInstance().getRegion(x, y));
		return position;
	}

	/**
	 * Spawn VisibleObject at current position [use setPosition ]. Object will be visible by others and will see other
	 * objects.
	 * 
	 * @param object
	 * @throws AlreadySpawnedException
	 *             when object is already spawned.
	 */
	public void spawn(VisibleObject object)
	{
		if(object.isSpawned())
			throw new AlreadySpawnedException();

		object.getPosition().setIsSpawned(true);
		if(object.getSpawn() != null)
			object.getSpawn().setSpawned(true, object.getInstanceId());
		object.getActiveRegion().add(object);

		object.updateKnownlist();
	}

	/**
	 * Despawn VisibleObject, object will become invisible and object position will become invalid. All others objects
	 * will be noticed that this object is no longer visible.
	 * 
	 * @throws NullPointerException
	 *             if object is already despawned
	 */
	public void despawn(VisibleObject object)
	{
		object.getActiveRegion().remove(object);
		object.getPosition().setIsSpawned(false);
		if(object.getSpawn() != null)
			object.getSpawn().setSpawned(false, object.getInstanceId());

		object.clearKnownlist();
	}

	/**
	 * dummy method to initialize World.
	 */
	public static void init()
	{
	}
}
