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
package com.aionemu.commons.callbacks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generic interface for all enhanced object.<br>
 * <font color="red">NEVER IMPLEMENT THIS CLASS MANUALLY!!!</font> <br>
 * <br>
 * <b>Thread safety, concurrency, deadlocks:</b><br>
 * It's allowed to remove/add listeners from listeners.<br>
 * Listeners are stored in the {@link java.util.concurrent.CopyOnWriteArrayList}, so their behavior is similiar.<br>
 * Briefly speaking, if you will try to remove/add a listener from another listener - the current ivocation won't be
 * affected, current implementation allocates all listeners that are going to be invoked before execution.<br>
 * <br> {@link Callback#beforeCall(Object, Object[])} and {@link Callback#afterCall(Object, Object[], Object)} are threated
 * as separate invocations, so adding/removing listener in beforeCall will affect afterCall.
 * 
 * @author SoulKeeper
 */
public interface EnhancedObject
{
	/**
	 * Adds callback to this object.<br> {@link com.aionemu.commons.callbacks.EnhancedObject concurrency description}
	 * 
	 * @param callback
	 *            instance of callback to add
	 * @see com.aionemu.commons.callbacks.CallbackHelper#addCallback(Callback, EnhancedObject)
	 */
	@SuppressWarnings("unchecked")
	public void addCallback(Callback callback);

	/**
	 * Removes callback from this object.<br> {@link com.aionemu.commons.callbacks.EnhancedObject concurrency description}
	 * 
	 * @param callback
	 *            instance of callback to remove
	 * @see com.aionemu.commons.callbacks.CallbackHelper#removeCallback(Callback, EnhancedObject)
	 */
	@SuppressWarnings("unchecked")
	public void removeCallback(Callback callback);

	/**
	 * Returns all callbacks associated with this
	 * 
	 * @return unmodifiable list of callbacks
	 */
	@SuppressWarnings("unchecked")
	public Map<Class<? extends Callback>, List<Callback>> getCallbacks();

	/**
	 * Returns lock that is used to ensure thread safety
	 * 
	 * @return lock that is used to ensure thread safety
	 */
	public ReentrantLock getCallbackLock();
}
