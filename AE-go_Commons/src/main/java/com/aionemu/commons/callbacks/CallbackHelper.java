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
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

/**
 * Class that implements helper methods for callbacks.<br>
 * All enhanced objects are delegating main part of their logic to this class
 * 
 * @author SoulKeeper
 */
public class CallbackHelper
{
	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CallbackHelper.class);

	/**
	 * Private empty constructor to prevent initialization
	 */
	private CallbackHelper()
	{

	}

	/**
	 * Adds callback to the list.<br>
	 * Sorting is done while adding to avoid extra calls.
	 * 
	 * @param callback
	 *            what to add
	 * @param object
	 *            add callback to which objec
	 */
	@SuppressWarnings( { "unchecked" })
	public static void addCallback(Callback callback, EnhancedObject object)
	{
		try
		{
			Map<Class<? extends Callback>, List<Callback>> cbMap = object.getCallbacks();
			object.getCallbackLock().lock();

			List<Callback> list = cbMap.get(callback.getBaseClass());
			if(list == null)
			{
				list = new CopyOnWriteArrayList<Callback>();
				cbMap.put(callback.getBaseClass(), list);
			}

			int callbackPriority = getCallbackPriority(callback);

			// hand-made sorting, if needed to insert to the middle
			for(int i = 0, n = list.size(); i < n; i++)
			{
				Callback c = list.get(i);

				int cPrio = getCallbackPriority(c);

				if(callbackPriority < cPrio)
				{
					list.add(i, callback);
					return;
				}
			}

			// add last
			list.add(callback);
		}
		finally
		{
			object.getCallbackLock().unlock();
		}
	}

	/**
	 * Removes callback from the list
	 * 
	 * @param callback
	 *            what to remove
	 * @param object
	 *            remove callback from which object
	 */
	@SuppressWarnings("unchecked")
	public static void removeCallback(Callback callback, EnhancedObject object)
	{
		try
		{
			object.getCallbackLock().lock();

			Map<Class<? extends Callback>, List<Callback>> cbMap = object.getCallbacks();

			List<Callback> list = cbMap.get(callback.getBaseClass());
			if(list == null || !list.remove(callback))
			{
				// noinspection ThrowableInstanceNeverThrown
				log.error("Attempt to remove callback that doesn't exists", new RuntimeException());
				return;
			}

			if(list.isEmpty())
			{
				cbMap.remove(callback.getBaseClass());
			}
		}
		finally
		{
			object.getCallbackLock().unlock();
		}
	}

	/**
	 * This method call callbacks before actual method invocation takes place
	 * 
	 * @param obj
	 *            object that callbacks are invoked for
	 * @param callbackClass
	 *            base callback class
	 * @param args
	 *            args of method
	 * @return {@link Callback#beforeCall(Object, Object[])}
	 */
	@SuppressWarnings( { "unchecked" })
	public static CallbackResult<?> beforeCall(EnhancedObject obj, Class callbackClass, Object... args)
	{
		List<Callback> list = obj.getCallbacks().get(callbackClass);

		if(list == null || list.isEmpty())
		{
			return CallbackResult.newContinue();
		}

		CallbackResult<?> cr = null;

		for(Callback c : list)
		{
			try
			{
				cr = c.beforeCall(obj, args);
				if(cr.isBlockingCallbacks())
				{
					break;
				}
			}
			catch(Throwable t)
			{
				log.error("Uncaught exception in callback", t);
			}
		}

		return cr == null ? CallbackResult.newContinue() : cr;
	}

	/**
	 * This method invokes callbacks after method invocation
	 * 
	 * @param obj
	 *            object that invokes this method
	 * @param callbackClass
	 *            superclass of callback
	 * @param args
	 *            method args
	 * @param result
	 *            method invokation result
	 * @return {@link Callback#afterCall(Object, Object[], Object)}
	 */
	@SuppressWarnings("unchecked")
	public static CallbackResult<?> afterCall(EnhancedObject obj, Class callbackClass, Object[] args, Object result)
	{
		List<Callback> list = obj.getCallbacks().get(callbackClass);

		if(list == null || list.isEmpty())
		{
			return CallbackResult.newContinue();
		}

		CallbackResult<?> cr = null;

		for(Callback c : list)
		{
			try
			{
				cr = c.afterCall(obj, args, result);
				if(cr.isBlockingCallbacks())
				{
					break;
				}
			}
			catch(Throwable t)
			{
				log.error("Uncaught exception in callback", t);
			}
		}

		return cr == null ? CallbackResult.newContinue() : cr;
	}

	/**
	 * Returns priority of callback.<br>
	 * Method checks if callback is instance of {@link CallbackPriority}, and returns
	 * 
	 * <pre>
	 * {@link CallbackPriority#DEFAULT_PRIORITY} - {@link CallbackPriority#getPriority()}
	 * </pre>
	 * 
	 * .<br>
	 * If callback is not instance of CallbackPriority then it returns {@link CallbackPriority#DEFAULT_PRIORITY}
	 * 
	 * @param callback
	 *            priority to get from
	 * @return priority of callback
	 */
	@SuppressWarnings("unchecked")
	private static int getCallbackPriority(Callback callback)
	{
		if(callback instanceof CallbackPriority)
		{
			CallbackPriority instancePriority = (CallbackPriority) callback;
			return CallbackPriority.DEFAULT_PRIORITY - instancePriority.getPriority();
		}
		else
		{
			return CallbackPriority.DEFAULT_PRIORITY;
		}
	}
}
