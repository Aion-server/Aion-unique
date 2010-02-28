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
package com.aionemu.gameserver.spawnengine;

import com.aionemu.gameserver.controllers.factory.StaticObjectControllerFactory;
import com.aionemu.gameserver.dataholders.ItemData;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.KnownList;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class StaticObjectSpawnManager
{
	@Inject
	private ItemData itemData;
	@IDFactoryAionObject
	@Inject
	private IDFactory aionObjectsIDFactory;
	@Inject
	private World	world;
	@Inject
	private StaticObjectControllerFactory controllerFactory;
	
	/**
	 * 
	 * @param spawnGroup
	 * @param instanceIndex
	 */
	public void spawnGroup(SpawnGroup spawnGroup, int instanceIndex)
	{
		VisibleObjectTemplate objectTemplate = itemData.getItemTemplate(spawnGroup.getNpcid());
		if(objectTemplate == null)
			return;
		
		int pool = spawnGroup.getPool();
		for(int i = 0; i < pool; i++)
		{
			SpawnTemplate spawn = spawnGroup.getNextAvailableTemplate(instanceIndex);
			int objectId = aionObjectsIDFactory.nextId();
			StaticObject staticObject = new StaticObject(objectId, controllerFactory.create(), spawn, objectTemplate);
			staticObject.setKnownlist(new KnownList(staticObject));
			bringIntoWorld(staticObject, spawn, instanceIndex);
		}
	}
	
	/**
	 * 
	 * @param visibleObject
	 * @param spawn
	 * @param instanceIndex
	 */
	private void bringIntoWorld(VisibleObject visibleObject, SpawnTemplate spawn, int instanceIndex)
	{
		world.storeObject(visibleObject);
		world.setPosition(visibleObject, spawn.getWorldId(), instanceIndex, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		world.spawn(visibleObject);
	}
}
