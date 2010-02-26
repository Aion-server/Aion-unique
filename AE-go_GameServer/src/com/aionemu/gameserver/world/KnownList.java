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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * KnownList.
 * 
 * @author -Nemesiss-
 * 
 */
public class KnownList implements Iterable<VisibleObject>
{
	/**
	 * Visibility distance.
	 */

	// how far player will see visible object
	private static final int						playerVisibilityDistance	= 90;

	// how far any visible object will see other visible object
	private static final int						npcVisibilityDistance		= 80;

	/**
	 * Owner of this KnownList.
	 */
	private final VisibleObject						owner;
	/**
	 * List of objects that this KnownList owner known
	 */
	private final Map<Integer, VisibleObject>	knownObjects				= new ConcurrentHashMap<Integer, VisibleObject>();

	/**
	 * COnstructor.
	 * 
	 * @param owner
	 */
	public KnownList(VisibleObject owner)
	{
		this.owner = owner;
	}

	/**
	 * Do KnownList update.
	 */
	public void doUpdate()
	{
		forgetObjects();
		findVisibleObjects();
	}

	/**
	 * Clear known list. Used when object is despawned.
	 */
	public void clear()
	{
		Iterator<VisibleObject> knownIt = iterator();
		while(knownIt.hasNext())
		{
			VisibleObject obj = knownIt.next();
			knownIt.remove();
			obj.getKnownList().del(owner);
		}
	}

	/**
	 * Check if object is known
	 * 
	 * @param object
	 * @return true if object is known
	 */
	public boolean knowns(AionObject object)
	{
		return knownObjects.containsKey(object.getObjectId());
	}

	/**
	 * Returns an iterator over VisibleObjects on this known list
	 * 
	 * @return objects iterator
	 */
	@Override
	public Iterator<VisibleObject> iterator()
	{
		return knownObjects.values().iterator();
	}

	/**
	 * Add VisibleObject to this KnownList.
	 * 
	 * @param object
	 */
	private final void add(VisibleObject object)
	{
		/**
		 * object is not known.
		 */
		if(knownObjects.put(object.getObjectId(), object) == null)
			owner.getController().see(object);
	}

	/**
	 * Delete VisibleObject from this KnownList.
	 * 
	 * @param object
	 */
	private final void del(VisibleObject object)
	{
		/**
		 * object was known.
		 */
		if(knownObjects.remove(object.getObjectId()) != null)
			owner.getController().notSee(object);
	}

	/**
	 * forget out of distance objects.
	 */
	private void forgetObjects()
	{
		Iterator<VisibleObject> knownIt = iterator();

		while(knownIt.hasNext())
		{
			VisibleObject obj = knownIt.next();

			if(!checkObjectInRange(owner, obj))
			{
				knownIt.remove();
				owner.getController().notSee(obj);
				obj.getKnownList().del(owner);
			}
		}
	}

	/**
	 * Find objects that are in visibility range.
	 */
	private void findVisibleObjects()
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

	private boolean checkObjectInRange(VisibleObject owner, VisibleObject newObject)
	{
		if(owner instanceof Player)
			return MathUtil.isInRange(owner, newObject, playerVisibilityDistance);
		else
			return MathUtil.isInRange(owner, newObject, npcVisibilityDistance);
	}
}
