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
package com.aionemu.commons.network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.aionemu.commons.network.packet.BaseClientPacket;

/**
 * Packet Processor responsible for executing packets in correct order with respecting rules: - 1 packet / client at one
 * time. - execute packets in received order.
 * 
 * @author -Nemesiss-
 * @param <T>
 *            AConnection - owner of client packets.
 * 
 */
public class PacketProcessor<T extends AConnection>
{
	/**
	 * Logger for PacketProcessor
	 */
	private static final Logger				log					= Logger.getLogger(PacketProcessor.class.getName());
	/**
	 * When one working thread should be killed.
	 */
	private final static int				reduceThreshold		= 3;
	/**
	 * When one working thread should be created.
	 */
	private final static int				increaseThreshold	= 50;

	/**
	 * Lock for synchronization.
	 */
	private final Lock						lock				= new ReentrantLock();

	/**
	 * Not Empty condition.
	 */
	private final Condition					notEmpty			= lock.newCondition();

	/**
	 * Queue of packet that will be executed in correct order.
	 */
	private final List<BaseClientPacket<T>>	packets				= new LinkedList<BaseClientPacket<T>>();

	/**
	 * Working threads.
	 */
	private final List<Thread>				threads				= new ArrayList<Thread>();

	/**
	 * minimum number of working Threads
	 */
	private final int						minThreads;

	/**
	 * maximum number of working Threads
	 */
	private final int						maxThreads;

	/**
	 * Create and start PacketProcessor responsible for executing packets.
	 * 
	 * @param minThreads
	 *            - minimum number of working Threads.
	 * @param maxThreads
	 *            - maximum number of working Threads.
	 */
	public PacketProcessor(int minThreads, int maxThreads)
	{
		if(minThreads <= 0)
			minThreads = 1;
		if(maxThreads < minThreads)
			maxThreads = minThreads;

		this.minThreads = minThreads;
		this.maxThreads = maxThreads;

		if(minThreads != maxThreads)
			startCheckerThread();

		for(int i = 0; i < minThreads; i++)
			newThread();
	}

	/**
	 * Start Checker Thread. Checker is responsible for increasing / reducing PacketProcessor Thread count based on
	 * Runtime needs.
	 */
	private void startCheckerThread()
	{
		new Thread(new CheckerTask(), "PacketProcessor:Checker").start();
	}

	/**
	 * Create and start new PacketProcessor Thread, but only if there wont be more working Threads than "maxThreads"
	 * 
	 * @return true if new Thread was created.
	 */
	private boolean newThread()
	{
		if(threads.size() >= maxThreads)
			return false;

		String name = "PacketProcessor:" + threads.size();
		log.debug("Creating new PacketProcessor Thread: " + name);

		Thread t = new Thread(new PacketProcessorTask(), name);
		threads.add(t);
		t.start();

		return true;
	}

	/**
	 * Kill one PacketProcessor Thread, but only if there are more working Threads than "minThreads"
	 */
	private void killThread()
	{
		if(threads.size() < minThreads)
		{
			Thread t = threads.remove((threads.size() - 1));
			log.debug("Killing PacketProcessor Thread: " + t.getName());
			t.interrupt();
		}
	}

	/**
	 * Add packet to execution queue and execute it as soon as possible on another Thread.
	 * 
	 * @param packet
	 *            that will be executed.
	 */
	public final void executePacket(BaseClientPacket<T> packet)
	{
		lock.lock();
		try
		{
			packets.add(packet);
			notEmpty.signal();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Return first packet available for execution with respecting rules: - 1 packet / client at one time. - execute
	 * packets in received order.
	 * 
	 * @return first available BaseClientPacket
	 */
	private BaseClientPacket<T> getFirstAviable()
	{
		for(;;)
		{
			while(packets.isEmpty())
				notEmpty.awaitUninterruptibly();

			ListIterator<BaseClientPacket<T>> it = packets.listIterator();
			while(it.hasNext())
			{
				BaseClientPacket<T> packet = it.next();
				if(packet.getConnection().tryLockConnection())
				{
					it.remove();
					return packet;
				}
			}
			notEmpty.awaitUninterruptibly();
		}
	}

	/**
	 * Packet Processor Task that will execute packet with respecting rules: - 1 packet / client at one time. - execute
	 * packets in received order.
	 * 
	 * @author -Nemesiss-
	 * 
	 */
	private final class PacketProcessorTask implements Runnable
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			BaseClientPacket<T> packet = null;
			for(;;)
			{
				lock.lock();
				try
				{
					if(packet != null)
						packet.getConnection().unlockConnection();

					/* thread killed */
					if(Thread.interrupted())
						return;

					packet = getFirstAviable();
				}
				finally
				{
					lock.unlock();
				}
				packet.run();
			}
		}
	}

	/**
	 * Checking if PacketProcessor is busy or idle and increasing / reducing numbers of threads.
	 * 
	 * @author -Nemesiss-
	 * 
	 */
	private final class CheckerTask implements Runnable
	{
		/**
		 * How often CheckerTask should do check.
		 */
		private final int	sleepTime	= 60 * 1000;
		/**
		 * Number of packets waiting for execution on last check.
		 */
		private int			lastSize	= 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			/* Sleep for some time */
			try
			{
				Thread.sleep(sleepTime);
			}
			catch(InterruptedException e)
			{
				// we dont care
			}

			/* Number of packets waiting for execution */
			int sizeNow = packets.size();

			if(sizeNow < lastSize)
			{
				if(sizeNow < reduceThreshold)
				{
					// too much threads
					killThread();
				}
			}
			else if(sizeNow > lastSize && sizeNow > increaseThreshold)
			{
				// too low threads
				if(!newThread() && sizeNow >= increaseThreshold * 3)
					log
						.info("Lagg detected! ["
							+ sizeNow
							+ " client packets are waiting for execution]. You should consider increasing PacketProcessor maxThreads or hardware upgrade.");
			}
			lastSize = sizeNow;
		}
	}
}
