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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.world.World;

/**
 * This class is for controlling VisibleObjects [players, npc's etc].
 * Its controlling movement, visibility etc.
 * 
 * @author -Nemesiss-
 * 
 */
public abstract class VisibleObjectController<T extends VisibleObject>
{
	/**
	 * Object that is controlled by this controller.
	 */
	private T	owner;

	/**
	 * Set owner (controller object).
	 * 
	 * @param owner
	 */
	public void setOwner(T owner)
	{
		this.owner = owner;
	}

	/**
	 * Get owner (controller object).
	 */
	public T getOwner()
	{
		return owner;
	}

	/**
	 * Called when controlled object is seeing other VisibleObject.
	 * 
	 * @param object
	 */
	public void see(VisibleObject object)
	{

	}

	/**
	 * Called when controlled object no longer see some other VisibleObject.
	 * 
	 * @param object
	 */
	public void notSee(VisibleObject object)
	{

	}

	/**
	 * Removes controlled object from the world.
	 */
	public void delete()
	{
		World world = getOwner().getPosition().getWorld();
		/**
		 * despawn object from world.
		 */
		world.despawn(getOwner());
		/**
		 * Delete object from World.
		 */
		world.removeObject(getOwner());
	}
}
