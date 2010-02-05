/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.world.listeners;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * Listener that is invoked after object was added to the world
 * 
 * @author SoulKeeper
 */
@SuppressWarnings("unchecked")
public abstract class ObjectAddedToWorldListener implements Callback
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CallbackResult beforeCall(Object o, Object[] objects)
	{
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CallbackResult afterCall(Object o, Object[] objects, Object o1)
	{
		objectAdded((VisibleObject) objects[0]);
		return CallbackResult.newContinue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Class getBaseClass()
	{
		return ObjectAddedToWorldListener.class;
	}

	/**
	 * Mehtod that is invoked after object has been added to the world
	 * 
	 * @param object
	 *            Object that was added to the world
	 */
	protected abstract void objectAdded(VisibleObject object);
}
