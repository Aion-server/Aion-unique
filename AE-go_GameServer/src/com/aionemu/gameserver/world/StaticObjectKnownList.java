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
package com.aionemu.gameserver.world;

import java.util.Collection;
import java.util.Iterator;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Mr. Poke
 *
 */
public class StaticObjectKnownList extends KnownList
{

	/**
	 * @param owner
	 */
	public StaticObjectKnownList(VisibleObject owner)
	{
		super(owner);
	}

	/**
	 * Add VisibleObject to this KnownList.
	 * 
	 * @param object
	 */
	@Override
	protected void add(VisibleObject object)
	{
		if (object instanceof Player)
			super.add(object);
	}

	/**
	 * Find objects that are in visibility range.
	 */
	@Override
	protected void findVisibleObjects()
	{
		if(owner == null || !owner.isSpawned())
			return;
		
		Iterator<MapRegion> neighboursIt = owner.getActiveRegion().getNeighboursIterator();

		while(neighboursIt.hasNext())
		{
			MapRegion r = neighboursIt.next();
			Collection<VisibleObject> objects = r.getObjects();

			for(VisibleObject newObject : objects)
			{

				if(newObject == owner || newObject == null)
					continue;

				if (!(newObject instanceof Player))
					continue;

				if(!checkObjectInRange(owner, newObject))
					continue;

				/**
				 * New object is not known.
				 */
				if(knownObjects.put(newObject.getObjectId(), newObject) == null)
				{
					newObject.getKnownList().add(owner);
					owner.getController().see(newObject);
				}
			}
		}
	}
}
