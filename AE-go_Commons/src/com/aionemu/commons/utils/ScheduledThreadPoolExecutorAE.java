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
package com.aionemu.commons.utils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * This is custom implementation of ScheduledThreadPoolExecutor. Only differ is that this implementation handle uncaught
 * exceptions and notify thread UncaughtExceptionHandler.
 * 
 * @author -Nemesiss-
 * 
 */
public class ScheduledThreadPoolExecutorAE extends ScheduledThreadPoolExecutor
{
	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given initial parameters.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are idle
	 * @param handler
	 *            the handler to use when execution is blocked because the thread bounds and queue capacities are
	 *            reached
	 * @throws IllegalArgumentException
	 *             if <tt>corePoolSize &lt; 0</tt>
	 * @throws NullPointerException
	 *             if handler is null
	 */
	public ScheduledThreadPoolExecutorAE(int corePoolSize, RejectedExecutionHandler handler)
	{
		super(corePoolSize, handler);
	}

	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given initial parameters.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are idle
	 * @param threadFactory
	 *            the factory to use when the executor creates a new thread
	 * @param handler
	 *            the handler to use when execution is blocked because the thread bounds and queue capacities are
	 *            reached.
	 * @throws IllegalArgumentException
	 *             if <tt>corePoolSize &lt; 0</tt>
	 * @throws NullPointerException
	 *             if threadFactory or handler is null
	 */
	public ScheduledThreadPoolExecutorAE(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler)
	{
		super(corePoolSize, threadFactory, handler);
	}

	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given initial parameters.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are idle
	 * @param threadFactory
	 *            the factory to use when the executor creates a new thread
	 * @throws IllegalArgumentException
	 *             if <tt>corePoolSize &lt; 0</tt>
	 * @throws NullPointerException
	 *             if threadFactory is null
	 */
	public ScheduledThreadPoolExecutorAE(int corePoolSize, ThreadFactory threadFactory)
	{
		super(corePoolSize, threadFactory);
	}

	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given core pool size.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are idle
	 * @throws IllegalArgumentException
	 *             if <tt>corePoolSize &lt; 0</tt>
	 */
	public ScheduledThreadPoolExecutorAE(int corePoolSize)
	{
		super(corePoolSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t)
	{
		super.afterExecute(r, t);
		/**
		 * Check if there was exception
		 */
		Future<?> f = ((Future<?>) r);
		if(f.isDone())
		{
			try
			{
				f.get();
			}
			catch(ExecutionException e)
			{
				/**
				 * Get cause and notify thread UncaughtExceptionHandler
				 */
				Thread thread = Thread.currentThread();
				thread.getUncaughtExceptionHandler().uncaughtException(thread, e.getCause());
			}
			catch(InterruptedException e)
			{
				// we dont care
			}
			catch(CancellationException e)
			{
				// we dont care
			}
		}
	}
}
