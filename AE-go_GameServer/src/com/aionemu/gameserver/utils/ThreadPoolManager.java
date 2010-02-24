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
package com.aionemu.gameserver.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.aionemu.commons.network.DisconnectionTask;
import com.aionemu.commons.network.DisconnectionThreadPool;
import com.aionemu.commons.utils.ScheduledThreadPoolExecutorAE;

/**
 * @author -Nemesiss-
 */
public class ThreadPoolManager implements DisconnectionThreadPool
{
	/**
	 * Logger for this class
	 */
	private static final Logger				log			= Logger.getLogger(ThreadPoolManager.class);

	/**
	 * instance of the singleton
	 */
	private static ThreadPoolManager		instance	= new ThreadPoolManager();

	/**
	 * common scheduled threads pool
	 */
	private ScheduledThreadPoolExecutorAE	scheduledThreadPool;

	/**
	 * Disconnection scheduled threads pool
	 */
	private ScheduledThreadPoolExecutorAE	disconnectionScheduledThreadPool;

	/**
	 * Effects scheduled threads pool
	 */
	private ScheduledThreadPoolExecutorAE	effectsScheduledThreadPool;

	/**
	 * AI scheduled threads pool
	 */
	private ScheduledThreadPoolExecutorAE	aiScheduledThreadPool;
	
	/**
	 * KnownListUpdate scheduled threads pool
	 */
	private ScheduledThreadPoolExecutorAE	taskManagerThreadPool;

	/**
	 * Thread manager for loginServer packets
	 */
	private ThreadPoolExecutor				loginServerPacketsThreadPool;

	/**
	 * @return ThreadPoolManager instance.
	 */
	public static ThreadPoolManager getInstance()
	{
		return instance;
	}

	/**
	 * Constructor.
	 */
	private ThreadPoolManager()
	{
		scheduledThreadPool = new ScheduledThreadPoolExecutorAE(5, new PriorityThreadFactory("ScheduledThreadPool",
			Thread.NORM_PRIORITY));
		// scheduledThreadPool.setRemoveOnCancelPolicy(true);

		effectsScheduledThreadPool = new ScheduledThreadPoolExecutorAE(6, new PriorityThreadFactory(
			"EffectsScheduledThreadPool", Thread.NORM_PRIORITY));

		aiScheduledThreadPool = new ScheduledThreadPoolExecutorAE(12, new PriorityThreadFactory(
			"AiScheduledThreadPool", Thread.NORM_PRIORITY));

		disconnectionScheduledThreadPool = new ScheduledThreadPoolExecutorAE(4, new PriorityThreadFactory(
			"ScheduledThreadPool", Thread.NORM_PRIORITY));
		// disconnectionScheduledThreadPool.setRemoveOnCancelPolicy(true);

		loginServerPacketsThreadPool = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("Login Server Packet Pool",
				Thread.NORM_PRIORITY + 3));
		
		taskManagerThreadPool = new ScheduledThreadPoolExecutorAE(4, new PriorityThreadFactory(
				"TaskManagerThreadPool", Thread.NORM_PRIORITY));
	}

	/**
	 * Standard tasks schedulers (once)
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param delay
	 *            wait before task execution
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> schedule(T r, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			return (ScheduledFuture<T>) scheduledThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null; /* shutdown, ignore */
		}
	}

	/**
	 * Standard tasks schedulers (cyclic)
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param initial
	 *            wait before first exec
	 * @param delay
	 *            delay between executions
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleAtFixedRate(T r, long initial, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			if(initial < 0)
				initial = 0;
			return (ScheduledFuture<T>) scheduledThreadPool.scheduleAtFixedRate(r, initial, delay,
				TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}

	/**
	 * Effects schedulers (once)
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param delay
	 *            wait before task execution
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleEffect(T r, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			return (ScheduledFuture<T>) effectsScheduledThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}

	/**
	 * Effects schedulers (cyclic)
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param initial
	 *            wait before first task execution
	 * @param delay
	 *            delay between executions
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleEffectAtFixedRate(T r, long initial, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			if(initial < 0)
				initial = 0;
			return (ScheduledFuture<T>) effectsScheduledThreadPool.scheduleAtFixedRate(r, initial, delay,
				TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}

	/**  **/

	/**
	 * AI schedulers
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param delay
	 *            wait before task execution
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleAi(T r, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			return (ScheduledFuture<T>) aiScheduledThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}

	/**
	 * AI schedulers
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param initial
	 *            wait before first task execution
	 * @param delay
	 *            delay between executions
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleAiAtFixedRate(T r, long initial, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			if(initial < 0)
				initial = 0;
			return (ScheduledFuture<T>) aiScheduledThreadPool.scheduleAtFixedRate(r, initial, delay,
				TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}

	/**
	 * Executes a loginServer packet task
	 * 
	 * @param pkt
	 *            runnable packet for Login Server
	 */
	public void executeLsPacket(Runnable pkt)
	{
		loginServerPacketsThreadPool.execute(pkt);
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aionemu.commons.network.DisconnectionThreadPool#scheduleDisconnection(com.aionemu.commons.network.
	 * DisconnectionTask, long)
	 */
	@Override
	public final void scheduleDisconnection(DisconnectionTask dt, long delay)
	{
		if(delay < 0)
			delay = 0;
		scheduledThreadPool.schedule(dt, delay, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.commons.network.DisconnectionThreadPool#waitForDisconnectionTasks()
	 */
	@Override
	public void waitForDisconnectionTasks()
	{
		try
		{
			disconnectionScheduledThreadPool.shutdown();
			disconnectionScheduledThreadPool.awaitTermination(6, TimeUnit.MINUTES);
		}
		catch(Exception e)
		{
		}
	}

	/**
	 * TaskManager schedulers
	 * 
	 * @param <T>
	 *            Template for Runnable
	 * @param r
	 *            runnable task
	 * @param delay
	 *            wait before task execution
	 * @return scheduled task
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ScheduledFuture<T> scheduleTaskManager(T r, long delay)
	{
		try
		{
			if(delay < 0)
				delay = 0;
			return (ScheduledFuture<T>) taskManagerThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		}
		catch(RejectedExecutionException e)
		{
			return null;
		}
	}	
	
	/**
	 * Custom implementation of ThreadFactory to manage priorities
	 * 
	 * @author -Nemesiss-
	 * 
	 */
	private class PriorityThreadFactory implements ThreadFactory
	{
		private int				prio;
		private String			name;
		private AtomicInteger	threadNumber	= new AtomicInteger(1);
		private ThreadGroup		group;

		/**
		 * Parametered Constructor
		 * 
		 * @param name
		 *            Thread name
		 * @param prio
		 *            Thread priority
		 */
		public PriorityThreadFactory(String name, int prio)
		{
			this.prio = prio;
			this.name = name;
			group = new ThreadGroup(this.name);
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(group, r);
			t.setName(name + "-" + threadNumber.getAndIncrement());
			t.setPriority(prio);
			t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
			return t;
		}
	}

	/**
	 * Shutdown all thread pools.
	 */
	public void shutdown()
	{
		try
		{
			scheduledThreadPool.shutdown();
			effectsScheduledThreadPool.shutdown();
			aiScheduledThreadPool.shutdown();
			loginServerPacketsThreadPool.shutdown();
			scheduledThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			effectsScheduledThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			aiScheduledThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			loginServerPacketsThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			taskManagerThreadPool.awaitTermination(2,TimeUnit.SECONDS);
			log.info("All ThreadPools are stopped now.");
		}
		catch(InterruptedException e)
		{
			log.error("Can't shutdown ThreadPoolManager", e);
		}
	}
}
